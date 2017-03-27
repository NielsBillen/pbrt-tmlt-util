package task;

import java.util.concurrent.atomic.AtomicInteger;

import pbrt.scene.PBRTScene;

/**
 * Interface which should be implemented by all render tasks.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderTaskInterface {
	/**
	 * 
	 */
	private static AtomicInteger nextIdentifier = new AtomicInteger(0);

	/**
	 * 
	 */
	public final int identifier = nextIdentifier.getAndIncrement();

	/**
	 * 
	 */
	private final String directory;

	/**
	 * 
	 */
	private final String filename;

	/**
	 * 
	 * @param directory
	 * @param filename
	 */
	public RenderTaskInterface(String directory, String filename) {
		if (directory == null)
			throw new NullPointerException("the given directory is null!");
		if (filename == null)
			throw new NullPointerException("the given filename is null!");
		this.directory = directory;
		this.filename = filename;
	}

	/**
	 * Returns the filename to write the result to (without an extension).
	 * 
	 * @return the filename to write the result to (without an extension).
	 */
	final public String getFilename() {
		return String.format("%s-id-%d", filename, identifier);
	}

	/**
	 * Returns the directory to write the result to.
	 * 
	 * @return the directory to write the result to.
	 */
	final public String getDirectory() {
		return directory;
	}

	/**
	 * Returns the horizontal resolution for rendering the image.
	 * 
	 * @return the horizontal resolution for rendering the image.
	 */
	public abstract int getXResolution();

	/**
	 * Returns the vertical resolution for rendering the image.
	 * 
	 * @return the vertical resolution for rendering the image.
	 */
	public abstract int getYResolution();

	/**
	 * Returns the configuration file of the scene to be rendered.
	 * 
	 * @return the configuration file of the scene to be rendered.
	 */
	public abstract PBRTScene getScene();

	/**
	 * Returns the name of the scene.
	 * 
	 * @return the name of the scene.
	 */
	public abstract String getSceneName();

	/**
	 * Returns a seed for rendering the image.
	 * 
	 * @return a seed for rendering the images.
	 */
	public abstract long getSeed();

	/**
	 * Returns the number of samples per pixel to render the image with.
	 * 
	 * @return the number of samples per pixel to render the image with.
	 */
	public abstract int nbOfSamples();
}
