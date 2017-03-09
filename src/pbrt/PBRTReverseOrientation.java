package pbrt;

/**
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTReverseOrientation extends PBRTElement {
	/**
	 * 
	 */
	public PBRTReverseOrientation() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append("ReverseOrientation\n");
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
				"cannot add children to PBRTReverseOrientation!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTReverseOrientation();
	}
}
