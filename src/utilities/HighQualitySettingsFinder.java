package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import task.PSSMLTRenderTask;
import task.RenderTaskInterface;
import cli.CommandLineArguments;
import distributed.Computer;
import distributed.LocalComputer;
import distributed.RemoteAuthentication;
import distributed.RemoteCluster;
import distributed.RemoteComputer;
import distributed.RemoteExecutionMonitor;
import distributed.RenderTaskExecutionService;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class HighQualitySettingsFinder {
	private final int repetitions = 100;
	private final List<RenderTaskInterface> tasks = new ArrayList<RenderTaskInterface>();
	private final File pbrtDirectory = LocalComputer.get().pbrtDirectory;
	private final String pbrtDirectoryPath = pbrtDirectory.getAbsolutePath();
	private final int samples = 4096;

	/**
	 * 
	 */
	public HighQualitySettingsFinder() {
		pssmlt_mirrorring();
		execute();
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new HighQualitySettingsFinder();
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(CommandLineArguments arguments) {
		new HighQualitySettingsFinder();
	}

	/**
	 * 
	 */
	public void pssmlt_mirrorring() {
		double sigma = 0.0122;
		double largestep = 0.065;

		final Random random = new Random(0);
		final String scene = "mirror-ring";

		final String sceneOutputDirectory = String.format(
				"%s/output/pssmlt-highquality/%s", pbrtDirectoryPath, scene);

		for (int i = 0; i < repetitions; ++i) {
			int seed = random.nextInt(Integer.MAX_VALUE);

			String filename = String.format("pssmlt-highquality-%d-%d-seed-%d",
					i + 1, repetitions, seed);

			PSSMLTRenderTask task = new PSSMLTRenderTask("mirror-ring",
					sceneOutputDirectory, filename, samples, 256, 256, 8,
					sigma, largestep, seed);

			boolean done = LocalComputer.get().done(task);
			if (!done)
				tasks.add(task);

		}
	}

	/**
	 * 
	 */
	public void execute() {
		RenderTaskExecutionService service = new RenderTaskExecutionService();

		for (RenderTaskInterface task : tasks)
			service.submit(task);

		service.add(LocalComputer.get());

		RemoteComputer merida = new RemoteComputer("merida.cs.kuleuven.be",
				new RemoteAuthentication("niels", true));
		service.add(merida);

		for (Computer computer : RemoteCluster.getCluster(3, true))
			service.add(computer);

		service.addListener(new RemoteExecutionMonitor(service));

		service.shutdown();
		service.awaitTermination();
	}
}
