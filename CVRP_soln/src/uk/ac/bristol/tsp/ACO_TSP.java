package uk.ac.bristol.tsp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ACO_TSP {
	private Ant_TSP[] ants; // 蚂蚁
	private int antNum; // 蚂蚁数量
	private int cityNum; // 城市数量
	private int MAX_GEN; // 运行代数
	private float[][] pheromone; // 信息素矩阵
	private int[][] distance; // 距离矩阵
	private int bestLength; // 最佳长度
	private int[] bestTour; // 最佳路径

	private float alpha;
	private float beta;
	private float rho;

	public ACO_TSP() {

	}

	public ACO_TSP(int antNum, int cityNum, int mAX_GEN, float alpha, float beta, float rho) {
		super();
		this.antNum = antNum;
		this.cityNum = cityNum;
		MAX_GEN = mAX_GEN;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.ants = new Ant_TSP[antNum];
	}

	public void init(String filename) throws Exception {
		int[] x;
		int[] y;

		String strbuff;
		@SuppressWarnings("resource")
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

		distance = new int[cityNum][cityNum];
		x = new int[cityNum];
		y = new int[cityNum];
		for (int i = 0; i < cityNum; i++) {
			strbuff = data.readLine();
			String[] strcols = strbuff.split(" ");
			x[i] = Integer.valueOf(strcols[1]);
			y[i] = Integer.valueOf(strcols[2]);
		}

		for (int i = 0; i < cityNum - 1; i++) {
			distance[i][i] = 0; // 对角线为0
			for (int j = i + 1; j < cityNum; j++) {
				double rij = Math.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j])) / 10.0);
				int tij = (int) Math.round(rij);
				if (tij < rij) {
					distance[i][j] = tij + 1;
					distance[j][i] = distance[i][j];
				} else {
					distance[i][j] = tij;
					distance[j][i] = distance[i][j];
				}
			}
		}
		distance[cityNum - 1][cityNum - 1] = 0;

		pheromone = new float[cityNum][cityNum];
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				pheromone[i][j] = 0.1f;
			}
		}

		bestLength = Integer.MAX_VALUE;
		bestTour = new int[cityNum + 1];

		for (int i = 0; i < antNum; i++) {
			ants[i] = new Ant_TSP(cityNum);
			ants[i].init(distance, alpha, beta);
		}
	}
	
	public void solve() {
		for(int g = 0; g < MAX_GEN; g++) {
			for(int i = 0; i < antNum; i++) {
				for(int j = 1; j < cityNum; j++) {
					ants[i].selectNextCity(pheromone);
				}
				
				ants[i].getTabu().add(ants[i].getFirstCity());
				if(ants[i].getTourLength() < bestLength) {
					bestLength = ants[i].getTourLength();
					for(int k = 0; k < cityNum + 1; k++) {
						bestTour[k] = ants[i].getTabu().get(k);
					}
				}
				
				for (int j = 0; j < cityNum; j++) {
					ants[i].getDelta()[ants[i].getTabu().get(j)][ants[i].getTabu().get(j+1)] = (float) (1./ants[i].getTourLength());
					ants[i].getDelta()[ants[i].getTabu().get(j+1)][ants[i].getTabu().get(j)] = (float) (1./ants[i].getTourLength());
				}
			}
			updatePheremone();
			
			for(int i = 0; i < antNum; i++) {
				ants[i].init(distance, alpha, beta);
			}
		}
		printOptimal();
	}
	private void updatePheremone() {
		// release pheromone
		for(int i = 0; i < cityNum; i++) {
			for(int j = 0; j < cityNum; j++) {
				pheromone[i][j] = pheromone[i][j] * (1 - rho);
			}
		}
		
		// update pheromone
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				for (int k = 0; k < antNum; k++) {
					pheromone[i][j] += ants[k].getDelta()[i][j];
				}
			}
		}
	}
	
	private void printOptimal() {
		System.out.println("The optimal length is: " + bestLength);
		System.out.println("The optimal route is: ");
		for(int i = 0; i < cityNum + 1; i++) {
			System.out.println(bestTour[i]);
		}
	}

	public Ant_TSP[] getAnts() {
		return ants;
	}

	public void setAnts(Ant_TSP[] ants) {
		this.ants = ants;
	}

	public int getAntNum() {
		return antNum;
	}

	public void setAntNum(int antNum) {
		this.antNum = antNum;
	}

	public int getCityNum() {
		return cityNum;
	}

	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	public int getMAX_GEN() {
		return MAX_GEN;
	}

	public void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}

	public float[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}

	public int[][] getDistance() {
		return distance;
	}

	public void setDistance(int[][] distance) {
		this.distance = distance;
	}

	public int getBestLength() {
		return bestLength;
	}

	public void setBestLength(int bestLength) {
		this.bestLength = bestLength;
	}

	public int[] getBestTour() {
		return bestTour;
	}

	public void setBestTour(int[] bestTour) {
		this.bestTour = bestTour;
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

	public float getRho() {
		return rho;
	}

	public void setRho(float rho) {
		this.rho = rho;
	}
	
	public static void main(String[] args) throws Exception {
		ACO_TSP aco = new ACO_TSP(100, 48, 100, 1.0f, 5.0f, 0.5f);
		aco.init("D://data.txt");
		aco.solve();
	}
	
}
