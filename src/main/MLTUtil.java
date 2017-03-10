package main;

import java.util.LinkedList;

import utilities.PBRTCleaner;

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
		LinkedList<String> argumentList = new LinkedList<String>();
		for (String argument : args)
			argumentList.add(argument);

		while (!argumentList.isEmpty()) {
			String current = argumentList.poll();

			if (current.equals("-clean")) {
				PBRTCleaner.clean(argumentList);
			}
		}
	}
}
