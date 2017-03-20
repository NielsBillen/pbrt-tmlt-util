package task;

import pbrt.PBRTScene;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RepeatedTask extends RenderTaskInterface {
	public final RenderTaskInterface task;
	public final int index;
	public final int total;
	public final long seed;

	/**
	 * 
	 * @param task
	 * @param index
	 * @param total
	 * @param seed
	 */
	public RepeatedTask(RenderTaskInterface task, int index, int total,
			long seed) {
		this.task = task;
		this.index = index;
		this.total = total;
		this.seed = seed < 0 ? seed + Long.MAX_VALUE : seed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getScene()
	 */
	@Override
	public PBRTScene getScene() {
		return task.getScene();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getFilename()
	 */
	@Override
	public String getFilename() {
		return String
				.format("%s-index-%d-%d", task.getFilename(), index, total)
				.replaceAll("-seed-{1,2}[0-9]+", "-seed-" + getSeed());
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
	 * @see task.RenderTaskInterface#getSeed()
	 */
	@Override
	public long getSeed() {
		return seed;
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
