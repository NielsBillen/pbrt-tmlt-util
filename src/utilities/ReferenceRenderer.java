package utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.CLI;
import util.Settings;
import core.PSSMLTJob;

/**
 * A utility which renders the reference renders of a scene.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class ReferenceRenderer {
	/**
	 * 
	 * @param arguments
	 */
	public static void generateReferences(LinkedList<String> arguments) {
		int xresolution = Settings.xResolution;
		int yresolution = Settings.yResolution;
		int samples = Settings.referenceSamples;
		int maxDepth = 8;
		double sigma = 0.01;
		double largeStep = 0.3;
		String pbrt = "pbrt";
		String output = "output/pssmltsettings";
		String directory = "";

		List<PSSMLTJob> renders = new ArrayList<PSSMLTJob>();
		while (!arguments.isEmpty()) {
			String token = arguments.removeFirst();

			if (token.equals("-help") || token.equals("--help")) {
				System.out
						.println("usage: -reference [<options>] <scene directories>");
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
						+ "samples for the reference image (default 1048576).");
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
				System.out.println("examples:");
				System.out.println();
				System.out.println("  [ larger references ]");
				System.out
						.println("  java -jar pbrt-tmlt-util.jar -reference -dir /home/niels/workspace/pbrt-tmlt -pbrt pbrt -output output/reference -xresolution 640 -yresolution 480 -maxDepth 8 scenes/mirror-balls scenes/kitchen -xresolution 480 scenes/mirror-ring scenes/caustic-glass -maxDepth 100 scenes/volume-caustic");
				System.out.println("  [ tiny references ]");
				System.out
						.println("  java -jar pbrt-tmlt-util.jar -reference -dir /home/niels/workspace/pbrt-tmlt -pbrt pbrt -output output/reference -xresolution 120 -yresolution 64 -maxDepth 8 scenes/mirror-balls scenes/kitchen -xresolution 64 scenes/mirror-ring scenes/caustic-glass -maxDepth 100 scenes/volume-caustic");

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
			else if (token.equals("-sigma") || token.equals("--sigma"))
				sigma = CLI.nextDouble(token, arguments);
			else if (token.equals("-largestep") || token.equals("--largestep"))
				largeStep = CLI.nextDouble(token, arguments);
			else if (token.equals("-maxDepth") || token.equals("--maxDepth"))
				maxDepth = CLI.nextInteger(token, arguments);
			else if (token.equals("-dir") || token.equals("--dir")) {
				String d = CLI.nextString(token, arguments);
				if (d.endsWith("/"))
					directory = d;
				else
					directory = d + "/";
			} else {
				PSSMLTJob job = new PSSMLTJob(directory + token, xresolution,
						yresolution, samples, sigma, largeStep, maxDepth);
				renders.add(job);
			}
		}

		System.out.println("reference:");
		System.out.format("  found %d render jobs ... \n", renders.size());
		for (PSSMLTJob render : renders)
			System.out.format("  %s ...\n", render.getOutputFilename()
					+ ".pbrt");

		for (PSSMLTJob render : renders) {
			System.out.format("started rendering \"%s\" ...\n",
					render.getOutputFilename());
			render.execute(directory + pbrt, directory + output, 0, 0);
		}
	}
}
