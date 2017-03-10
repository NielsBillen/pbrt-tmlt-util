package pbrt;

import java.util.Arrays;

public class PBRTTransform extends PBRTElement {
	private float[] elements;

	/**
	 * 
	 * @param floats
	 */
	public PBRTTransform(float... floats) {
		if (floats.length != 16)
			throw new IllegalArgumentException(
					"expected 16 floats for the transformation!");
		this.elements = Arrays.copyOf(floats, floats.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTTransform(this.elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTElement#describeSelf(java.lang.StringBuilder, int)
	 */
	@Override
	protected void describeSelf(StringBuilder builder, int indent)
			throws NullPointerException {
		indent(builder, indent).append("Transform [");
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j)
				builder.append(String.format(" %f", this.elements[4 * i + j]));

			if (i < 3) {
				builder.append("\n");
				indent(builder, indent).append("           ");
			}
		}
		builder.append(" ]\n");
	}
}
