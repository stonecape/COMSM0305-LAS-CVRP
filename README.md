# CVRP_COMSM0305
##Introduction
It is an important logistics problem to find an efficient vehicle route. Ant Colony Optimizations (ACO) are new optimization methods proposed by Marco Dorigo in 1996[2]. ACO simulates the behavior of ant colonies when they forage for food and find the most efficient routes from their nests to food sources[1].

Since the vehicle routing problem is considered as NP-hard. For this kind of problems, using heuristic methods is regarded as a reasonable way in seeking solutions[1]. Marco compares the ACO with other heuristic approaches, such as Hill Climbing, Simulated Annealing (SA) and etc., and the experiment result shows that the performance of ACO was always very good[2].

Although many improvement strategies, like candidate list, multiple ant colonies and route mutation, are applied to ACO in order to enhance the performance of the algorithm, SA shows a better result (cost) than ACO in FruityBun Challenge. In this poster, both of them will be introduced to solve Capacitated Vehicle Routing Problem (CVRP).

##the code structure
###Ant Colony Optimizations (ACO)
run CVRP_COMSM0305/CVRP_soln/src/uk/ac/bristol/cvrp/ACO_CVRP.java

###Simulated Annealing (SA)
run CVRP_COMSM0305/CVRP_soln/src/uk/ac/bristol/cvrp/Main.java

tips:
Before runing main functions, please place the data file **fruitybun250.vrp** under the right path set in *CVRP_COMSM0305/CVRP_soln/src/uk/ac/bristol/cvrp/CVRPData.java

##References
[1] John E Bell and Patrick R McMullen. Ant colony optimization techniques for the vehicle routing problem. Advanced Engineering Informatics, 18(1):41–48, 2004.

[2] Marco Dorigo, Vittorio Maniezzo, and Alberto Colorni. Ant system: optimization by a colony of cooperating agents. IEEE Transactions on Systems, Man, and Cybernetics, Part B (Cybernetics), 26(1):29–41, 1996.

[3] Bin Yu, Zhong-Zhen Yang, and Baozhen Yao. An improved ant colony optimization for vehicle routing problem. European journal of operational research, 196(1):171–176, 2009.
