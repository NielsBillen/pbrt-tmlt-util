package computer;

import java.io.File;
import java.io.IOException;

/**
 * Interface which should be implemented by all computers (either local or
 * remotely).
 * 
 * @author Niels Billen
 * @version 0.1
 */
public interface Computer {
	/**
	 * Copies the file with the given from filename to the given destination,
	 * making the required directories if necessary.
	 * 
	 * @param from
	 * @param to
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public void copy(String from, String to) throws NullPointerException, IOException;

	/**
	 * Copies the file with the given from filename to the given destination,
	 * making the required directories if necessary.
	 * 
	 * @param from
	 * @param to
	 * @throws NullPointerException
	 * @throws IOException 
	 */
	public void copy(File from, File to) throws NullPointerException, IOException;

	/**
	 * Copies the file with the given filename to the given pc.
	 * 
	 * @param from
	 * @param pc
	 * @param to
	 * @throws NullPointerException
	 */
	public void put(String from, Computer pc, String to)
			throws NullPointerException;
}
