package utilities;

import java.util.List;

import task.BDPTRenderTask;
import task.RenderTaskInterface;

import distributed.Computer;
import distributed.RemoteCluster;
import distributed.RemoteComputer;
import distributed.RenderTaskExecutionService;
import distributed.RenderTaskExecutionServiceListener;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class DistributedRendering {
	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		BDPTRenderTask mirrorBalls = new BDPTRenderTask("mirror-balls",
				"/tmp/mirror-balls", 1024 * 1024, 512, 512, 8, 0);

		List<RenderTaskInterface> tasks = mirrorBalls.getTiles(8);

		// build the execution service
		final RenderTaskExecutionService service = new RenderTaskExecutionService();
		for (RenderTaskInterface task : tasks)
			service.submit(task);
		for (RemoteComputer pc : RemoteCluster.getCluster(3, true))
			service.add(pc);

		// add the listener
		RenderTaskExecutionServiceListener listener = new RenderTaskExecutionServiceListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * computer.RenderTaskExecutionServiceListener#started(computer.
			 * Computer, task.RenderTaskInterface)
			 */
			@Override
			public void started(Computer computer, RenderTaskInterface task) {
				System.out
						.format("started job on %s ...\n", computer.getName());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * computer.RenderTaskExecutionServiceListener#progress(computer
			 * .Computer, task.RenderTaskInterface, double, double, double)
			 */
			@Override
			public void progress(Computer computer, RenderTaskInterface task,
					double progress, double elapsed, double eta) {
				System.out.format("%20s is %5.1f%% complete ...\n",
						computer.getName(), progress * 100);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * computer.RenderTaskExecutionServiceListener#finished(computer
			 * .Computer, task.RenderTaskInterface)
			 */
			@Override
			public void finished(Computer computer, RenderTaskInterface task) {
				int total = service.nbOfTasks();
				int finished = total - service.remainingTasks();
				double percentage = (100.0 * (double) finished)
						/ (double) total;
				System.out.format("finished job on %s!\n", computer.getName());
				System.out.format("progresss: %d / %d (%5.1f%%)\n", finished,
						total, percentage);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * computer.RenderTaskExecutionServiceListener#error(computer.Computer
			 * , task.RenderTaskInterface)
			 */
			@Override
			public void error(Computer computer, RenderTaskInterface task) {
				System.err.format("error executing job on %s !!!\n",
						computer.getName());

			}
		};
		service.addListener(listener);

		service.shutdown();
		service.awaitTermination();
		System.out.println("finished rendering!");
	}
}
