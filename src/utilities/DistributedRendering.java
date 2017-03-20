package utilities;

import java.util.List;

import task.PSSMLTRenderTask;
import task.RenderTaskInterface;

import computer.Computer;
import computer.RemoteAuthentication;
import computer.RemoteCluster;
import computer.RemoteComputer;
import computer.RenderTaskExecutionService;
import computer.RenderTaskExecutionServiceListener;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class DistributedRendering {
	public static void main(String[] args) throws InterruptedException {
		RemoteAuthentication niels = new RemoteAuthentication("niels");
		RemoteComputer anna = new RemoteComputer("anna.cs.kuleuven.be", niels);

		PSSMLTRenderTask mirrorBalls = new PSSMLTRenderTask("mirror-balls",
				"/tmp/mirror-balls", 1024, 480, 256, 8, 0.01, 0.03, 0);

		List<RenderTaskInterface> tasks = mirrorBalls.repeat(80);

		// build the execution service
		final RenderTaskExecutionService service = new RenderTaskExecutionService();
		for (RenderTaskInterface task : tasks)
			service.submit(task);
		for (RemoteComputer pc : RemoteCluster.getCluster(3))
			service.add(pc);
		service.add(anna);

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
		System.out.println("finished!");
	}
}
