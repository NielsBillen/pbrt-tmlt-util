package utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import util.CLI;
import util.Settings;
import core.PSSMLTJob;

public class PSSMLTSettingsFinder {
	/**
	 * 
	 * @param arguments
	 */
	public static void generateData(LinkedList<String> arguments) {
		int xresolution = Settings.xResolution;
		int yresolution = Settings.yResolution;
		int repetitions = 5;
		int samples = 1024;
		int maxDepth = 8;
		String pbrt = "pbrt";
		String output = "output/references";
		String directory = "";

		List<PSSMLTJob> renders = new ArrayList<PSSMLTJob>();
		while (!arguments.isEmpty()) {
			String token = arguments.removeFirst();

			if (token.equals("-help") || token.equals("--help")) {
				System.out
						.println("usage: -pssmltsettings [<options>] <scene directories>");
				System.out.println("  -help                     prints "
						+ "the manual for this command");
				System.out.println("  -dir <filename>           specifies the "
						+ "working directory. All other files will be "
						+ "found relative to this directory.");
				System.out.println("  -xresolution <integer>    horizontal "
						+ "resolution of the reference image (default 128).");
				System.out.println("  -yresolution <integer>    vertical r"
						+ "esolution of the reference image (default 128).");
				System.out.println("  -samples <integer>        number of "
						+ "samples for the reference image (default 1024).");
				System.out.println("  -sigma <double>           small step "
						+ "mutation size (default 0.01).");
				System.out.println("  -largestep <double>       large step "
						+ "probability (default 0.3).");
				System.out.println("  -pbrt <filename>          filename of "
						+ "the pbrt executable (default pbrt)");
				System.out.println("  -maxDepth <integer>       maximum "
						+ "recursion depth (default 8)");
				System.out.println("  -output <filename>        output "
						+ "directory to write the results (default "
						+ "output/pssmltsettings)");
				System.out.println();
				System.out.println("example:");
				System.out.println();
				System.out.println("  java -jar pbrt-tmlt-util.jar "
						+ "-reference "
						+ "-dir /home/niels/workspace/pbrt-tmlt "
						+ "-pbrt pbrt " + "-output output/reference "
						+ "-xresolution 240 " + "scenes/kitchen "
						+ "scenes/mirror-balls " + "-xresolution 128 "
						+ "scenes/mirror-ring "
						+ "scenes/caustic-glass -maxDepth 100"
						+ "scenes/volume-caustic");
				return;
			} else if (token.equals("-xresolution")
					|| token.equals("--xresolution"))
				xresolution = CLI.nextInteger(token, arguments);
			else if (token.equals("-yresolution")
					|| token.equals("--yresolution"))
				yresolution = CLI.nextInteger(token, arguments);
			else if (token.equals("-samples") || token.equals("--samples"))
				samples = CLI.nextInteger(token, arguments);
			else if (token.equals("-pbrt") || token.equals("--pbrt"))
				pbrt = CLI.nextString(token, arguments);
			else if (token.equals("-output") || token.equals("--output"))
				output = CLI.nextString(token, arguments);
			else if (token.equals("-maxDepth") || token.equals("--maxDepth"))
				maxDepth = CLI.nextInteger(token, arguments);
			else if (token.equals("-dir") || token.equals("--dir")) {
				String d = CLI.nextString(token, arguments);
				if (d.endsWith("/"))
					directory = d;
				else
					directory = d + "/";
			} else {
				for (double sigma = 0.04; sigma <= 1.0; sigma += 0.04) {
					for (double largeStep = 0.04; largeStep <= 1.0; largeStep += 0.04) {
						PSSMLTJob job = new PSSMLTJob(directory + token,
								xresolution, yresolution, samples, sigma,
								largeStep, maxDepth);
						renders.add(job);
					}
				}
			}
		}

		System.out.println("pssmlt settings finder:");
		System.out.format("  found %d render jobs ... \n", renders.size()
				* repetitions);
		// for (PSSMLTJob render : renders)
		// System.out.format("  %s ...\n", render.getOutputFilename()
		// + ".pbrt");

		Random random = new Random(0);
		for (int i = 0; i < repetitions; ++i) {
			for (PSSMLTJob render : renders) {
				String outputFilename = render.getOutputFilename();

				System.out.format("started rendering \"%s-%d\" ...\n",
						outputFilename, i);

				long startTime = System.currentTimeMillis();
				render.execute(directory + pbrt, directory + output, i,
						Math.abs(random.nextLong()));
				long duration = System.currentTimeMillis() - startTime;
				System.out.format("finished rendering \"%s-%d\" in %.1fs!\n",
						outputFilename, i, duration * 0.001);
			}
		}
	}
}
