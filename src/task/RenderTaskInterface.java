package task;

import java.util.concurrent.atomic.AtomicInteger;

import pbrt.PBRTScene;

/**
 * Interface which should be implemented by all render tasks.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderTaskInterface {
	private static AtomicInteger nextIdentifier = new AtomicInteger(0);

	/**
	 * 
	 */
	public final int identifier = nextIdentifier.getAndIncrement();

	/**
	 * Returns the directory to store the results in.
	 * 
	 * @return the directory to store the results in.
	 */
	public abstract String getResultDirectory();

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
	 * Returns a unique filename for the output of the render task.
	 * 
	 * @return a unique filename for the output of the render task.
	 */
	public String getFilename() {
		return getSceneName();
	}

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
