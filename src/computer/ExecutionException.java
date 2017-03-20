package computer;

/**
 * An exception thrown during the execution of a render task.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class ExecutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception which displays the given message.
	 * 
	 * @param message
	 *            the message of the exception.
	 */
	public ExecutionException(String message) {
		super(message);
	}

	/**
	 * Creates a new exception which displays the given message and the cause of
	 * the exception.
	 * 
	 * @param message
	 *            the message of the exception.
	 * 
	 * @param cause
	 *            the Throwable which caused the exception to be thrown.
	 */
	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
