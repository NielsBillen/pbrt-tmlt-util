package computer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Represents the local computer currently being used.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class LocalComputer implements Computer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#copy(java.lang.String, java.lang.String)
	 */
	@Override
	public void copy(String from, String to) throws NullPointerException, IOException {
		copy(new File(from), new File(to));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#copy(java.io.File, java.io.File)
	 */
	@Override
	public void copy(File from, File to) throws NullPointerException,
			IOException {
		Files.copy(from.toPath(), to.toPath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#put(java.lang.String, computer.Computer,
	 * java.lang.String)
	 */
	@Override
	public void put(String from, Computer pc, String to) {
	}


}
