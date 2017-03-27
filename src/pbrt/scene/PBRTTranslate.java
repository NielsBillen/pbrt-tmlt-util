package pbrt.scene;

/**
 * Represents a translation.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTTranslate extends PBRTElement {
	private float x, y, z;

	/**
	 * Represents a translation along the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public PBRTTranslate(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
				"cannot add children to PBRTTranslate!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append("Translate " + x + "\t" + y + "\t" + z + "\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTTranslate(x, y, z);
	}
}
