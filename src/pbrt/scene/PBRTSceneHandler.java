package pbrt.scene;

import java.util.LinkedList;

/**
 * Creates a scene description.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTSceneHandler implements PBRTParseHandler {
	private PBRTScene root;
	private LinkedList<PBRTElement> e = new LinkedList<PBRTElement>();

	/**
	 * 
	 */
	public PBRTSceneHandler() {
	}

	/**
	 * 
	 * @return
	 */
	public PBRTScene getRoot() {
		return root;
	}

	/*
	 * 
	 */
	@Override
	public void start() {
		root = new PBRTScene();
		e.addFirst(root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#end()
	 */
	@Override
	public void end() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyStart(java.lang.String)
	 */
	@Override
	public void propertyStart(String property) {
		PBRTProperty p = new PBRTProperty(property);
		e.addFirst(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyValue(java.lang.String[])
	 */
	@Override
	public void propertyValue(String... values) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		for (String value : values)
			property.addValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyEnd(java.lang.String)
	 */
	@Override
	public void propertyEnd(String property) {
		if (e.size() < 2)
			throw new IllegalStateException("degenerate stack!");
		PBRTElement element = e.removeFirst();
		e.getFirst().addChild(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#integerParameter(java.lang.String, int)
	 */
	@Override
	public void integerParameter(String name, int value) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		property.setIntegerSetting(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#floatParameter(java.lang.String, float)
	 */
	@Override
	public void floatParameter(String name, float value) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		property.setFloatSetting(name, value);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#stringParameter(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void stringParameter(String name, String value) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		property.setStringSetting(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#textureParameter(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void textureParameter(String name, String value) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		property.setTextureSetting(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#arrayIntegerParameter(java.lang.String,
	 * java.lang.String, int[])
	 */
	@Override
	public void arrayIntegerParameter(String name, String type, int[] array) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;

		PBRTArray<Integer> intArray = new PBRTArray<Integer>();
		for (int a : array)
			intArray.add(a);
		property.setIntegerArraySetting(name, type, intArray);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#arrayFloatParameter(java.lang.String,
	 * java.lang.String, float[])
	 */
	@Override
	public void arrayFloatParameter(String name, String type, float[] array) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;

		PBRTArray<Float> floatArray = new PBRTArray<Float>();
		for (float a : array)
			floatArray.add(a);
		property.setFloatArraySetting(name, type, floatArray);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#lookAt(float, float, float, float,
	 * float, float, float, float, float)
	 */
	@Override
	public void lookAt(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3) {
		PBRTLookAt lookat = new PBRTLookAt(x1, y1, z1, x2, y2, z2, x3, y3, z3);
		e.getFirst().addChild(lookat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#concatTransform(float, float, float, float,
	 * float, float, float, float, float, float, float, float, float, float,
	 * float, float)
	 */
	@Override
	public void concatTransform(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		PBRTConcatTransform concat = new PBRTConcatTransform(m00, m01, m02,
				m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
		e.getFirst().addChild(concat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#concatTransform(float, float, float, float,
	 * float, float, float, float, float, float, float, float, float, float,
	 * float, float)
	 */
	@Override
	public void transform(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33) {
		PBRTTransform transform = new PBRTTransform(m00, m01, m02, m03, m10,
				m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
		e.getFirst().addChild(transform);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#attributeBegin()
	 */
	@Override
	public void attributeBegin() {
		PBRTGroup group = new PBRTGroup("AttributeBegin", "AttributeEnd");
		e.addFirst(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#attributeEnd()
	 */
	@Override
	public void attributeEnd() {
		if (e.size() < 2)
			throw new IllegalStateException("degenerate stack!");
		PBRTElement element = e.removeFirst();
		e.getFirst().addChild(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTParseHandler#transformBegin()
	 */
	@Override
	public void transformBegin() {
		PBRTGroup group = new PBRTGroup("TransformBegin", "TransformEnd");
		e.addFirst(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTParseHandler#transformEnd()
	 */
	@Override
	public void transformEnd() {
		if (e.size() < 2)
			throw new IllegalStateException("degenerate stack!");
		PBRTElement element = e.removeFirst();
		e.getFirst().addChild(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#worldBegin()
	 */
	@Override
	public void worldBegin() {
		PBRTGroup group = new PBRTGroup("WorldBegin", "WorldEnd");
		e.addFirst(group);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#worldEnd()
	 */
	@Override
	public void worldEnd() {
		if (e.size() < 2)
			throw new IllegalStateException("degenerate stack!");
		PBRTElement element = e.removeFirst();
		e.getFirst().addChild(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#translate(float, float, float)
	 */
	@Override
	public void translate(float x, float y, float z) {
		PBRTTranslate translate = new PBRTTranslate(x, y, z);
		e.getFirst().addChild(translate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#rotate(float, float, float, float)
	 */
	@Override
	public void rotate(float angle, float x, float y, float z) {
		PBRTRotate translate = new PBRTRotate(angle, x, y, z);
		e.getFirst().addChild(translate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#scale(float, float, float)
	 */
	@Override
	public void scale(float x, float y, float z) {
		PBRTScale scale = new PBRTScale(x, y, z);
		e.getFirst().addChild(scale);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#reverseOrientation()
	 */
	@Override
	public void reverseOrientation() {
		e.getFirst().addChild(new PBRTReverseOrientation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#boolParameter(java.lang.String, boolean)
	 */
	@Override
	public void boolParameter(String name, boolean value) {
		if (e.size() == 0)
			throw new IllegalStateException("element stack is zero!");
		PBRTElement element = e.getFirst();

		if (!(element instanceof PBRTProperty))
			throw new IllegalStateException("element is not a property!");
		PBRTProperty property = (PBRTProperty) element;
		property.setBoolSetting(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#objectBegin(java.lang.String)
	 */
	@Override
	public void objectBegin(String name) {
		PBRTGroup group = new PBRTGroup("ObjectBegin \"" + name + "\"",
				"ObjectEnd");
		e.addFirst(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#objectEnd()
	 */
	@Override
	public void objectEnd() {
		if (e.size() < 2)
			throw new IllegalStateException("degenerate stack!");
		PBRTElement element = e.removeFirst();
		e.getFirst().addChild(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return root.toString();
	}
}
