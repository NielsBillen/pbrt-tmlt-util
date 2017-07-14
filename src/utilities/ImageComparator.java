package utilities;

import java.io.File;
import java.io.FilenameFilter;

import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;
import pfm.PFMWriter;
import util.FileUtil;
import util.Printer;
import util.Statistics;
import cli.CommandLineAdapter;
import cli.CommandLineArguments;

/**
 * --reference /home/niels/renderdata/reference/mirror-ring/mirror-ring-pssmlt-mutations-1048576-maxdepth-8-sigma-0.01-step-0.3-seed-0-0.pfm /home/niels/workspace/pbrt-tmlt/results/mirror-ring/mirror-ring-tpssmlt.pfm /home/niels/renderdata/pssmlt/mirror-ring/sigma-0.01/largestep-0.09
 * 
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class ImageComparator extends CommandLineAdapter {
	/**
	 * 
	 * @throws NullPointerException
	 */
	public ImageComparator() throws NullPointerException {
		super("imagecomparator",
				"--directory <directory> --reference <image data...>");

		File home = new File(System.getProperty("user.home"));

		addStringSetting("directory", "Relative directory to look for files",
				new File(".").getAbsoluteFile().getParent());
		addStringSetting("reference", "Filename of the reference image.",
				new File(home, "renderdata").getAbsolutePath());

		/*
		 * --reference
		 * /home/niels/renderdata/reference-highquality/mirror-ring/mirror
		 * -ring-pssmlt
		 * -mutations-1048576-maxdepth-8-sigma-0.01-step-0.3-seed-0-0.pfm
		 * /home/niels
		 * /workspace/pbrt-tmlt/results/mirror-ring/mirror-ring-pssmlt.pfm
		 * /home/
		 * niels/workspace/pbrt-tmlt/results/mirror-ring/mirror-ring-tpssmlt.pfm
		 */
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new ImageComparator().parse(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(CommandLineArguments arguments) {
		new ImageComparator().parse(arguments);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineAdapter#handleArgument(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		System.out.println(argument);
		try {
			final String directoryFilename = getStringSetting("directory");
			final String referenceFilename = getStringSetting("reference");
			final File referenceFile = FileUtil.get(directoryFilename,
					referenceFilename);
			final PFMImage reference = PFMUtil.normalizeByAverage(PFMReader
					.read(referenceFile));
			final File file = FileUtil.get(directoryFilename, argument);

			final String filename = file.getName();

			System.out.format("[ %s ]\n", filename);

			if (filename.endsWith(".pfm")) {
				final PFMImage image = PFMUtil.normalizeByAverage(PFMReader
						.read(file));
				final double mse = PFMUtil.getMSE(image, reference, 2.2);

				System.out.format("  %-20s: %.16f\n", "mean squared error:",mse);

				final String extensionless = filename.replaceAll("\\.pfm$", "");
				final PFMImage mseImage = PFMUtil.getMSEImage(image, reference,
						2.2);

				PFMWriter.write(extensionless + "-difference.pfm", mseImage);
				mseImage.write(extensionless + "-difference.png", 1.0);
			} else if (file.isDirectory()) {
				final Statistics statistics = new Statistics();

				final File[] images = file.listFiles(new FilenameFilter() {
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

				int index = 0;
				double[] mses = new double[images.length];
				for (File imageFile : images) {
					final PFMImage image = PFMUtil.normalizeByAverage(PFMReader
							.read(imageFile));
					double mse = PFMUtil.getMSE(image, reference, 2.2);
					
					mses[index++] = mse;
					statistics.add(mse);
				}
				
				for(int i = 0; i < mses.length; ++i)
					System.out.format("%s ",Printer.print(mses[i]));
	

				System.out.println(statistics);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
