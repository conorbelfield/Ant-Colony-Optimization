/*
 * Elitist Ant System
 * 
 * Update pheromone requirements:
 * -all pheromones evaporate a lil bit
 * -pheromone added to the bsf tour
 * -pheromone added to all tours taken by ants
 */

import java.util.Random;
import java.util.ArrayList;

public class EAS extends AntSystem {

	private double elitism;

	public EAS(TSP problem, int numAnts, double alpha, double beta, double evapFactor, double elitism) {
		super(problem, numAnts, alpha, beta, evapFactor);

		this.elitism = elitism;

	}

	
	@Override
	// values recommended by Darth Dorigo
	public double calcInitPheromoneWeight(int numAnts, double nnLength) {
		return (super.getNumNodes() + numAnts) / (super.getEvapFactor() * nnLength);
	}

	// self explanatory
	public boolean addNextNodeForAnt(Ant ant) {
		int nextNode = this.chooseNextNodeForAnt(ant); //choose the next node
		double distanceToNextNode = super.getNodeDists()[ant.getCurrNode()][nextNode]; //find the distance
		return ant.addNodeToTour(nextNode, distanceToNextNode); //add the node
	}

	// lets keep it basic
	public int chooseNextNodeForAnt(Ant ant) {
		return super.chooseNextNodeForAntProbabilistically(ant);
	}

	// update pheromone weights
	public void updatePheromone() {
		this.evapPheromone();
		this.depositPheromoneAllAnts();
		this.depositPheromoneBSF();
	}

	// evaporate pheromone on the entire pheromone matrix
	public void evapPheromone() {
		double[][] pheromoneWeights = super.getPheromoneWeights();
		for (int i = 0; i < super.getNumNodes(); i++) {
			for (int j = 0; j < super.getNumNodes(); j++) {
				pheromoneWeights[i][j] *= (1 - super.getEvapFactor());
			}
		}
		super.setPheromoneWeights(pheromoneWeights);
	}

	// deposit pheromone on each ants tour
	public void depositPheromoneAllAnts() {
		for (int i = 0; i < super.getNumAnts(); i++) {
			this.depositPheromoneSingleAnt(super.getAnts()[i]);
		}
	
	}

	// deposit pheromone on a single ant's tour
	public void depositPheromoneSingleAnt(Ant ant) {
		super.depositPheromoneSingleTour(ant.getTourLength(), ant.getVisitedNodes(), 1.0);
	}

	// deposit pheromone on the bsf tour
	public void depositPheromoneBSF() {
		super.depositPheromoneSingleTour(super.getBsfLength(), super.getBsfTour(), this.elitism);
	}

}
