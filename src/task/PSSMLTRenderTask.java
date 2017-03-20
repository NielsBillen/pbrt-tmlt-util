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
	 * @param resultDirectory
	 * @param samples
	 * @param xResolution
	 * @param yResolution
	 * @param maxDepth
	 * @param sigma
	 * @param largestepprobability
	 * @param seed
	 */
	public PSSMLTRenderTask(String scene, String resultDirectory, int samples,
			int xResolution, int yResolution, int maxDepth, double sigma,
			double largestepprobability, long seed) {
		super(scene, resultDirectory, samples, xResolution, yResolution,
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
				.setFloatSetting("sigma", sigma)
				.setFloatSetting("largestepprobability", largeStepProbability);

		PBRTProperty include = new PBRTProperty("Include")
				.setValue("common.pbrt");

		scene.addChild(film);
		scene.addChild(integrator);
		scene.addChild(include);

		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.RenderTaskInterface#getFilename()
	 */
	@Override
	public String getFilename() {
		return String.format("pssmlt-sigma-%.10f-largestep-%.10f-%s", sigma,
				largeStepProbability, super.getFilename());
	}
}
