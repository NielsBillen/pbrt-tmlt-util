package distributed;

import task.RenderTaskInterface;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public interface RenderTaskExecutionServiceListener {
	/**
	 * 
	 * @param computer
	 * @param task
	 */
	public void started(Computer computer, RenderTaskInterface task);

	/**
	 * 
	 * @param computer
	 * @param task
	 * @param progress
	 * @param elapsed
	 * @param eta
	 */
	public void progress(Computer computer, RenderTaskInterface task,
			double progress, double elapsed, double eta);

	/**
	 * 
	 * @param computer
	 * @param task
	 */
	public void finished(Computer computer, RenderTaskInterface task);

	/**
	 * 
	 * @param computer
	 * @param task
	 */
	public void error(Computer computer, RenderTaskInterface task);
}
