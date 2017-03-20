package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public abstract class CommandLineInterface {
	/**
	 * The command line name to call this command line interface.
	 */
	private final String command;

	/**
	 * 
	 */
	private final String usage;

	/**
	 * 
	 */
	private final TreeMap<String, String> actions = new TreeMap<String, String>();

	/**
	 * The list of integer settings.
	 */
	private final TreeMap<String, CommandLineSetting<Integer>> integers = new TreeMap<String, CommandLineSetting<Integer>>();

	/**
	 * The list of floating point settings.
	 */
	private final TreeMap<String, CommandLineSetting<Double>> doubles = new TreeMap<String, CommandLineSetting<Double>>();

	/**
	 * The list of string settings.
	 */
	private final TreeMap<String, CommandLineSetting<String>> strings = new TreeMap<String, CommandLineSetting<String>>();

	/**
	 * 
	 */
	private final List<String> examples = new ArrayList<String>();

	/**
	 * 
	 * @param command
	 * @throws NullPointerException
	 */
	public CommandLineInterface(String command, String usage)
			throws NullPointerException {
		if (command == null)
			throw new NullPointerException("the given command is null!");
		if (usage == null)
			throw new NullPointerException("the usage is null!");
		this.command = command;
		this.usage = usage;

		// put in the help command
		addAction("help", "Prints this help text.");
	}

	/**
	 * 
	 * @param example
	 */
	public void addExample(String example) {
		if (example == null)
			throw new NullPointerException("the given example is null!");
		examples.add(example);
	}

	/**
	 * 
	 * @param key
	 * @param description
	 */
	public void addAction(String key, String description) {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		if (description == null)
			throw new NullPointerException("the given description is null!");
		actions.put(key, description);
	}

	/**
	 * 
	 * @param key
	 * @param description
	 * @param defaultValue
	 */
	public void addIntegerSetting(String key, String description,
			int defaultValue) throws NullPointerException {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		integers.put(key, new CommandLineSetting<Integer>(description,
				defaultValue));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Integer getIntegerSetting(String key) {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		CommandLineSetting<Integer> result = integers.get(key);

		if (result == null)
			throw new IllegalArgumentException("the given key \"" + key
					+ "\" is not present!");
		return result.getValue();
	}

	/**
	 * 
	 * @param key
	 * @param description
	 * @param defaultValue
	 */
	public void addDoubleSetting(String key, String description,
			double defaultValue) throws NullPointerException {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		doubles.put(key, new CommandLineSetting<Double>(description,
				defaultValue));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Double getDoubleSetting(String key) {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		CommandLineSetting<Double> result = doubles.get(key);

		if (result == null)
			throw new IllegalArgumentException("the given key \"" + key
					+ "\" is not present!");
		return result.getValue();
	}

	/**
	 * 
	 * @param key
	 * @param description
	 * @param defaultValue
	 */
	public void addStringSetting(String key, String description,
			String defaultValue) throws NullPointerException {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		strings.put(key, new CommandLineSetting<String>(description,
				defaultValue));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getStringSetting(String key) {
		if (key == null)
			throw new NullPointerException("the given key is null!");
		CommandLineSetting<String> result = strings.get(key);

		if (result == null)
			throw new IllegalArgumentException("the given key \"" + key
					+ "\" is not present!");
		return result.getValue();
	}

	/**
	 * 
	 * @return
	 */
	public String getHelp() {
		StringBuilder builder = new StringBuilder("usage: ").append(command)
				.append(" ").append(usage);

		int longestKeyLength = 0;
		int longestDescriptionLength = 0;

		// check the integer lengths
		for (Entry<String, CommandLineSetting<Integer>> e : integers.entrySet()) {
			longestKeyLength = Math.max(longestKeyLength, e.getKey().length());
			longestDescriptionLength = Math.max(longestDescriptionLength, e
					.getValue().getDescription().length());
		}

		// check double lengths
		for (Entry<String, CommandLineSetting<Double>> e : doubles.entrySet()) {
			longestKeyLength = Math.max(longestKeyLength, e.getKey().length());
			longestDescriptionLength = Math.max(longestDescriptionLength, e
					.getValue().getDescription().length());
		}

		// check string lengths
		for (Entry<String, CommandLineSetting<String>> e : strings.entrySet()) {
			longestKeyLength = Math.max(longestKeyLength, e.getKey().length());
			longestDescriptionLength = Math.max(longestDescriptionLength, e
					.getValue().getDescription().length());
		}

		// check action lengths
		for (Entry<String, String> e : actions.entrySet()) {
			longestKeyLength = Math.max(longestKeyLength, e.getKey().length());
			longestDescriptionLength = Math.max(longestDescriptionLength, e
					.getValue().length());
		}

		longestKeyLength += 2;

		builder.append("\n\nCommand line options:");

		// print actions
		for (Entry<String, String> e : actions.entrySet()) {
			String line = String.format("\n  %-" + longestKeyLength + "s  %-"
					+ longestDescriptionLength + "s", "--" + e.getKey(),
					e.getValue());
			builder.append(line);
		}

		// print integer settings
		for (Entry<String, CommandLineSetting<Integer>> e : integers.entrySet()) {
			String line = String.format("\n  %-" + longestKeyLength + "s  %-"
					+ longestDescriptionLength + "s", "--" + e.getKey(), e
					.getValue().getDescription());
			builder.append(line);
		}

		// print double settings
		for (Entry<String, CommandLineSetting<Double>> e : doubles.entrySet()) {
			String line = String.format("\n  %-" + longestKeyLength + "s  %-"
					+ longestDescriptionLength + "s", "--" + e.getKey(), e
					.getValue().getDescription());
			builder.append(line);
		}

		// print string settings
		for (Entry<String, CommandLineSetting<String>> e : strings.entrySet()) {
			String line = String.format("\n  %-" + longestKeyLength + "s  %-"
					+ longestDescriptionLength + "s", "--" + e.getKey(), e
					.getValue().getDescription());
			builder.append(line);
		}

		// print usage examples
		if (!examples.isEmpty()) {
			if (examples.size() == 1)
				builder.append("\n\nExample");
			else
				builder.append("\n\nExamples");
			for (String example : examples)
				builder.append("\n  ").append(example);
		}
		return builder.toString();
	}

	/**
	 * 
	 * @param arguments
	 */
	public void parse(CommandLineArguments arguments) {
		if (arguments.nbOfArguments() == 0) {
			System.out.println(getHelp());
			return;
		}
		while (arguments.hasNext()) {
			// get the current token
			String token = arguments.next();

			if (token.startsWith("-")) {
				String command = token.replaceAll("^\\-+", "");

				if (command.equals("help")) {
					System.out.println(getHelp());
				} else if (actions.containsKey(command))
					handleAction(command, arguments);
				else {
					// check the index
					if (!arguments.hasNext())
						throw new IllegalArgumentException(
								"missing value for token \"" + token + "\"");

					if (integers.containsKey(command))
						integers.get(command).setValue(
								Integer.parseInt(arguments.next()));
					else if (doubles.containsKey(command))
						doubles.get(command).setValue(
								Double.parseDouble(arguments.next()));
					else if (strings.containsKey(command))
						strings.get(command).setValue(arguments.next());
					else
						throw new IllegalStateException(
								"unknown command line parameter \"" + token
										+ "\"!");
				}
			} else
				handleArgument(token, arguments);
		}

		finished();
	}

	/**
	 * 
	 * @param arguments
	 */
	public void parse(String... arguments) {
		CommandLineArguments cla = new CommandLineArguments(arguments);
		parse(cla);
	}

	/**
	 * 
	 * @param token
	 * @param arguments
	 */
	public abstract void handleAction(String token,
			CommandLineArguments arguments);

	/**
	 * 
	 * @param argument
	 */
	public abstract void handleArgument(String argument,
			CommandLineArguments arguments);

	/**
	 * 
	 * @param argument
	 */
	public abstract void finished();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[ ").append(command).append(" ]");
		for (Entry<String, CommandLineSetting<Integer>> e : integers.entrySet()) {
			builder.append("\n").append(e.getKey()).append(" = ")
					.append(e.getValue().getValue());
		}
		for (Entry<String, CommandLineSetting<Double>> e : doubles.entrySet()) {
			builder.append("\n").append(e.getKey()).append(" = ")
					.append(e.getValue().getValue());
		}
		for (Entry<String, CommandLineSetting<String>> e : strings.entrySet()) {
			builder.append("\n").append(e.getKey()).append(" = ")
					.append(e.getValue().getValue());
		}

		return builder.toString();
	}
}
