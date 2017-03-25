package main;

import java.io.IOException;

import utilities.BibTexCleaner;
import utilities.PBRTCleaner;
import utilities.PSSMLTAnalysis;
import utilities.PSSMLTSettingsFinder;
import utilities.RenderDataOrganizer;
import cli.CommandLineArguments;
import cli.CommandLineInterface;

/**
 * Entry point of the utility program.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class MLTUtil extends CommandLineInterface {
	/**
	 * 
	 */
	public MLTUtil() {
		super("mltutil", "--utilityname <options>");

		addAction("clean-pbrt", "Starts the utility for cleaning scene files.");
		addAction("clean-bibtex",
				"Starts the utility for cleaning bibtex files.");
		addAction("pssmltanalysis",
				"Starts the utility for finding the optimal pssmlt settings.");
		addAction("pssmltsettings",
				"Executes the experiments to generate the data for"
						+ " finding the optimal pssmlt settings.");
	}

	/**
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) {
		MLTUtil util = new MLTUtil();
		util.parse(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handle(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		throw new IllegalStateException("MLTUtil does not accept arguments!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#parsed()
	 */
	@Override
	public void finished() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleToken(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleAction(String token, CommandLineArguments arguments) {
		if (token.equals("clean-pbrt"))
			PBRTCleaner.main(arguments);
		else if (token.equals("clean-bibtex"))
			BibTexCleaner.main(arguments);
		else if (token.equals("pssmltanalysis"))
			PSSMLTAnalysis.main(arguments);
		else if (token.equals("pssmltsettings"))
			PSSMLTSettingsFinder.main(arguments);
		else if (token.equals("pssmltorganizer"))
			RenderDataOrganizer.
	}
}
