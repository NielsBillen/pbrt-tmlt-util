package pbrt;

import java.util.Arrays;

/**
 * Specifies the look at direction in a {@link PBRTScene}.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTLookAt extends PBRTElement {
	private float[] array;

	/**
	 * 
	 * @param xFrom
	 * @param yFrom
	 * @param zFrom
	 * @param xTo
	 * @param yTo
	 * @param zTo
	 * @param xUp
	 * @param yUp
	 * @param zUp
	 */
	public PBRTLookAt(float xFrom, float yFrom, float zFrom, float xTo,
			float yTo, float zTo, float xUp, float yUp, float zUp) {
		array = new float[] { xFrom, yFrom, zFrom, xTo, yTo, zTo, xUp, yUp, zUp };
	}

	/**
	 * Creates a new {@link PBRTLookAt} element from the given array.
	 * 
	 * @param array
	 *            the array to construct this {@link PBRTLookAt} from.
	 * @throws NullPointerException
	 *             when the given array is null.
	 */
	public PBRTLookAt(float... array) {
		if (array == null)
			throw new NullPointerException("the given array is null!");
		this.array = Arrays.copyOf(array, 9);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append("LookAt\n");
		for (int i = 0; i < 3; ++i) {
			indent(b, indent);
			for (int j = 0; j < 3; ++j)
				b.append("\t").append(array[3 * i + j]);
			b.append("\n");
		}
		b.append("\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#addChild(parser.PBRTElement)
	 */
	@Override
	public void addChild(PBRTElement element) throws NullPointerException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"cannot add a child to a PBRTArray!");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTLookAt(array);
	}
}
