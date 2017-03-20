package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import pbrt.PBRTParser;
import pbrt.PBRTScene;
import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;
import util.FileUtil;
import util.Statistics;
import cli.CommandLineArguments;
import cli.CommandLineInterface;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class PSSMLTAnalysis extends CommandLineInterface {
	/**
	 * 
	 */
	public PSSMLTAnalysis() {
		super("pssmltanalysis", "<reference directory> <data directory>");

		addStringSetting("directory",
				"Sets the relative directory to the given value.",
				new File(".").getAbsoluteFile().getParent() + "/");
		addStringSetting("output",
				"Sets the output directory to the given value", new File(
						"pssmltsettings").getAbsolutePath());
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new PSSMLTAnalysis().parse(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void analyse(CommandLineArguments arguments) {
		new PSSMLTAnalysis().parse(arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleAction(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleAction(String token, CommandLineArguments arguments) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleArgument(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		try {
			analyse(argument, arguments.next());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#parsed()
	 */
	@Override
	public void finished() {
	}

	/**
	 * 
	 * @param reference
	 * @param data
	 * @throws IOException
	 */
	private void analyse(String reference, String data) throws IOException {
		if (reference == null)
			throw new NullPointerException(
					"the given reference folder filename is null!");
		if (data == null)
			throw new NullPointerException(
					"the given data folder filename is null!");

		String directory = getStringSetting("directory").replaceAll("/+$", "");

		File referenceFile;
		if (reference.startsWith("/"))
			referenceFile = new File(reference);
		else
			referenceFile = new File(directory + "/" + reference);
		File dataFile;
		if (data.startsWith("/"))
			dataFile = new File(data);
		else
			dataFile = new File(directory + "/" + data);

		analyse(referenceFile, dataFile);
	}

	/**
	 * 
	 * @param referenceFolder
	 * @param dataFolder
	 * @throws IOException
	 */
	private void analyse(File referenceFolder, File dataFolder)
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

		TreeMap<Double, TreeMap<Double, Statistics>> dataMap = new TreeMap<Double, TreeMap<Double, Statistics>>();

		for (File dataImageFile : data) {
			/*------------------------------------------------------------------
			 * Calculate the mean squared error
			 *----------------------------------------------------------------*/

			String dataImageFilename = dataImageFile.getName();
			PFMImage dataImage = PFMReader.read(dataImageFile);
			double mse = PFMUtil.MSE(dataImage, referenceImage);

			/*------------------------------------------------------------------
			 * Find the relevant parameter in the scene file
			 *----------------------------------------------------------------*/

			File dataPBRTFile = new File(dataFolder,
					dataImageFilename.replaceAll(".pfm$", ".pbrt"));
			if (!dataPBRTFile.exists()) {
				System.err.format("missing .pbrt scene file for %s!",
						dataPBRTFile.getAbsolutePath());
				continue;
			}

			PBRTScene scene = PBRTParser.parse(dataPBRTFile);
			double sigma = Double.parseDouble(scene
					.findSetting("Integrator.sigma"));
			double largestep = Double.parseDouble(scene
					.findSetting("Integrator.largestepprobability"));

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

		/***********************************************************************
		 * Write the data and find the optimal settings
		 **********************************************************************/

		double bestSigma = 0;
		double bestLargeStep = 0;
		double bestAverage = Double.POSITIVE_INFINITY;
		double minimum = Double.POSITIVE_INFINITY;
		double maximum = Double.NEGATIVE_INFINITY;

		String outputDirectoryName = String.format("%s/%s",
				getStringSetting("output"), sceneName);
		File outputDirectory = new File(outputDirectoryName);
		FileUtil.mkdirs(outputDirectory);
		File file = new File(outputDirectory, sceneName + ".txt");

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			for (Entry<Double, TreeMap<Double, Statistics>> e1 : dataMap
					.entrySet()) {
				for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
					double sigma = e1.getKey();
					double largeStep = e2.getKey();
					Statistics statistic = e2.getValue();
					double average = statistic.getAverage();
					double median = statistic.getMedian();
					double variance = statistic.getVariance();
					int size = statistic.size();

					writer.write(String.format(
							"%.10f %.10f %.10f %10f %.10f %d\n", sigma,
							largeStep, average, median, variance, size));

					if (average < bestAverage) {
						bestAverage = average;
						bestSigma = sigma;
						bestLargeStep = largeStep;
					}
					if (average < minimum)
						minimum = average;
					if (average > maximum)
						maximum = average;

				}
				writer.write("\n");
			}
		}

		System.out.format("[ PSSMLT Analysis %s ] \n", sceneName);
		System.out.format("sigma:                  %.10f\n", bestSigma);
		System.out.format("large step probability: %.10f\n", bestLargeStep);

		/***********************************************************************
		 * Generate tikz plot data
		 **********************************************************************/

		File tex = new File(outputDirectory, sceneName + ".tex");
		try (BufferedWriter writer = Files.newBufferedWriter(tex.toPath())) {
			writer.write("\\documentclass{article}\n");
			writer.write("\\usepackage{tikz}\n");
			writer.write("\\usepackage{pgfplots}\n");
			writer.write("\\begin{document}\n");
			writer.write("\\\tbegin{tikzpicture}\n");
			writer.write("\\\t\tbegin{axis}view={-20}{20}, grid=both]\n");
			writer.write("\\\t\t\t\\addplot3[surf] file { " + sceneName
					+ ".txt };\n");
			writer.write("\\\t\tend{axis}\n");
			writer.write("\\\tend{tikzpicture}\n");
			writer.write("\\end{document}\n");
		}
	}
}
