/*
 * Class to read in the TSP problem.
 * It is assumed that the TSP problem is symmetric, and 2D-Euclidean.
 * 
 * Important instance variables include: 
 * -the number of nodes (int numNodes)
 * -the distance between nodes (a double[][] nodeDists).
 * 
 * Important methods include:
 * auxiliary method calcNodeDistances, which basically is the distance function that every highschooler knows,
 * and then is applied to every pair of nodes.
 * 
 * Admittedly, the method readFile is pretty long, but it happens.
 * The star method of this class is the nodeDistances, which is pretty much the only product that
 * the algorithm will use.
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TSP {

	private String fileName; // if you want to print nice things out
	private String fileDescription; // unnecessary but cool to have I guess
	private String edgeWeightType; // unnecessary as well
	private int dimension; // not super necessary, since we're only working with 2D

	private int numNodes; // the number of nodes
	private double[][] nodeCoords; // coordinates of the nodes
	private double[][] nodeDists; //distances between pairs of nodes, indicated by index

	public TSP(String fileName) {
		if (this.readFile(fileName)) { // if the reading of the file is successful
			this.nodeDists = this.calcNodeDistances(); // calculate node distances
			return;
		}
		System.out.println("Error: could not read file");
	}

	public boolean readFile(String fileName) {
		try {
			Scanner fileScan = new Scanner(new File(fileName));

			if (!fileScan.hasNext()) {
				System.out.println("Error: file is empty.");
				fileScan.close();
				return false;
			}

			while (fileScan.hasNextLine()) {
				String nextLine = fileScan.nextLine();

				while (!nextLine.startsWith("NODE_COORD")) {

					if (nextLine.startsWith("NAME")) {
						this.fileName = nextLine.split("\\s+:\\s+")[1];
					}

					else if (nextLine.startsWith("COMMENT")) {
						this.fileDescription = nextLine.split("\\s+:\\s+")[1];
					}

					else if (nextLine.startsWith("DIMENSION")) {
						this.numNodes = Integer.parseInt(nextLine.split("\\s+:\\s+")[1]);
					}

					else if (nextLine.startsWith("EDGE_WEIGHT_TYPE")) {
						this.edgeWeightType = nextLine.split("\\s+:\\s+")[1];
						// stack overflow
						// this.dimension =
						// Integer.parseInt(this.edgeWeightType.replaceAll("[\\D]",
						// ""));
						
						// ended up just fixing this because some files have weird names for the edgeWeightTYpe
						this.dimension = 2; 
						
					}

					nextLine = fileScan.nextLine();

				}

				nextLine = fileScan.nextLine();

				// importing node coordinates
				// for a node i, nodeCoords[i][0] contains the x-coord, and
				// nodeCoords[i][1] contains the y-coord
				double[][] nodeCoords = new double[this.numNodes][this.dimension];
				for (int i = 0; i < this.numNodes; i++) {
					String[] node = nextLine.split("\\s+");
					for (int j = 0; j < this.dimension; j++) {
						nodeCoords[i][j] = Double.parseDouble(node[j + 1]);
					}
					nextLine = fileScan.nextLine();
				}
				this.nodeCoords = nodeCoords;

			}

			fileScan.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: file not found.");
			return false;
		}

		// successfully imported the problem
		return true;

	}

	// for each pair of cities, calc distance and put it in an array
	// such that the distance between city i and j is at array[i][j]
	public double[][] calcNodeDistances() {
		double[][] nodeDists = new double[numNodes][numNodes];
		for (int i = 0; i < numNodes; i++) {
			for (int j = i; j < numNodes; j++) {
				double deltaX = this.nodeCoords[i][0] - this.nodeCoords[j][0];
				double deltaY = this.nodeCoords[i][1] - this.nodeCoords[j][1];
				nodeDists[i][j] = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
				nodeDists[j][i] = nodeDists[i][j];
			}
		}
		return nodeDists;
	}

	public String toString() {
		return this.fileName + ": " + this.numNodes + " nodes";
	}

	// Getters and Setters
	public String getFileName() {
		return fileName;
	}

	public double[][] getNodeDists() {
		return nodeDists;
	}

	public void setNodeDists(double[][] nodeDists) {
		this.nodeDists = nodeDists;
	}

	public void setNodeCoords(double[][] nodeCoords) {
		this.nodeCoords = nodeCoords;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public void setNumNodes(int numNodes) {
		this.numNodes = numNodes;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public double[][] getNodeCoords() {
		return nodeCoords;
	}

}
