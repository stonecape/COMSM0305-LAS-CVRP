package uk.ac.bristol.cvrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/** 
* @author  Frank Chen
* hc16191@my.bristol.ac.uk
*/
public class HillCliming_CVRP {
	private static final Integer FIRST_NODE = 1;
	private int nodeNumber;
	private Vector<Integer> optializedRoute;
	double bestLength;
	
	public HillCliming_CVRP() {
		this.nodeNumber = CVRPData.NUM_NODES;
		optializedRoute = new Vector<Integer>();
		optializedRoute.add(FIRST_NODE);
		
		Vector<Integer> temp = new Vector<Integer>();
		for(int i = 2; i <= nodeNumber; i++) {
			temp.add(i);
		}
		Collections.shuffle(temp);
		optializedRoute.addAll(temp);
		optializedRoute.add(FIRST_NODE);
		
		this.setBestLength(calDistance(this.getOptializedRoute()));
	}
	
	private Vector<Integer> mutateRoute(Vector<Integer> route) {
		Vector<Integer> result = new Vector<Integer>(route);
		Random random = new Random();
		int r1 = 0, r2 = 0;
        // randomly choose number [1, 249]
			r1 = random.nextInt(nodeNumber - 1) + 1;
	        r2 = random.nextInt(nodeNumber - 1) + 1;
        
       if(r1 == r2) {
        	r2 = random.nextInt(nodeNumber - 1) + 1;
        }
        int temp = result.get(r1);
        result.set(r1, result.get(r2));
        result.set(r2, temp);
        
		return result;
	}
	
	private double calDistance(Vector<Integer> route) {
		double len = 0.0d;
		Vector<Integer> tour = null;
		List<Route> splitedRoutes = splitRoute(route);
		for(Route r : splitedRoutes) {
			tour = r.getTour();
			for(int i = 0; i < tour.size() - 1; i++) {
				len += CVRPData.getDistance(tour.get(i), tour.get(i + 1));
			}
		}
		
		return len;
	}
	
	private List<Route> splitRoute(Vector<Integer> route) {
		List<Route> result = new ArrayList<Route>();
		Route r = new Route();
		r.getTour().add(FIRST_NODE);
		int nextWeight = 0;
		
		for(int i = 0; i < route.size() - 1; i++) {
			int demand = CVRPData.getDemand(route.get(i + 1));
			nextWeight += demand;
			if(nextWeight > CVRPData.VEHICLE_CAPACITY) {
				r.getTour().add(FIRST_NODE);
				result.add(r);
				
				r = new Route();
				r.getTour().add(FIRST_NODE);
				r.getTour().add(route.get(i + 1));
				nextWeight = CVRPData.getDemand(route.get(i + 1));
			} else {
				r.getTour().add(route.get(i + 1));
			}
		}
		result.add(r);
		
		return result;
	}
	
	private void printOptimalRoutes(Vector<Integer> route) {
		List<Route> bestTourList = splitRoute(route);
		for(Route r : bestTourList) {
			StringBuffer sbf = new StringBuffer();
			for(int i : r.getTour()) {
				sbf.append(i + "->");
			}
			System.out.println(sbf.substring(0, sbf.lastIndexOf("->")));
		}
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public Vector<Integer> getOptializedRoute() {
		return optializedRoute;
	}

	public void setOptializedRoute(Vector<Integer> optializedRoute) {
		this.optializedRoute = optializedRoute;
	}
	

	public double getBestLength() {
		return bestLength;
	}

	public void setBestLength(double bestLength) {
		this.bestLength = bestLength;
	}
	

	
	public void solve(){
		Vector<Integer> tempRoute = new Vector<Integer>();
		Vector<Integer> currentRoute = new Vector<Integer>(this.getOptializedRoute());
		double currentDis = this.getBestLength();
		
		float am = 0.9999999f; // control the speed of temperature reducing
		float temperature = 45.0f; // initial temperature
		Random random = new Random(System.currentTimeMillis());
		
		while(temperature >= 1) { // the minimum temperature
			tempRoute = this.mutateRoute(currentRoute);
			double tempDis = this.calDistance(tempRoute);
			if(this.getBestLength() > tempDis) {
				this.setBestLength(tempDis);
				this.setOptializedRoute(tempRoute);
			}
			System.out.println(this.getBestLength());
			float r = random.nextFloat();
			if(currentDis > tempDis){
				currentDis = tempDis;
				currentRoute = new Vector<Integer>(tempRoute);
			} else if(Math.exp((currentDis - tempDis) / temperature) > r) {
				currentDis = tempDis;
				currentRoute = new Vector<Integer>(tempRoute);	
			}
			
			temperature *= am;
		}
		System.out.println("login hc16191 31288");
		System.out.println("name Chen Haoyan");
		System.out.println("algorithm SA");
		System.out.println("cost " + this.getBestLength());
		
		this.printOptimalRoutes(this.getOptializedRoute());
	}
	
	public static void main(String[] args) {
		long begin = System.currentTimeMillis();
		HillCliming_CVRP hill = new HillCliming_CVRP();
		hill.solve();
		System.out.println((System.currentTimeMillis() - begin) / 60000);
	}
	
}
 