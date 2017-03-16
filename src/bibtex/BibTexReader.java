package bibtex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class BibTexReader {
	private final BufferedReader reader;

	/**
	 * Creates a new BibTexReader given the File to read from.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public BibTexReader(File file) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(file));
	}

	/**
	 * 
	 * @return
	 */
	public BibTexEntry read() throws IOException {
		return null;
	}
	

	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Once the stream has been closed, further read(), ready(), mark(),
	 * reset(), or skip() invocations will throw an IOException. Closing a
	 * previously closed stream has no effect.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public void close() throws IOException {
		reader.close();
	}

}
