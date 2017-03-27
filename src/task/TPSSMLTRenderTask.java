package task;

import java.util.Arrays;

import pbrt.scene.PBRTProperty;
import pbrt.scene.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class TPSSMLTRenderTask extends RenderTask {
	public final double[] temperatures;
	public final double[] sigmas;
	public final double[] largeStepProbabilities;
	public final double mixingRate;

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
	public TPSSMLTRenderTask(String scene, String directory, String filename,
			int samples, int xResolution, int yResolution, int maxDepth,
			double[] temperatures, double[] sigmas,
			double[] largestepprobabilities, double mixingRate, int seed) {
		super(scene, directory, filename, samples, xResolution, yResolution,
				maxDepth, seed);

		if (largestepprobabilities.length != temperatures.length)
			throw new IllegalArgumentException("mismatch in size!");
		if (largestepprobabilities.length != sigmas.length)
			throw new IllegalArgumentException("mismatch in size!");

		this.temperatures = Arrays.copyOf(temperatures, temperatures.length);
		this.sigmas = Arrays.copyOf(sigmas, sigmas.length);
		this.largeStepProbabilities = Arrays.copyOf(largestepprobabilities,
				largestepprobabilities.length);
		this.mixingRate = mixingRate;

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
				.setValue("tpssmlt")
				.setIntegerSetting("mutationsperpixel", samples)
				.setIntegerSetting("maxdepth", maxDepth)
				.setBoolSetting("swaponsplat", false)
				.setFloatSetting("mixingprobability", mixingRate)
				.setFloatArraySetting("temperatures", temperatures)
				.setFloatArraySetting("sigmas", sigmas)
				.setFloatArraySetting("largestepprobabilities",
						largeStepProbabilities);

		PBRTProperty include = new PBRTProperty("Include")
				.setValue("common.pbrt");

		scene.addChild(film);
		scene.addChild(integrator);
		scene.addChild(include);

		return scene;
	}
}
