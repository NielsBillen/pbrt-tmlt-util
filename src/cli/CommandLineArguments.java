package cli;

import java.util.Arrays;

/**
 * Wrapper for command line arguments.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class CommandLineArguments {
	private final String[] arguments;
	private int index = 0;

	/**
	 * 
	 * @param arguments
	 */
	public CommandLineArguments(String[] arguments) {
		if (arguments == null)
			throw new NullPointerException("the given arguments are null!");
		this.arguments = Arrays.copyOf(arguments, arguments.length);
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
		return arguments[index];
	}

	/**
	 * Returns the current argument and advances to the next argument.
	 * 
	 * @return the current argument and advances to the next argument.
	 */
	public String next() {
		return arguments[index++];
	}
}
