package task;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public interface RenderTaskProgressListener {
	/**
	 * 
	 * @param percentage
	 *            percentage completion (between 0 and 1).
	 * @param elapsed
	 *            the elapsed time.
	 * @param eta
	 *            estimated remaining time.
	 */
	public void completion(double percentage, double elapsed, double eta);
}
