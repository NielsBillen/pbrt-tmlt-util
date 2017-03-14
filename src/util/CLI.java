package util;

import java.util.LinkedList;

/**
 * Utility functions for implementing a command line interface.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class CLI {
	/**
	 * 
	 * @param command
	 * @return
	 */
	public static LinkedList<String> commandToList(String command) {
		LinkedList<String> result = new LinkedList<String>();
		for (String c : command.split(" +"))
			result.add(c);
		return result;
	}

	/**
	 * 
	 * @param commands
	 * @return
	 */
	public static String commandToString(LinkedList<String> commands) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String command : commands) {
			builder.append(command);

			if (i++ < commands.size() - 1)
				builder.append(" ");
		}

		return builder.toString();
	}

	/**
	 * 
	 * @param name
	 * @param arguments
	 * @return
	 */
	public static String nextString(String name, LinkedList<String> arguments) {
		if (name == null)
			throw new NullPointerException(
					"the name of the integer setting is null!");
		if (arguments == null)
			throw new NullPointerException(
					"the given list of arguments is null!");
		if (arguments.isEmpty())
			throw new IllegalArgumentException("missing integer after \""
					+ name + "\"");

		String token = arguments.removeFirst();
		if (token == null)
			throw new NullPointerException(
					"argument \"null\" encountered after \"" + name + "\"");
		return token;
	}

	/**
	 * 
	 * @param name
	 * @param arguments
	 * @return
	 */
	public static int nextInteger(String name, LinkedList<String> arguments) {
		String token = nextString(name, arguments);

		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"could not parse an integer number from \"" + token
							+ "\" for the integer setting \"" + name + "\"");
		}
	}

	/**
	 * 
	 * @param name
	 * @param arguments
	 * @return
	 */
	public static double nextDouble(String name, LinkedList<String> arguments) {
		String token = nextString(name, arguments);

		try {
			return Double.parseDouble(token);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"could not parse a double number from \"" + token
							+ "\" for the double setting \"" + name + "\"");
		}
	}
}
