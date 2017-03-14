package core;

import java.io.File;

import pbrt.PBRTProperty;
import pbrt.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PSSMLTJob extends RenderJob {
	public final int xResolution;
	public final int yResolution;
	public final int mutationsPerPixel;
	public final double sigma;
	public final double largeStep;

	/**
	 * Creates a new PSSMLT render job.
	 * 
	 * @param filename
	 *            the filename of the directory containing all the data of the
	 *            scene.
	 * @param mutationsPerPixel
	 *            the required number of mutations per pixel.
	 * @param sigma
	 *            small step mutation size.
	 * @param largeStep
	 *            large step mutation probability.
	 */
	public PSSMLTJob(String filename, int xResolution, int yResolution,
			int mutationsPerPixel, double sigma, double largeStep) {
		this(new File(filename), xResolution, yResolution, mutationsPerPixel,
				sigma, largeStep);

	}

	/**
	 * Creates a new PSSMLT render job.
	 * 
	 * @param directory
	 *            the directory containing all the data of the scene.
	 * @param mutationsPerPixel
	 *            the required number of mutations per pixel.
	 * @param sigma
	 *            small step mutation size.
	 * @param largeStep
	 *            large step mutation probability.
	 */
	public PSSMLTJob(File directory, int xResolution, int yResolution,
			int mutationsPerPixel, double sigma, double largeStep) {
		super(directory.getAbsoluteFile());

		if (mutationsPerPixel <= 0)
			throw new IllegalArgumentException(
					"the number of mutations must be strictly larger than zero!");
		if (sigma < 0)
			throw new IllegalArgumentException("sigma cannot be negative!");
		if (largeStep <= 0)
			throw new IllegalArgumentException(
					"the large step probability must be strictly larger than zero!");

		this.mutationsPerPixel = mutationsPerPixel;
		this.sigma = sigma;
		this.largeStep = largeStep;
		this.xResolution = xResolution;
		this.yResolution = yResolution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.RenderJob#outputFilename()
	 */
	@Override
	public String getOutputFilename() {
		return String.format("%s-pssmlt-mutations-%d-sigma-%f-step-%f",
				sceneName, mutationsPerPixel, sigma, largeStep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see core.RenderJob#build(java.io.File)
	 */
	@Override
	public PBRTScene build(File outputFile) {
		PBRTScene scene = new PBRTScene();
		scene.addChild(new PBRTProperty("Integrator").setValue("pssmlt")
				.setFloatSetting("sigma", sigma)
				.setFloatSetting("largestep", largeStep)
				.setIntegerSetting("mutationsperpixel", mutationsPerPixel)
				.setIntegerSetting("maxdepth", 16));
		scene.addChild(new PBRTProperty("Film").setValue("image")
				.setIntegerSetting("xresolution", xResolution)
				.setIntegerSetting("yresolution", yResolution)
				.setStringSetting("filename", outputFile.getAbsolutePath()));
		scene.addChild(new PBRTProperty("Include").setValue("common.pbrt"));
		return scene;
	}
}
