package cli;

/**
 * Provides default implementation for the CommandLineInterface.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class CommandLineAdapter extends CommandLineInterface {
	/**
	 * 
	 * @param command
	 * @param usage
	 * @throws NullPointerException
	 */
	public CommandLineAdapter(String command, String usage)
			throws NullPointerException {
		super(command, usage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleAction(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleAction(String token, CommandLineArguments arguments) {
		throw new IllegalArgumentException("cannot handle action \"" + token
				+ "\"");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#handleArgument(java.lang.String,
	 * cli.CommandLineArguments)
	 */
	@Override
	public void handleArgument(String argument, CommandLineArguments arguments) {
		throw new IllegalArgumentException("cannot handle argument \""
				+ argument + "\"");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cli.CommandLineInterface#finished()
	 */
	@Override
	public void finished() {
	}

}
