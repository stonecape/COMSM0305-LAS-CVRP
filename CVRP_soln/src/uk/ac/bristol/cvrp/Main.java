package uk.ac.bristol.cvrp;

import java.util.Random;

/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class Main {

	public static void main(String[] args) {
		
		//CVRPData data = new CVRPData();
		//CVRPData.readFile("D:\\data.txt");
		
		Hillclimber hill = new Hillclimber();
		
		double optimalDis = hill.calculateDis(hill.getOptimalQueue());

		//initialize current array which is equal to optimalQueue
		int current[] = new int[251];
		current = hill.getOptimalQueue();
		double currentDis = optimalDis;
		
		//array temp is a neighbor of current array
		int temp[] = new int[251];
		
		float a = 0.9999999f; // Annealling Magnitude
		float t = 30.0f; // Initial temperature
		float r = 0.0f; // Random rate, used in simulated annealing
		Random random = new Random(System.currentTimeMillis());

		while(t>1) {
			temp = hill.createTempQueue(current);	   
			double tempDis = hill.calculateDis(temp);
	
				// if distance of temp is shorter, change optimal queue to temp
				if(optimalDis > tempDis) {
					optimalDis = tempDis;
					hill.setOptimalQueue(temp);
				}	
			   
				//if distance of temp is shorter than current, change current to temp
				r = random.nextFloat();
				if(currentDis > tempDis){

					currentDis = tempDis;
					
					for(int i=0; i<251; i++){
						current[i] = temp[i];
					}
				}
				//if distance is longer than current, accept this solution base on a probability
				else if(currentDis < tempDis && Math.exp((currentDis - tempDis) / t) > r) {
					
					currentDis = tempDis;
					System.out.println(currentDis);

					for(int i=0; i<251; i++) {
						current[i] = temp[i];
					}	
				}
				
				t *= a;
	    }

		System.out.println(optimalDis + " is optimal distance");
		
		//show route
		hill.calTrunks(hill.getOptimalQueue());
		
		/*
		System.out.println("The whole route:");
		for(int k=0; k<hill.getoptimalQueue().length; k++) {
			System.out.println(hill.getoptimalQueue()[k]);
		}
		*/
		
	}
}
 