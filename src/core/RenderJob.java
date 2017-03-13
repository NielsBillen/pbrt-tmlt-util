package core;

import java.io.File;
import java.io.IOException;

import pbrt.PBRTScene;
import util.FileUtil;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderJob {
	/**
	 * 
	 */
	public final File directory;

	/**
	 * 
	 * @param directory
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public RenderJob(File directory) throws NullPointerException,
			IllegalArgumentException {
		if (directory == null)
			throw new NullPointerException("the given directory is null!");
		if (!directory.exists())
			throw new IllegalArgumentException(
					"the given directory does not exist!");
		if (!directory.isDirectory())
			throw new IllegalArgumentException(
					"the given directory is not a folder!");
		this.directory = directory;
	}

	/**
	 * 
	 * @param pbrt
	 * @param outputDirectory
	 * @param repetition
	 * @param seed
	 */
	public void execute(String pbrt, String outputDirectory, int repetition,
			int seed) {
		execute(new File(pbrt), new File(outputDirectory), repetition, seed);
	}

	/**
	 * 
	 * @param pbrt
	 * @param outputDirectory
	 * @param repetition
	 * @param seed
	 * @throws NullPointerException
	 */
	public void execute(final File pbrt, final File outputDirectory,
			int repetition, long seed) throws NullPointerException {
		if (pbrt == null)
			throw new NullPointerException("the given pbrt executable is null!");
		if (!pbrt.exists())
			throw new IllegalArgumentException(
					"the given pbrt file does not exist!");
		if (!pbrt.isFile())
			throw new IllegalArgumentException(
					"the given pbrt file is not a file!");

		final String sceneName = directory.getName();
		final File sceneFile = new File(directory, sceneName.concat(".pbrt"));

		if (!sceneFile.exists())
			throw new IllegalArgumentException("the scene \""
					+ sceneFile.getAbsolutePath() + "\" could not be found!");
		if (!sceneFile.isFile())
			throw new IllegalArgumentException("the scene \""
					+ sceneFile.getAbsolutePath() + "\" is not a file!");

		// Create the output directory
		String outputName = outputFilename(sceneName, seed, repetition);
		File outputSceneDirectory = new File(outputDirectory, sceneName);
		File outputFile = new File(outputSceneDirectory, outputName);

		if (!outputSceneDirectory.exists()
				&& !FileUtil.mkdirs(outputSceneDirectory))
			throw new IllegalArgumentException(
					"could not generate output directory \""
							+ outputSceneDirectory.getAbsolutePath() + "\"");

		PBRTScene scene = build(outputFile);

		File job = new File(directory, outputName + ".pbrt");
		try {
			scene.print(job);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"could not write the scene to render!");
		}

		String[] commands = new String[] { pbrt.getAbsolutePath(), "--quiet",
				"--seed", "" + seed, job.getAbsolutePath() };

		try {
			ProcessBuilder builder = new ProcessBuilder(commands);
			builder = builder.directory(pbrt.getParentFile().toPath().toFile()
					.getAbsoluteFile());
			Process process = builder.start();

			int result = process.waitFor();
			if (result != 0)
				throw new IllegalStateException("rendering job \""
						+ job.getAbsolutePath() + "\" has failed!");

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File outputScene = new File(outputSceneDirectory, outputName + ".pbrt");
		job.renameTo(outputScene);

	}

	/**
	 * 
	 * @param sceneName
	 * @param seed
	 * @param repetition
	 * @return
	 */
	public abstract String outputFilename(String sceneName, long seed,
			int repetition);

	/**
	 * 
	 * @return
	 */
	public abstract PBRTScene build(File outputFile);
}
