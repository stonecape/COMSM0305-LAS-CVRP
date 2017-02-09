package uk.ac.bristol.cvrp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class ACO_CVRP {
	private static final float DEFAULT_PHEROMONE = 0.0000018f;
	private static final int FIRST_NODE = 1;// default
	private static final int HC_MAX_ITERATION = 50;
	private static final int ANT_NUM = 30;
	private static final int MIN_MUTATION_TOUR_SIZE = 8;
	private static final int MAX_STAGNANT_COUNT = 5000;
	private static final float STEP_Q0 = 0.02f;
	private List<Double> stepsLength;
	
	private Ant_CVRP[] ants; 
	private int nodeNum; 
	private int eachVehicleCapacity;
	
	//the max num of Generation
	private int maxGen;
	private double[][][] pheromone; 
	private float[][][] delta;
	private double bestLength; 
	private List<Route> bestTourList; 
	
	private List<Route> currentTourList;
	
	// some parameters of Ant Colony System
	private float alpha;
	private float beta;
	private float rho;
	float q0;
	float qMutation;
	
	// global Tabu and AllowedNodes
	private Vector<Integer> globalTabu;
	private Vector<Integer> globalAllowedNodes;
	
	public static final boolean OPEN_ROUTE_OPTIMIZATION = true;
	public static final boolean OPEN_MUTATION = true;
	private static final boolean OPEN_CANDIDATE_LIST = true;
	private static final boolean OPEN_INIT_PHEREMONE_UPDATING = false;
	long beginTime;
	
	
	public ACO_CVRP() {
		
	}

	public ACO_CVRP(int nodeNum, int maxGen, int eachVehicleCapacity,
			float alpha, float beta, float rho, float q0, float qMutation, long beginTime ) {
		super();
		this.nodeNum = nodeNum;
		this.maxGen = maxGen;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.eachVehicleCapacity = eachVehicleCapacity;
		this.q0 = q0;
		this.qMutation = qMutation;
		this.beginTime = beginTime;
	}
	
	public void initFromSA() {
		pheromone = new double[ANT_NUM + 1][nodeNum + 1][nodeNum + 1];
		delta = new float[ANT_NUM + 1][nodeNum + 1][nodeNum + 1];
		stepsLength = new ArrayList<Double>();
		for(int a = 1; a <= ANT_NUM; a++) {
			for(int i = 1; i <= nodeNum; i++) {
				for(int j = 1; j <= nodeNum; j++) {
					pheromone[a][i][j] = DEFAULT_PHEROMONE;
					delta[a][i][j] = 0.0f;
				}
			}
		}
		
	}
	
	/**
	 * initialization
	 * @param firstNode
	 * @param capacity
	 */
	public void init() {
		this.bestTourList = new ArrayList<Route>();
		pheromone = new double[ANT_NUM + 1][nodeNum + 1][nodeNum + 1];
		delta = new float[ANT_NUM + 1][nodeNum + 1][nodeNum + 1];
		stepsLength = new ArrayList<Double>();
		for(int a = 1; a <= ANT_NUM; a++) {
			for(int i = 1; i <= nodeNum; i++) {
				for(int j = 1; j <= nodeNum; j++) {
					pheromone[a][i][j] = DEFAULT_PHEROMONE;
					delta[a][i][j] = 0.0f;
				}
			}
		}
		if(OPEN_INIT_PHEREMONE_UPDATING) {
			initUpdatingPheremone();
		}
		
		bestLength = Double.MAX_VALUE;
		
	}
	
	public void solve() {
		int stagnantCount = 0;
		
		for(int g = 0; g < maxGen; g++) {
			// MAX num of gengeration
			boolean flag = true;
			double currentLength = 0;
			this.currentTourList = new ArrayList<Route>();
			this.globalTabu = new Vector<Integer>();
			this.globalAllowedNodes = new Vector<Integer>();
			for(int i = 1; i <= nodeNum; i++) {
				globalAllowedNodes.add(i);
			}
			
			int countAnt = 1;
			do {
				Ant_CVRP ant = new Ant_CVRP(nodeNum, eachVehicleCapacity, OPEN_CANDIDATE_LIST);
				ant.init(alpha, beta, FIRST_NODE, globalTabu, globalAllowedNodes);
				
				Result result = null;
				boolean localFlag = true;
				//double[][] weightedPheomone = getWeightedPheromone(pheromone);
				do {
					result = ant.selectNextNode(pheromone[countAnt], q0);
					
					if(result.getStatus() != Status.NOMAL) {
						localFlag = false;
					}
				} while(localFlag);
				
				// update global variables
				this.setGlobalAllowedNodes(result.getGloAllowedNodes());
				this.setGlobalTabu(result.getGloTabu());
				
				Route routr = null;
				if(OPEN_ROUTE_OPTIMIZATION) {
					//routr = optimizeRoutePerAnt(result.getRoute());
					routr = optimizeRoutePerAntbyHc(result.getRoute());
					
				} else {
					routr = result.getRoute();
				}
				this.getCurrentTourList().add(routr);
				
				// local updating pheromone
				localUpdatingPheremone(countAnt, routr.getTour());
				
				if(result.getStatus() == Status.FINISHED) {
					if(OPEN_MUTATION) {
						Random random = new Random(System.currentTimeMillis());
						float randomMutationP = random.nextFloat();
						if(randomMutationP <= qMutation) {
							globalMutation();
						}
						
					}
					// compare bestlength and tourlist, then update
					currentLength = calCurrentLength();
					if(calCurrentLength() < this.getBestLength()) {
						stagnantCount = 0;
						this.setBestLength(currentLength);
						this.setBestTourList(this.getCurrentTourList());
					} else {
						stagnantCount ++;
						//System.out.println(stagnantCount + " " + this.q0);
						if(stagnantCount > MAX_STAGNANT_COUNT) {
							if(this.q0 - STEP_Q0 > 0.0) {
								this.q0 -= STEP_Q0;
							}
							stagnantCount = 0;
						}
					}
					System.out.println("current bestlength->" + this.getBestLength());
					flag = false;
					stepsLength.add(this.getBestLength());
				}
				
				countAnt++;
			} while(flag);
			
			
			globalUpdatingPheremone();
		}
		
		printOptimal();
		
	}

	private void globalMutation() {
		int r1 = 0, r2 = 0;
		while(r1 == r2) {
			r1 = (int) (this.getCurrentTourList().size() * Math.random());
			r2 = (int) (this.getCurrentTourList().size() * Math.random());
		}
		boolean flag = true;
		int count = 0;
		Route route1 = this.getCurrentTourList().get(r1);
		Route route2 = this.getCurrentTourList().get(r2);
		
		if(route1.getTour().size() >= MIN_MUTATION_TOUR_SIZE && route2.getTour().size() >= MIN_MUTATION_TOUR_SIZE) {
			while(flag && count < 100000) {
				int r1TourSize = route1.getTour().size();
				int r2TourSize = route2.getTour().size();
				int r1CustomerIndex = (int) ((r1TourSize - 1) * Math.random());
				int r2CustomerIndex = (int) ((r2TourSize - 1) * Math.random());
				if(r1CustomerIndex != 0 && r2CustomerIndex != 0) {
					// mutate
					int temp = route1.getTour().get(r1CustomerIndex);
					route1.getTour().set(r1CustomerIndex,route2.getTour().get(r2CustomerIndex));
					route2.getTour().set(r2CustomerIndex, temp);
					
					if (judgeCapacity(route1.getTour()) && judgeCapacity(route2.getTour())) {
						flag = false;
					} else {
						route2.getTour().set(r2CustomerIndex, route1.getTour().get(r1CustomerIndex));
						route1.getTour().set(r1CustomerIndex, temp);
					}
				}
				count ++;
			}
			
			if(count >= 100000) {
				System.err.println("count > 50000  " + count);
			} else {
				route1 = optimizeRoutePerAntbyHc(route1);
				route2 = optimizeRoutePerAntbyHc(route2);
			}
		}
	
	}
	
	private boolean judgeCapacity(Vector<Integer> tour) {
		int currentWeight = 0;
		for(Integer t : tour) {
			currentWeight += CVRPData.getDemand(t);
		}
		if(currentWeight > CVRPData.VEHICLE_CAPACITY) {
			return false;
		}
		return true;
	}
	
	// hill climbing algorithm to optimize the Route
	private Route optimizeRoutePerAntbyHc(Route preProcessRoute) {
		Route result = null;
		Vector<Integer> hcTour = new Vector<Integer>(preProcessRoute.getTour());
		List<Integer> tourExceptDepotsList = hcTour.subList(1, hcTour.size() - 1);
		
		if(tourExceptDepotsList.size() > 1) {
			Vector<Integer> bestRoute = new Vector<Integer>(preProcessRoute.getTour());
			double bestLength = calLengthPerAnt(bestRoute);
			int stableCount = 0;
			while(stableCount < HC_MAX_ITERATION) {
				Vector<Integer> adjacentRoute = obtainAdjacentRoute(tourExceptDepotsList);
				double adjacentLength = calLengthPerAnt(adjacentRoute);
				if(adjacentLength < bestLength) {
					bestLength = adjacentLength;
					bestRoute = new Vector<Integer>(adjacentRoute);
					stableCount = 0;
				} else {
					stableCount++;
				}
			}
			result = new Route(bestRoute);
		} else {
			result = new Route(preProcessRoute.getTour());
		}
		return result;
	}
	
	private Vector<Integer> obtainAdjacentRoute(List<Integer> tourExceptDepotsList) {
		Vector<Integer> result = new Vector<Integer>();
		result.add(FIRST_NODE);
		int x1 = 0, x2 = 0;
		while(x1 == x2) {
			x1 = (int) (tourExceptDepotsList.size() * Math.random());
			x2 = (int) (tourExceptDepotsList.size() * Math.random());
		}
		int temp = tourExceptDepotsList.get(x1);
		tourExceptDepotsList.set(x1, tourExceptDepotsList.get(x2));
		tourExceptDepotsList.set(x2, temp);
		result.addAll(tourExceptDepotsList);
		result.add(FIRST_NODE);
		return result;
	}
	
	private void printOptimal() {
		System.out.println("login hc16191 31288");
		System.out.println("name Chen Haoyan");
		System.out.println("algorithm improved ACO");
		System.out.println("cost " + this.getBestLength());
		Vector<Integer> wholeRoute = new Vector<Integer>();
		wholeRoute.add(FIRST_NODE);
		for(Route r : this.getBestTourList()) {
			StringBuffer sbf = new StringBuffer();
			for(int i : r.getTour()) {
				if(i != FIRST_NODE) {
					wholeRoute.add(i);
				}
				sbf.append(i + "->");
			}
			System.out.println(sbf.substring(0, sbf.lastIndexOf("->")));
		}
	}

	private void localUpdatingPheremone(int countAnt, Vector<Integer> tour) {
		for(int i = 1; i <= nodeNum; i++) {
			for (int j = 1; j <= nodeNum; j++) {
				pheromone[countAnt][i][j] = pheromone[countAnt][i][j] * rho
						+ ((1 - rho) * DEFAULT_PHEROMONE);
			}
		}
	}
	private void globalUpdatingPheremone() {
		List<Route> routes = this.getBestTourList();
		for(int i = 0; i < routes.size(); i++) {
			Vector<Integer> tour = routes.get(i).getTour();
			double antLength = calLengthPerAnt(tour);
			for(int j = 0; j < tour.size() - 1; j++) {
				if(antLength >= 0) {
					this.getDelta()[i + 1][tour.get(j)][tour.get(j+1)] = (float) (1.0 / antLength);
					this.getDelta()[i + 1][tour.get(j+1)][tour.get(j)] = (float) (1.0 / antLength);
				} else {
					System.err.println("ERROR:currentLength == 0");
				}
			}
		}
		
		for(int a = 1; a <= this.getBestTourList().size(); a++) {
			for(int i = 1; i <= nodeNum; i++) {
				for(int j = 1; j <= nodeNum; j++) {
					pheromone[a][i][j] = pheromone[a][i][j] * rho + ((1 - rho) * this.getDelta()[a][i][j]);
				}
			}
		}
	}
	
	
	private void initUpdatingPheremone() {
		for(int countAnt = 1; countAnt <= ANT_NUM; countAnt++) {
			for(int i = 1; i <= nodeNum; i++) { 
				for(int j = 1; j <= nodeNum; j++) {
					pheromone[countAnt][i][j] = pheromone[countAnt][i][j] * (1.0 / CVRPData.getDistance(i, j));
				}
			}
		}
		
	}
	
	private double calCurrentLength() {
		double len = 0.0d;
		Vector<Integer> tour = null;
		for(Route r : this.getCurrentTourList()) {
			tour = r.getTour();
			for(int i = 0; i < tour.size() - 1; i++) {
				len += CVRPData.getDistance(tour.get(i), tour.get(i + 1));
			}
		}
		return len;
	}
	
	private double calLengthPerAnt(Vector<Integer> tour) {
		double len = 0.0d;
		for(int i = 0; i < tour.size() - 1; i++) {
			len += CVRPData.getDistance(tour.get(i), tour.get(i + 1));
		}
		return len;
	}
	
	public Ant_CVRP[] getAnts() {
		return ants;
	}

	public void setAnts(Ant_CVRP[] ants) {
		this.ants = ants;
	}

	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}

	public int getEachVehicleCapacity() {
		return eachVehicleCapacity;
	}

	public void setEachVehicleCapacity(int eachVehicleCapacity) {
		this.eachVehicleCapacity = eachVehicleCapacity;
	}

	public int getMaxGen() {
		return maxGen;
	}

	public void setMaxGen(int maxGen) {
		this.maxGen = maxGen;
	}


	public double[][][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(double[][][] pheromone) {
		this.pheromone = pheromone;
	}

	public float[][][] getDelta() {
		return delta;
	}

	public void setDelta(float[][][] delta) {
		this.delta = delta;
	}

	public double getBestLength() {
		return bestLength;
	}

	public void setBestLength(double bestLength) {
		this.bestLength = bestLength;
	}

	public List<Route> getBestTourList() {
		return bestTourList;
	}

	public void setBestTourList(List<Route> bestTourList) {
		this.bestTourList = bestTourList;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getRho() {
		return rho;
	}

	public void setRho(float rho) {
		this.rho = rho;
	}


	public Vector<Integer> getGlobalTabu() {
		return globalTabu;
	}

	public void setGlobalTabu(Vector<Integer> globalTabu) {
		this.globalTabu = globalTabu;
	}

	public Vector<Integer> getGlobalAllowedNodes() {
		return globalAllowedNodes;
	}

	public void setGlobalAllowedNodes(Vector<Integer> globalAllowedNodes) {
		this.globalAllowedNodes = globalAllowedNodes;
	}

	public List<Route> getCurrentTourList() {
		return currentTourList;
	}

	public void setCurrentTourList(List<Route> currentTourList) {
		this.currentTourList = currentTourList;
	}
	

	
	public static void main(String[] args) throws Exception {
		int max_gen = 2;
		float alpha = 1.0f;
		float beta = 7.0f;
		float rho = 0.7f;
		float q0 = 0.5f;
		float qMutation = 0.5f;
		
		long begin = System.currentTimeMillis();
		ACO_CVRP aco = new ACO_CVRP(CVRPData.NUM_NODES, max_gen, CVRPData.VEHICLE_CAPACITY, 
				alpha, beta, rho, q0, qMutation, begin);
		aco.init();
		aco.solve();
	}
	
	
}
