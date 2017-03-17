package pbrt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a generic array of a data type in the PBRT file format.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PBRTArray<T> extends PBRTElement {
	private final List<T> array = new ArrayList<T>();

	/**
	 * Creates a new empty array.
	 */
	public PBRTArray() {
	}

	/**
	 * 
	 * @param t
	 */
	@SafeVarargs
	public PBRTArray(T... t) {
		for (T element : t)
			add(element);
	}

	/**
	 * Creates a new {@link PBRTArray} filled with the elements from the given
	 * {@link Collection}.
	 * 
	 * @param collection
	 *            the {@link Collection} with the elements to initialize this
	 *            {@link PBRTArray}.
	 * @throws NullPointerException
	 *             when the given collection is null.
	 * @throws NullPointerException
	 *             when one of the given elements is null.
	 */
	public PBRTArray(Collection<T> collection) throws NullPointerException {
		if (collection == null)
			throw new NullPointerException("the given collection is null!");
		for (T element : collection)
			add(element);
	}

	/**
	 * Adds the given element to the {@link PBRTArray}.
	 * 
	 * @param element
	 *            the element to add to this {@link PBRTArray}.
	 * @throws NullPointerException
	 *             when the given element is null.
	 */
	public void add(T element) throws NullPointerException {
		if (element == null)
			throw new NullPointerException("the given element is null!");
		array.add(element);
	}

	/**
	 * Returns the element at the given index inside this {@link PBRTArray}.
	 * 
	 * @param index
	 *            the index of the element to retrieve.
	 * @throws IndexOutOfBoundsException
	 *             when the given index is smaller than zero or larger than or
	 *             equal to the number of elements in this {@link PBRTArray}.
	 */
	public T get(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException("the given index '" + index
					+ "' is outside the range of this array!");
		return array.get(index);
	}

	/**
	 * Returns the number of elements in this {@link PBRTArray}.
	 * 
	 * @return the number of elements in this {@link PBRTArray}.
	 */
	public int size() {
		return array.size();
	}

	/**
	 * Removes the element at the given index in this {@link PBRTArray}.
	 * 
	 * @param index
	 *            the index of the element to remove.
	 * @throws IndexOutOfBoundsException
	 *             when the given index is smaller than zero or larger than or
	 *             equal to the number of elements in this {@link PBRTArray}.
	 * @return the element which is removed.
	 */
	public T remove(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException("the given index '" + index
					+ "' is outside the range of this array!");
		return array.remove(index);
	}

	/**
	 * Returns the index of the first occurrence of the given element or -1 when
	 * the given element is not present within this {@link PBRTArray}.
	 * 
	 * @param element
	 *            the element to retrieve the first index of.
	 * @return the index of the first occurrence of the given element or -1 when
	 *         the given element is not present within this {@link PBRTArray}.
	 */
	public int indexOf(T element) {
		if (element == null)
			return -1;
		for (int i = 0; i < size(); ++i)
			if (get(i).equals(element))
				return i;
		return -1;
	}

	/**
	 * Removes the first occurrence of the given element in this
	 * {@link PBRTArray} and returns the index of the element that was removed
	 * (or -1 when the given element was not present).
	 * 
	 * @param element
	 *            the element to remove.
	 * @throws ConcurrentModificationException
	 *             when concurrent modification of this {@link PBRTArray}
	 *             prevents from returning the consistent index which belongs to
	 *             the element to be removed due to concurreny.
	 * @return the index of the element that was removed (or -1 when the given
	 *         element was not present).
	 */
	public int remove(T element) throws ConcurrentModificationException {
		int index = indexOf(element);

		if (index >= 0) {
			try {
				T e = remove(index);

				if (!e.equals(element))
					throw new ConcurrentModificationException(
							"tried to remove an element which should exist, but due to concurrent modification has been removed already!");
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException(
						"tried to remove an element which should exist, but due to concurrent modification has been removed already!");
			}
		}

		return index;
	}

	/**
	 * Returns an {@link Iterator} over the contents of this array.
	 * 
	 * @return an {@link Iterator} over the contents of this array.
	 */
	public Iterator<T> arrayIterator() {
		return array.iterator();
	}

	/**
	 * Returns a shallow copy of the contents of this {@link PBRTArray}.
	 * 
	 * @return a shallow copy of the contents of this {@link PBRTArray}.
	 */
	public List<T> getList() {
		return new ArrayList<T>(array);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder, int)
	 */
	@Override
	protected void describeSelf(StringBuilder builder, int indent) {
		// use an additional string builder to keep array printing within
		// terminal length
		StringBuilder line = new StringBuilder();
		indent(line, indent);
		line.append("[");

		// whether an element has already been added
		boolean hasElement = false;

		// iterate over the contents of the array.
		for (int i = 0; i < size(); ++i) {
			String element = get(i).toString();

			/*
			 * check whether we will exceed the terminal width. However, we only
			 * do this when at least one element has been added to the line to
			 * avoid rejecting elements which are themselves longer than the
			 * terminal width.
			 */
			if (hasElement && line.length() + element.length() > 80) {
				builder.append(line).append("\n  ");
				line = new StringBuilder();
				indent(line, indent);
				line.append(element);
			} else {
				line.append(" ").append(element);
				hasElement = true;
			}
		}
		builder.append(line);
		builder.append(" ]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#addChild(parser.PBRTElement)
	 */
	@Override
	public void addChild(PBRTElement element) throws NullPointerException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"cannot add a child to a PBRTArray!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		return new PBRTArray<T>(array);
	}
}
