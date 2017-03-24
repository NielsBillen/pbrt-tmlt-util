package task;

import pbrt.PBRTProperty;
import pbrt.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PSSMLTRenderTask extends RenderTask {
	public final double sigma;
	public final double largeStepProbability;

	/**
	 * 
	 * @param scene
	 * @param directory
	 * @param filename
	 * @param samples
	 * @param xResolution
	 * @param yResolution
	 * @param maxDepth
	 * @param sigma
	 * @param largestepprobability
	 * @param seed
	 */
	public PSSMLTRenderTask(String scene, String directory, String filename,
			int samples, int xResolution, int yResolution, int maxDepth,
			double sigma, double largestepprobability, int seed) {
		super(scene, directory, filename, samples, xResolution, yResolution,
				maxDepth, seed);

		this.sigma = sigma;
		this.largeStepProbability = largestepprobability;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.RenderTaskInterface#getScene()
	 */
	@Override
	public PBRTScene getScene() {
		PBRTScene scene = new PBRTScene();
		PBRTProperty film = new PBRTProperty("Film").setValue("image")
				.setIntegerSetting("xresolution", xResolution)
				.setIntegerSetting("yresolution", yResolution);

		PBRTProperty integrator = new PBRTProperty("Integrator")
				.setValue("pssmlt")
				.setIntegerSetting("mutationsperpixel", samples)
				.setIntegerSetting("maxdepth", maxDepth)
				.setFloatArraySetting("temperatures", 1.0)
				.setFloatArraySetting("sigmas", sigma)
				.setFloatArraySetting("largestepprobabilities", largeStepProbability);

		PBRTProperty include = new PBRTProperty("Include")
				.setValue("common.pbrt");

		scene.addChild(film);
		scene.addChild(integrator);
		scene.addChild(include);

		return scene;
	}
}
