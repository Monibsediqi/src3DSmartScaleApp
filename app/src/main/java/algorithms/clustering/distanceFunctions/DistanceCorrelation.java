package algorithms.clustering.distanceFunctions;

import patterns.cluster.DoubleArray;

public class DistanceCorrelation extends DistanceFunction {
	/** the name of this distance function */
	static String NAME = "correlation";
	
	/**
	 * Calculate the Correlation distance between two vectors of doubles. The
	 * correlation distance function calculates the distance between two vectors
	 * of double and returns a value in [0,1]. A result of 0 means that there is
	 * a positive linear relationship. A result of 1 means that there is a
	 * negative linear relationship. A result of 0.5 means that there is no linear
	 * relationship (but there might be a non linear relationship).
	 * 
	 * @param vector1
	 *            the first vector
	 * @param vector2
	 *            the second vector
	 * @return the distance
	 */
	public double calculateDistance(DoubleArray vector1, DoubleArray vector2) {
		double mean1 = calculateMean(vector1);	
		double mean2 = calculateMean(vector2);	
		double standardDeviation1 = calculateStdDeviation(vector1, mean1);	
		double standardDeviation2 = calculateStdDeviation(vector2, mean2);	
		
		double correlation = 0;
		for(int i=0; i< vector1.data.length; i++){
			correlation -= (vector1.data[i] - mean1) * (vector2.data[i] - mean2);
		}
		// protection to avoid dividing by 0
		if(standardDeviation1 == 0) {
			standardDeviation1 = 0.0001;
		}
		// protection to avoid dividing by 0
		if(standardDeviation2 == 0) {
			standardDeviation2 = 0.0001;
		}
		double bottom = (standardDeviation1 * standardDeviation2 * (vector1.data.length - 1));
		correlation = correlation / (bottom );
		return (1.0 + correlation) / 2.0;
		// 0.5 0 1 1 
	}
	
	/**
	 * This method calculate the mean of a list of doubles
	 * @param list the list of doubles
	 * @return the mean 
	 */
	private static double calculateMean(DoubleArray vector) {
		double sum = 0;
		for (double val : vector.data) {
			sum += val;
		}
		return sum / vector.data.length;
	}

	/**
	 * This method calculate the standard deviation of a list of double.
	 * Note that it divides by n-1 instead of n, assuming that it is
	 * the standard deviation of a sample rather than a population.
	 * @param list the list of doubles
	 * @param the man of the list of double values
	 * @return the standard deviation
	 */
	private static double calculateStdDeviation(DoubleArray vector, double mean) {
		double deviation = 0;
		for (double val : vector.data) {
			deviation += Math.pow(mean - val, 2);
		}
		return Math.sqrt(deviation / (vector.data.length - 1));
	}
	
	public static void main(String[] args) {
		DoubleArray array1 = new DoubleArray(new double[] {2, 3, 1, 1, 1});
		DoubleArray array2 = new DoubleArray(new double[] {2, 1, 1, 1, 1});
		System.out.println(new DistanceCorrelation().calculateDistance(array1,array2));
		// The result should be 0.5
		
		DoubleArray array5 = new DoubleArray(new double[] {3, 6, 0, 3, 6});
		DoubleArray array6 = new DoubleArray(new double[] {1, 2, 0, 1, 2});
		System.out.println(new DistanceCorrelation().calculateDistance(array5,array6));
		// The result should be 0
		
		DoubleArray array7 = new DoubleArray(new double[] {3, 6, 0, 3, 6});
		DoubleArray array8 = new DoubleArray(new double[] {-1, -2, 0, -1, -2});
		System.out.println(new DistanceCorrelation().calculateDistance(array7,array8));
		// The result should be 1.
		
		DoubleArray array3 = new DoubleArray(new double[] {3, -6, 0, 3, -6});
		DoubleArray array4 = new DoubleArray(new double[] {-1, 2, 0, -1, 2});
		System.out.println(new DistanceCorrelation().calculateDistance(array3,array4));
		// result should be 1
	}

	@Override
	public String getName() {
		return NAME;
	}
	

}
