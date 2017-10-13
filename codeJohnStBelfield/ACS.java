/*
 * Ant Colony System variant
 * 
 * 
 * updatepheromone requirements:
 * -pheromone only added to bsf
 * -pheromone evaporated from the bsf
 * -pheromone removed as ants build tours (which means a diff addNextNode method)
 * 
 * ants choose nodes in two ways: probabilistically, or just the best one based on weighted pheromones
 * ants choose between the above two method probabilistically, as well
 * 
 */


import java.util.ArrayList;

// Ant Colony System

public class ACS extends AntSystem {

	private double evapBalanceConstant; // generally equal to pheromone init value
	private double wearFactor; // how much pheromone is removed as ants choose nodes to build tours 
	// probability that ant chooses bestNextNode method to get next node
	private double bestNextNodeSelectionProb; 

	// default evapbalance constant
	public ACS(TSP problem, int numAnts, double alpha, double beta, double evapFactor,
			double wearFactor, double bestNextNodeSelectionProb){
		super(problem, numAnts, alpha, beta, evapFactor);

		this.evapBalanceConstant = super.getInitPheromoneWeight();
		this.wearFactor = wearFactor;
		this.bestNextNodeSelectionProb = bestNextNodeSelectionProb;
	}

	// input evapbalance constant
	public ACS(TSP problem, int numAnts, double alpha, double beta, double evapFactor,
			double wearFactor, double bestNextNodeSelectionProb, double evapBalanceConstant){
		this(problem, numAnts, alpha, beta, evapFactor, wearFactor, bestNextNodeSelectionProb);
		
		this.evapBalanceConstant = evapBalanceConstant;
		
	}
	
	@Override
	// Dorigo recommended value
	public double calcInitPheromoneWeight(int numAnts, double nnLength) {
		return 1 / (numAnts * nnLength);
	}

	// gotta evaporate those cheetohs
	// returns false when ant tour is full
	public boolean addNextNodeForAnt(Ant ant) {
		int currNode = ant.getCurrNode();
		int nextNode = this.chooseNextNodeForAnt(ant);
		double distanceToNextNode = super.getNodeDists()[currNode][nextNode];
		
		// evaporating pheromone based on wearFactor on the chosen node
		double newWeight = (1 - this.wearFactor) * super.getPheromoneWeights()[currNode][nextNode]
				+ this.wearFactor * this.evapBalanceConstant;
		super.updateSinglePheromoneWeight(currNode, nextNode, newWeight);
		
		return ant.addNodeToTour(nextNode, distanceToNextNode);
	}

	// deterministically pick the best next node
	public int chooseBestNextNodeForAnt(Ant ant) {
		int currNode = ant.getCurrNode();
		ArrayList<Integer> unvisitedNodes = ant.getUnvisitedNodes();
		
		// extremes...to be changed!
		double maxWeight = 0;
		int nextNodeIndex = -1;
		
		// find the maximal weight; return the node index associated with that weight
		for (int i = 0; i < unvisitedNodes.size(); i++) {
			double prospectiveNodeWeight = super.getNextNodeWeights()[currNode][unvisitedNodes.get(i)];
			if (maxWeight < prospectiveNodeWeight) {
				maxWeight = prospectiveNodeWeight;
				nextNodeIndex = i;
			}
		}
		
		return unvisitedNodes.get(nextNodeIndex);
	}

	// chooses a selection method based on a probability 
	public int chooseNextNodeForAnt(Ant ant) {
		// probabilistically
		if (Math.random() > this.bestNextNodeSelectionProb) {
			return super.chooseNextNodeForAntProbabilistically(ant);
		}
		// node with highest weighted pheromone
		return this.chooseBestNextNodeForAnt(ant);
	}

	// update pheromone, but only dependent on bsf
	public void updatePheromone() {
		double[][] pheromoneWeights = super.getPheromoneWeights();
		pheromoneWeights = this.updatePheromoneBSF(super.getBsfTour(), pheromoneWeights);
		super.setPheromoneWeights(pheromoneWeights);
	
	}

	// update pheromone (through addition and evaporation) only on the bsfTour
	public double[][] updatePheromoneBSF(ArrayList<Integer> bsfTour, double[][] pheromoneWeights) {
		// amount to deposit
		double deposit = super.getEvapFactor() / super.getBsfLength();
		
		// for each arc on the bsf tour
		for (int i = 0; i < bsfTour.size(); i++) {
			int node1 = bsfTour.get(i);
			int node2 = bsfTour.get((i+1) % bsfTour.size());
			
			pheromoneWeights[node1][node2] = 
					(1 - super.getEvapFactor()) * pheromoneWeights[node1][node2] + deposit;
		}
		
		return pheromoneWeights;
	}
}
