package utilities;

import java.util.List;

import task.RenderTask;
import task.RenderTaskInterface;
import task.TPSSMLTRenderTask;
import distributed.RemoteCluster;
import distributed.RemoteComputer;
import distributed.RemoteExecutionMonitor;
import distributed.RenderTaskExecutionService;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class DistributedRenderer {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String scene = "kitchen";
		boolean wide = true;
		int samples = 4096;
		int xresolution = wide ? 480 : 256;
		int yresolution = 256;
		int maxdepth = 8;
		double sigma = 0.0256;
		double largestep = 0.25;

		// PSSMLTRenderTask task = new PSSMLTRenderTask(scene,
		// "output/distributed/pssmlt" + scene, samples, xresolution,
		// yresolution, maxdepth, sigma, largestep, 0);
		TPSSMLTRenderTask task = new TPSSMLTRenderTask(scene,
				"output/distributed/tpssmlt/" + scene, samples, xresolution,
				yresolution, maxdepth, new double[] { 1, 3, 9 }, new double[] {
						sigma, sigma, sigma },
				new double[] { 0, 0, largestep }, 0.2, 0);

		render(task, 80);
	}

	/**
	 * 
	 * @param task
	 * @param repetitions
	 */
	public static void render(RenderTask task, int repetitions) {
		List<RenderTaskInterface> tasks = task.repeat(repetitions);

		RenderTaskExecutionService service = new RenderTaskExecutionService();
		for (RenderTaskInterface repetition : tasks)
			service.submit(repetition);

		for (RemoteComputer computer : RemoteCluster.getCluster(4, false))
			service.add(computer);

		service.addListener(new RemoteExecutionMonitor(service));

		service.shutdown();
		service.awaitTermination();
	}
}
