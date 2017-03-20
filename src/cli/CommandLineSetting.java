package cli;

/**
 * A setting for a command line interface.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 * @param <T>
 */
public class CommandLineSetting<T> {
	private final String description;
	private final T defaultValue;
	private T value;

	/**
	 * 
	 * @param defaultValue
	 * @param description
	 */
	public CommandLineSetting(String description, T defaultValue)
			throws NullPointerException {
		this.description = description;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return String.format("%s (default value \"%s\")", description,
				getDefaultValue());
	}

	/**
	 * 
	 * @return
	 */
	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 
	 * @return
	 */
	public T getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(T value) {
		this.value = value;
	}
}
