package util;

/**
 * 
 * @author niels
 * 
 */
public class DoublePrinter {
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String toString(double value) {
		String result = String.format("%.8f", value);

		if (result.matches("[0-9]*\\.[0-9]*[1-9]0+"))
			result = result.replaceAll("0+$", "");
		return result;
	}
}
