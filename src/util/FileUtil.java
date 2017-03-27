package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utilities for operations on files.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class FileUtil {
	/**
	 * 
	 * @param offset
	 * @param file
	 * @return
	 */
	public static File get(String offset, String file) {
		if (file.startsWith("/"))
			return new File(file);
		else
			return new File(offset + "/" + file);
	}

	/**
	 * 
	 * @param file
	 * @param regex
	 * @return
	 */
	public static File findFirst(File file, String regex) {
		if (file == null)
			throw new NullPointerException("the given file is null!");
		if (file.isFile())
			return file.getName().matches(regex) ? file : null;
		else {
			for (File f : file.listFiles()) {
				File result = findFirst(f, regex);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param file
	 * @param parent
	 * @return
	 */
	public static boolean isChild(File file, File parent) {
		if (file.equals(parent))
			return false;

		File p = file.getParentFile();
		if (p == null)
			return false;

		return isChild(p, parent);
	}

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
		if (file == null)
			return false;
		if (file.exists()) {
			if (file.isDirectory())
				return true;
			else
				return false;
		} else
			return file.mkdirs();
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean cp(String from, String to) {
		return cp(new File(from), new File(to));
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public static boolean cp(File from, File to) {
		if (from.equals(to))
			return false;
		if (isChild(to, from))
			return false;
		if (to.exists())
			return false;
		if (!from.exists())
			return false;

		if (from.isDirectory()) {
			if (!mkdirs(to))
				return false;
			for (File fromFile : from.listFiles()) {
				File toFile = new File(to, fromFile.getName());
				if (!cp(fromFile, toFile))
					return false;

			}
			return true;

		} else {
			try {
				Files.copy(from.toPath(), to.toPath());
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	}
}
