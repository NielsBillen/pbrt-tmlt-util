package pbrt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents an abstract element in a PBRT scene file. The {@link PBRTElement}s
 * form a hierarchical tree.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class PBRTElement implements Iterable<PBRTElement> {
	// The list of the children.
	private final List<PBRTElement> children = new ArrayList<PBRTElement>();

	/**
	 * Add's the given element to this element.
	 * 
	 * @param element
	 *            The element to add to this element.
	 * @throws NullPointerException
	 *             When the given element is null.
	 * @throws UnsupportedOperationException
	 *             When the operation is not supported for this given element.
	 */
	public void addChild(PBRTElement element) throws NullPointerException,
			UnsupportedOperationException {
		if (element == null)
			throw new NullPointerException("the given element is null!");
		children.add(element);
	}

	/**
	 * Returns the child at the given index
	 * 
	 * @param index
	 *            Index of the child to retrieve.
	 * @return the child at the given index.
	 * @throws IndexOutOfBoundsException
	 *             When the given index is out of bounds.
	 * @throws UnsupportedOperationException
	 *             When the given operation is not supported.
	 */
	public PBRTElement getChild(int index) throws IndexOutOfBoundsException,
			UnsupportedOperationException {
		if (index < 0 || index >= nbOfChildren())
			throw new IndexOutOfBoundsException("the given index " + index
					+ " is out of bounds!");
		return children.get(index);
	}

	/**
	 * Returns the number of children of this element.
	 * 
	 * @return the number of children of this element.
	 */
	public int nbOfChildren() {
		return children.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<PBRTElement> iterator() {
		return children.iterator();
	}

	/**
	 * Returns a {@link String} which consists of the given number of tabs.
	 * 
	 * @param tabs
	 *            the number of tabs in the result.
	 * @return a {@link String} which consists of the given number of tabs.
	 */
	protected String indent(int tabs) {
		if (tabs < 1)
			return "";
		else if (tabs == 1)
			return "\t";
		else if (tabs % 2 == 0) {
			String indentation = indent(tabs / 2);
			return indentation.concat(indentation);
		} else
			return "\t".concat(indent(tabs - 1));
	}

	/**
	 * Appends the given number of tabs to the given {@link StringBuilder}.
	 * 
	 * @param builder
	 *            the {@link StringBuilder} to append the tabs to.
	 * @param tabs
	 *            the number of tabs which should be appended to the
	 *            {@link StringBuilder}.
	 * @throws NullPointerException
	 *             when the given {@link StringBuilder} is null.
	 * @return the given {@link StringBuilder}.
	 */
	protected StringBuilder indent(StringBuilder builder, int tabs) {
		if (builder == null)
			throw new NullPointerException("the given StringBuilder is null!");
		return builder.append(indent(tabs));
	}

	/**
	 * Appends the content of this {@link PBRTElement} to the given
	 * {@link StringBuilder}.
	 * 
	 * @param builder
	 *            the {@link StringBuilder} to append to.
	 * @throws NullPointerException
	 *             when the given {@link StringBuilder} is null.
	 */
	private void append(StringBuilder builder) throws NullPointerException {
		append(builder, 0);
	}

	/**
	 * Appends the content of this PBRT element to the given StringBuilder.
	 * 
	 * @param builder
	 *            the StringBuilder to append to.
	 * @param indent
	 *            amount of indentation for pretty printing.
	 * @throws NullPointerException
	 *             when the given string builder is null.
	 */
	protected void append(StringBuilder builder, int indent)
			throws NullPointerException {
		if (builder == null)
			throw new NullPointerException("the given string builder is null!");
		describeSelf(builder, indent);
		for (PBRTElement e : this)
			e.append(builder, indent + 1);
	}

	/**
	 * Prints a description of this {@link PBRTElement} without the contents of
	 * its children.
	 * 
	 * @param builder
	 *            the builder to write this {@link PBRTElement} to.
	 * @param indent
	 *            the amount of indentation.
	 * @throws NullPointerException
	 *             When the given string builder is null.
	 */
	protected abstract void describeSelf(StringBuilder builder, int indent)
			throws NullPointerException;

	/**
	 * Returns the first property in this {@link PBRTElement} which matches the
	 * given name or null when no such element is found.
	 * 
	 * @param name
	 *            the name of the element.
	 * @return the first property in this {@link PBRTElement} which matches the
	 *         given name or null when no such element is found.
	 */
	public PBRTProperty findProperty(String name) {
		if (name == null)
			return null;
		for (PBRTElement e : this) {
			PBRTProperty p = e.findProperty(name);
			if (p != null)
				return p;
		}
		return null;
	}

	/**
	 * Returns a {@link List} containing all the properties which match the
	 * given name.
	 * 
	 * @param name
	 *            the name of the property.
	 * @return a {@link List} containing all the properties which match the
	 *         given name.
	 */
	public List<PBRTProperty> findProperties(String name) {
		List<PBRTProperty> result = new ArrayList<PBRTProperty>();

		if (name == null)
			return result;

		for (PBRTElement e : this)
			result.addAll(e.findProperties(name));

		return result;
	}

	/**
	 * Returns the first value of the {@link PBRTProperty} with the given name.
	 * Null is returned, when no such {@link PBRTProperty} exist or when the
	 * {@link PBRTProperty} does not have any values.
	 * 
	 * @param name
	 *            the name of the {@link PBRTProperty} to retrieve the first
	 *            value of.
	 * @return the first value of the {@link PBRTProperty} with the given name.
	 */
	public String findFirstPropertyValue(String name) {
		PBRTProperty property = findProperty(name);

		if (property == null || !property.hasValue())
			return null;
		return property.getFirstValue();
	}

	/**
	 * 
	 * @param query
	 * @return
	 */
	public String findSetting(String query) {
		String[] split = query.split("\\.");
		String key = split[split.length - 1];

		if (split.length < 2)
			return null;

		List<PBRTProperty> properties = findProperties(split[0]);

		for (int i = 1; i < split.length - 2; ++i) {
			List<PBRTProperty> t = new ArrayList<PBRTProperty>();
			for (PBRTProperty p : properties)
				t.addAll(p.findProperties(split[i]));
			properties = t;
		}

		for (PBRTProperty p : properties)
			if (p.hasSetting(key))
				return p.getSetting(key);
		return null;
	}

	/**
	 * Returns this {@link PBRTElement} as a formatted {@link String}.
	 * 
	 * @return this {@link PBRTElement} as a formatted {@link String}.
	 */
	public String print() {
		StringBuilder builder = new StringBuilder();
		append(builder);
		return builder.toString();
	}

	/**
	 * Writes this {@link PBRTElement} in the {@link File} with the given
	 * filename.
	 * 
	 * @param filename
	 *            the filename of the {@link File} to write to.
	 * @throws NullPointerException
	 *             when the given filename is null
	 * @throws IOException
	 *             when an exception occurs during the writing of the
	 *             {@link File}.
	 */
	public File print(String filename) throws NullPointerException, IOException {
		if (filename == null)
			throw new NullPointerException("the given filename is null!");
		return print(new File(filename));
	}

	/**
	 * Writes this {@link PBRTElement} in the {@link File}.
	 * 
	 * @param filename
	 *            the filename of the {@link File} to write to.
	 * @throws NullPointerException
	 *             when the given {@link File} is null
	 * @throws IOException
	 *             when an exception occurs during the writing of the
	 *             {@link File}.
	 */
	public File print(File file) throws NullPointerException, IOException {
		if (file == null)
			throw new NullPointerException("the given file is null!");
		if (file.isDirectory())
			throw new IllegalArgumentException("the given file is a directory!");

		// if the file does not exist, check whether the folders exists
		if (!file.exists()) {
			File parent = file.getAbsoluteFile().getParentFile();
			if (parent != null && !parent.exists())
				parent.mkdirs();
		}

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(),
				Charset.forName("US-ASCII"))) {
			writer.write(print());
			writer.close();
		}

		return file;
	}

	/**
	 * Writes this {@link PBRTElement} to the {@link File} specified by the
	 * given {@link Path}.
	 * 
	 * @param path
	 *            the {@link Path} of the {@link File} to write to.
	 * @throws NullPointerException
	 *             when the given {@link Path} is null
	 * @throws IOException
	 *             when an exception occurs during the writing of the
	 *             {@link File}.
	 */
	public File print(Path path) throws NullPointerException, IOException {
		return print(path.toFile());
	}

	/**
	 * 
	 * @return
	 */
	public abstract PBRTElement copy();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		append(b);
		return b.toString().replaceAll("\n", "\t").replaceAll("\t+", " ")
				.replaceAll(" +", " ").trim();
	}
}
