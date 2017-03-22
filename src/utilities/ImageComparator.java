package utilities;

import java.io.File;
import java.io.FilenameFilter;

import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;
import util.Statistics;
import cli.CommandLineAdapter;
import cli.CommandLineArguments;

/**
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
		super("imagecomparator", "--reference <image data...>");

		File home = new File(System.getProperty("user.home"));

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
			PFMImage reference = PFMReader.read(getStringSetting("reference"));

			File file = new File(argument);
			String filename = file.getName();
			System.out.format("[ %s ]\n", filename);

			if (filename.endsWith(".pfm")) {
				PFMImage image = PFMReader.read(file);
				double mse = PFMUtil.MSE(image, reference);
				System.out.format("mse: %.16f\n", mse);
			} else if (file.isDirectory()) {
				Statistics statistics = new Statistics();

				File[] images = file.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".pfm");
					}
				});

				for (File imageFile : images) {
					PFMImage image = PFMReader.read(imageFile);
					double mse = PFMUtil.MSE(image, reference);
					statistics.add(mse);
				}

				System.out.println(statistics);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
