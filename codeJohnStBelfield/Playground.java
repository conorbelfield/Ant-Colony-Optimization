import javax.swing.plaf.synth.SynthSeparatorUI;

public class Playground {

	private static final String[] DEBUG_3000 = new String[] { "d2103", "u2152", "u2319", "pr2392" };
	private static final String[] DEBUG_4000 = new String[] { "pcb3038", "fl3795" };
	private static final String[] DEBUG_5000 = new String[] { "fnl4461" };
	private static final String[] DEBUG_6000 = new String[] { "rl5915", "rl5934" };

	private static final String[][] DEBUG = new String[][] { DEBUG_3000, DEBUG_4000, DEBUG_5000, DEBUG_6000 };

	private static String fileName = "problems/" + DEBUG[0][0] + ".tsp";
	// private static String fileName = "problems/att48.tsp";

	public static void main(String[] args) {

		TSP problem = new TSP(fileName);
		// int numAnts = 30;

		int numAntsEAS = 20;
		int numAntsACS = 10;

		int elitism = numAntsEAS;
		double[] alphas = new double[]{.75, 1.125};
		double beta = 3.0;
		double evapFact = 0.1;
		double wearFact = 0.1;
		double bestNodeSelectionProb = 0.9;
		double optimal = 10.0;
		double breakRatio = 1.01;
		
		
		//ACS sol = new ACS(problem, numAntsACS,  alpha,  beta, evapFact, wearFact, bestNodeSelectionProb);
		for (int k = 0; k < 2; k++) {
			double alpha = alphas[k];
			double[] iters = new double[31];
			EAS sol = new EAS(problem, numAntsEAS, alpha, beta, evapFact, elitism);
			for (int i = 0; i <= 750; i++) {

				sol.singleIterationAllAnts();
				if (i % 25 == 0) {
					iters[i/25] = sol.getBsfLength();
				}
				//statement to stop iterations if solution is within a certain ratio 
				//of the optimal as defined by the constants above
				if(optimal*breakRatio >= sol.getBsfLength()){
					break;
				}
			}
			System.out.println(alpha);
			System.out.println(sol.getBsfLength());
			for (int i = 0; i < 31; i++) {
				System.out.println(iters[i]);
			}
		}

	}

}
