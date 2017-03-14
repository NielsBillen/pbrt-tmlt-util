package main;

import java.util.LinkedList;

import utilities.PBRTCleaner;
import utilities.PSSMLTSettingsFinder;
import utilities.ReferenceRenderer;

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
		if (args.length == 0) {
			System.out.println("usage: tmlt-util");
			System.out
					.println("  -clean [<options>] <filename ...>       cleans "
							+ "the formatting of given pbrt scene file.");
			System.out
					.println("  -reference [<options>] <filename ... >  generates "
							+ "the reference images for the specified files");
			System.out
					.println("  -pssmltsettings [<options>] <filename ... >  generates "
							+ "the reference images for the specified files");
		}

		final LinkedList<String> argumentList = new LinkedList<String>();
		for (String argument : args)
			argumentList.add(argument);

		while (!argumentList.isEmpty()) {
			String current = argumentList.poll();

			if (current.equals("-clean") || current.equals("--clean"))
				PBRTCleaner.clean(argumentList);
			else if (current.equals("-reference")
					|| current.equals("--reference"))
				ReferenceRenderer.generateReferences(argumentList);
			else if (current.equals("-pssmltsettings")
					|| current.equals("--pssmltsettings"))
				PSSMLTSettingsFinder.generateData(argumentList);
		}
	}
}
