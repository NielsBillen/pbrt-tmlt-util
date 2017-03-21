package util;

import java.util.TreeMap;

/**
 * A utility which allows to print tabular data.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class TablePrinter {
	private TreeMap<Integer, TreeMap<Integer, String>> table = new TreeMap<Integer, TreeMap<Integer, String>>();
	private TreeMap<Integer, Integer> columnWidth = new TreeMap<Integer, Integer>();
	private final String seperator;

	private final String headSeperator;
	private final String tailSeperator;

	/**
	 * Creates a new table printer.
	 */
	public TablePrinter() {
		this("|", "|", "|");
	}

	/**
	 * 
	 * @param seperator
	 */
	public TablePrinter(String seperator) {
		this(seperator, seperator, seperator);
	}

	/**
	 * 
	 * @param seperator
	 */
	public TablePrinter(String seperator, String headSeperator,
			String tailSeperator) {
		this.seperator = seperator;
		this.headSeperator = headSeperator;
		this.tailSeperator = tailSeperator;

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
		return String.format("%-" + length + "%s", string);
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
				if (column == 0) {
					if (headSeperator != null)
						builder.append(headSeperator);
				} else if (column < nbOfColumns)
					builder.append(seperator);

				String entry = appendToLength(get(row, column),
						getColumnWidth(column));
				builder.append(" ").append(entry).append(" ");
			}
			if (tailSeperator != null)
				builder.append(tailSeperator);
			if (row < nbOfRows - 1)
				builder.append("\n");
		}

		return builder.toString();
	}
}