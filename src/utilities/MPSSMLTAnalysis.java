package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;
import util.FileUtil;
import util.Statistics;
import cli.CommandLineAdapter;
import cli.CommandLineArguments;

/**
 * Searches the optimal settings for rendering a scene with MPSSMLT integrator.
 * 
 * --directory /home/niels/renderdata --output /home/niels/analysis
 * reference/milk mpssmlt/milk
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class MPSSMLTAnalysis extends CommandLineAdapter {
	/**
	 * 
	 */
	private static final Pattern sigmaPattern = Pattern
			.compile("sigma\\-([0-9]*\\.)?[0-9]+");

	/**
	 * 
	 */
	private static final Pattern largestepPattern = Pattern
			.compile("largestep\\-([0-9]*\\.)?[0-9]+");

	/**
	 * 
	 */
	private static final FileFilter sigmaFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return file.isDirectory()
					&& sigmaPattern.matcher(file.getName()).matches();
		};
	};

	/**
	 * 
	 */
	private static final FileFilter largestepFilter = new FileFilter() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File file) {
			return file.isDirectory()
					&& largestepPattern.matcher(file.getName()).matches();
		};
	};

	/**
	 * 
	 */
	private static final FileFilter pfmFilter = new FileFilter() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().endsWith(".pfm");
		}
	};

	/**
	 * 
	 */
	public MPSSMLTAnalysis() {
		super("pssmltanalysis", "<reference directory> <data directory>");

		addStringSetting("directory",
				"Sets the relative directory to the given value.",
				new File(".").getAbsoluteFile().getParent() + "/");
		addStringSetting("output",
				"Sets the output directory to the given value", new File(
						"pssmltsettings").getAbsolutePath());

		addExample("--directory /home/niels/renderdata --output /home/niels/analysis reference/kitchen pssmlt/kitchen");
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new MPSSMLTAnalysis().parse(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(CommandLineArguments arguments) {
		new MPSSMLTAnalysis().parse(arguments);
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

		System.out.format("analysing data from %s and %s\n",
				referenceFile.getAbsolutePath(), dataFile.getAbsolutePath());

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
			throw new IllegalArgumentException("the given data folder \""
					+ dataFolder + "\" does not exist!");
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

		final PFMImage referenceImage = PFMUtil.normalizeByAverage(PFMReader
				.read(referenceImageFile));
		System.out.println("read reference image");

		/***********************************************************************
		 * Find the best settings
		 **********************************************************************/

		final ReentrantLock lock = new ReentrantLock();
		final TreeMap<Double, TreeMap<Double, Statistics>> data = new TreeMap<Double, TreeMap<Double, Statistics>>();
		final ExecutorService service = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());

		File[] sigmaFolders = dataFolder.listFiles(sigmaFilter);
		Arrays.sort(sigmaFolders);

		for (File sigmaFolder : sigmaFolders) {
			final String sigmaFolderName = sigmaFolder.getName();
			final double sigma = Double.parseDouble(sigmaFolderName.replaceAll(
					"sigma\\-", ""));

			final File[] largestepFolders = sigmaFolder
					.listFiles(largestepFilter);
			Arrays.sort(largestepFolders);

			for (final File largestepFolder : largestepFolders) {
				final String largestepFolderName = largestepFolder.getName();
				final double largestep = Double.parseDouble(largestepFolderName
						.replaceAll("largestep\\-", ""));

				Thread thread = new Thread() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Thread#run()
					 */
					public void run() {
						try {
							Statistics statistics = new Statistics();

							final File[] images = largestepFolder
									.listFiles(pfmFilter);
							Arrays.sort(images);
							
							if (images.length < 400)
								return;

							for (final File image : images) {
								try {
									/*----------------------------------------------
									 * Calculate the mean squared error
									 *--------------------------------------------*/

									PFMImage dataImage = PFMUtil
											.normalizeByAverage(PFMReader
													.read(image));
									double mse = PFMUtil.getMSE(dataImage,
											referenceImage, 2.2);

									/*----------------------------------------------
									 * Find the relevant parameter in the scene file
									 *--------------------------------------------*/

									File dataPBRTFile = new File(image
											.getAbsolutePath().replaceAll(
													".pfm$", ".pbrt"));

									if (!dataPBRTFile.exists()) {
										System.err
												.format("missing .pbrt scene file for %s!\n",
														dataPBRTFile
																.getAbsolutePath());
										continue;
									}

									statistics.add(mse);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							/*------------------------------------------------------------------
							 * Add the statistics
							 *----------------------------------------------------------------*/

							lock.lock();

							try {
								TreeMap<Double, Statistics> largestepData = data
										.get(sigma);

								if (largestepData == null) {
									largestepData = new TreeMap<Double, Statistics>();
									data.put(sigma, largestepData);
								}

								largestepData.put(largestep, statistics);

								System.out.format("%.10f %.10f : %.16f\n",
										sigma, largestep,
										statistics.getAverage());
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								lock.unlock();
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.err.flush();
						}
					};
				};
				service.submit(thread);
			}
		}

		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/***********************************************************************
		 * 
		 */

		TreeMap<Double, TreeMap<Double, Statistics>> collection = data;

//		for (int i = 0; i < 1; ++i)
//			collection = subsample_uncorrelated(collection);

		/***********************************************************************
		 * Write the data and find the optimal settings
		 **********************************************************************/

		double bestAverageSigma = Double.NaN;
		double bestAverageLargeStep = Double.NaN;
		double bestAverage = Double.POSITIVE_INFINITY;
		double minimumAverage = Double.POSITIVE_INFINITY;
		double maximumAverage = Double.NEGATIVE_INFINITY;
		double bestMedianSigma = Double.NaN;
		double bestMedianLargeStep = Double.NaN;
		double bestMedian = Double.POSITIVE_INFINITY;
		double minimumMedian = Double.POSITIVE_INFINITY;
		double maximumMedian = Double.NEGATIVE_INFINITY;

		String outputDirectoryName = String.format("%s/%s",
				getStringSetting("output"), sceneName);
		File outputDirectory = new File(outputDirectoryName);
		FileUtil.mkdirs(outputDirectory);
		File file = new File(outputDirectory, sceneName + ".txt");

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			writer.write("sigma largestep average median std variance size\n");

			for (Entry<Double, TreeMap<Double, Statistics>> e1 : collection
					.entrySet()) {
				for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
					double sigma = e1.getKey();
					double largeStep = e2.getKey();
					Statistics statistic = e2.getValue();
					double average = statistic.getAverage();
					double median = statistic.getMedian();
					double std = statistic.getStandardDeviation();
					double variance = statistic.getVariance();
					int size = statistic.size();

					writer.write(String.format(
							"%.16f %.16f %.16f %.16f %.16f %.16f %d\n", sigma,
							largeStep, average, median, std, variance, size));

					// keep track of the best average setting
					if (average < bestAverage) {
						bestAverage = average;
						bestAverageSigma = sigma;
						bestAverageLargeStep = largeStep;
					}

					if (average < minimumAverage)
						minimumAverage = average;
					if (average > maximumAverage)
						maximumAverage = average;

					// keep track of the best median setting
					if (median < bestMedian) {
						bestMedian = median;
						bestMedianSigma = sigma;
						bestMedianLargeStep = largeStep;
					}
					if (median < minimumMedian)
						minimumMedian = median;
					if (median > maximumMedian)
						maximumMedian = median;

				}
				writer.write("\n");
			}
		}

		/***********************************************************************
		 * Write the results
		 **********************************************************************/

		StringBuilder builder = new StringBuilder(String.format(
				"[ PSSMLT Analysis %s ] \n", sceneName));
		builder.append("  Optimal settings (based on average)\n");
		builder.append(String.format("    %-40s %.10f\n", "sigma:",
				bestAverageSigma));
		builder.append(String.format("    %-40s %.10f\n", "largestep:",
				bestAverageLargeStep));

		Statistics bestAverageStatistics = collection.get(bestAverageSigma)
				.get(bestAverageLargeStep);

		builder.append(String.format("    %-40s %.10f\n", "average mse:",
				bestAverageStatistics.getAverage()));
		builder.append(String.format("    %-40s %.10f\n", "median mse:",
				bestAverageStatistics.getMedian()));
		builder.append(String.format("    %-40s %.10f\n",
				"standard deviation of mse:",
				bestAverageStatistics.getStandardDeviation()));
		builder.append(String.format("    %-40s %.10f\n", "variance of mse:",
				bestAverageStatistics.getVariance()));
		builder.append(String.format("    %-40s %.10f\n", "minimum mse:",
				bestAverageStatistics.getMinimum()));
		builder.append(String.format("    %-40s %.10f\n", "maximum mse:",
				bestAverageStatistics.getMaximum()));
		builder.append(String.format("    %-40s %d\n", "statistic size:",
				bestAverageStatistics.size()));

		builder.append("  Optimal settings (based on median)\n");
		builder.append(String.format("    %-40s %.10f\n", "sigma:",
				bestMedianSigma));
		builder.append(String.format("    %-40s %.10f\n", "largestep:",
				bestMedianLargeStep));

		Statistics bestMedianStatistics = collection.get(bestMedianSigma).get(
				bestMedianLargeStep);

		builder.append(String.format("    %-40s %.10f\n", "average mse:",
				bestMedianStatistics.getAverage()));
		builder.append(String.format("    %-40s %.10f\n", "median mse:",
				bestMedianStatistics.getMedian()));
		builder.append(String.format("    %-40s %.10f\n",
				"standard deviation of mse:",
				bestMedianStatistics.getStandardDeviation()));
		builder.append(String.format("    %-40s %.10f\n", "variance of mse:",
				bestMedianStatistics.getVariance()));
		builder.append(String.format("    %-40s %.10f\n", "minimum mse:",
				bestMedianStatistics.getMinimum()));
		builder.append(String.format("    %-40s %.10f\n", "maximum mse:",
				bestMedianStatistics.getMaximum()));
		builder.append(String.format("    %-40s %d\n", "statistic size:",
				bestMedianStatistics.size()));

		System.out.println(builder);

		File results = new File(outputDirectory, sceneName + "-results.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(results.toPath())) {
			writer.write(builder.toString());
		}

		/***********************************************************************
		 * Generate tikz plot data
		 **********************************************************************/

		System.out.println(outputDirectory.getAbsolutePath());
		File tex = new File(outputDirectory, sceneName + ".tex");
		try (BufferedWriter writer = Files.newBufferedWriter(tex.toPath())) {
			writer.write("\\documentclass{standalone}\n");
			writer.write("\\usepackage{tikz}\n");
			writer.write("\\usepackage{pgfplots}\n");
			writer.write("\\begin{document}\n");
			writer.write("\t\\begin{tikzpicture}\n");
			writer.write("\t\t\\begin{axis}[view={-20}{20}, grid=both]\n");
			writer.write("\t\t\t\\addplot3[surf] table[x=sigma, y=largestep, z=average] {"
					+ sceneName + ".txt};\n");
			writer.write("\t\t\\end{axis}\n");
			writer.write("\t\\end{tikzpicture}\n");
			writer.write("\\end{document}\n");
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public TreeMap<Double, TreeMap<Double, Statistics>> subsample_correlated(
			TreeMap<Double, TreeMap<Double, Statistics>> data) {
		int nx = data.size();
		int ny = data.firstEntry().getValue().size();

		double[] x = new double[nx];
		double[] y = new double[ny];
		Statistics[][] statistics = new Statistics[nx][ny];

		int i = 0;
		for (Entry<Double, TreeMap<Double, Statistics>> e1 : data.entrySet()) {
			x[i] = e1.getKey();

			int j = 0;
			for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
				if (i == 0) {
					y[j] = e2.getKey();
				} else if (y[j] != e2.getKey()) {
					throw new IllegalStateException("data mismatch!");
				}
				statistics[i][j] = e2.getValue();
				++j;
			}

			++i;
		}

		TreeMap<Double, TreeMap<Double, Statistics>> result = new TreeMap<Double, TreeMap<Double, Statistics>>();

		for (int k = 0; k < nx - 1; ++k) {
			TreeMap<Double, Statistics> c = new TreeMap<Double, Statistics>();
			double xx = (x[k] + x[k + 1]) * 0.5;

			for (int l = 0; l < ny - 1; ++l) {
				double yy = (y[l] + y[l + 1]) * 0.5;

				Statistics average = new Statistics();
				average.add(statistics[k][l]);
				average.add(statistics[k + 1][l]);
				average.add(statistics[k][l + 1]);
				average.add(statistics[k + 1][l + 1]);

				c.put(yy, average);

			}

			result.put(xx, c);
		}

		return result;

	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public TreeMap<Double, TreeMap<Double, Statistics>> subsample_uncorrelated(
			TreeMap<Double, TreeMap<Double, Statistics>> data) {
		int nx = data.size();
		int ny = data.firstEntry().getValue().size();

		double[] x = new double[nx];
		double[] y = new double[ny];
		Statistics[][] statistics = new Statistics[nx][ny];

		int i = 0;
		for (Entry<Double, TreeMap<Double, Statistics>> e1 : data.entrySet()) {
			x[i] = e1.getKey();

			int j = 0;
			for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
				if (i == 0) {
					y[j] = e2.getKey();
				} else if (y[j] != e2.getKey()) {
					throw new IllegalStateException("data mismatch!");
				}
				statistics[i][j] = e2.getValue();
				++j;
			}

			++i;
		}

		TreeMap<Double, TreeMap<Double, Statistics>> result = new TreeMap<Double, TreeMap<Double, Statistics>>();

		for (int k = 0; k < nx - 1; k += 2) {
			TreeMap<Double, Statistics> c = new TreeMap<Double, Statistics>();
			double xx = (x[k] + x[k + 1]) * 0.5;

			for (int l = 0; l < ny - 1; l += 2) {
				double yy = (y[l] + y[l + 1]) * 0.5;

				Statistics average = new Statistics();
				average.add(statistics[k][l]);
				average.add(statistics[k + 1][l]);
				average.add(statistics[k][l + 1]);
				average.add(statistics[k + 1][l + 1]);

				c.put(yy, average);

			}

			result.put(xx, c);
		}

		return result;

	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public TreeMap<Double, TreeMap<Double, Statistics>>
			subsample_uncorrelated3(
					TreeMap<Double, TreeMap<Double, Statistics>> data) {
		int nx = data.size();
		int ny = data.firstEntry().getValue().size();

		double[] x = new double[nx];
		double[] y = new double[ny];
		Statistics[][] statistics = new Statistics[nx][ny];

		int i = 0;
		for (Entry<Double, TreeMap<Double, Statistics>> e1 : data.entrySet()) {
			x[i] = e1.getKey();

			int j = 0;
			for (Entry<Double, Statistics> e2 : e1.getValue().entrySet()) {
				if (i == 0) {
					y[j] = e2.getKey();
				} else if (y[j] != e2.getKey()) {
					throw new IllegalStateException("data mismatch!");
				}
				statistics[i][j] = e2.getValue();
				++j;
			}

			++i;
		}

		TreeMap<Double, TreeMap<Double, Statistics>> result = new TreeMap<Double, TreeMap<Double, Statistics>>();

		for (int k = 0; k < nx - 2; k += 3) {
			TreeMap<Double, Statistics> c = new TreeMap<Double, Statistics>();
			double xx = x[k + 1];

			for (int l = 0; l < ny - 2; l += 3) {
				double yy = y[l + 1];

				Statistics average = new Statistics();
				average.add(statistics[k][l]);
				average.add(statistics[k + 1][l]);
				average.add(statistics[k + 2][l]);
				average.add(statistics[k][l + 1]);
				average.add(statistics[k + 1][l + 2]);
				average.add(statistics[k + 2][l + 2]);
				average.add(statistics[k][l + 2]);
				average.add(statistics[k + 1][l + 2]);
				average.add(statistics[k + 2][l + 2]);

				c.put(yy, average);

			}

			result.put(xx, c);
		}

		return result;

	}
}
