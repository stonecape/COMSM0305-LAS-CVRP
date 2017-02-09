package uk.ac.bristol.cvrp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class Hillclimber {
	private int nodeNumber;
	private int[] optimalQueue; // This is the optimal queue

	public int[] getOptimalQueue() {
		return optimalQueue;
	}
	
	public void setOptimalQueue(int[] queue) {
		this.optimalQueue = queue;
	}
	
	//create optimalQueue which is a array from 1 to 250
	public Hillclimber() {
		
		nodeNumber = CVRPData.NUM_NODES;
		optimalQueue = new int[nodeNumber+1];
        
		for(int i=0; i<nodeNumber; i++) {
			optimalQueue[i] = i+1;
		}	
		optimalQueue[250] = 1;
        
	}
	
	//create a neighbor of an input array
	public int[] createTempQueue(int[] array) {
		/**Vector<Integer> route = new Vector<>();
		for(int i : array) {
			route.add(i);
		}
		Vector<Integer> result = new Vector<Integer>(route);
		Random random = new Random();
		int r1 = 0, r2 = 0;
        while(r1 == r2) {
        	// randomly choose number [1, 249]
        	r1 = random.nextInt(nodeNumber - 1) + 1;
        	r2 = random.nextInt(nodeNumber - 1) + 1;  
        }
        int temp = result.get(r1);
        result.set(r1, result.get(r2));
        result.set(r2, temp);
        
        int[] re = new int[result.size()];
        for(int i = 0; i < result.size(); i++) {
        	re[i] = result.get(i);
        }
		return re;
       /* 
		}	**/
	
		//create two unequal randoms
		int tempQueue[] = new int[nodeNumber+1];
		for(int i=0; i<array.length; i++) {
			int j = array[i];
			tempQueue[i] = j;
		}
		Random random = new Random();
        int r1 = random.nextInt(249) + 1;
        int r2 = random.nextInt(249) + 1;  
        
	    while(r1==r2){
	    	r2=random.nextInt(249) + 1;
	    }
	    
		// Exchange the position of two nodes 
	    int temp = array[r1];
        int temp2 = array[r2];
        tempQueue[r1] = temp2;
        tempQueue[r2] = temp;
     
        return tempQueue;
	}

	// Calculate distance of an input array
	/**
	 * @param array
	 * @return
	 */
	public double calculateDis(int array[]){
		/*Vector<Integer> route = new Vector<>();
		for(int i : array) {
			route.add(i);
		}
		double len = 0.0d;
		Vector<Integer> tour = null;
		List<Route> splitedRoutes = splitRoute(route);
		for(Route r : splitedRoutes) {
			tour = r.getTour();
			for(int i = 0; i < tour.size() - 1; i++) {
				len += CVRPData.getDistance(tour.get(i), tour.get(i + 1));
			}
		}
		
		return len;*/
		double newDistance = 0;
		double distance = 0;
		int capacity = 500;
		
		//calculate distance of the entire array
		for(int i=0; i < array.length-1; i++)
		{
			distance = distance + CVRPData.getDistance(array[i], array[i+1]);
		}
		//calculate the distance of : empty car return to plot & new car move to next place
		for(int i=0; i<249; i++) {
			int demand = CVRPData.getDemand(array[i+1]);

			if(capacity-demand<0) {
				newDistance = newDistance + CVRPData.getDistance(array[i], array[0]);
				newDistance = newDistance + CVRPData.getDistance(array[i+1], array[0]);
				newDistance = newDistance - CVRPData.getDistance(array[i], array[i+1]);
				capacity = 500 - demand;
			}else {
				capacity = capacity - demand;
			}
		}
		
		return (distance+newDistance);
	}
	private List<Route> splitRoute(Vector<Integer> route) {
		List<Route> result = new ArrayList<Route>();
		Route r = new Route();
		r.getTour().add(1);
		int nextWeight = 0;
		
		for(int i = 0; i < route.size() - 1; i++) {
			int demand = CVRPData.getDemand(route.get(i + 1));
			nextWeight += demand;
			if(nextWeight > CVRPData.VEHICLE_CAPACITY) {
				r.getTour().add(1);
				result.add(r);
				
				r = new Route();
				r.getTour().add(1);
				r.getTour().add(route.get(i + 1));
				nextWeight = CVRPData.getDemand(route.get(i + 1));
			} else {
				r.getTour().add(route.get(i + 1));
			}
		}
		result.add(r);
		
		return result;
	}
	//calculate trunks and print route 
	public void calTrunks(int[] array) {
		/*Vector<Integer> route = new Vector<Integer>();
		for(int i : array) {
			route.add(i);
		}
		List<Route> bestTourList = splitRoute(route);
		for(Route r : bestTourList) {
			StringBuffer sbf = new StringBuffer();
			for(int i : r.getTour()) {
				sbf.append(i + "->");
			}
			System.out.println(sbf.substring(0, sbf.lastIndexOf("->")));
		}*/
		int capacity = 500;
		int trunk = 1;
		int demand = 0; // demand of every node
		int changeCar[] = new int[28]; // store those nodes which needs to change a new car
		
		for(int i=0; i<249; i++) {
			
			demand = CVRPData.getDemand(array[i+1]);

			if(capacity-demand < 0) {
				changeCar[trunk-1] = array[i];
				//System.out.println("changeCar when node ="+changeCar[trunk-1]);
				trunk++;
				capacity = 500 - demand;
				
			}else{
				capacity = capacity - demand;
			}	
		}
		
		System.out.println(trunk + " trunks needed");
		
		//print route of this input array
		int tag = 0;
		
		for(int i=0; i<trunk; i++) {
			int changeNode = changeCar[i];
			System.out.printf("1->");
	
			//every time begin with a node which is the next node of a car change node
			for(int j=tag+1; j <array.length-1; j++) {
				//if current node is not a node needs a new car,print it
				if(array[j] != changeNode ) {
					
					if(j!=249) {
						System.out.printf(array[j]+"->");
					}
					else {
						System.out.println(array[j]+"->1");
					}
				}
				//if current node needs a new car,set tag to the index of this node
				else {
	    			System.out.println(array[j]+"->1");
	    			tag = j;
	    			break;
				}
			}
		}
	}
}
 