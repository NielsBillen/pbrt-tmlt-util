package core;

import java.io.File;

/**
 * Represents a .pbrt scene file which needs to be rendered.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RenderJob implements Comparable<RenderJob> {
	/**
	 * 
	 */
	public final File file;

	/**
	 * 
	 */
	public final int samples;

	/**
	 * 
	 */
	public final String sceneName;

	/**
	 * 
	 * @param file
	 * @param sceneName
	 * @param samples
	 */
	public RenderJob(File file, String sceneName, int samples) {
		this.file = file;
		this.sceneName = sceneName;
		this.samples = samples;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RenderJob o) {
		if (samples < o.samples)
			return -1;
		else if (samples > o.samples)
			return 1;
		else
			return file.getName().compareTo(o.file.getName());
	}

}
