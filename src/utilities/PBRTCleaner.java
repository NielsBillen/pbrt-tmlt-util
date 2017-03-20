package utilities;

import java.io.IOException;

import pbrt.PBRTParser;
import pbrt.PBRTScene;
import cli.CommandLineArguments;
import cli.CommandLineInterface;

/**
 * A utility which can cleanup the formatting of a .pbrt scene file.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PBRTCleaner extends CommandLineInterface {
	/**
	 * 
	 */
	public PBRTCleaner() {
		super("clean-pbrt");

		addStringSetting("output", "Destination to write the cleaned file to",
				"cleaned.pbrt");
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new PBRTCleaner().parse(args);

	}

	/**
	 * 
	 * @param arguments
	 */
	public static void clean(CommandLineArguments arguments) {
		new PBRTCleaner().parse(arguments);
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

	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		try {
			PBRTScene scene = PBRTParser.parse(argument);
			scene.print(getStringSetting("output"));
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
}
