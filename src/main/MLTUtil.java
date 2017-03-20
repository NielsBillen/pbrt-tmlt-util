package main;

import java.io.IOException;

import utilities.BibTexCleaner;
import utilities.PBRTCleaner;

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
		super("mltutil");

		addAction("clean-pbrt", "Starts the utility for cleaning scene files.");
		addAction("clean-bibtex",
				"Starts the utility for cleaning bibtex files.");
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

		// // // RemoteReferenceRenderer.main(args);
		// if (args.length == 0) {
		// System.out.println("usage: tmlt-util");
		// System.out
		// .println("  -clean [<options>] <filename ...>       cleans "
		// + "the formatting of given pbrt scene file.");
		// System.out
		// .println("  -reference [<options>] <filename ... >  generates "
		// + "the reference images for the specified files");
		// System.out
		// .println("  -pssmltsettings [<options>] <filename ... >  generates "
		// + "the reference images for the specified files");
		// }
		//
		// final LinkedList<String> argumentList = new LinkedList<String>();
		// for (String argument : args)
		// argumentList.add(argument);
		//
		// while (!argumentList.isEmpty()) {
		// String current = argumentList.poll();
		//
		// if (current.equals("-clean") || current.equals("--clean"))
		// PBRTCleaner.clean(argumentList);
		// else if (current.equals("-bibtexclean")
		// || current.equals("--bibtexclean"))
		// BibTexCleaner.clean(argumentList);
		// else if (current.equals("-reference")
		// || current.equals("--reference"))
		// ReferenceRenderer.generateReferences(argumentList);
		// else if (current.equals("-pssmltsettings")
		// || current.equals("--pssmltsettings"))
		// PSSMLTSettingsFinder.generateData(argumentList);
		// else if (current.equals("-pssmltanalysis")
		// || current.equals("--pssmltanalysis"))
		// PSSMLTAnalysis.analyze(argumentList);
		// }
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
		if (token.equals("clean-pbrt")) {
			PBRTCleaner.clean(arguments);

			BibTexCleaner.clean(arguments);
		} else if (token.equals("clean-bibtex")) {
			BibTexCleaner.clean(arguments);
		}
	}
}
