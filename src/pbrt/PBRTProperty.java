package pbrt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Represents an element has a name, a value and which can have several
 * properties
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PBRTProperty extends PBRTElement {
	// List with children.
	private String elementName;
	private List<String> elementValues = new ArrayList<String>();

	private final TreeMap<String, Integer> integers = new TreeMap<String, Integer>();
	private final TreeMap<String, String> strings = new TreeMap<String, String>();
	private final TreeMap<String, String> textures = new TreeMap<String, String>();
	private final TreeMap<String, Float> floats = new TreeMap<String, Float>();
	private final TreeMap<String, Boolean> booleans = new TreeMap<String, Boolean>();
	private final TreeMap<String, PBRTArray<Float>> floatArray = new TreeMap<String, PBRTArray<Float>>();
	private final TreeMap<String, PBRTArray<Integer>> intArray = new TreeMap<String, PBRTArray<Integer>>();

	/**
	 * Creates a new {@link PBRTProperty} which has the given name.
	 * 
	 * @param name
	 *            The name for the PBRTElement.
	 * @throws NullPointerException
	 *             When the given name is null.
	 * @throws IllegalArgumentException
	 *             When the name is empty.
	 */
	public PBRTProperty(String name) {
		setName(name);
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
				"cannot add children to PBRTReverseOrientation!");
	}

	/**
	 * Sets the name of this {@link PBRTProperty} to the given name.
	 * 
	 * @param name
	 *            the name for the {@link PBRTProperty}.
	 * @throws NullPointerException
	 *             when the given name is null.
	 * @throws IllegalArgumentException
	 *             when the name is empty.
	 */
	private void setName(String name) throws NullPointerException,
			IllegalArgumentException {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		name = name.trim();
		if (name.isEmpty())
			throw new IllegalArgumentException(
					"the name of the PBRTElement cannot be empty!");
		this.elementName = name;
	}

	/**
	 * Returns the name of this {@link PBRTProperty}.
	 * 
	 * @return the name of this {@link PBRTProperty}.
	 */
	public String getName() {
		return elementName;
	}

	/**
	 * Sets the value of this {@link PBRTProperty} to the given value. If this
	 * {@link PBRTProperty} previously had one or more values, these are first
	 * cleared.
	 * 
	 * @param value
	 *            value for this {@link PBRTProperty}.
	 * @throws NullPointerException
	 *             when the given value is null.
	 * @throws IllegalArgumentException
	 *             when the value is empty.
	 * @return a reference to this property.
	 */
	public PBRTProperty setValue(String value) throws NullPointerException,
			IllegalArgumentException {
		if (value == null)
			throw new NullPointerException("the given value cannot be null!");
		value = value.trim();

		if (value.isEmpty())
			throw new IllegalArgumentException(
					"the value of the PBRTElement cannot be set to empty!");
		clearValues();
		elementValues.add(value);

		return this;
	}

	/**
	 * Adds the given value to the {@link PBRTProperty}.
	 * 
	 * @param value
	 *            the value to append to this {@link PBRTProperty}.
	 * @throws NullPointerException
	 *             when the given value is null.
	 * @throws IllegalArgumentException
	 *             when the value is empty.
	 * @return a reference to this property.
	 */
	public PBRTProperty addValue(String value) throws NullPointerException,
			IllegalArgumentException {
		if (value == null)
			throw new NullPointerException("the given value cannot be null!");
		value = value.trim();

		if (value.isEmpty())
			throw new IllegalArgumentException(
					"the value of the PBRTElement cannot be set to empty!");
		elementValues.add(value);

		return this;
	}

	/**
	 * Returns whether this {@link PBRTProperty} has at least one value.
	 * 
	 * @return whether this {@link PBRTProperty} has at least one value.
	 */
	public boolean hasValue() {
		return nbOfValues() > 0;
	}

	/**
	 * Returns the first value of this {@link PBRTProperty} if one is present.
	 * 
	 * @throws NoSuchElementException
	 *             when no first element is present.
	 * @return the first value of this {@link PBRTProperty} if one is present.
	 */
	public String getFirstValue() throws NoSuchElementException {
		if (!hasValue())
			throw new NoSuchElementException(
					"this PBRTProperty does not have any values!");
		return getValueAt(0);
	}

	/**
	 * Returns this {@link PBRTProperty}'s value at the given index.
	 * 
	 * @param index
	 *            the index of the value to retrieve.
	 * @throws IndexOutOfBoundsException
	 *             when the given index is outside of the valid range.
	 * @return this {@link PBRTProperty}'s value at the given index.
	 */
	public String getValueAt(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= nbOfValues())
			throw new IndexOutOfBoundsException(
					"the given index is outside of the range of the element values!");
		return elementValues.get(index);
	}

	/**
	 * Returns the number of element values this {@link PBRTProperty} has.
	 * 
	 * @return the number of element values this {@link PBRTProperty} has.
	 */
	public int nbOfValues() {
		return elementValues.size();
	}

	/**
	 * Removes all the values of this {@link PBRTProperty}.
	 */
	public void clearValues() {
		elementValues.clear();
	}

	/**
	 * Returns a shallow copy of the {@link List} with all the values of this
	 * {@link PBRTProperty}.
	 * 
	 * @return a shallow copy of the {@link List} with all the values of this
	 *         {@link PBRTProperty}.
	 */
	public List<String> getValues() {
		return new ArrayList<String>(elementValues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#describeSelf(java.lang.StringBuilder)
	 */
	@Override
	protected void describeSelf(StringBuilder b, int indent) {
		indent(b, indent).append(elementName);

		if (hasValue())
			for (String elementValue : elementValues)
				b.append(" \"" + elementValue + "\"");
		for (Entry<String, String> e : strings.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"string " + e.getKey() + "\" [ \"" + e.getValue()
					+ "\" ]");
		}
		for (Entry<String, Boolean> e : booleans.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"bool " + e.getKey() + "\" [ \"" + e.getValue() + "\" ]");
		}
		for (Entry<String, String> e : textures.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"texture " + e.getKey() + "\" [ \"" + e.getValue()
					+ "\" ]");
		}
		for (Entry<String, Float> e : floats.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"float " + e.getKey() + "\" [ " + e.getValue() + " ]");
		}
		for (Entry<String, Integer> e : integers.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"integer " + e.getKey() + "\" [ " + e.getValue() + " ]");
		}
		for (Entry<String, PBRTArray<Integer>> e : intArray.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"" + e.getKey() + "\" " + e.getValue());
		}
		for (Entry<String, PBRTArray<Float>> e : floatArray.entrySet()) {
			indent(b.append("\n"), indent + 1);
			b.append("\"" + e.getKey() + "\" " + e.getValue());
		}

		if (nbOfSettings() > 0)
			b.append("\n");
		b.append("\n");
	}

	// public <T> T getSetting(Class<T> c, String name) {
	// if (integers.getClass().getGenericInterfaces())
	// return null;
	//
	// }

	/**
	 * Returns whether this property has a setting with the given name.
	 * 
	 * @param name
	 *            The name of the setting.
	 * @return whether a setting with the given name exists.
	 */
	public boolean hasSetting(String name) {
		return hasIntegerSetting(name) || hasStringSetting(name)
				|| hasTextureSetting(name) || hasFloatSetting(name)
				|| hasBooleanSetting(name) || hasFloatArraySetting(name)
				|| hasIntArraySetting(name);
	}

	/**
	 * 
	 * @return
	 */
	public int nbOfSettings() {
		return integers.size() + strings.size() + textures.size()
				+ floats.size() + booleans.size() + floatArray.size()
				+ intArray.size();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getSetting(String name) {
		if (hasIntegerSetting(name))
			return getIntegerSetting(name).toString();
		else if (hasStringSetting(name))
			return getStringSetting(name).toString();
		else if (hasTextureSetting(name))
			return getTextureSetting(name);
		else if (hasFloatSetting(name))
			return getFloatSetting(name).toString();
		else if (hasBooleanSetting(name))
			return (getBoolSetting(name) ? "true" : "false");
		else if (hasFloatArraySetting(name))
			return getFloatArraySetting(name).toString();
		else if (hasIntArraySetting(name))
			return getIntegerArraySetting(name).toString();
		return null;

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeSetting(String name) {
		if (hasSetting(name)) {
			integers.remove(name);
			strings.remove(name);
			textures.remove(name);
			floats.remove(name);
			booleans.remove(name);
			floatArray.remove(name);
			intArray.remove(name);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasIntegerSetting(String name) {
		return integers.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasStringSetting(String name) {
		return strings.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasTextureSetting(String name) {
		return textures.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasFloatSetting(String name) {
		return floats.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasBooleanSetting(String name) {
		return booleans.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasFloatArraySetting(String name) {
		return floatArray.containsKey(name);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasIntArraySetting(String name) {
		return intArray.containsKey(name);
	}

	/**
	 * Returns the value of the given integer setting.
	 * 
	 * @param name
	 * @return
	 */
	public Integer getIntegerSetting(String name) {
		return integers.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param integer
	 * @throws NullPointerException
	 */
	public PBRTProperty setIntegerSetting(String name, int integer)
			throws NullPointerException {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		integers.put(name, integer);
		return this;
	}

	/**
	 * Returns the value of a certain string setting or null when it is not
	 * present.
	 * 
	 * @param name
	 *            The name of the string setting.
	 * @return the value of the string setting or null when it is not present.
	 */
	public String getStringSetting(String name) {
		return strings.get(name);
	}

	/**
	 * Sets the value of the given string setting to the given value.
	 * 
	 * @param name
	 *            The name of the string setting to set.
	 * @param value
	 *            The value of the string setting to set.
	 * @throws NullPointerException
	 *             When one of the given string is null
	 */
	public PBRTProperty setStringSetting(String name, String value) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		if (value == null)
			throw new NullPointerException("the given value is null!");
		removeSetting(name);
		strings.put(name, value);
		return this;
	}

	/**
	 * Returns the value of a certain string setting or null when it is not
	 * present.
	 * 
	 * @param name
	 *            The name of the string setting.
	 * @return the value of the string setting or null when it is not present.
	 */
	public String getTextureSetting(String name) {
		return textures.get(name);
	}

	/**
	 * Sets the value of the given string setting to the given value.
	 * 
	 * @param name
	 *            The name of the string setting to set.
	 * @param value
	 *            The value of the string setting to set.
	 * @throws NullPointerException
	 *             When one of the given string is null
	 */
	public PBRTProperty setTextureSetting(String name, String value) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		if (value == null)
			throw new NullPointerException("the given value is null!");
		removeSetting(name);
		textures.put(name, value);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Float getFloatSetting(String name) {
		return floats.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param setting
	 * @throws NullPointerException
	 */
	public PBRTProperty setFloatSetting(String name, float setting) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		floats.put(name, setting);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @param setting
	 * @throws NullPointerException
	 */
	public PBRTProperty setFloatSetting(String name, double setting) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		floats.put(name, (float) setting);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Boolean getBoolSetting(String name) {
		return booleans.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param setting
	 * @throws NullPointerException
	 */
	public PBRTProperty setBoolSetting(String name, boolean bool) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		booleans.put(name, bool);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public PBRTArray<Float> getFloatArraySetting(String name) {
		return floatArray.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param setting
	 * @return
	 */
	public PBRTProperty setFloatArraySetting(String name, String type,
			PBRTArray<Float> setting) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		floatArray.put(name + " " + type, setting);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public PBRTArray<Integer> getIntegerArraySetting(String name) {
		return intArray.get(name);
	}

	/**
	 * 
	 * @param name
	 * @param setting
	 * @return
	 */
	public PBRTProperty setIntegerArraySetting(String name, String type,
			PBRTArray<Integer> setting) {
		if (name == null)
			throw new NullPointerException("the given name is null!");
		removeSetting(name);
		intArray.put(name + " " + type, setting);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#findProperty(java.lang.String)
	 */
	@Override
	public PBRTProperty findProperty(String name) {
		if (getName().equals(name))
			return this;
		else
			return super.findProperty(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#findProperty(java.lang.String)
	 */
	@Override
	public List<PBRTProperty> findProperties(String name) {
		List<PBRTProperty> properties = super.findProperties(name);

		if (getName().equals(name))
			properties.add(this);

		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see parser.PBRTElement#copy()
	 */
	@Override
	public PBRTElement copy() {
		PBRTProperty result = new PBRTProperty(elementName);
		for (String value : elementValues)
			result.addValue(value);

		for (Entry<String, Integer> e : integers.entrySet())
			result.setIntegerSetting(e.getKey(), e.getValue());
		for (Entry<String, String> e : strings.entrySet())
			result.setStringSetting(e.getKey(), e.getValue());
		for (Entry<String, String> e : textures.entrySet())
			result.setTextureSetting(e.getKey(), e.getValue());
		for (Entry<String, Float> e : floats.entrySet())
			result.setFloatSetting(e.getKey(), e.getValue());
		for (Entry<String, Boolean> e : booleans.entrySet())
			result.setBoolSetting(e.getKey(), e.getValue());
		for (Entry<String, PBRTArray<Float>> e : floatArray.entrySet()) {
			String key = e.getKey();
			int index = key.indexOf(" ");

			String name = key.substring(0, index);
			String type = key.substring(index + 1);
			result.setFloatArraySetting(name, type, e.getValue());
		}

		for (Entry<String, PBRTArray<Integer>> e : intArray.entrySet()) {
			String key = e.getKey();
			int index = key.indexOf(" ");

			String name = key.substring(0, index);
			String type = key.substring(index + 1);
			result.setIntegerArraySetting(name, type, e.getValue());
		}

		return result;
	}
}
