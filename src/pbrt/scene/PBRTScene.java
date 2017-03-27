package pbrt.scene;

/**
 * Represents an entire PBRT scene.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PBRTScene extends PBRTElement {
	/**
	 * 
	 * @param sceneName
	 * @throws NullPointerException
	 */
	public PBRTScene() throws NullPointerException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#append(java.lang.StringBuilder, int)
	 */
	@Override
	protected void append(StringBuilder builder, int indent)
			throws NullPointerException {
		for (PBRTElement element : this)
			element.append(builder, indent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		PBRTScene result = new PBRTScene();
		for (PBRTElement element : this)
			result.addChild(element.copy());
		return result;
	}
}
