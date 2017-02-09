package uk.ac.bristol.tsp;

import java.util.Random;
import java.util.Vector;

public class Ant_TSP {
	// cities are not allowed to visit
	private Vector<Integer> tabu;
	private Vector<Integer> allowedCities;
	private float[][] delta;
	private int[][] distance;

	private float alpha;
	private float beta;

	private int tourLength;
	private int cityNum;

	private int firstCity;
	private int currentCity;

	public Ant_TSP() {
		this.cityNum = 30;
		this.tourLength = 0;
	}

	public Ant_TSP(int cityNum) {
		this.cityNum = cityNum;
		this.tourLength = 0;
	}

	/**
	 * 设t=0，初始化bestLength为一个非常大的数（正无穷），bestTour为空。初始化所有的蚂蚁的Delt矩阵所有元素初始化为0，
	 * Tabu表清空，Allowed表中加入所有的城市节点。随机选择它们的起始位置（也可以人工指定）。在Tabu中加入起始节点，
	 * Allowed中去掉该起始节点。
	 * 
	 * @param distance
	 *            matrix
	 * @param a
	 *            alpha
	 * @param b
	 *            beta
	 */
	public void init(int[][] distance, float a, float b) {
		this.alpha = a;
		this.beta = b;
		this.distance = distance;

		allowedCities = new Vector<Integer>();
		tabu = new Vector<Integer>();
		delta = new float[cityNum][cityNum];

		for (int i = 0; i < cityNum; i++) {
			allowedCities.add(i);
			for (int j = 0; j < cityNum; j++) {
				delta[i][j] = 0.0f;
			}
		}

		Random random = new Random(System.currentTimeMillis());
		// this.firstCity = random.nextInt(cityNum);
		this.firstCity = 0;
		for (Integer i : allowedCities) {
			if (firstCity == i) {
				allowedCities.remove(i);
				break;
			}
		}

		tabu.add(firstCity);
		this.currentCity = firstCity;

	}

	/**
	 * 
	 * @param pheromone
	 *            信息素矩阵 公式(4)
	 */
	public void selectNextCity(float[][] pheromone) {
		float[] p = new float[cityNum];
		float sum = 0.0f;

		// calculate the denominator
		for (Integer i : allowedCities) {
			sum += Math.pow(pheromone[currentCity][i], alpha) * Math.pow(1.0 / distance[currentCity][i], beta);
		}

		for (int i = 0; i < cityNum; i++) {
			boolean flag = false;
			for (Integer j : allowedCities) {
				if (i == j) {
					p[i] = (float) (Math.pow(pheromone[currentCity][i], alpha)
							* Math.pow(1.0 / distance[currentCity][i], beta)) / sum;
					flag = true;
					break;
				}
				
				if(flag == false) {
					p[i] = 0.0f;
				}
			}
			
		}
		
		Random random = new Random(System.currentTimeMillis());
		float sleectP = random.nextFloat();
		int selectCity = 0;
		float sum1 = 0.0f;
		for (int i = 0; i < cityNum; i++) {
			sum1 += p[i];
			if (sum1 >= sleectP) {
				selectCity = i;
				break;
			}
		}
		
		for(Integer i : allowedCities) {
			if(i == selectCity) {
				allowedCities.remove(i);
				break;
			}
		}
		
		tabu.add(selectCity);
		this.currentCity = selectCity;

	}
	
	private int calTourLength() {
		int len = 0;
		for(int i = 0; i < cityNum; i++) {
			len += distance[this.tabu.get(i)][this.tabu.get(i + 1)];
		}
		return len;
	}

	public Vector<Integer> getTabu() {
		return tabu;
	}

	public void setTabu(Vector<Integer> tabu) {
		this.tabu = tabu;
	}

	public Vector<Integer> getAllowedCities() {
		return allowedCities;
	}

	public void setAllowedCities(Vector<Integer> allowedCities) {
		this.allowedCities = allowedCities;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public int[][] getDistance() {
		return distance;
	}

	public void setDistance(int[][] distance) {
		this.distance = distance;
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

	public int getTourLength() {
		tourLength = calTourLength();
		return tourLength;
	}

	public void setTourLength(int tourLength) {
		this.tourLength = tourLength;
	}

	public int getCityNum() {
		return cityNum;
	}

	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	public int getFirstCity() {
		return firstCity;
	}

	public void setFirstCity(int firstCity) {
		this.firstCity = firstCity;
	}

	public int getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(int currentCity) {
		this.currentCity = currentCity;
	}

}
