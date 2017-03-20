package distributed;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import task.RenderTaskInterface;
import util.TablePrinter;

/**
 * A graphical user interface to monitor the progress of the a
 * RenderTaskExecutionService.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemoteExecutionMonitor implements
		RenderTaskExecutionServiceListener {
	private final RenderTaskExecutionService service;
	private TablePrinter status = new TablePrinter();
	private HashMap<Computer, Integer> map = new HashMap<Computer, Integer>();
	private ReentrantLock lock = new ReentrantLock();
	private long lastUpdateTime = System.currentTimeMillis();

	/**
	 * 
	 * @param service
	 * @throws NullPointerException
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
		lock.lock();
		Integer number = map.get(computer);

		if (number == null) {
			number = map.size();
			map.put(computer, number);
		}

		int column = number % 4;
		int row = number / 4;

		status.add(row, 2 * column, computer.getName());
		status.add(row, 2 * column + 1, "started");

		lock.unlock();

		printStatus();
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
		lock.lock();
		Integer number = map.get(computer);

		if (number == null) {
			number = map.size();
			map.put(computer, number);
		}

		int column = number % 4;
		int row = number / 4;

		status.add(row, 2 * column, computer.getName());
		status.add(row, 2 * column + 1, "%5.1f%%", 100 * progress);

		lock.unlock();

		printStatus();
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
		lock.lock();
		Integer number = map.get(computer);

		if (number == null) {
			number = map.size();
			map.put(computer, number);
		}

		int column = number % 4;
		int row = number / 4;

		status.add(row, 2 * column, computer.getName());
		status.add(row, 2 * column + 1, "finished");

		lock.unlock();

		printStatus();
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
		lock.lock();
		Integer number = map.get(computer);

		if (number == null) {
			number = map.size();
			map.put(computer, number);
		}

		int column = number % 4;
		int row = number / 4;

		status.add(row, 2 * column, computer.getName());
		status.add(row, 2 * column + 1, "error");

		lock.unlock();

		printStatus();

	}

	/**
	 * 
	 */
	public void printStatus() {
		lock.lock();
		try {
			if (System.currentTimeMillis() - lastUpdateTime < 1000)
				return;
			lastUpdateTime = System.currentTimeMillis();
			String status = getStatus();
			System.out.println(status);

			try (BufferedWriter writer = Files.newBufferedWriter(new File(
					"renderstatus.txt").toPath())) {
				writer.write(status);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		StringBuilder builder = new StringBuilder("[ Remote Rendering Status ]");
		builder.append(String.format("\ntask completion: %d / %d (%5.1f%%)",
				service.finishedTasks(), service.nbOfTasks(),
				100.0 * service.finishedTasks() / service.nbOfTasks()));
		builder.append("\n").append(status.toString());
		return builder.toString();
	}
}
