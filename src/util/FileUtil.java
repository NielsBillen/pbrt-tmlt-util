package util;

import java.io.File;

/**
 * Utilities for operations on files.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class FileUtil {
	/**
	 * 
	 * @param format
	 * @param objects
	 * @return
	 */
	public static boolean rm(String format, Object... objects) {
		return rm(String.format(format, objects));
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean rm(String filename) {
		return rm(new File(filename));
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static boolean rm(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles())
				if (!rm(f))
					return false;

		}

		return file.delete();
	}

	/**
	 * 
	 * @param format
	 * @param objects
	 * @return
	 */
	public static boolean mkdirs(String format, Object... objects) {
		return mkdirs(String.format(format, objects));
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean mkdirs(String filename) {
		return mkdirs(new File(filename));
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static boolean mkdirs(File file) {
		File absolute = file.getAbsoluteFile();
		File parent = absolute.getParentFile();
		if (!parent.exists()) {
			if (!mkdirs(parent))
				return false;
		}
		return absolute.mkdir();
	}
}
