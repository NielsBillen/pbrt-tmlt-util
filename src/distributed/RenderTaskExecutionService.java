package distributed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import task.RenderTaskInterface;
import task.RenderTaskProgressListener;

/**
 * Utility class which is reponsible for executing rendering tasks using a set
 * of Computers.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RenderTaskExecutionService {
	/**
	 * The list with all the tasks.
	 */
	private final List<RenderTaskInterface> tasks = new ArrayList<RenderTaskInterface>();

	/**
	 * The list with all the remaining tasks to be executed.
	 */
	private final LinkedList<RenderTaskInterface> remainingTasks = new LinkedList<RenderTaskInterface>();

	/**
	 * The list with all the finished tasks.
	 */
	private final LinkedList<RenderTaskInterface> finishedTasks = new LinkedList<RenderTaskInterface>();

	/**
	 * The list containing all the computers responsible for completing the
	 * tasks.
	 */
	private final List<Computer> computers = new ArrayList<Computer>();

	/**
	 * The list containing all the computers which are available for executing a
	 * task.
	 */
	private final LinkedList<Computer> available = new LinkedList<Computer>();

	/**
	 * 
	 */
	private final LinkedList<Computer> busy = new LinkedList<Computer>();

	/**
	 * 
	 */
	private final ReentrantLock monitor = new ReentrantLock();

	/**
	 * 
	 */
	private boolean started = false;

	/**
	 * The thread which executes the rendering of the tasks.
	 */
	private Thread executionThread;

	/**
	 * 
	 */
	private final List<RenderTaskExecutionServiceListener> listeners = new ArrayList<RenderTaskExecutionServiceListener>();

	/**
	 * Creates an empty render task execution service.
	 */
	public RenderTaskExecutionService() {
	}

	/**
	 * 
	 * @return
	 */
	public List<Computer> getComputers() {
		monitor.lock();
		List<Computer> result = new ArrayList<Computer>();
		monitor.unlock();
		return result;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addListener(RenderTaskExecutionServiceListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	/**
	 * 
	 * @return
	 */
	public int nbOfTasks() {
		monitor.lock();
		int result = tasks.size();
		monitor.unlock();
		return result;
	}

	public int finishedTasks() {
		monitor.lock();
		int result = finishedTasks.size();
		monitor.unlock();
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public int remainingTasks() {
		monitor.lock();
		int result = tasks.size() - finishedTasks.size();
		monitor.unlock();
		return result;
	}

	/**
	 * 
	 * @param task
	 */
	public void submit(RenderTaskInterface task) {
		monitor.lock();
		try {
			if (started)
				throw new IllegalStateException(
						"cannot submit new tasks when execution has started!");
			tasks.add(task);
			remainingTasks.add(task);
		} finally {
			monitor.unlock();
		}
	}

	/**
	 * 
	 * @param computer
	 */
	public void add(Computer computer) {
		monitor.lock();
		try {
			if (started)
				throw new IllegalStateException(
						"cannot add new computers when the execution has already started!");
			computers.add(computer);
			available.add(computer);
		} finally {
			monitor.unlock();
		}
	}

	/**
	 * Executes the tasks using the available computers.
	 */
	public void shutdown() {
		monitor.lock();

		try {
			started = true;
			executionThread = new ExecutionThread();
		} finally {
			monitor.unlock();
		}

		executionThread.start();
	}

	/**
	 * 
	 */
	public void awaitTermination() {
		monitor.lock();
		boolean isStarted = started;
		monitor.unlock();

		if (isStarted)
			try {
				executionThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		else
			throw new IllegalStateException("not started yet!");

	}

	/**
	 * 
	 * @author Niels Billen
	 * @version 0.1
	 */
	private class ExecutionThread extends Thread {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			while (true) {
				Thread.yield();

				// check if there are available jobs
				monitor.lock();
				if (!available.isEmpty() && !remainingTasks.isEmpty()) {
					Computer pc = available.removeFirst();
					RenderTaskInterface task = remainingTasks.removeFirst();
					busy.add(pc);
					monitor.unlock();

					schedule(pc, task);
				} else {
					boolean isBusy = !busy.isEmpty();
					monitor.unlock();

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}

					if (!isBusy)
						break;
				}
			}
		}

		/**
		 * 
		 * @param pc
		 * @param task
		 */
		private void schedule(final Computer pc, final RenderTaskInterface task) {
			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				public void run() {
					try {
						monitor.lock();
						try {
							for (RenderTaskExecutionServiceListener listener : listeners)
								listener.started(pc, task);
						} finally {
							monitor.unlock();
						}

						pc.execute(task, new RenderTaskProgressListener() {
							/*
							 * (non-Javadoc)
							 * 
							 * @see
							 * task.RenderTaskProgressListener#completion(double
							 * , double, double)
							 */
							@Override
							public void completion(double percentage,
									double elapsed, double eta) {
								monitor.lock();
								try {
									for (RenderTaskExecutionServiceListener listener : listeners)
										listener.progress(pc, task, percentage,
												elapsed, eta);
								} finally {
									monitor.unlock();
								}
							}
						});

						monitor.lock();
						try {
							busy.remove(pc);
							available.add(pc);
							finishedTasks.add(task);

							for (RenderTaskExecutionServiceListener listener : listeners)
								listener.finished(pc, task);
						} finally {
							monitor.unlock();
						}
					} catch (Exception e) {
						monitor.lock();
						try {
							e.printStackTrace();
							busy.remove(pc);
							remainingTasks.add(task);
							for (RenderTaskExecutionServiceListener listener : listeners)
								listener.error(pc, task);
						} finally {
							monitor.unlock();
						}

						TimerTask delayed = new TimerTask() {
							/*
							 * (non-Javadoc)
							 * 
							 * @see java.util.TimerTask#run()
							 */
							@Override
							public void run() {
								monitor.lock();
								available.add(pc);
								monitor.unlock();
							}
						};
						new Timer().schedule(delayed, 60000);
					}
				};
			};
			thread.start();
		}
	}
}
