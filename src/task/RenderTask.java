package task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a task to be rendered.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class RenderTask extends RenderTaskInterface {
	public final String scene;
	public final String resultDirectory;
	public final int samples;
	public final int xResolution;
	public final int yResolution;
	public final int maxDepth;
	public final long seed;

	/**
	 * 
	 * @param scene
	 * @param resultDirectory
	 * @param samples
	 * @param xResolution
	 * @param yResolution
	 * @param maxDepth
	 * @param seed
	 */
	public RenderTask(String scene, String resultDirectory, int samples,
			int xResolution, int yResolution, int maxDepth, long seed) {
		this.scene = scene;
		this.resultDirectory = resultDirectory;
		this.samples = samples;
		this.xResolution = xResolution;
		this.yResolution = yResolution;
		this.maxDepth = maxDepth;
		this.seed = seed < 0 ? seed + Long.MAX_VALUE : seed;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see task.RenderTaskInterface#getResultDirectory()
	 */
	@Override
	public String getResultDirectory() {
		return resultDirectory;
	}

	/**
	 * Subdivides this render task by subdividing the image in tiles of the
	 * given size and returning rendering tasks which only render the specific
	 * tile.
	 * 
	 * @param tileSize
	 *            the size for the tiles.
	 * @return a list containing render tasks which render the tiles of the
	 *         image.
	 */
	public List<RenderTaskInterface> getTiles(int tileSize) {
		Random random = new Random(getSeed());
		List<RenderTaskInterface> result = new ArrayList<RenderTaskInterface>();

		for (int x = 0; x < xResolution; x += tileSize) {
			for (int y = 0; y < yResolution; y += tileSize) {
				int x1 = Math.min(x + tileSize, xResolution);
				int y1 = Math.min(y + tileSize, yResolution);
				long seed = random.nextLong();
				RenderTaskInterface tiled = new TiledRenderTask(this, seed, x,
						y, x1, y1);
				result.add(tiled);

			}
		}
		return result;
	}

	/**
	 * 
	 * @param repetitions
	 * @return
	 */
	public List<RenderTaskInterface> repeat(int repetitions) {
		List<RenderTaskInterface> result = new ArrayList<RenderTaskInterface>();

		Random random = new Random(getSeed());

		for (int i = 0; i < repetitions; ++i) {
			RenderTaskInterface task = new RepeatedTask(this, i, repetitions,
					random.nextLong());
			result.add(task);
		}

		return result;
	}
}
