package pbrt;

public class PBRTRotate extends PBRTElement {
	private float angle, x, y, z;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public PBRTRotate(float angle, float x, float y, float z) {
		this.angle = angle;
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
				"cannot add children to PBRTRotate!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append(
				"Rotate " + angle + "\t" + x + "\t" + y + "\t" + z + "\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTRotate(angle, x, y, z);
	}
}
