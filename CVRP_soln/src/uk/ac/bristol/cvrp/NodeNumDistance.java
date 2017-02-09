package uk.ac.bristol.cvrp;

/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class NodeNumDistance implements Comparable<NodeNumDistance> {
	private Integer nodeNum;
	private Double distance;
	
	public Integer getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(Integer nodeNum) {
		this.nodeNum = nodeNum;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	@Override
	public int compareTo(NodeNumDistance o) {
		return this.getDistance().compareTo(o.getDistance());
	}
	
}
 