package task;

import pbrt.PBRTArray;
import pbrt.PBRTProperty;
import pbrt.PBRTScene;

/**
 * Decorator for a render task which only renders a small tile of the image.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class TiledRenderTask extends RenderTaskInterface {
	private final RenderTaskInterface task;
	private final int x0;
	private final int y0;
	private final int x1;
	private final int y1;
	private final long seed;

	/**
	 * 
	 * @param task
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 */
	public TiledRenderTask(RenderTaskInterface task, long seed, int x0, int y0,
			int x1, int y1) {
		this.task = task;
		this.seed = seed < 0 ? seed + Long.MAX_VALUE : seed;
		this.x0 = Math.min(x0, x1);
		this.y0 = Math.min(y0, y1);
		this.x1 = Math.max(x0, x1);
		this.y1 = Math.max(y0, y1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.RenderTaskInterface#getScene()
	 */
	@Override
	public PBRTScene getScene() {
		PBRTScene scene = task.getScene();

		// access the film or add if it is missing
		PBRTProperty film = scene.findProperty("Film");
		if (film == null) {
			film = new PBRTProperty("Film").setValue("image")
					.setIntegerSetting("xresolution", task.getXResolution())
					.setIntegerSetting("yresolution", task.getYResolution());
			scene.addChild(film);
		}

		// set the tile settings
		double invX = 1.0 / (double) task.getXResolution();
		double invY = 1.0 / (double) task.getYResolution();
		film.setFloatArraySetting("float", "cropwindow", new PBRTArray<Float>(
				(float) (x0 * invX), (float) (x1 * invX), (float) (y0 * invY),
				(float) (y1 * invY)));

		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.RenderTaskInterface#getFilename()
	 */
	@Override
	public String getFilename() {
		return String.format("%s-tile-%d-%d-%d-%d", task.getFilename(), x0, y0,
				x1, y1).replaceAll("-seed-{1,2}[0-9]+", "-seed-" + getSeed());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getSceneName()
	 */
	@Override
	public String getSceneName() {
		return task.getSceneName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getXResolution()
	 */
	@Override
	public int getXResolution() {
		return task.getXResolution();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getYResolution()
	 */
	@Override
	public int getYResolution() {
		return task.getYResolution();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#nbOfSamples()
	 */
	@Override
	public int nbOfSamples() {
		return task.nbOfSamples();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getResultDirectory()
	 */
	@Override
	public String getResultDirectory() {
		return task.getResultDirectory();
	}
}
