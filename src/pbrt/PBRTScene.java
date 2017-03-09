package pbrt;

/**
 * Represents an entire PBRT scene.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PBRTScene extends PBRTElement {
	/**
	 * The name of the scene.
	 */
	private String sceneName;

	/**
	 * 
	 * @param sceneName
	 * @throws NullPointerException
	 */
	public PBRTScene(String sceneName) throws NullPointerException {
		if (sceneName == null)
			throw new NullPointerException("the given scene name is null!");
		this.sceneName = sceneName;
	}

	/**
	 * Returns the name of the scene;
	 * 
	 * @return
	 */
	public String getName() {
		return sceneName;
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
		PBRTScene result = new PBRTScene(sceneName);
		for (PBRTElement element : this)
			result.addChild(element.copy());
		return result;
	}
}
