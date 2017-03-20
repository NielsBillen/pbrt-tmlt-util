package task;

import pbrt.PBRTProperty;
import pbrt.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BDPTRenderTask extends RenderTask {
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
	public BDPTRenderTask(String scene, String resultDirectory, int samples,
			int xResolution, int yResolution, int maxDepth, long seed) {
		super(scene, resultDirectory, samples, xResolution, yResolution,
				maxDepth, seed);
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

		PBRTProperty sampler = new PBRTProperty("Sampler").setValue(
				"lowdiscrepancy").setIntegerSetting("pixelsamples", samples);

		PBRTProperty integrator = new PBRTProperty("Integrator").setValue(
				"bdpt").setIntegerSetting("maxdepth", maxDepth);

		PBRTProperty include = new PBRTProperty("Include")
				.setValue("common.pbrt");

		scene.addChild(film);
		scene.addChild(sampler);
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
		return String.format("bdpt-%s", super.getFilename());
	}
}
