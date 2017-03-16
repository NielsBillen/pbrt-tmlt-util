package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import bibtex.BibTexEntry;
import bibtex.BibTexParser;

/**
 * A utility which can cleanup the formatting of a .pbrt scene file.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexCleaner {
	/**
	 * Cleans the .pbrt file specified by the given filename.
	 * 
	 * @param filename
	 *            the filename of the .pbrt file to clean.
	 * @return a string containing the cleaned .pbrt file.
	 */
	public static String clean(String filename) {
		return clean(new File(filename));
	}

	/**
	 * Cleans the .pbrt file specified by the given file.
	 * 
	 * @param file
	 *            the .pbrt file to clean.
	 * @return a string containing the cleaned .pbrt file.
	 */
	public static String clean(File file) {
		StringBuilder builder = new StringBuilder();
		try {
			List<BibTexEntry> entries = BibTexParser.parse(file);

			for (int i = 0; i < entries.size(); ++i) {
				BibTexEntry entry = entries.get(i);
				builder.append(entry);

				if (i < entries.size() - 1)
					builder.append("\n\n");
			}
		} catch (Exception e) {
		}
		return builder.toString();
	}

	/**
	 * Provides a command-line interface for cleaning multiple .pbrt scene
	 * files.
	 * 
	 * @param arguments
	 *            the arguments from the command-line interface.
	 * @throws NullPointerException
	 *             when the given list of arguments is null.
	 * @throws NullPointerException
	 *             when one of the commands is null.
	 */
	public static void clean(LinkedList<String> arguments)
			throws NullPointerException {
		if (arguments == null)
			throw new NullPointerException(
					"the given list of arguments is null!");

		while (!arguments.isEmpty()) {
			String argument = arguments.getFirst();

			if (argument == null)
				throw new NullPointerException(
						"one of the command line arguments is null!");
			else if (argument.equals("-help")) {
				System.out
						.println("usage: -bibtexclean <filename> -output <filename>");
				System.out
						.println("  -help                 prints the manual for this command");
				System.out
						.println("  -output <filename>    where the output should be written to.");
				System.out
						.println("                        default is System.out");
				arguments.removeFirst();
			} else if (argument.startsWith("-")) {
				break;
			} else {
				String cleaned = clean(argument);
				arguments.removeFirst();

				// Check whether an output is defined
				if (!arguments.isEmpty()
						&& arguments.getFirst().equals("-output")) {
					arguments.removeFirst();

					File file = new File(arguments.removeFirst());
					Path path = file.toPath();
					try (BufferedWriter writer = Files.newBufferedWriter(path)) {
						writer.write(cleaned);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					System.out.println(cleaned);
			}
		}
	}
}
