package util;

import java.util.TreeMap;

/**
 * 
 * @author niels
 * 
 */
public class TablePrinter {
	private TreeMap<Integer, TreeMap<Integer, String>> table = new TreeMap<Integer, TreeMap<Integer, String>>();
	private TreeMap<Integer, Integer> columnWidth = new TreeMap<Integer, Integer>();
	private final String seperator;

	private final String headSeperator;
	private final boolean seperateHead;

	private final String tailSeperator;
	private final boolean seperateTail;

	/**
	 * 
	 */
	public TablePrinter() {
		this("|", "|", true, "|", true);
	}

	/**
	 * 
	 * @param seperator
	 */
	public TablePrinter(String seperator) {
		this(seperator, seperator, true, seperator, true);
	}

	/**
	 * 
	 * @param seperator
	 */
	public TablePrinter(String seperator, String headSeperator,
			boolean seperateHead, String tailSeperator, boolean seperateTail) {
		this.seperator = seperator;
		this.headSeperator = headSeperator;
		this.seperateHead = seperateHead;

		this.tailSeperator = tailSeperator;
		this.seperateTail = seperateTail;

	}

	/**
	 * 
	 */
	public void clear() {
		table.clear();
		columnWidth.clear();
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param data
	 */
	public void add(int row, int column, String data) {
		// add the entry
		TreeMap<Integer, String> r = table.get(row);

		if (r == null) {
			r = new TreeMap<Integer, String>();
			table.put(row, r);
		}
		r.put(column, data);

		// update the column width
		Integer width = columnWidth.get(column);
		if (width == null || width < data.length())
			columnWidth.put(column, data.length());
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @param data
	 */
	public void add(int row, int column, String format, Object... arguments) {
		add(row, column, String.format(format, arguments));
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public String get(int row, int column) {
		TreeMap<Integer, String> r = table.get(row);

		if (r == null)
			return "";
		String result = r.get(column);

		if (result == null)
			return "";
		return result;
	}

	/**
	 * 
	 * @param column
	 * @return
	 */
	public int getColumnWidth(int column) {
		Integer width = columnWidth.get(column);

		return width == null ? 0 : width;
	}

	/**
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
	private String appendToLength(String string, int length) {
		while (string.length() < length)
			string = string.concat(" ");
		return string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int nbOfRows = table.size();
		int nbOfColumns = columnWidth.size();

		if (nbOfColumns == 0 || nbOfRows == 0)
			return "";

		StringBuilder builder = new StringBuilder();

		for (int row = 0; row < nbOfRows; ++row) {
			for (int column = 0; column < nbOfColumns; ++column) {
				if (column > 0 && column < nbOfColumns)
					builder.append(seperator);
				else if (seperateHead)
					builder.append(headSeperator);

				String entry = appendToLength(get(row, column),
						getColumnWidth(column));
				builder.append(" ").append(entry).append(" ");
			}
			if (seperateTail)
				builder.append(tailSeperator);
			if (row < nbOfRows - 1)
				builder.append("\n");
		}

		return builder.toString();
	}
}