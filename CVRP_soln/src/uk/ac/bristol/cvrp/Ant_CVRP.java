package uk.ac.bristol.cvrp;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class Ant_CVRP {
	// cities are not allowed to visit
	private Vector<Integer> localTabu;
	private Vector<Integer> localAllowedNodes;
	private Vector<Integer> localRoute;

	private float alpha;
	private float beta;

	private int nodeNum;
	private int capacity;// the capacity of each truck (500)
	
	private Integer firstNode;
	private Integer currentNode;
	private int currentWeight;// how many goods the ant(truck) carry currently
	
	private boolean openCandidateList;// open candidatelist optimization?

	public Ant_CVRP() {
		this.nodeNum = 250;
		this.capacity = 500;
		this.currentWeight = 0;
		this.openCandidateList = false;
	}

	public Ant_CVRP(int nodeNum, int capacity, boolean openCandidateList) {
		this.nodeNum = nodeNum;
		this.capacity = capacity;
		this.currentWeight = 0;
		this.openCandidateList = openCandidateList;
	}
	
	/**
	 * initialize ant
	 * @param alpha
	 * @param beta
	 * @param firstNode
	 */
	public void init(float alpha, float beta, Integer firstNode,
			Vector<Integer> tabu, Vector<Integer> allowedNodes) {
		this.alpha = alpha;
		this.beta = beta;
		this.localRoute = new Vector<Integer>();
		this.localTabu = tabu;
		this.localAllowedNodes = allowedNodes;

		Result result = isNeedNext(firstNode);
		// calculate the weight
		if(result.getStatus() == Status.NOMAL) {
			this.firstNode = firstNode;
			localAllowedNodes.remove(firstNode);
			// add the first node to tabu table
			if(!localTabu.contains(firstNode)) {
				localTabu.add(firstNode);
			}
			localRoute.add(firstNode);
			this.currentNode = firstNode;
		} else {
			 System.err.println(result.getMessage());
		     System.exit(-1);
		}
		
		
	}

	public Result selectNextNode(double[][] pheromone, float q0) {
		double[] p = new double[nodeNum + 1];
		double sum = 0.0;
		
		// all customers are traveled?
		// if completeResult.getStatus != Status.NOMAL- END
		Result completeResult = isComplete();
		if(completeResult.getStatus() == Status.FINISHED) {
			return completeResult;
		}
		
		Vector<Integer> tempLocalAllowedNodes = null;
		// candidate list to filter customers
		if(openCandidateList) {
			Vector<Integer> candidateList = new Vector<Integer>(CVRPData.getCandidateList(currentNode));
			tempLocalAllowedNodes = new Vector<Integer>();
			for(Integer candidate : candidateList) {
				if(localAllowedNodes.contains(candidate)) {
					tempLocalAllowedNodes.add(candidate);
				}
			}
		}
		
		if(tempLocalAllowedNodes == null || tempLocalAllowedNodes.size() <= 0) {
			tempLocalAllowedNodes = new Vector<Integer>(localAllowedNodes);
		}
		
		Random random = new Random(System.currentTimeMillis());
		Integer selectNode = (Integer)0;
		
		float q = random.nextFloat();
		
		if(q <= q0) {
			double maxVal = 0.0;
			int j = 0;
			for(int i : tempLocalAllowedNodes) {
				double val =pheromone[currentNode][i] 
						* Math.pow((1.0 / CVRPData.getDistance(currentNode, i)), beta);
				if (val > maxVal) {
					maxVal = val;
					j = i;
				}
			}
			selectNode = j;
		} else {
			// calculate the denominator
			for(int i : tempLocalAllowedNodes) {
				//sum += Math.pow(pheromone[currentNode][i], alpha) * Math.pow((1.0 / CVRPData.getDistance(currentNode, i)), beta);
				sum += pheromone[currentNode][i]
						* Math.pow((1.0 / CVRPData.getDistance(currentNode, i)), beta);
			}
			if(sum == 0.0) {
				System.err.println("sum == 0");
			}
			
			// calculate the possibility of choice
			for(int i = 1; i <= nodeNum; i++) {
				if(tempLocalAllowedNodes.contains(i)) {
					/*p[i] = (double) (Math.pow(pheromone[currentNode][i], alpha)
							* Math.pow((1.0 / CVRPData.getDistance(currentNode, i)), beta)) / sum;*/
					p[i] =  (pheromone[currentNode][i] * Math.pow((1.0 / CVRPData.getDistance(currentNode, i)), beta)) / sum;
				} else {
					p[i] = 0.0f;
				}
				
			}
			
			// use roulette to choose next node
			double sump = 0.0;
			
			while (selectNode == 0) {
				float selectP = random.nextFloat();
				for (int i = 1; i <= nodeNum; i++) {
					sump += p[i];
					if (sump >= selectP) {
						selectNode = i;
						break;
					}
				}
			}
		}
		
		if(selectNode == 0) {
			System.err.println("selectNode == 0");
			System.err.println(this.getLocalAllowedNodes());
			System.err.println(this.getLocalTabu());
			System.err.println(this.getLocalRoute());
			System.err.println(Arrays.toString(p));
			System.err.println(sum);
			System.err.println(Arrays.toString(pheromone));
		}
		
		Result iresult = isNeedNext(selectNode);
		if(iresult.getStatus() == Status.NOMAL) {
			// remove selectnode from allowedNodes
			localAllowedNodes.remove(selectNode);
			localTabu.add(selectNode);
			localRoute.add(selectNode);
			this.currentNode = selectNode;
		} else {
			localRoute.add(firstNode);// go back to firstnode
		}
		
		iresult.setGloAllowedNodes(this.getLocalAllowedNodes());
		iresult.setGloTabu(this.getLocalTabu());
		
		Route route = new Route();
		route.setTour(localRoute);
		iresult.setRoute(route);
		
		return iresult;
	}
	
	// return 0: nextNode is legal and do next - CONTINUE
	// return 1: all ants have traveled the whole customers - END
	private Result isComplete() {
		Result result = new Result();
		if(this.getLocalAllowedNodes().size() <= 0 && this.getLocalTabu().size() >= nodeNum) {
			// all ants have traveled the whole customers
			result.setStatus(Status.FINISHED);
			result.setMessage(Status.FINISHED_MSG);
			result.setGloAllowedNodes(this.getLocalAllowedNodes());
			result.setGloTabu(this.getLocalTabu());
			
			localRoute.add(firstNode);// go back to firstnode
			Route route = new Route();
			route.setTour(localRoute);
			result.setRoute(route);
			
		} else {
			result.setStatus(Status.NOMAL);
			result.setMessage(Status.NOMAL_MSG);
		}
		return result;
	}
	
	// return 0: nextNode is legal and do next - CONTINUE
	// return 2: it's overweight - END
	private Result isNeedNext(int nextNode) {
		Result result = new Result();
		
		int demand = CVRPData.getDemand(nextNode);
		int nextWeight = this.currentWeight + demand;
		if(nextWeight <= this.capacity) {
			this.currentWeight = nextWeight;
			result.setStatus(Status.NOMAL);
			result.setMessage(Status.NOMAL_MSG);
			return result;
		} else {
			result.setStatus(Status.OVERWEIGHT);
			result.setMessage(Status.OVERWEIGHT_MSG);
			return result;
		}
		
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

	public int getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getFirstNode() {
		return firstNode;
	}

	public void setFirstNode(int firstNode) {
		this.firstNode = firstNode;
	}

	public int getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(int currentNode) {
		this.currentNode = currentNode;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}

	public Vector<Integer> getLocalTabu() {
		return localTabu;
	}

	public void setLocalTabu(Vector<Integer> localTabu) {
		this.localTabu = localTabu;
	}

	public Vector<Integer> getLocalAllowedNodes() {
		return localAllowedNodes;
	}

	public void setLocalAllowedNodes(Vector<Integer> localAllowedNodes) {
		this.localAllowedNodes = localAllowedNodes;
	}

	public Vector<Integer> getLocalRoute() {
		return localRoute;
	}

	public void setLocalRoute(Vector<Integer> localRoute) {
		this.localRoute = localRoute;
	}

}