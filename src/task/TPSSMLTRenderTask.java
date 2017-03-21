package task;

import pbrt.PBRTArray;
import pbrt.PBRTProperty;
import pbrt.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class TPSSMLTRenderTask extends RenderTask {
	public final Float[] temperatures;
	public final Float[] sigmas;
	public final Float[] largeStepProbabilities;
	public final float mixingRate;

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
	public TPSSMLTRenderTask(String scene, String resultDirectory, int samples,
			int xResolution, int yResolution, int maxDepth,
			double[] temperatures, double[] sigmas,
			double[] largestepprobabilities, double mixingRate, long seed) {
		super(scene, resultDirectory, samples, xResolution, yResolution,
				maxDepth, seed);

		if (largestepprobabilities.length != temperatures.length)
			throw new IllegalArgumentException("mismatch in size!");
		if (largestepprobabilities.length != sigmas.length)
			throw new IllegalArgumentException("mismatch in size!");

		this.temperatures = new Float[temperatures.length];
		this.sigmas = new Float[temperatures.length];
		this.largeStepProbabilities = new Float[temperatures.length];

		for (int i = 0; i < temperatures.length; ++i) {
			this.temperatures[i] = (float) temperatures[i];
			this.sigmas[i] = (float) sigmas[i];
			this.largeStepProbabilities[i] = (float) largestepprobabilities[i];
		}
		this.mixingRate = (float) mixingRate;

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
				.setFloatSetting("mixingprobability", mixingRate)
				.setFloatArraySetting("float", "temperatures",
						new PBRTArray<Float>(temperatures))
				.setFloatArraySetting("float", "sigmas",
						new PBRTArray<Float>(sigmas))
				.setFloatArraySetting("float", "largestepprobabilities",
						new PBRTArray<Float>(largeStepProbabilities));

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
		// StringBuilder builder = new StringBuilder("tppsmlt-temp");
		// for (Float temperature : temperatures)
		// builder.append("-").append(String.format("%.4f", temperature));
		// builder.append("-sigmas");
		// for (Float sigma : sigmas)
		// builder.append("-").append(String.format("%.4f", sigma));
		// builder.append("-largesteps");
		// for (Float largestep : largeStepProbabilities)
		// builder.append("-").append(String.format("%.4f", largestep));
		// builder.append("-mixing-").append(String.format("%.4f", mixingRate));
		// builder.append("-").append(super.getFilename());
		//
		// return builder.toString();
		return String.format("tppsmlt-%s-%d", super.getFilename(), identifier);
	}
}
