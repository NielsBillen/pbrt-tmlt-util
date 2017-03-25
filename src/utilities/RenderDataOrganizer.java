package utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.FileUtil;
import util.Printer;
import cli.CommandLineAdapter;
import cli.CommandLineArguments;

/**
 * Properly organizes pssmlt render settings in subfolders with the appropriate
 * name.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class RenderDataOrganizer extends CommandLineAdapter {
	/**
	 * 
	 */
	private static final Pattern sigmaPattern = Pattern
			.compile("sigma\\-([0-9]*\\.[0-9]*)");

	/**
	 * 
	 */
	private static final Pattern largestepPattern = Pattern
			.compile("largestep\\-([0-9]*\\.[0-9]*)");

	/**
	 * 
	 */
	private static final Pattern seedPattern = Pattern
			.compile("seed\\-([0-9]*)");

	/**
	 * 
	 * @param command
	 * @param usage
	 * @throws NullPointerException
	 */
	public RenderDataOrganizer() throws NullPointerException {
		super("pssmltorganizer", "<folder>");
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new RenderDataOrganizer().parse(args);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(CommandLineArguments arguments) {
		new RenderDataOrganizer().parse(arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineAdapter#handleArgument(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		File directory = new File(argument);
		if (!directory.exists())
			throw new IllegalArgumentException(
					"the given directory does not exist!");
		if (!directory.isDirectory())
			throw new IllegalArgumentException(
					"the given file corresponding to the given argument \""
							+ argument + "\" exists but is not a directory!");

		String scene = directory.getName();

		File[] pfms = directory.listFiles(new FilenameFilter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.io.FilenameFilter#accept(java.io.File,
			 * java.lang.String)
			 */
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("sigma") && name.contains("largestep")
						&& name.contains("seed");
			}
		});

		int uniqueIdentifier = 0;
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		HashMap<String, Integer> total = new HashMap<String, Integer>();

		// Count for each setting the number of occurrences
		for (File pfm : pfms) {
			String filename = pfm.getName().replaceAll("\\.[a-z]+$", "");

			Matcher sigmaMatcher = sigmaPattern.matcher(filename);
			if (!sigmaMatcher.find()) {
				System.out.format("could not find sigma in filename \"%s\"",
						filename);
				continue;
			}

			Matcher largestepMatcher = largestepPattern.matcher(filename);
			if (!largestepMatcher.find()) {
				System.out.format(
						"could not find large step in filename \"%s\"",
						filename);
				continue;
			}
			Matcher seedMatcher = seedPattern.matcher(filename);
			if (!seedMatcher.find()) {
				System.out.format("could not find seed in filename \"%s\"",
						filename);
				continue;
			}

			double sigma = Double.parseDouble(sigmaMatcher.group(1));
			double largestep = Double.parseDouble(largestepMatcher.group(1));

			String sigmaDirectoryName = String.format("sigma-%s",
					Printer.print(sigma));
			String largestepDirectoryName = String.format("largestep-%s",
					Printer.print(largestep));
			String key = sigmaDirectoryName + "/" + largestepDirectoryName;

			Integer occurences = total.get(key);
			if (occurences == null)
				total.put(key, 1);
			else
				total.put(key, occurences + 1);
		}

		// Count for each setting the number of occurrences
		for (File pfm : pfms) {
			String filename = pfm.getName().replaceAll("\\.[a-z]+$", "");

			Matcher sigmaMatcher = sigmaPattern.matcher(filename);
			if (!sigmaMatcher.find()) {
				System.out.format("could not find sigma in filename \"%s\"",
						filename);
				continue;
			}

			Matcher largestepMatcher = largestepPattern.matcher(filename);
			if (!largestepMatcher.find()) {
				System.out.format(
						"could not find large step in filename \"%s\"",
						filename);
				continue;
			}

			Matcher seedMatcher = seedPattern.matcher(filename);
			if (!seedMatcher.find()) {
				System.out.format("could not find seed in filename \"%s\"",
						filename);
				continue;
			}

			double sigma = Double.parseDouble(sigmaMatcher.group(1));
			double largestep = Double.parseDouble(largestepMatcher.group(1));
			long seed = Long.parseLong(seedMatcher.group(1));

			String sigmaDirectoryName = String.format("sigma-%s",
					Printer.print(sigma));
			String largestepDirectoryName = String.format("largestep-%s",
					Printer.print(largestep));
			String key = sigmaDirectoryName + "/" + largestepDirectoryName;

			File pfmDirectory = new File(directory, key);
			if (!FileUtil.mkdirs(pfmDirectory)) {
				System.err
						.println("could not create the directory to store the files in!");
				continue;
			}

			Integer totalOccurences = total.get(key);
			if (totalOccurences == null)
				throw new IllegalStateException("total occurences cannot be 0!");
			Integer id = counter.get(key);
			if (id == null) {
				id = 1;
				counter.put(key, id + 1);
			} else {
				counter.put(key, id + 1);
			}

			String renamed = String.format("%s-pssmlt-%d-%d-seed-%d-id-%d",
					scene, id, totalOccurences, seed, uniqueIdentifier++);

			// perform moves
			String[] extensions = new String[] { "pfm", "exr", "txt", "pbrt",
					"png" };
			for (String extension : extensions) {
				File orignalFile = new File(directory, filename + "."
						+ extension);
				File renamedFile = new File(pfmDirectory, renamed + "."
						+ extension);

				if (!orignalFile.exists())
					continue;
				if (renamedFile.exists()) {
					throw new IllegalStateException(
							"was about to overwrite file!");
				}


				if (!orignalFile.renameTo(renamedFile))
					System.err.format("failed to move %s to %s...\n",
							orignalFile.getName(), renamedFile.getName());
			}

		}

//		System.out.println(total);
	}
}
