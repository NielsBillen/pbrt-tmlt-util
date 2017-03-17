package core;

import java.io.File;
import java.io.IOException;

import pbrt.PBRTScene;
import util.FileUtil;

/**
 * A utility class for rendering scenes with pbrt.
 * 
 * There are a couple of requirements for the format of a scene:
 * <ul>
 * <li>all the data of a scene is contained in a single folder <i>scenename</i></li>
 * <li>the name of the folder (i.e. <i>scenename</i>) is the name of the scene</li>
 * <li>all the specific rendering settings are stored in the file
 * <i>scenename</i>.pbrt, where <i>scenename</i> is the name</li>.
 * <li>all scene invariant data such as the geometry, textures, etc, are stored
 * in <i>common.pbrt</i></li>.
 * </ul>
 * 
 * <p>
 * Summarized, each folder containing a scene should have the following
 * contents.
 * </p>
 * 
 * <ul>
 * <li>
 * <i>scenename</i>
 * <ul>
 * <li>scenename.pbrt</li>
 * <li>common.pbrt</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderJob {
	/**
	 * The directory containing all the data of the scene.
	 */
	public final File directory;

	/**
	 * The name of the scene.
	 */
	public final String sceneName;

	/**
	 * File containing the default rendering parameters of the scene.
	 */
	public final File sceneFile;

	/**
	 * Creates a new rendering job which is responsible for rendering the scene
	 * stored in the given directory.
	 * 
	 * @param directory
	 *            the directory containing the scene.
	 * @throws NullPointerException
	 *             when the given directory is null.
	 * @throws IllegalArgumentException
	 *             when the given directory does not exist or is not a folder.
	 */
	public RenderJob(File directory) throws NullPointerException,
			IllegalArgumentException {
		if (directory == null)
			throw new NullPointerException("the given directory is null!");
		if (!directory.exists())
			throw new IllegalArgumentException(String.format(
					"the given directory \"%s\" does not exist!",
					directory.getAbsolutePath()));
		if (!directory.isDirectory())
			throw new IllegalArgumentException(String.format(
					"the given directory \"%s\" does not exist!",
					directory.getAbsolutePath()));

		this.directory = directory;
		this.sceneName = directory.getName();
		this.sceneFile = new File(this.directory,
				this.sceneName.concat(".pbrt"));

		if (!sceneFile.exists())
			throw new IllegalArgumentException(String.format(
					"the given directory \"%s\" does not adhere to the "
							+ "requirements of a valid scene. "
							+ "The required file \"%s\" is missing!",
					directory.getAbsolutePath(), sceneFile.getAbsolutePath()));
		if (!sceneFile.isFile())
			throw new IllegalArgumentException(String.format(
					"the given directory \"%s\" does not adhere to the "
							+ "requirements of a valid scene. "
							+ "The required file \"%s\" is folder!",
					directory.getAbsolutePath(), sceneFile.getAbsolutePath()));
	}

	/**
	 * Executes the rendering job, using the given pbrt executable and storing
	 * the result in the specified output directory.
	 * 
	 * @param pbrt
	 *            the filename of the pbrt executable to render the scene.
	 * @param outputDirectory
	 *            the filename of the directory in which the output should be
	 *            written.
	 * @param repetition
	 *            an integer which specifies the sequence number of the scene is
	 *            rendered multiple times with different seeds.
	 * @param seed
	 *            a seed to use for rendering.
	 * @param quiet
	 *            whether the process should do no printouts about the progress.
	 */
	public void execute(String pbrt, String outputDirectory, int repetition,
			long seed, boolean quiet) {
		execute(new File(pbrt), new File(outputDirectory), repetition, seed,
				quiet);
	}

	/**
	 * Executes the rendering job, using the given pbrt executable and storing
	 * the result in the specified output directory.
	 * 
	 * @param pbrt
	 *            the filename of the pbrt executable to render the scene.
	 * @param outputDirectory
	 *            the filename of the directory in which the output should be
	 *            written.
	 * @param repetition
	 *            an integer which specifies the sequence number of the scene is
	 *            rendered multiple times with different seeds.
	 * @param seed
	 *            a seed to use for rendering.
	 * @param quiet
	 *            whether the process should do no printouts about the progress.
	 */
	public void execute(final File pbrt, final File outputDirectory,
			int repetition, long seed, boolean quiet)
			throws NullPointerException {
		/***********************************************************************
		 * Check whether the executable is valid
		 **********************************************************************/

		if (pbrt == null)
			throw new NullPointerException(
					String.format("the given pbrt executable is null!"));
		if (!pbrt.exists())
			throw new NullPointerException(String.format(
					"the given pbrt executable \"%s\" does not exist!",
					pbrt.getAbsolutePath()));
		if (!pbrt.isFile())
			throw new NullPointerException(String.format(
					"the given pbrt executable \"%s\" is not a file!",
					pbrt.getAbsolutePath()));

		/***********************************************************************
		 * Create the output directory if necessary
		 **********************************************************************/

		final String outputName = String.format("%s-seed-%d-%d",
				getOutputFilename(), seed, repetition);
		final File outputSceneDirectory = new File(outputDirectory, sceneName);
		final File outputFile = new File(outputSceneDirectory, outputName);
		if (!outputSceneDirectory.exists()
				&& !FileUtil.mkdirs(outputSceneDirectory))
			throw new IllegalArgumentException(
					"could not generate output directory \""
							+ outputSceneDirectory.getAbsolutePath() + "\"");

		/***********************************************************************
		 * Write the scene to render
		 **********************************************************************/

		final PBRTScene scene = build(outputFile);
		final File job = new File(directory, outputName + ".pbrt");
		final File stdout = new File(outputSceneDirectory,
				outputName.concat("-stdout.txt"));
		job.deleteOnExit();
		try {
			scene.print(job);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"could not write the scene to render!");
		}

		/***********************************************************************
		 * Execute the render
		 **********************************************************************/

		final String[] commands;
		if (quiet)
			commands = new String[] { pbrt.getAbsolutePath(), "--quiet",
					"--seed", "" + seed, job.getAbsolutePath() };
		else
			commands = new String[] { pbrt.getAbsolutePath(), "--seed",
					"" + seed, job.getAbsolutePath() };

		try {
			ProcessBuilder builder = new ProcessBuilder(commands);
			builder = builder.directory(pbrt.getParentFile().toPath().toFile()
					.getAbsoluteFile());

			if (quiet) {
				builder = builder.redirectError(stdout);
				builder = builder.redirectOutput(stdout);
			} else
				builder.inheritIO();
			Process process = builder.start();

			int result = process.waitFor();
			if (result != 0)
				System.err.println("rendering job \"" + job.getAbsolutePath()
						+ "\" has failed!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/***********************************************************************
		 * Move the .pbrt file containing the rendering settings to the output
		 * directory for future reference
		 **********************************************************************/

		final File outputScene = new File(outputSceneDirectory,
				outputName.concat(".pbrt"));
		FileUtil.cp(job, outputScene);
		job.delete();
	}

	/**
	 * Returns a unique filename based on the rendering parameters of the
	 * rendering job.
	 * 
	 * @return a unique filename based on the rendering parameters of the
	 *         rendering job.
	 */
	public abstract String getOutputFilename();

	/**
	 * Builds a .pbrt scene containing the rendering parameters of this job.
	 * 
	 * @return a .pbrt scene containing the rendering parameters of this job.
	 */
	public abstract PBRTScene build(File outputFile);
}
