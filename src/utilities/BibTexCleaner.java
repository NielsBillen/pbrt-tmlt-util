package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import bibtex.BibTexEntry;
import bibtex.BibTexParser;
import cli.CommandLineArguments;
import cli.CommandLineInterface;

/**
 * A utility which can cleanup the formatting of a .pbrt scene file.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexCleaner extends CommandLineInterface {
	/**
	 * 
	 */
	public BibTexCleaner() {
		super("clean-bibtex", "[<options>] <filename.bibtex...>");

		addStringSetting("output", "Destination to write the cleaned file to",
				"cleaned.bib");
		addExample("clean-bibtex --output output.bib input.bib");
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void main(String[] arguments) {
		new BibTexCleaner().parse(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void clean(CommandLineArguments arguments) {
		new BibTexCleaner().parse(arguments);
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
			String cleaned = clean(argument);
			File outputFile = new File(getStringSetting("output"));
			Path outputPath = outputFile.toPath();

			try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
				writer.write(cleaned);
			}
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
	 * Cleans the .pbrt file specified by the given filename.
	 * 
	 * @param filename
	 *            the filename of the .pbrt file to clean.
	 * @return a string containing the cleaned .pbrt file.
	 */
	public static String clean(String filename) throws IOException {
		return clean(new File(filename));
	}

	/**
	 * Cleans the .pbrt file specified by the given file.
	 * 
	 * @param file
	 *            the .pbrt file to clean.
	 * @return a string containing the cleaned .pbrt file.
	 */
	public static String clean(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		List<BibTexEntry> entries = BibTexParser.parse(file);

		for (int i = 0; i < entries.size(); ++i) {
			BibTexEntry entry = entries.get(i);
			builder.append(entry);

			if (i < entries.size() - 1)
				builder.append("\n\n");
		}

		return builder.toString();
	}
}
