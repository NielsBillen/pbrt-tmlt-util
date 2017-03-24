package util;

/**
 * 
 * @author Niels
 * @version 0.1
 * 
 */
public class Printer {
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String print(double value) {
		String result = String.format("%.10f", value);

		if (result.matches("[0-9]*\\.0+"))
			return result.replaceAll("0+$", "");
		else if (result.matches("[0-9]*\\.[0-9]*[1-9]0+"))
			return result.replaceAll("0+$", "");
		return result;
	}
}
