package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class Statistics {
	private double minimum = Double.POSITIVE_INFINITY;
	private double maximum = Double.NEGATIVE_INFINITY;
	private double average = 0;
	private double variance = 0;
	private int n = 0;
	private final List<Double> data = new ArrayList<Double>();
	private boolean isDataDirty = false;

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

		average = (value + n * average) / (++n);
		variance = variance + (value - average) * (value - oldAverage);
		minimum = Math.min(minimum, value);
		maximum = Math.max(maximum, value);
		data.add(value);
		isDataDirty = true;
	}

	/**
	 * 
	 * @return
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * 
	 * @return
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * 
	 * @return
	 */
	public double getMedian() {
		if (data.isEmpty())
			return Double.NaN;

		if (isDataDirty) {
			isDataDirty = false;
			Collections.sort(data);
		}

		if (data.size() % 2 == 0) {
			int offset = data.size() / 2;

			return data.get(offset) * 0.5 + 0.5 * data.get(offset - 1);
		} else
			return data.get(data.size() / 2);
	}

	/**
	 * 
	 * @return
	 */
	public double getAverage() {
		return average;
	}

	/**
	 * 
	 * @return
	 */
	public double getVariance() {
		if (n == 0)
			return Double.POSITIVE_INFINITY;
		else if (n == 1)
			return 0;
		return variance / (n - 1);
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return n;
	}
}
