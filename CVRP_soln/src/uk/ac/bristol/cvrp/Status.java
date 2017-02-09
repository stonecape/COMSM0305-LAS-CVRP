package uk.ac.bristol.cvrp; 
/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class Status {
	public static final Integer NOMAL = 0;
	public static final Integer FINISHED = 1;
	public static final Integer OVERWEIGHT = 2;
	
	public static final String FINISHED_MSG = "all ants have traveled all customers";
	public static final String NOMAL_MSG = "do next";
	public static final String OVERWEIGHT_MSG = "it's overweight";
}
 