package core;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class Tile {
	/**
	 * 
	 */
	public final int minX;

	/**
	 * 
	 */
	public final int maxX;

	/**
	 * 
	 */
	public final int minY;

	/**
	 * 
	 */
	public final int maxY;

	/**
	 * 
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 */
	public Tile(int minX, int minY, int maxX, int maxY) {
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
	}

	/**
	 * 
	 * @param resolution
	 * @return
	 */
	public double[] toDouble(int resolution) {
		double inv = 1.0 / resolution;
		return new double[] { minX * inv, minY * inv, maxX * inv, maxY * inv };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[%d %d] x [%d %d]", minX, minY, maxX, maxY);
	}
}
