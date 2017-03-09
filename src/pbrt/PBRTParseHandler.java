package pbrt;

/**
 * Handler used during the parsing of a file.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public interface PBRTParseHandler {
	/**
	 * 
	 * @param sceneName
	 */
	public void start(String sceneName);

	public void end();

	public void propertyStart(String property);

	public void propertyValue(String... value);

	public void propertyEnd(String property);

	public void integerParameter(String name, int value);

	public void floatParameter(String name, float value);

	public void boolParameter(String name, boolean value);

	public void stringParameter(String name, String value);

	public void textureParameter(String name, String value);

	public void arrayIntegerParameter(String name, String type, int[] array);

	public void arrayFloatParameter(String name, String type, float[] array);

	public void lookAt(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3);

	public void concatTransform(float m00, float m01, float m02, float m03,
			float m10, float m11, float m12, float m13, float m20, float m21,
			float m22, float m23, float m30, float m31, float m32, float m33);

	public void attributeBegin();

	public void attributeEnd();

	public void worldBegin();

	public void worldEnd();

	public void objectBegin(String name);

	public void objectEnd();

	public void translate(float x, float y, float z);

	public void rotate(float angle, float x, float y, float z);

	public void scale(float x, float y, float z);

	public void reverseOrientation();
}
