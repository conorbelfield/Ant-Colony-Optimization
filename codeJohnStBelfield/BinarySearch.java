/*
 * it's lit
 */


public class BinarySearch {

	public static int binarySearchForIndex(double[] weights, double randWeight) {

		int low = 0;
		int high = weights.length - 1;
		int middle = (low + high) / 2; // integer division rounds and preserves

		while (low <= high) {

			middle = (low + high) / 2;

			// find i such that: weights[i] <= randWeight < weights[i + 1]
			if (weights[middle] <= randWeight && randWeight < weights[middle + 1]) {
				// System.out.println(middle);
				return middle;
			}

			// randWeight is less than weights[middle]
			else if (randWeight < weights[middle]) {
				high = middle - 1;
			}

			// randWeight comes after
			else {
				low = middle + 1;
			}

		}
		// should never get here, because this means:
		return -1;

	}

}
