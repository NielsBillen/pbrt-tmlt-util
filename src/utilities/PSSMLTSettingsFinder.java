package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import task.PSSMLTRenderTask;
import task.RenderTaskInterface;
import util.Printer;
import cli.CommandLineAdapter;
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
public class PSSMLTSettingsFinder extends CommandLineAdapter {
	/**
	 * 
	 */
	private final List<RenderTaskInterface> tasks = new ArrayList<RenderTaskInterface>();

	/**
	 * 
	 */
	public PSSMLTSettingsFinder() {
		super("pssmltsettings", "[<options>] <scene directory...>");

		addIntegerSetting("xresolution", "Sets the horizontal resolution.", 256);
		addIntegerSetting("yresolution", "Sets the vertical resolution.", 256);
		addIntegerSetting("maxdepth", "Sets the maximum recursion depth.", 8);
		addIntegerSetting("repetitions",
				"Number of times the experiments have to be repeated.", 1000);
		addIntegerSetting("samples",
				"Number of samples to render each experiment with.", 1024);

		// -samples 1024 -xresolution 120 -yresolution 64 -maxdepth 8
		// mirror-balls kitchen -xresolution 64 mirror-ring caustic-glass
		// -maxdepth 100 volume-caustic
		addExample("-samples 4096 -xresolution 2 -yresolution 2 -maxdepth 1000 milk");
		addExample("-samples 1024 -xresolution 120 -yresolution 64 -maxdepth 8 "
				+ "mirror-balls kitchen "
				+ "-xresolution 64 mirror-ring caustic-glass "
				+ "-maxdepth 100 volume-caustic");
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new PSSMLTSettingsFinder().parse(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(CommandLineArguments arguments) {
		new PSSMLTSettingsFinder().parse(arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleArgument(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		final File pbrtDirectory = LocalComputer.get().pbrtDirectory;
		final String pbrtDirectoryPath = pbrtDirectory.getAbsolutePath();
		final String sceneOutputDirectory = String.format(
				"%s/output/pssmlt/%s", pbrtDirectoryPath, argument);
		final int repetitions = getIntegerSetting("repetitions");

		final Random random = new Random(0);

		// final List<Double> sigmas = new ArrayList<Double>();
		// for (int s = 2; s <= 80; s += 2) {
		// double sigma = Math.pow(s * 0.01, 3);
		// sigmas.add(sigma);
		// }
		final List<Double> sigmas = new ArrayList<Double>();
		for (int s = 2; s <= 64; s += 2) {
			double sigma = Math.pow(s * 0.01, 2);
			sigmas.add(sigma);
		}

		final List<Double> largesteps = new ArrayList<Double>();
		for (int t = 10; t < 100; t += 10) {
			double largestep = Math.pow(t * 0.01, 2);
			largesteps.add(largestep);
		}

		for (int i = 0; i < repetitions; ++i) {
			for (double sigma : sigmas) {
				for (double largestep : largesteps) {
					int seed = random.nextInt(Integer.MAX_VALUE);

					String directory = String.format(
							"%s/sigma-%s/largestep-%s", sceneOutputDirectory,
							Printer.print(sigma), Printer.print(largestep));

					String filename = String.format("pssmlt-%d-%d-seed-%d",
							i + 1, repetitions, seed);

					PSSMLTRenderTask task = new PSSMLTRenderTask(argument,
							directory, filename, getIntegerSetting("samples"),
							getIntegerSetting("xresolution"),
							getIntegerSetting("yresolution"),
							getIntegerSetting("maxdepth"), sigma, largestep,
							seed);

					boolean done = LocalComputer.get().done(task);
					if (!done)
						tasks.add(task);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#finished()
	 */
	@Override
	public void finished() {
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
