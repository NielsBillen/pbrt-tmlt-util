package distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantLock;

import pbrt.PBRTScene;
import task.RenderTaskInterface;
import task.RenderTaskProgressListener;

/**
 * Represents the local computer.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class LocalComputer extends Computer {
	/**
	 * Lock for thread-safe access to the instance.
	 */
	private static ReentrantLock instanceLock = new ReentrantLock();

	/**
	 * Singleton instance of the local computer.
	 */
	private static LocalComputer instance;

	/**
	 * The home directory of the local computer.
	 */
	public final File homeDirectory = new File(System.getProperty("user.home"));

	/**
	 * Location containing the pbrt repository.
	 */
	public final File pbrtDirectory = new File(homeDirectory,
			"workspace/pbrt-tmlt");

	/**
	 * Location containing the scenes.
	 */
	public final File scenesDirectory = new File(homeDirectory,
			"workspace/pbrt-tmlt/scenes");

	/**
	 * Constructs the singleton instance of the local computer.
	 */
	private LocalComputer() {
		super(0);
	}

	/**
	 * Returns the reference to this local computer.
	 * 
	 * @return the reference to this local computer.
	 */
	public static LocalComputer get() {
		instanceLock.lock();
		if (instance == null)
			instance = new LocalComputer();
		instanceLock.unlock();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#getName()
	 */
	@Override
	public String getName() {
		return "localcomputer";
	}

	/**
	 * 
	 * @return
	 */
	public File getPBRTDirectory() {
		return pbrtDirectory;
	}

	/**
	 * Returns the directory containing the scenes.
	 * 
	 * (by default this is in <code>~/workspace/pbrt-tmlt/scenes</code>)
	 * 
	 * @return the direcotry containing the scenes.
	 */
	public File getScenesDirectory() {
		return scenesDirectory;
	}

	/**
	 * Returns the directory containing the data of the scene with the given
	 * name.
	 * 
	 * @param sceneName
	 *            the name of the scene.
	 * @return the directory containing the data of the scene with the given
	 *         name.
	 */
	public File getSceneDirectory(String sceneName) {
		return new File(scenesDirectory, sceneName);
	}

	/**
	 * 
	 * @return
	 */
	public File getPBRT() {
		return new File(pbrtDirectory, "pbrt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#execute(task.RenderTaskInterface,
	 * java.lang.String, computer.ProgressListener)
	 */
	@Override
	public void execute(RenderTaskInterface task,
			RenderTaskProgressListener listener) {
		/*----------------------------------------------------------------------
		 * Allocate the result directory
		 *--------------------------------------------------------------------*/

		final File resultDirectory = new File(task.getResultDirectory());
		if (!resultDirectory.exists()) {
			if (!resultDirectory.mkdirs())
				throw new ExecutionException("could not allocate the "
						+ "directory containing the results of the rendering!");
		} else if (!resultDirectory.isDirectory())
			throw new ExecutionException(
					"the requested results directory exists as a file!");

		/*----------------------------------------------------------------------
		 * Write the task to the scene directory
		 *--------------------------------------------------------------------*/

		File sceneDirectory = getSceneDirectory(task.getSceneName());
		File sceneFile = new File(sceneDirectory, task.getFilename() + ".pbrt");
		PBRTScene scene = task.getScene();
		try {
			scene.print(sceneFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sceneFile.deleteOnExit();

		/*----------------------------------------------------------------------
		 * Execute the pbrt command
		 *--------------------------------------------------------------------*/

		String command;

		if (nCores() <= 0)
			command = String.format(
					"./pbrt --outfile %s --seed %s %s",
					resultDirectory.getAbsolutePath() + "/"
							+ task.getFilename(), task.getSeed(),
					sceneFile.getAbsolutePath());
		else
			command = String.format(
					"./pbrt --outfile %s --seed %s --nthreads %d %s",
					resultDirectory.getAbsolutePath() + "/"
							+ task.getFilename(), task.getSeed(), nCores(),
					sceneFile.getAbsolutePath());

		ProcessBuilder builder = new ProcessBuilder(command.split(" +"));
		builder.directory(getPBRTDirectory());

		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw new ExecutionException(
					"could not start the rendering process!", e);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		double percentage = 0;
		while (process.isAlive()) {
			Thread.yield();

			try {
				if (reader.ready()) {
					String line = reader.readLine();
					percentage = updateProgress(line, percentage, listener);
				}
			} catch (Exception e) {
			}
		}

		int result;
		try {
			result = process.waitFor();
		} catch (InterruptedException e) {
			throw new ExecutionException(
					"interrupted while waiting for the rendering"
							+ " process to finish!", e);
		}

		if (result != 0)
			throw new ExecutionException("pbrt stopped with exit code "
					+ result + "!");

		// perform cleanup
		File sceneFileCopy = new File(resultDirectory, sceneFile.getName());
		try {
			Files.copy(sceneFile.toPath(), sceneFileCopy.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ExecutionException("failed to copy the scene file \""
					+ sceneFile.getAbsolutePath()
					+ "\" to the result directory \""
					+ resultDirectory.getAbsolutePath() + "\"");
		}
		sceneFile.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "localpc";
	}
}
