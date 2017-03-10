package pbrt;

import java.util.Arrays;

/**
 * Specifies the look at direction in a {@link PBRTScene}.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTConcatTransform extends PBRTElement {
	private float[] array;

	/**
	 * Creates a new {@link PBRTConcatTransform} element from the given array.
	 * 
	 * @param array
	 *            the array to construct this {@link PBRTConcatTransform} from.
	 * @throws NullPointerException
	 *             when the given array is null.
	 */
	public PBRTConcatTransform(float... array) {
		if (array == null)
			throw new NullPointerException("the given array is null!");
		if (array.length != 16)
			throw new IllegalArgumentException(
					"expected 16 floats for the transformation!");
		this.array = Arrays.copyOf(array, 16);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append("ConcatTransform [");
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j)
				b.append(String.format(" %f", this.array[4 * i + j]));

			if (i < 3) {
				b.append("\n");
				indent(b, indent).append("                 ");
			}
		}
		b.append(" ]\n");
		// for (int i = 0; i < 4; ++i) {
		// indent(b, indent);
		// for (int j = 0; j < 4; ++j)
		// b.append(" ").append(array[4 * i + j]);
		// }
		// b.append(" ]\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTConcatTransform(array);
	}
}
