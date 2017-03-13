package pbrt;

/**
 * Parser handler which prints the PBRT scene file on the command line.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTEchoHandler implements PBRTParseHandler {
	private int indentation = 0;
	private boolean echo = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#worldBegin()
	 */
	@Override
	public void worldBegin() {
		println("WorldBegin");
		++indentation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#worldEnd()
	 */
	@Override
	public void worldEnd() {
		--indentation;
		println("WorldEnd");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#attributeBegin()
	 */
	@Override
	public void attributeBegin() {
		println("AttributeBegin");
		++indentation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#attributeEnd()
	 */
	@Override
	public void attributeEnd() {
		--indentation;
		println("AttributeEnd");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTParseHandler#transformBegin()
	 */
	@Override
	public void transformBegin() {
		println("TransformBegin");
		++indentation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTParseHandler#transformEnd()
	 */
	@Override
	public void transformEnd() {
		--indentation;
		println("TransformEnd");
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyValue(java.lang.String[])
	 */
	@Override
	public void propertyValue(String... values) {
		for (String value : values)
			print("\"" + value + "\"\t");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyStart(java.lang.String)
	 */
	@Override
	public void propertyStart(String property) {
		for (int i = 0; i < indentation; ++i)
			print("\t");
		print(property + " ");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#propertyEnd(java.lang.String)
	 */
	@Override
	public void propertyEnd(String property) {
		println();
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
		println("LookAt " + x1 + " " + y1 + " " + z1 + " " + x2 + " " + y2
				+ " " + z2 + " " + x3 + " " + y3 + " " + z3);

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
		println("ConcatTransform [" + m00 + " " + m01 + " " + m02 + " " + m03
				+ " " + m10 + " " + m11 + " " + m12 + " " + m13 + m20 + " "
				+ m21 + " " + m22 + " " + m23 + m30 + " " + m31 + " " + m32
				+ " " + m33 + " ]");
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
		println("Transform [" + m00 + " " + m01 + " " + m02 + " " + m03 + " "
				+ m10 + " " + m11 + " " + m12 + " " + m13 + m20 + " " + m21
				+ " " + m22 + " " + m23 + m30 + " " + m31 + " " + m32 + " "
				+ m33 + " ]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#integerParameter(java.lang.String, int)
	 */
	@Override
	public void integerParameter(String name, int value) {
		print(" \"integer " + name + "\" [" + value + "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#floatParameter(java.lang.String, float)
	 */
	@Override
	public void floatParameter(String name, float value) {
		print(" \"float " + name + "\" [" + value + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#stringParameter(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void stringParameter(String name, String value) {
		print(" \"string " + name + "\" [" + value + "]");

	}

	/*
	 * 
	 */
	@Override
	public void textureParameter(String name, String value) {
		print(" \"texture " + name + "\" [" + value + "]");
	}

	/*
	 * 
	 */
	@Override
	public void arrayIntegerParameter(String name, String type, int[] array) {
		print(" \"" + name + " " + type + "\" [");
		for (int f : array)
			print(f + " ");
		print("]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#arrayParameter(java.lang.String,
	 * java.lang.String, float[])
	 */
	@Override
	public void arrayFloatParameter(String name, String type, float[] array) {
		print(" \"" + name + " " + type + "\" [");
		for (Float f : array)
			print(f + " ");
		print("]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrtparser.PBRTParseHandler#scale(float, float, float)
	 */
	@Override
	public void scale(float x, float y, float z) {
		println("Scale " + x + " " + y + " " + z);
	}

	/*
	 * 
	 */
	@Override
	public void translate(float x, float y, float z) {
		println("Translate " + x + " " + y + " " + z);

	}

	/*
	 * 
	 */
	@Override
	public void rotate(float angle, float x, float y, float z) {
		println("Rotate " + angle + " " + x + " " + y + " " + z);

	}

	/**
	 * 
	 * @param string
	 */
	private void print(String string) {
		if (echo)
			System.out.print(string);
	}

	/**
	 * 
	 */
	private void println() {
		if (echo)
			System.out.println();
	}

	/**
	 * 
	 * @param string
	 */
	private void println(String string) {
		if (echo) {
			for (int i = 0; i < indentation; ++i)
				System.out.print("\t");
			System.out.println(string);
		}
	}

	/*
	 * 
	 */
	@Override
	public void reverseOrientation() {
		println("ReverseOrientation");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pbrt.PBRTParseHandler#start()
	 */
	@Override
	public void start() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#end()
	 */
	@Override
	public void end() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#boolParameter(java.lang.String, boolean)
	 */
	@Override
	public void boolParameter(String name, boolean value) {
		println(" \"bool " + name + "\" [\"" + (value ? "true" : "false")
				+ "\"]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#objectBegin(java.lang.String)
	 */
	@Override
	public void objectBegin(String name) {
		println("ObjectBegin \"" + name + "\"");
		++indentation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTParseHandler#objectEnd()
	 */
	@Override
	public void objectEnd() {
		--indentation;
		println("ObjectEnd");
	}
}
