package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows to calculate statistics of data.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class Statistics {
	/**
	 * The minimum value of the data.
	 */
	private double minimum = Double.POSITIVE_INFINITY;

	/**
	 * The maximum value of the data.
	 */
	private double maximum = Double.NEGATIVE_INFINITY;

	/**
	 * The average value of the data.
	 */
	private double average = 0;

	/**
	 * The variance of the data.
	 */
	private double variance = 0;

	/**
	 * List containing the data (required for finding the median value).
	 */
	private final List<Double> data = new ArrayList<Double>();

	/**
	 * Boolean flag to check whether the data is sorted.
	 */
	private boolean isSorted = true;

	/**
	 * 
	 */
	public Statistics() {
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static double getAverage(double... values) {
		double sum = 0;
		for (int i = 0; i < values.length; ++i)
			sum += values[i];
		return sum / values.length;
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static double getVariance(double... values) {
		double sum = 0;
		double average = getAverage(values);
		for (int i = 0; i < values.length; ++i) {
			double difference = (average - values[i]);
			sum += difference * difference;
		}
		return sum / (values.length - 1);
	}

	/**
	 * 
	 * @param value
	 */
	public void add(double value) {
		final double oldAverage = average;
		final int n = size();

		average = (value + n * average) / (double) (n + 1);
		variance = variance + (value - average) * (value - oldAverage);
		minimum = Math.min(minimum, value);
		maximum = Math.max(maximum, value);
		data.add(value);
		isSorted = false;
	}

	/**
	 * Returns the minimum value of the data.
	 * 
	 * @return the minimum value of the data.
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * Returns the maximum value of the data.
	 * 
	 * @return the maximum value of the data.
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Returns the median value of the statistic.
	 * 
	 * @return the median value of the statistic.
	 */
	public double getMedian() {
		if (data.isEmpty())
			return Double.NaN;

		if (!isSorted) {
			isSorted = true;
			Collections.sort(data);
		}

		final int n = data.size();

		if (n % 2 == 0) {
			int offset = n / 2;

			return data.get(offset) * 0.5 + 0.5 * data.get(offset - 1);
		} else
			return data.get(n / 2);
	}

	/**
	 * Returns the average value of the data.
	 * 
	 * @return the average value of the data.
	 */
	public double getAverage() {
		return average;
	}

	/**
	 * Returns the variance of the data.
	 * 
	 * @return the variance of the data.
	 */
	public double getVariance() {
		final int n = size();
		if (n == 0)
			return Double.POSITIVE_INFINITY;
		else if (n == 1)
			return 0;
		return variance / (n - 1);
	}

	/**
	 * Returns the standard deviation of the data.
	 * 
	 * @return the standard deviation of the data.
	 */
	public double getStandardDeviation() {
		final int n = size();
		if (n == 0)
			return Double.POSITIVE_INFINITY;
		else if (n == 1)
			return 0;
		else
			return Math.sqrt(getVariance());
	}

	/**
	 * Returns the amount of data values.
	 * 
	 * @return the amount of data values.
	 */
	public int size() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"[ Statistics ]:\n" + "n:                  %d\n"
						+ "average:            %.10f\n"
						+ "median:             %.10f\n"
						+ "standard deviation: %.10f"
						+ "variance:           %.10f\n"
						+ "minimum:            %.10f\n"
						+ "maximum:            %.10f\n", size(), getAverage(),
				getMedian(), getStandardDeviation(), getVariance(),
				getMinimum(), getMaximum());
	}
}
