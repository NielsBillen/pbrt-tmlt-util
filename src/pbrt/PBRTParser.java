package pbrt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class which is able to parse a PBRT scene description file.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTParser {
	private File file;
	private String subString = "";
	private String currentProperty = null;
	private static final Set<String> properties = new HashSet<String>();

	/**
	 * 
	 * @param file
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	private PBRTParser(File file) throws NullPointerException,
			IllegalArgumentException {
		if (file == null)
			throw new NullPointerException("the given file is null!");
		if (!file.exists())
			throw new IllegalArgumentException("the given file \""
					+ file.getAbsolutePath() + "\" does not exist!");
		this.file = file;

		properties.add("Film");
		properties.add("Sampler");
		properties.add("Accelerator");
		properties.add("PixelFilter");
		properties.add("SurfaceIntegrator");
		properties.add("VolumeIntegrator");
		properties.add("Integrator");
		properties.add("Camera");
		properties.add("Material");
		properties.add("NamedMaterial");
		properties.add("MakeNamedMaterial");
		properties.add("MakeNamedMedium");
		properties.add("MediumInterface");
		properties.add("Texture");
		properties.add("Shape");
		properties.add("LightSource");
		properties.add("AreaLightSource");
		properties.add("Include");
		properties.add("ObjectInstance");
		properties.add("Renderer");
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static PBRTScene parse(String filename) {
		return parse(new File(filename));
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static PBRTScene parse(File file) {
		PBRTParser parser = new PBRTParser(file);
		PBRTSceneHandler handler = new PBRTSceneHandler();
		parser.parse(handler);

		return handler.getRoot();
	}

	/**
	 * 
	 * @param handler
	 */
	public void parse(PBRTParseHandler handler) {
		StringBuilder b = new StringBuilder();

		FileReader reader;
		BufferedReader r;

		try {
			reader = new FileReader(file);
			r = new BufferedReader(reader);
			String line;

			while ((line = r.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				int index = line.indexOf("#");
				if (index > 0)
					line = line.substring(0, index - 1);
				if (line.trim().equals(""))
					continue;
				b.append(line + " ");
			}

			r.close();
			reader.close();
		} catch (Exception e) {
		}

		String fileString = b.toString().replaceAll("\t", " ");
		while (fileString.contains("  "))
			fileString = fileString.replaceAll("  ", " ");

		List<String> groups = group(fileString, handler);

		handler.start();
		parse(groups, handler);
		handler.end();
	}

	/**
	 * 
	 * @param string
	 * @param handler
	 */
	private List<String> group(String string, PBRTParseHandler handler) {
		List<String> result = new ArrayList<String>();

		string = string.replaceAll("\" *\\]", "\"]");
		string = string.replaceAll("\\[ *\"", "[\"");

		String[] split = string.split(" ");

		boolean startedArray = false;
		for (String s : split) {
			if (s.startsWith("\"") && !s.endsWith("\"")) {
				push(result);
				append(s.substring(1) + " ");
			} else if (!s.startsWith("\"") && s.endsWith("\"")) {
				append(s.substring(0, s.length() - 1));
				push(result);
			} else if (s.startsWith("\"") && s.endsWith("\"")) {
				push(result);
				append(s.substring(1, s.length() - 1));
				push(result);
			} else if (s.startsWith("[") && !s.endsWith("]")) {
				push(result);
				startedArray = true;
				append(s.substring(1));
				push(result);
			} else if (!s.startsWith("[") && s.endsWith("]")) {
				if (startedArray) {
					startedArray = false;
					push(result);
				}
				append(s.substring(0, s.length() - 1));
				push(result);
			} else if (s.startsWith("[") && s.endsWith("]")) {
				push(result);
				append(s.substring(1, s.length() - 1));
				push(result);
			} else {
				if (startedArray)
					push(result);
				append(s);
				push(result);
			}
		}

		return result;

	}

	/**
	 * 
	 * @param groups
	 * @param handler
	 */
	private void parse(List<String> groups, PBRTParseHandler handler) {
		int i = 0;
		while (i < groups.size()) {
			String element = groups.get(i);

			if (!element.contains(" "))
				endProperty(handler);

			if (element.contains(" ")) {
				String[] s = element.split(" ");

				if (s[0].equals("integer") && s[1].equals("indices")) {
					int k = 1;
					while (true) {
						try {
							Integer.parseInt(groups.get(i + k));
							++k;
						} catch (NumberFormatException e) {
							break;
						}
					}

					handler.arrayIntegerParameter(s[0], s[1],
							parseIntegerArray(groups, i + 1, i + k));
					i += k - 2;
				} else if (s[0].equals("float") && s[1].equals("uv")) {
					int k = 1;
					while (true) {
						try {
							Float.parseFloat(groups.get(i + k));
							++k;
						} catch (NumberFormatException e) {
							break;
						}
					}

					handler.arrayFloatParameter(s[0], s[1],
							parseFloatArray(groups, i + 1, i + k));
					i += k - 2;
				} else if (s[0].equals("float") && s[1].equals("st")) {
					int k = 1;
					while (true) {
						try {
							Float.parseFloat(groups.get(i + k));
							++k;
						} catch (NumberFormatException e) {
							break;
						}
					}

					handler.arrayFloatParameter(s[0], s[1],
							parseFloatArray(groups, i + 1, i + k));
					i += k - 2;
				} else if (s[0].equals("normal") && s[1].equals("N")) {
					int k = 1;
					while (true) {
						try {
							Float.parseFloat(groups.get(i + k));
							++k;
						} catch (NumberFormatException e) {
							break;
						}
					}

					handler.arrayFloatParameter(s[0], s[1],
							parseFloatArray(groups, i + 1, i + k));
					i += k - 2;
				} else if (s[0].equals("texture")) {
					handler.textureParameter(s[1], groups.get(i + 1));
				} else if (s[0].equals("integer") && !s[1].equals("indices")) {
					int value = parseInteger(groups.get(i + 1));
					handler.integerParameter(s[1], value);
				} else if (s[0].equals("bool")) {
					handler.boolParameter(s[1],
							Boolean.parseBoolean(groups.get(i + 1)));
				} else if (s[0].equals("string")) {
					handler.stringParameter(s[1], groups.get(i + 1));
				} else if (s[0].equals("float")) {
					float value = parseFloat(groups.get(i + 1));
					handler.floatParameter(s[1], value);
				} else if (s[0].equals("color") || s[0].equals("rgb")
						|| s[0].equals("point")) {
					int k = 1;
					while (true) {
						try {
							Float.parseFloat(groups.get(i + k));
							++k;
						} catch (NumberFormatException e) {
							break;
						}
					}

					handler.arrayFloatParameter(s[0], s[1],
							parseFloatArray(groups, i + 1, i + k));
					i += k - 2;
				} else
					System.err
							.println("Warning: unhandled element: " + element);
				++i;

			} else if (element.equals("Scale")) {
				float[] array = parseFloatArray(groups, i + 1, i + 4);
				handler.scale(array[0], array[1], array[2]);
				i += array.length;
			} else if (properties.contains(element)) {
				newProperty(element, handler);

				if (element.equals("Texture")) {
					handler.propertyValue(groups.get(i + 1), groups.get(i + 2),
							groups.get(i + 3));
					i += 2;
				} else
					handler.propertyValue(groups.get(i + 1));
				++i;
			} else if (element.equals("LookAt")) {
				float[] array = parseFloatArray(groups, i + 1, i + 10);
				handler.lookAt(array[0], array[1], array[2], array[3],
						array[4], array[5], array[6], array[7], array[8]);
				i += array.length;
			} else if (element.equals("ConcatTransform")) {
				float[] array = parseFloatArray(groups, i + 1, i + 17);
				handler.concatTransform(array[0], array[1], array[2], array[3],
						array[4], array[5], array[6], array[7], array[8],
						array[9], array[10], array[11], array[12], array[13],
						array[14], array[15]);
				i += array.length;
			} else if (element.equals("Transform")) {
				float[] array = parseFloatArray(groups, i + 1, i + 17);
				handler.transform(array[0], array[1], array[2], array[3],
						array[4], array[5], array[6], array[7], array[8],
						array[9], array[10], array[11], array[12], array[13],
						array[14], array[15]);
				i += array.length;
			} else if (element.equals("Translate")) {
				float[] array = parseFloatArray(groups, i + 1, i + 4);
				handler.translate(array[0], array[1], array[2]);
				i += array.length;
			} else if (element.equals("Rotate")) {
				float[] array = parseFloatArray(groups, i + 1, i + 5);
				handler.rotate(array[0], array[1], array[2], array[3]);
				i += array.length;
			} else if (element.equals("WorldBegin"))
				handler.worldBegin();
			else if (element.equals("WorldEnd"))
				handler.worldEnd();
			else if (element.equals("AttributeBegin"))
				handler.attributeBegin();
			else if (element.equals("AttributeEnd"))
				handler.attributeEnd();
			else if (element.equals("TransformBegin"))
				handler.attributeBegin();
			else if (element.equals("TransformEnd"))
				handler.attributeEnd();
			else if (element.equals("ObjectBegin"))
				handler.objectBegin(groups.get(++i));
			else if (element.equals("ObjectEnd"))
				handler.objectEnd();
			else if (element.equals("AttributeEnd"))
				handler.attributeEnd();
			else if (element.equals("ReverseOrientation"))
				handler.reverseOrientation();
			else
				System.err.println("Warning: unhandled element: " + element);
			++i;
		}
	}

	/**
	 * 
	 * @param property
	 * @param handler
	 */
	private void newProperty(String property, PBRTParseHandler handler) {
		endProperty(handler);
		currentProperty = property;
		handler.propertyStart(currentProperty);
	}

	/**
	 * 
	 * @param handler
	 */
	private void endProperty(PBRTParseHandler handler) {
		if (currentProperty != null)
			handler.propertyEnd(currentProperty);
		currentProperty = null;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private int parseInteger(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private float parseFloat(String string) {
		try {
			float result = Float.parseFloat(string);
			if (Math.abs(result) < 1e-6)
				return 0;
			return result;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * 
	 * @param strings
	 * @param start
	 * @param end
	 * @return
	 */
	private int[] parseIntegerArray(List<String> strings, int start, int end) {
		int[] result = new int[end - start];
		for (int i = start; i < end; ++i)
			result[i - start] = parseInteger(strings.get(i));
		return result;
	}

	/**
	 * 
	 * @param strings
	 * @param start
	 * @param end
	 * @return
	 */
	private float[] parseFloatArray(List<String> strings, int start, int end) {
		float[] result = new float[end - start];
		for (int i = start; i < end; ++i)
			result[i - start] = parseFloat(strings.get(i));
		return result;
	}

	/**
	 * 
	 */
	private void push(List<String> groups) {
		if (subString.equals(""))
			return;
		groups.add(subString);
		subString = "";
	}

	/**
	 * 
	 * @param string
	 */
	private void append(String string) {
		subString += string.replaceAll("\"", "");
	}
}
