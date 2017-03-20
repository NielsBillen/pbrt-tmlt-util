package distributed;

import javax.swing.JFrame;

import task.RenderTaskInterface;

/**
 * A graphical user interface to monitor the progress of the a
 * RenderTaskExecutionService.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemoteExecutionMonitor extends JFrame implements
		RenderTaskExecutionServiceListener {
	private static final long serialVersionUID = 1546072090177766891L;
	private final RenderTaskExecutionService service;

	/**
	 * 
	 */
	public RemoteExecutionMonitor(RenderTaskExecutionService service)
			throws NullPointerException {
		if (service == null)
			throw new NullPointerException("the given service is null!");
		this.service = service;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * distributed.RenderTaskExecutionServiceListener#started(distributed.Computer
	 * , task.RenderTaskInterface)
	 */
	@Override
	public void started(Computer computer, RenderTaskInterface task) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * distributed.RenderTaskExecutionServiceListener#progress(distributed.Computer
	 * , task.RenderTaskInterface, double, double, double)
	 */
	@Override
	public void progress(Computer computer, RenderTaskInterface task,
			double progress, double elapsed, double eta) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * distributed.RenderTaskExecutionServiceListener#finished(distributed.Computer
	 * , task.RenderTaskInterface)
	 */
	@Override
	public void finished(Computer computer, RenderTaskInterface task) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * distributed.RenderTaskExecutionServiceListener#error(distributed.Computer
	 * , task.RenderTaskInterface)
	 */
	@Override
	public void error(Computer computer, RenderTaskInterface task) {
		// TODO Auto-generated method stub

	}

}
