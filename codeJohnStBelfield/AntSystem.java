
/*
 * AntSystem class: abstract class from which variants of Ant System can be extended.
 * Note that in our work, the variants differed only in a few places: 
 * -update method
 * -calculating initial pheromone
 * -ant selection of the next node when building a tour
 * 
 * Furthermore, by "differing," they still took inspiration from base ant system. So it made most
 * sense to make an abstract class.
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

abstract class AntSystem {

	// the following three variables are imported from TSP
	private int numNodes;
	private double[][] nodeCoords;
	private double[][] nodeDists;

	// the following variables are directly based on the input parameters
	private final static double HUUUUUUUUUUUUGE = 100000000;
	private double alpha;
	private double beta;
	private double evapFactor;
	private int numAnts;
	private Ant[] ants;

	// the following variables are indirectly based on input parameters
	private double nnLength; // length of nearest neighbor tour
	private double initPheromoneWeight; // the value of the initial pheromone weight
	private double[][] pheromoneWeights; // matrix of pheromone weights
	private double[][] heuristicInfo; // this matrix is essentially nodeDists, but the entries are inverses
	private double[][] nextNodeWeights; // element-wise product of powers of the above two matrices
	

	// Best so far variables; determined as the algorithm progresses
	private double bsfLength;
	private ArrayList<Integer> bsfTour;

	public AntSystem(TSP problem, int numAnts, double alpha, double beta, double evapFactor) {
		// import from TSP
		this.numNodes = problem.getNumNodes();
		this.nodeCoords = problem.getNodeCoords();
		this.nodeDists = problem.getNodeDists();
		
		// create Ants array
		this.numAnts = numAnts;
		Ant[] ants = new Ant[this.numAnts];
		for (int i = 0; i < this.numAnts; i++) {
			ants[i] = new Ant(this.numNodes);
		}
		this.ants = ants;

		// import from parameters
		this.alpha = alpha;
		this.beta = beta;
		this.evapFactor = evapFactor;
		this.bsfLength = this.getHuuuuuuuuuuuuge(); // yeeeeeeeeeeeeaaaaa

		this.nnLength = this.calcNearestNeighborTourLength();
		this.initPheromoneWeight = this.calcInitPheromoneWeight(this.numAnts, this.nnLength);
		this.pheromoneWeights = this.buildPheromoneWeightsMatrix(this.numNodes, this.initPheromoneWeight);
		this.heuristicInfo = this.buildHeuristicInfoMatrix(this.nodeDists, this.beta);
		this.nextNodeWeights = this.updateNextNodeWeightsMatrix(this.pheromoneWeights, this.heuristicInfo, this.alpha);

	}

	// yessir
	public static double getHuuuuuuuuuuuuge() {
		return HUUUUUUUUUUUUGE;
	}

	// calculate the nearest neighbor tour length
	public double calcNearestNeighborTourLength() {
	
		// create an ant. this ant goes on his own special tour, because he/she is special!
		Ant nnAnt = new Ant(this.numNodes, 0);
		int nextNode = chooseNearestNeighborNode(nnAnt); // specific next node method
		double distanceToNextNode = this.nodeDists[nnAnt.getCurrNode()][nextNode];
		// while the ants tour is not full
		while (nnAnt.addNodeToTour(nextNode, distanceToNextNode)) {
			nextNode = chooseNearestNeighborNode(nnAnt);
			distanceToNextNode = this.nodeDists[nnAnt.getCurrNode()][nextNode];
		}
	
		// note that the distance does not include dist(currNode, homeNode), so add that
		return nnAnt.getTourLength() + this.nodeDists[nnAnt.getHomeNode()][nnAnt.getHomeNode()];
	}

	// pick the nearest node for the ant, given its current node
	public int chooseNearestNeighborNode(Ant ant) {
		int currNode = ant.getCurrNode();
		ArrayList<Integer> unvisitedNodes = ant.getUnvisitedNodes();
	
		// extreme values that will be changed
		double distanceToNextNode = this.getHuuuuuuuuuuuuge();
		int nextNode = -1;
		
		for (int i = 0; i < unvisitedNodes.size(); i++) {
			int prospectiveNode = unvisitedNodes.get(i);
			// if prospectiveNode is closer thant he current "hiscore"
			if (this.nodeDists[currNode][prospectiveNode] < distanceToNextNode) {
				distanceToNextNode = nodeDists[currNode][prospectiveNode];
				nextNode = prospectiveNode;
			}
		}
	
		return nextNode;
	
	}

	// calculate intial pheromone levels. this is dependent on choice of variant, but it always
	// involves the nnLength and usually the numAnts as well
	public abstract double calcInitPheromoneWeight(int numAnts, double nnLength);

	
	//build the initial pheromoneWeightsMatrix using the initial pheromone weight
	// essentially just fill an entire array with the exact same value
	public double[][] buildPheromoneWeightsMatrix(int numNodes, double initPheromoneWeight) {
		double[][] pheromoneWeights = new double[numNodes][numNodes];
		for (int i = 0; i < numNodes; i++) {
			// start j at i to be twice as fast. speedkills 
			for (int j = i; j < numNodes; j++) {
				pheromoneWeights[i][j] = initPheromoneWeight;
				pheromoneWeights[j][i] = pheromoneWeights[i][j];
			}
		}
		return pheromoneWeights;
	}

	// basically just reciprocal of distances
	public double[][] buildHeuristicInfoMatrix(double[][] nodeDists, double beta) {
		int numNodes = nodeDists.length;
		double[][] heuristicInfo = new double[numNodes][numNodes];
		for (int i = 0; i < numNodes; i++) {
			for (int j = i; j < numNodes; j++) {
				heuristicInfo[i][j] = Math.pow(1 / nodeDists[i][j], beta);
				heuristicInfo[j][i] = heuristicInfo[i][j];
			}
		}
		return heuristicInfo;
	}

	// product of pheromone weights and heuristic info
	public double[][] updateNextNodeWeightsMatrix(double[][] pheromoneWeights, double[][] heuristicInfo, double alpha) {
		int numNodes = pheromoneWeights.length;
		double[][] nextNodeWeights = new double[numNodes][numNodes];

		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				nextNodeWeights[i][j] = Math.pow(pheromoneWeights[i][j], alpha) * heuristicInfo[i][j];
			}
		}

		return nextNodeWeights;
	}

	// iterate through all ants once: build tours, update pheromones, etc.
	public boolean singleIterationAllAnts() {
	
		// change to true if we find the BSF
		boolean newBSFWasFound = false;
	
		// update next node weights: note that in one iteration, all ants use the same weights, so this avoids
		// constant recalculation during an iteration
		this.nextNodeWeights = this.updateNextNodeWeightsMatrix(this.pheromoneWeights, this.heuristicInfo, this.alpha);
		// while tour construction incomplete, this while loop will continue
		while (this.addNextNodeForAllAnts()) {
		}
	
		// calculate final distances for the ants, since they don't include
		// dist(homeNode, currNode)
		for (int i = 0; i < this.numAnts; i++) {
			this.updateSingleAntTourLengths(this.ants[i]);
		}
	
		// check for BSF
		if (this.identifyInterimBSF()) {
			newBSFWasFound = true;
		}
	
		// update pheromones
		this.updatePheromone();
	
		// clear ants, ready for the next iteration
		// has to be below pheromone udpate because pheromone update needs ant
		// info still, specifically the 2d array of the path
		for (int i = 0; i < this.numAnts; i++) {
			this.ants[i].clearCurrTourHistory();
		}
	
		return newBSFWasFound;
	
	}

	// returns false if ants[0] has finished building a tour...thus all ants
	// have finished building tours
	public boolean addNextNodeForAllAnts() {
		// every ant except for ant[0]
		for (int i = 1; i < this.numAnts; i++) {
			this.addNextNodeForAnt(this.ants[i]);
		}
		
		// now do ant[0]
		return this.addNextNodeForAnt(this.ants[0]);
	}

	// returns false if the ant has finished building a tour
	// this needs to be abstract, since some variants (i.e. ACS) have ants that remove pheromone
	// as they build their tours
	public abstract boolean addNextNodeForAnt(Ant ant);

	// also abstract, since variants have different ways for ants to choose the next node
	public abstract int chooseNextNodeForAnt(Ant ant);

	// probabilistically chooses the next node; returns that node (not the
	// index!)
	// EAS: same method
	// ACS: extend this method by adding ability to directly exploit the best
	// neighbor
	public int chooseNextNodeForAntProbabilistically(Ant ant) {
		int currNode = ant.getCurrNode();
		ArrayList<Integer> unvisitedNodes = ant.getUnvisitedNodes();
		int numUnvisitedNodes = unvisitedNodes.size();

		// the first entry in "weights" is 0
		// this facilitates the use of the array as a pseudo-probability space..
		// i.e. 1 is scaled to the sum of weights
		double[] weights = new double[numUnvisitedNodes + 1];

		for (int i = 0; i < numUnvisitedNodes; i++) {

			// note that there's no need to divide by total weight...the
			// probability is exactly the same (b/c using a pseudo-probability
			// space)
			weights[i + 1] = this.nextNodeWeights[currNode][unvisitedNodes.get(i)] + weights[i];
		}

		double sumOfWeights = weights[numUnvisitedNodes];

		// returns a double in the range: (0, sumOfWeights) which is exactly
		// what we want
		double randWeight = Math.random() * sumOfWeights;

		// binarySearch returns i such that: weights[i] <= randWeight <
		// weights[i + 1]
		// thus, for our program: our chosen node has index of i in
		// unvisitedNodes
		int nextNodeIndex = BinarySearch.binarySearchForIndex(weights, randWeight);
		return unvisitedNodes.get(nextNodeIndex);

	}

	// retrieve the best tour found (in a given iteration)
	public boolean identifyInterimBSF() {
	
		double interimBsfLength = this.getHuuuuuuuuuuuuge(); // I love using this getter, for obvious reasons
		ArrayList<Integer> interimBsfTour = new ArrayList<Integer>();
	
		for (int i = 0; i < this.numAnts; i++) {
			if (interimBsfLength > this.ants[i].getTourLength()) {
				interimBsfLength = this.ants[i].getTourLength();
				interimBsfTour = this.ants[i].getVisitedNodes();
			}
		}
	
		// hiscore!!!!!!!!!
		if (this.bsfLength > interimBsfLength) {
			this.bsfLength = interimBsfLength;
			this.bsfTour = (ArrayList<Integer>) interimBsfTour.clone();
			return true;
		}
	
		// :(
		return false;
	}

	// because the tour lengths don't include the dist between last city and the
	// first
	public void updateSingleAntTourLengths(Ant ant) {
		double tourLength = ant.getTourLength();
		tourLength += nodeDists[ant.getCurrNode()][ant.getHomeNode()];
		ant.setTourLength(tourLength);
	}

	// template: given a tour (i.e. bsf tour, an ant's tour), calculates the amount of pheromone
	// to deposit. 
	public void depositPheromoneSingleTour(double tourLength, ArrayList<Integer> tour, double factor) {

		double deposit = factor / tourLength;
		for (int i = 0; i < tour.size(); i++) {
			int node1 = tour.get(i);
			int node2 = tour.get((i + 1) % tour.size());
			this.pheromoneWeights[node1][node2] += deposit;
		}
	}

	// heavily dependent on choice of variant
	public abstract void updatePheromone();

	// update a single weight in the matrix
	public void updateSinglePheromoneWeight(int node1, int node2, double newWeight) {
		this.pheromoneWeights[node1][node2] = newWeight;
	}

	// Getters and Setters
	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public int getNumAnts() {
		return numAnts;
	}

	public void setNumAnts(int numAnts) {
		this.numAnts = numAnts;
	}

	public Ant[] getAnts() {
		return ants;
	}

	public void setAnts(Ant[] ants) {
		this.ants = ants;
	}

	public double[][] getPheromoneWeights() {
		return pheromoneWeights;
	}

	public void setPheromoneWeights(double[][] pheromoneWeights) {
		this.pheromoneWeights = pheromoneWeights;
	}

	public double getBsfLength() {
		return bsfLength;
	}

	public void setBsfLength(double bsfLength) {
		this.bsfLength = bsfLength;
	}

	public ArrayList<Integer> getBsfTour() {
		return bsfTour;
	}

	public void setBsfTour(ArrayList<Integer> bsfTour) {
		this.bsfTour = bsfTour;
	}

	public double getEvapFactor() {
		return evapFactor;
	}

	public void setEvapFactor(double evapFactor) {
		this.evapFactor = evapFactor;
	}

	public double[][] getNodeDists() {
		return nodeDists;
	}

	public void setNodeDists(double[][] nodeDists) {
		this.nodeDists = nodeDists;
	}

	public double getNnLength() {
		return nnLength;
	}

	public void setNnLength(double nnLength) {
		this.nnLength = nnLength;
	}

	public double getInitPheromoneWeight() {
		return initPheromoneWeight;
	}

	public void setInitPheromoneWeight(double initPheromoneWeight) {
		this.initPheromoneWeight = initPheromoneWeight;
	}

	public double[][] getNextNodeWeights() {
		return nextNodeWeights;
	}

	public void setNextNodeWeights(double[][] nextNodeWeights) {
		this.nextNodeWeights = nextNodeWeights;
	}

}
