package task;

/**
 * Represents a task to be rendered.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderTask extends RenderTaskInterface {
	/**
	 * The name of the scene.
	 */
	public final String scene;

	/**
	 * The number of samples to use.
	 */
	public final int samples;

	/**
	 * The horizontal resolution of the image.
	 */
	public final int xResolution;

	/**
	 * The vertical resolution of the image.
	 */
	public final int yResolution;

	/**
	 * The maximum recursion depth of the ray cast.
	 */
	public final int maxDepth;

	/**
	 * The seed to use for the ray tracing procedure.
	 */
	public final int seed;

	/**
	 * Creates a new render task which renders the given scene with the given
	 * settings, storing the result in the given folder.
	 * 
	 * @param scene
	 *            the name of the scene.
	 * @param directory
	 *            the directory to write the result to.
	 * @param filename
	 *            the filename of the results (should have no extension).
	 * @param samples
	 *            the number of samples to render the image with.
	 * @param xResolution
	 *            the horizontal resolution of the image.
	 * @param yResolution
	 *            the vertical resolution of the image.
	 * @param maxDepth
	 *            the maximum recursion depth for tracing the rays.
	 * @param seed
	 *            the seed to use to generate different results.
	 */
	public RenderTask(String scene, String directory, String filename,
			int samples, int xResolution, int yResolution, int maxDepth,
			int seed) {
		super(directory, filename);

		this.scene = scene;
		this.samples = samples;
		this.xResolution = xResolution;
		this.yResolution = yResolution;
		this.maxDepth = maxDepth;
		this.seed = seed < 0 ? seed + Integer.MAX_VALUE : seed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getSceneName()
	 */
	@Override
	public String getSceneName() {
		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getXResolution()
	 */
	@Override
	public int getXResolution() {
		return xResolution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getYResolution()
	 */
	@Override
	public int getYResolution() {
		return yResolution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#nbOfSamples()
	 */
	@Override
	public int nbOfSamples() {
		return samples;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getSeed()
	 */
	@Override
	public long getSeed() {
		return seed;
	}
}
