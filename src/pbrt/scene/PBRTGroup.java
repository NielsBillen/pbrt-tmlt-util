package pbrt.scene;

/**
 * Represents a grouping of {@link PBRTElement}s. The children of a group are
 * indented and bounded by tags.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTGroup extends PBRTElement {
	public final String startTag;
	public final String endTag;

	/**
	 * Creates a new PBRTGroup with the given start and end tags.
	 * 
	 * @param startTag
	 *            The start tag for the group.
	 * @param endTag
	 *            The end tag for the group.
	 * @throws NullPointerException
	 *             When one of the tags are null.
	 */
	public PBRTGroup(String startTag, String endTag)
			throws NullPointerException {
		if (startTag == null)
			throw new NullPointerException("the given start tag is null!");
		if (endTag == null)
			throw new NullPointerException("the given end tag is null!");
		this.startTag = startTag;
		this.endTag = endTag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#append(java.lang.StringBuilder, int)
	 */
	@Override
	protected void append(StringBuilder b, int indent)
			throws NullPointerException {
		indent(b, indent).append(startTag + "\n");
		for (PBRTElement e : this)
			e.append(b, indent + 1);
		indent(b, indent).append(endTag + "\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder, int)
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
		PBRTGroup group = new PBRTGroup(startTag, endTag);
		for (PBRTElement element : this)
			group.addChild(element.copy());
		return group;
	}
}
