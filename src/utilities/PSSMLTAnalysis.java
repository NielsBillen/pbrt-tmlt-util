package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import pbrt.PBRTParser;
import pbrt.PBRTScene;
import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;
import util.FileUtil;
import util.Statistics;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class PSSMLTAnalysis {
	/**
	 * 
	 * @param args
	 */
	public static void analyze(LinkedList<String> arguments) {
		while (!arguments.isEmpty()) {
			String token = arguments.removeFirst();

			if (token.equals("-help") || token.equals("--help")) {
				System.out
						.println("usage: -pssmltanalysis <reference folder> <data folder> ...");
				return;
			} else if (arguments.isEmpty()) {
				throw new IllegalStateException(
						"no data folder specified for the given reference folder \""
								+ token + "\"!");
			} else {
				try {
					analyse(token, arguments.removeFirst());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 * @param reference
	 * @param data
	 * @throws IOException
	 */
	private static void analyse(String reference, String data)
			throws IOException {
		if (reference == null)
			throw new NullPointerException(
					"the given reference folder filename is null!");
		if (data == null)
			throw new NullPointerException(
					"the given data folder filename is null!");
		analyse(new File(reference), new File(data));

	}

	/**
	 * 
	 * @param referenceFolder
	 * @param dataFolder
	 * @throws IOException
	 */
	private static void analyse(File referenceFolder, File dataFolder)
			throws IOException {
		if (referenceFolder == null)
			throw new NullPointerException("the given reference file is null!");
		if (dataFolder == null)
			throw new NullPointerException("the given data file is null!");
		if (!referenceFolder.exists())
			throw new IllegalArgumentException(
					"the given reference folder does not exist!");
		if (!dataFolder.exists())
			throw new IllegalArgumentException(
					"the given data folder does not exist!");
		if (!referenceFolder.isDirectory())
			throw new IllegalArgumentException(
					"the given reference folder is not a directory!");
		if (!dataFolder.isDirectory())
			throw new IllegalArgumentException(
					"the given data folder is not a directory!");

		if (!referenceFolder.getName().equals(dataFolder.getName()))
			throw new IllegalArgumentException(
					"the file names of the folder do not match!");

		final String sceneName = referenceFolder.getName();
		final File referenceImageFile = FileUtil.findFirst(referenceFolder,
				sceneName + ".*\\.pfm");

		if (referenceImageFile == null)
			throw new IllegalStateException(
					"could not find the reference image in the specified folder \""
							+ referenceFolder + "\"!");

		/***********************************************************************
		 * Load the reference image
		 **********************************************************************/

		final PFMImage referenceImage = PFMReader.read(referenceImageFile);

		/***********************************************************************
		 * Find all the rendered data
		 **********************************************************************/

		final File[] data = dataFolder.listFiles(new FilenameFilter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.io.FilenameFilter#accept(java.io.File,
			 * java.lang.String)
			 */
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".pfm");
			}
		});
		Arrays.sort(data);

		/***********************************************************************
		 * Find the best settings
		 **********************************************************************/

		double minimumMSE = Double.POSITIVE_INFINITY;
		File minimumFile = null;
		TreeMap<Double, TreeMap<Double, Statistics>> dataMap = new TreeMap<Double, TreeMap<Double, Statistics>>();

		for (File dataImageFile : data) {
			/*------------------------------------------------------------------
			 * Calculate the mean squared error
			 *----------------------------------------------------------------*/
			String dataImageFilename = dataImageFile.getName();
			PFMImage dataImage = PFMReader.read(dataImageFile);
			double mse = PFMUtil.MSE(dataImage, referenceImage).doubleValue();

			if (mse < minimumMSE) {
				minimumFile = dataImageFile;
				minimumMSE = mse;
			}

			/*------------------------------------------------------------------
			 * Find the relevant parameter in the scene file
			 *----------------------------------------------------------------*/

			File dataPBRTFile = new File(dataFolder,
					dataImageFilename.replaceAll(".pfm$", ".pbrt"));
			if (!dataPBRTFile.exists())
				continue;
//				throw new IllegalArgumentException(
//						"missing .pbrt scene file for "
//								+ dataImageFile.getAbsolutePath() + "!");
			PBRTScene scene = PBRTParser.parse(dataPBRTFile);
			double sigma = Double.parseDouble(scene
					.findSetting("Integrator.sigma"));
			double largestep = Double.parseDouble(scene
					.findSetting("Integrator.largestep"));

			System.out.format("%f %f : %f\n", sigma, largestep, mse);

			/*------------------------------------------------------------------
			 * Add the statistics
			 *----------------------------------------------------------------*/

			TreeMap<Double, Statistics> map = dataMap.get(sigma);
			if (map == null) {
				map = new TreeMap<Double, Statistics>();
				dataMap.put(sigma, map);
			}

			Statistics statistics = map.get(largestep);
			if (statistics == null) {
				statistics = new Statistics();
				map.put(largestep, statistics);
			}

			statistics.add(mse);
		}

		File file = new File(sceneName + "-surface.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			for (Entry<Double, TreeMap<Double, Statistics>> e1 : dataMap
					.entrySet()) {
				for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
					writer.write(String.format("%f %f %f %f\n", e1.getKey(), e2
							.getKey(), e2.getValue().getAverage(), e2
							.getValue().getVariance()));
				}
			}
		}
	}
}