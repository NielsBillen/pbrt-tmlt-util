package main;

import util.Settings;
import core.PSSMLTJob;

/**
 * Entry point of the utility program.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class MLTUtil {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String pbrt = "/home/niels/workspace/pbrt-tmlt/pbrt";
		String output = "/home/niels/workspace/pbrt-tmlt/output";

		PSSMLTJob kitchen = new PSSMLTJob(
				"/home/niels/workspace/pbrt-tmlt/scenes/kitchen",
				Settings.xResolution, Settings.yResolution,
				Settings.referenceSamples, 0.01, 0.3);
		kitchen.execute(pbrt, output, 0, 0);

		PSSMLTJob mirrorBalls = new PSSMLTJob(
				"/home/niels/workspace/pbrt-tmlt/scenes/mirror-balls",
				Settings.xResolution, Settings.yResolution,
				Settings.referenceSamples, 0.01, 0.3);
		mirrorBalls.execute(pbrt, output, 0, 0);

		PSSMLTJob mirrorRing = new PSSMLTJob(
				"/home/niels/workspace/pbrt-tmlt/scenes/mirror-ring",
				Settings.xResolution, Settings.yResolution,
				Settings.referenceSamples, 0.01, 0.3);
		mirrorRing.execute(pbrt, output, 0, 0);

		PSSMLTJob glass = new PSSMLTJob(
				"/home/niels/workspace/pbrt-tmlt/scenes/caustic-glass",
				Settings.xResolution, Settings.yResolution,
				Settings.referenceSamples, 0.01, 0.3);
		glass.execute(pbrt, output, 0, 0);

		// if (args.length == 0) {
		// System.out.println("usage: tmlt-util");
		// System.out
		// .println("  -clean [<options>] <filename ...>       cleans "
		// + "the formatting of given pbrt scene file.");
		// System.out
		// .println("  -reference [<options>] <filename ... >  generates "
		// + "the reference images for the specified files");
		// }
		// final LinkedList<String> argumentList = new LinkedList<String>();
		// for (String argument : args)
		// argumentList.add(argument);
		//
		// while (!argumentList.isEmpty()) {
		// String current = argumentList.poll();
		//
		// if (current.equals("-clean"))
		// PBRTCleaner.clean(argumentList);
		// }
	}
}
