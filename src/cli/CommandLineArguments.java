package cli;

/**
 * Wrapper for command line arguments.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class CommandLineArguments {
	/**
	 * The provided command line arguments.
	 */
	private final String[] arguments;

	/**
	 * Index in the command line arguments.
	 */
	private int index = 0;

	/**
	 * Creates a wrapper for the given array of arguments.
	 * 
	 * @param arguments
	 *            the arguments.
	 * @throws NullPointerException
	 *             when the given array is null.
	 * @throws NullPointerException
	 *             when one of the arguments is null.
	 * @throws IllegalArgumentException
	 *             when one of the arguments is empty.
	 */
	public CommandLineArguments(String[] arguments)
			throws NullPointerException, IllegalArgumentException {
		if (arguments == null)
			throw new NullPointerException("the given arguments are null!");
		this.arguments = new String[arguments.length];
		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i] == null)
				throw new NullPointerException("the argument at index " + i
						+ " is null!");
			this.arguments[i] = arguments[i].trim();

			if (this.arguments[i].isEmpty())
				throw new IllegalArgumentException("the argument at index " + i
						+ " is empty!");
		}
	}

	/**
	 * Returns the number of arguments.
	 * 
	 * @return the number of arguments.
	 */
	public int nbOfArguments() {
		return arguments.length;
	}

	/**
	 * Returns the remaining number of arguments.
	 * 
	 * @return the remaining number of arguments.
	 */
	public int remainingArguments() {
		return nbOfArguments() - index;
	}

	/**
	 * Skips over the current argument.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             when you advance across the number of arguments.
	 */
	public void skip() {
		skip(1);
	}

	/**
	 * Skips the given number of arguments.
	 * 
	 * @param n
	 *            the number of arguments to skip.
	 * @throws IndexOutOfBoundsException
	 *             when you advance across the number of arguments.
	 */
	public void skip(int n) {
		if (index + n < 0)
			throw new IndexOutOfBoundsException("you have returned to far!");
		else if (index + n >= arguments.length)
			throw new IndexOutOfBoundsException("you have advanced to far!");
		else
			index += n;
	}

	/**
	 * Returns whether there is another command line argument.
	 * 
	 * @return whether there is another command line argument.
	 */
	public boolean hasNext() {
		return index < arguments.length;
	}

	/**
	 * Returns the current argument without advancing.
	 * 
	 * @return the current argument without advancing.
	 */
	public String peek() {
		return get(index);
	}

	/**
	 * Returns the current argument and advances to the next argument.
	 * 
	 * @return the current argument and advances to the next argument.
	 */
	public String next() {
		return get(index++);
	}

	/**
	 * Returns the argument at the given index.
	 * 
	 * @param index
	 *            the index of the argument to retrieve.
	 * @throws IndexOutOfBoundsException
	 *             when the index is smaller than zero or larger than or equal
	 *             to the number of arguments.
	 * @return the argument at the given index.
	 */
	public String get(int index) throws IndexOutOfBoundsException {
		if (index < 0)
			throw new IndexOutOfBoundsException(
					"the index is smaller than zero!");
		if (index >= nbOfArguments())
			throw new IndexOutOfBoundsException(
					"the index is larger than or equal to the number of arguments!");
		return arguments[index];
	}

	/**
	 * 
	 * @return
	 */
	public String[] subArray() {
		return subArray(index);
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public String[] subArray(int index) {
		int newSize = nbOfArguments() - index;
		String[] newCommands = new String[newSize];
		for (int i = 0; i < newSize; ++i)
			newCommands[i] = get(index + i);
		return newCommands;
	}

	/**
	 * 
	 * @return
	 */
	public CommandLineArguments subCommandLineArguments() {
		return subsubCommandLineArguments(index);
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public CommandLineArguments subsubCommandLineArguments(int index) {
		int newSize = nbOfArguments() - index;
		String[] newCommands = new String[newSize];
		for (int i = 0; i < newSize; ++i)
			newCommands[i] = get(index + i);
		return new CommandLineArguments(newCommands);
	}
}
