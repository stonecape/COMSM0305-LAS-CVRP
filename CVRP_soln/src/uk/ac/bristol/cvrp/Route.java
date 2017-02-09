package uk.ac.bristol.cvrp;

import java.util.Vector;

/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class Route {
	private Vector<Integer> tour = new Vector<Integer>();

	public Route() {
		
	}

	public Route(Vector<Integer> tour) {
		this.tour.addAll(tour);
	}

	public Vector<Integer> getTour() {
		return tour;
	}

	public void setTour(Vector<Integer> tour) {
		this.tour = tour;
	}

}
