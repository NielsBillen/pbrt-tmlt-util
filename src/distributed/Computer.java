package distributed;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import task.RenderTaskInterface;
import task.RenderTaskProgressListener;

/**
 * Interface which should be implemented by computers which can perform
 * rendering tasks.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class Computer {
	/**
	 * The number of cores available to render on.
	 */
	public final int nCores;

	/**
	 * Pattern of the pbrt progress reporter.
	 * 
	 * (matches strings of the given format:
	 * <code>Rendering: [++++       ] (0.4s|10.0s)</code>).
	 */
	public static final Pattern progress = Pattern
			.compile("Rendering: \\[(\\+*)( *)\\] *"
					+ "\\(([0-9]+\\.[0-9])s\\|([0-9]+\\.[0-9])s\\)");

	/**
	 * Initializes a computer which is able to utilize all the cores.
	 */
	public Computer() {
		this(0);
	}

	/**
	 * Creates a new computer.
	 * 
	 * @param nCores
	 *            the number of cores to use during execution of rendering
	 *            tasks. (0 = use all cores).
	 * @throws IllegalArgumentException
	 *             when the number of cores is smaller than zero.
	 */
	public Computer(int nCores) throws IllegalArgumentException {
		if (nCores < 0)
			throw new IllegalArgumentException(
					"the number of cores must be larger "
							+ "than or equal to zero!");
		this.nCores = nCores;
	}

	/**
	 * 
	 * @return
	 */
	public int nCores() {
		return nCores;
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * 
	 * @param task
	 * @param resultDirectory
	 * @param listener
	 */
	public abstract void execute(RenderTaskInterface task,
			RenderTaskProgressListener listener) throws NullPointerException,
			ExecutionException;

	/**
	 * 
	 * @param task
	 * @return
	 */
	public boolean done(RenderTaskInterface task) {
		String fileName = task.getFilename();
		String directoryName = task.getDirectory();

		File directory = new File(directoryName);
		File pbrt = new File(directory, fileName.concat(".pbrt"));
		File pfm = new File(directory, fileName.concat(".pfm"));
		File txt = new File(directory, fileName.concat(".txt"));
		File exr = new File(directory, fileName.concat(".exr"));
		File png = new File(directory, fileName.concat(".png"));
		return pbrt.exists() && pfm.exists() && txt.exists() && png.exists()
				&& exr.exists();

	}

	/**
	 * 
	 * @param line
	 * @param previousPercentage
	 * @param listener
	 * @return
	 */
	protected double updateProgress(String line, double previousPercentage,
			RenderTaskProgressListener listener) {
		final Matcher matcher = progress.matcher(line);

		if (matcher.find()) {
			String plusses = matcher.group(1);
			String spaces = matcher.group(2);
			String elapsedTime = matcher.group(3);
			String remainingTime = matcher.group(4);

			double percentage = (double) plusses.length()
					/ (double) (plusses.length() + spaces.length());

			if (percentage > previousPercentage) {
				double elapsed = Double.parseDouble(elapsedTime);
				double remaining = Double.parseDouble(remainingTime);
				listener.completion(percentage, elapsed, remaining);
				return percentage;
			}

		}

		return previousPercentage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
