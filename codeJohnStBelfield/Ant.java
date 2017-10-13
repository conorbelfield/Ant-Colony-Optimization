/*
 * Ant class. 
 * 
 * Important variables are basically a ton of arraylists, since it's easy to add/remove stuff, 
 * easy containment checks. Plus a nice cloning method.
 * 
 */

import java.util.ArrayList;

public class Ant {

	// number of nodes in the problem
	private int numNodes;
	public ArrayList<Integer> allNodes; // ArrayList of all nodes: sequence from 1 to numNodes

	private double tourLength; 	// length of the tour
	private int homeNode; // first node in the tour
	private int currNode; // curr node in the tour
	private ArrayList<Integer> visitedNodes; // ArrayList of visited nodes, in
												// order
	private ArrayList<Integer> unvisitedNodes; // ArrayList of unvisited nodes

	
	//initialize with random homeNode
	public Ant(int numNodes) {
		this.numNodes = numNodes;
		this.allNodes = this.generateSequentialArrayList(0, this.numNodes);
		this.visitedNodes = new ArrayList<Integer>(this.numNodes);
		this.clearCurrTourHistory();

	}

	//initialize with specific homeNode
	public Ant(int numNodes, int homeNode) {
		this(numNodes);
		this.visitedNodes.clear();
		this.unvisitedNodes = (ArrayList<Integer>) this.allNodes.clone();
		this.addNodeToTour(homeNode, 0);
		this.homeNode = homeNode;
	}

	// constructs ArrayList whose values are sequence from start to stop,
	// excluding stop
	public ArrayList<Integer> generateSequentialArrayList(int start, int stop) {
		ArrayList<Integer> sequence = new ArrayList<Integer>(stop - start);
		for (int i = start; i < stop; i++) {
			sequence.add(i);
		}
		return sequence;
	}

	// returns false once the path is full
	public boolean addNodeToTour(int addedNode, double distanceToAddedNode) {

		this.currNode = addedNode; // set current node to added node
		this.visitedNodes.add(addedNode); // add node to the path
		// remove the node from unvisited nodes
		this.unvisitedNodes.remove(unvisitedNodes.indexOf(addedNode));
		this.tourLength += distanceToAddedNode; // add dist to the tour length
		if (this.unvisitedNodes.isEmpty()) {
			return false;
		}
		return true;
	}

	// returns the homeNode
	public int setHomeNode(int homeNode) {
		this.addNodeToTour(homeNode, 0); // add homenode to the path
		this.tourLength = 0; // set pathLength to 0
		this.homeNode = homeNode;
		return homeNode;
	}

	public int getHomeNode() {
		return this.homeNode;
	}

	public int setHomeNodeRand() {
		// note that Math.random() never actually returns value of 1.00
		// ie max returned value is infinitesimally smaller than that
		int homeNode = (int) Math.floor(Math.random() * this.numNodes);
		this.setHomeNode(homeNode);
		return homeNode;
	}

	// empties the current tour; resets the nodes to visit to all nodes
	public void clearCurrTourHistory() {
		this.visitedNodes.clear();
		this.unvisitedNodes = (ArrayList<Integer>) this.allNodes.clone();
		this.setHomeNodeRand();
	}

	public String toString() {
		return "Ant(numNodes=" + this.numNodes + ", currNode=" + this.currNode + ", pathLength=" + this.tourLength
				+ ", sizeCurrPath=" + this.visitedNodes.size() + ", sizeNodesToVisit=" + this.unvisitedNodes.size()
				+ ")";
	}

	// Getters & Setters
	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public ArrayList<Integer> getAllNodes() {
		return allNodes;
	}

	public void setAllNodes(ArrayList<Integer> allNodes) {
		this.allNodes = allNodes;
	}

	public double getTourLength() {
		return tourLength;
	}

	public void setTourLength(double tourLength) {
		this.tourLength = tourLength;
	}

	public int getCurrNode() {
		return currNode;
	}

	public void setCurrNode(int currNode) {
		this.currNode = currNode;
	}

	public ArrayList<Integer> getVisitedNodes() {
		return visitedNodes;
	}

	public void setVisitedNodes(ArrayList<Integer> visitedNodes) {
		this.visitedNodes = visitedNodes;
	}

	public ArrayList<Integer> getUnvisitedNodes() {
		return unvisitedNodes;
	}

	public void setUnvisitedNodes(ArrayList<Integer> unvisitedNodes) {
		this.unvisitedNodes = unvisitedNodes;
	}

}
