package uk.ac.bristol.cvrp;

import java.util.Vector;

/** 
* @author  Frank Chen
* capechy@hotmail.com
 * @param <T>
*/
public class Result{
	private int status;
	private String message;
	private Vector<Integer> gloTabu;
	private Vector<Integer> gloAllowedNodes;
	private Route route;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
	}
	public Vector<Integer> getGloTabu() {
		return gloTabu;
	}
	public void setGloTabu(Vector<Integer> gloTabu) {
		this.gloTabu = gloTabu;
	}
	public Vector<Integer> getGloAllowedNodes() {
		return gloAllowedNodes;
	}
	public void setGloAllowedNodes(Vector<Integer> gloAllowedNodes) {
		this.gloAllowedNodes = gloAllowedNodes;
	}
}
 