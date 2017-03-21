package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import task.PSSMLTRenderTask;
import task.RenderTaskInterface;
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
				"Number of times the experiments have to be repeated.", 100);
		addIntegerSetting("samples",
				"Number of samples to render each experiment with.", 1024);

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
		File pbrtDirectory = LocalComputer.get().pbrtDirectory;
		File output = new File(pbrtDirectory, "output/pssmlt/" + argument);

		Random random = new Random(0);
		for (int sigma = 2; sigma <= 64; sigma += 2) {
			double s = Math.pow(sigma * 0.01, 2);
			for (int largeStep = 1; largeStep < 10; largeStep += 1) {
				double t = Math.pow(largeStep * 0.1, 2);

				PSSMLTRenderTask task = new PSSMLTRenderTask(argument,
						output.getAbsolutePath(), getIntegerSetting("samples"),
						getIntegerSetting("xresolution"),
						getIntegerSetting("yresolution"),
						getIntegerSetting("maxdepth"), s, t, random.nextLong());

				for (RenderTaskInterface repetition : task
						.repeat(getIntegerSetting("repetitions"))) {
					tasks.add(repetition);
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
		for (Computer computer : RemoteCluster.getCluster(4, true)) {
			service.add(computer);
		}

		RemoteAuthentication remote = new RemoteAuthentication("niels", true);
		RemoteComputer computer = new RemoteComputer("merida.cs.kuleuven.be",
				remote);
		service.add(computer);

		service.addListener(new RemoteExecutionMonitor(service));

		service.shutdown();
		service.awaitTermination();
	}
}
