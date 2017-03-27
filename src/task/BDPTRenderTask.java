package task;

import pbrt.scene.PBRTProperty;
import pbrt.scene.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BDPTRenderTask extends RenderTask {
	/**
	 * 
	 * @param scene
	 * @param directory
	 * @param filename
	 * @param samples
	 * @param xResolution
	 * @param yResolution
	 * @param maxDepth
	 * @param seed
	 */
	public BDPTRenderTask(String scene, String directory, String filename,
			int samples, int xResolution, int yResolution, int maxDepth,
			int seed) {
		super(scene, directory, filename, samples, xResolution, yResolution,
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
}
