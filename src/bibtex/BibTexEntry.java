package bibtex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import bibtex.types.BibTexArticle;
import bibtex.types.BibTexBook;
import bibtex.types.BibTexBooklet;
import bibtex.types.BibTexConference;
import bibtex.types.BibTexInBook;
import bibtex.types.BibTexInCollection;
import bibtex.types.BibTexInProceedings;
import bibtex.types.BibTexMisc;
import bibtex.types.BibTexPhDThesis;
import bibtex.types.BibTexProceedings;
import bibtex.types.BibTexTechReport;

/**
 * Abstract class representing an entry in a BibTex file.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public abstract class BibTexEntry implements Iterable<Entry<String, String>> {
	/**
	 * Citation key for the bibtex entry.
	 */
	private String citationKey = "key";

	/**
	 * A sorted map containing all the keys and values in this entry.
	 */
	private final TreeMap<String, String> fields = new TreeMap<String, String>(
			getKeyComparator());

	/**
	 * Creates a new {@link BibTexEntry} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexEntry}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexEntry(String citationKey) throws NullPointerException,
			IllegalArgumentException {
		setCitationKey(citationKey);
	}

	/**
	 * Returns a {@link BibTexEntry} which has the given type and with the given
	 * citation key.
	 * 
	 * @param entryType
	 * @param citationKey
	 * @return
	 */
	public static BibTexEntry getEntry(String entryType, String citationKey) {
		String cleanedEntryType = entryType.trim().toLowerCase();

		if (cleanedEntryType.equals("article"))
			return new BibTexArticle(citationKey);
		else if (cleanedEntryType.equals("book"))
			return new BibTexBook(citationKey);
		else if (cleanedEntryType.equals("booklet"))
			return new BibTexBooklet(citationKey);
		else if (cleanedEntryType.equals("conference"))
			return new BibTexConference(citationKey);
		else if (cleanedEntryType.equals("inbook"))
			return new BibTexInBook(citationKey);
		else if (cleanedEntryType.equals("incollection"))
			return new BibTexInCollection(citationKey);
		else if (cleanedEntryType.equals("inproceedings"))
			return new BibTexInProceedings(citationKey);
		else if (cleanedEntryType.equals("misc"))
			return new BibTexMisc(citationKey);
		else if (cleanedEntryType.equals("phdthesis"))
			return new BibTexPhDThesis(citationKey);
		else if (cleanedEntryType.equals("proceedings"))
			return new BibTexProceedings(citationKey);
		else if (cleanedEntryType.equals("techreport"))
			return new BibTexTechReport(citationKey);
		else
			throw new IllegalStateException("unknown bibtex entry type '"
					+ entryType + "'!");
	}

	/**
	 * Sets the citation key of this {@link BibTexEntry} to the given citation
	 * key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexEntry}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public void setCitationKey(String citationKey) throws NullPointerException,
			IllegalArgumentException {
		if (citationKey == null)
			throw new NullPointerException("the given citation key is null!");
		if (citationKey.isEmpty())
			throw new IllegalArgumentException(
					"the citation key cannot be empty!");
		this.citationKey = citationKey;
	}

	/**
	 * Returns the citation key of this {@link BibTexEntry}.
	 * 
	 * @return the citation key of this {@link BibTexEntry}.
	 */
	public String getCitationKey() {
		return citationKey;
	}

	/**
	 * Adds the a field with the given key and corresponding value to this
	 * {@link BibTexEntry}.
	 * 
	 * When a field with the same key is already present, it is overwritten. The
	 * method returns true when the previous key is overwritten.
	 * 
	 * @param key
	 *            the key of the field.
	 * @param value
	 *            the value of the field.
	 * @throws NullPointerException
	 *             when the given key is null.
	 * @throws NullPointerException
	 *             when the given value is null.
	 * @throws IllegalArgumentException
	 *             when the given key is empty.
	 * @return true when a previous key is overwritten.
	 */
	public boolean addField(String key, String value)
			throws NullPointerException, IllegalArgumentException {
		if (key == null)
			throw new NullPointerException("the key cannot be null!");
		if (value == null)
			throw new NullPointerException("the value cannot be null!");

		// Clean the entries
		key = key.trim().toLowerCase();
		value = value.trim();

		// Check the trimmed arguments
		if (key.isEmpty())
			throw new IllegalArgumentException("the key cannot be empty!");
		if (value.isEmpty())
			throw new IllegalArgumentException("the value cannot be empty!");

		// Replace dashes if necessary
		if (key.equals("page") || key.equals("pages")) {
			value = value.replaceAll("-", "--").replaceAll(" *--+ *", " -- ");
		}
		return fields.put(key, value) != null;
	}

	/**
	 * Adds an author with the given first and last name to this
	 * {@link BibTexEntry}.
	 * 
	 * @param firstName
	 *            the first name of the author.
	 * @param lastName
	 *            the second name of the author.
	 * @throws NullPointerException
	 *             when the first name is null.
	 * @throws NullPointerException
	 *             when the second name is null.
	 * @throws IllegalArgumentException
	 *             when the first and last name are empty.
	 */
	public void addAuthor(String firstName, String lastName)
			throws NullPointerException {
		if (firstName == null)
			throw new NullPointerException("the given first name is null!");
		if (lastName == null)
			throw new NullPointerException("the given last name is null!");
		if (firstName.isEmpty() && lastName.isEmpty())
			throw new IllegalArgumentException(
					"the first and last name cannot both be empty!");
		if (!fields.containsKey("author") || get("author").isEmpty())
			fields.put("author", lastName + ", " + firstName);
		else
			fields.put("author", fields.get("author") + " and " + lastName
					+ ", " + firstName);
	}

	/**
	 * Returns a {@link List} of {@link BibTexAuthor}s which wrote the
	 * {@link BibTexEntry}.
	 * 
	 * @return a {@link List} of {@link BibTexAuthor}s which wrote the
	 *         {@link BibTexEntry}.
	 */
	public List<BibTexAuthor> getAuthors() {
		List<BibTexAuthor> authors = new ArrayList<BibTexAuthor>();

		if (contains("author")) {
			String[] split = get("author").split(
					"(?<=.{1,1000} {0,1000}, {0,1000}.{1,1000}) +and +");
			for (String author : split) {
				String[] authorSplit = author.split(" *, *");
				if (authorSplit.length != 2)
					throw new IllegalStateException(
							"no comma separating the first and last name has "
									+ "been detected in file with citation key '"
									+ citationKey + "' at author '" + author
									+ "'!");
				authors.add(new BibTexAuthor(authorSplit[1], authorSplit[0]));
			}
		}
		return authors;
	}

	/**
	 * Returns whether the given key is present in this {@link BibTexEntry}.
	 * 
	 * @param key
	 *            the key to test.
	 * @return true when the given key is present in this {@link BibTexEntry}.
	 */
	public boolean contains(String key) {
		if (key == null)
			return false;
		return fields.get(key) != null;
	}

	/**
	 * Returns the value associated with the given key.
	 * 
	 * @param key
	 *            the key to retrieve from this {@link BibTexEntry}.
	 * @throws NullPointerException
	 *             when the given key is null.
	 * @return the value associated with the given key.
	 */
	public String get(String key) throws NullPointerException {
		return fields.get(key);
	}

	/**
	 * Returns a {@link List} containing the required fields for this
	 * {@link BibTexEntry}.
	 * 
	 * @return a {@link List} containing the required fields for this
	 *         {@link BibTexEntry}.
	 */
	public abstract List<String> requiredFields();

	/**
	 * Returns a {@link List} containing the optional fields for this
	 * {@link BibTexEntry}.
	 * 
	 * @return a {@link List} containing the optional fields for this
	 *         {@link BibTexEntry}.
	 */
	public abstract List<String> optionalFields();

	/**
	 * Returns a {@link Collection} containing all the keys present in this
	 * {@link BibTexEntry}.
	 * 
	 * @return a {@link Collection} containing all the keys present in this
	 *         {@link BibTexEntry}.
	 */
	public Collection<String> getKeys() {
		return fields.keySet();
	}

	/**
	 * Returns true when all the required fields are present in this
	 * {@link BibTexEntry}.
	 * 
	 * @return true when all the required fields are present in this
	 *         {@link BibTexEntry}.
	 */
	public boolean isValid() {
		for (String required : requiredFields())
			if (!contains(required))
				return false;
		return true;
	}

	/**
	 * Returns all the keys which are missing for a valid {@link BibTexEntry}
	 * (see {@link #isValid()}).
	 * 
	 * @return a collection with all the keys which are missing for a valid
	 *         {@link BibTexEntry} (see {@link #isValid()}).
	 */
	public Collection<String> getMissingKeys() {
		Set<String> result = new HashSet<String>();
		for (String required : requiredFields())
			if (!contains(required))
				result.add(required);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Entry<String, String>> iterator() {
		return fields.entrySet().iterator();
	}

	/**
	 * Returns the type of this {@link BibTexEntry}.
	 * 
	 * @return the type of this {@link BibTexEntry}.
	 */
	public abstract String getType();

	/**
	 * Returns a {@link Comparator} for sorting the keys of the
	 * {@link BibTexEntry}. The keys are sorted so that the required fields
	 * grouped alphabetically first, followed by the alphabetically sorted
	 * optional fields and finally the alphabetically sorted remaining user
	 * defined fields.
	 * 
	 * @return a {@link Comparator} for sorting the keys of the
	 *         {@link BibTexEntry}.
	 */
	public Comparator<String> getKeyComparator() {
		return new Comparator<String>() {
			List<String> required = requiredFields();
			List<String> optional = optionalFields();

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Comparator#compare()
			 */
			@Override
			public int compare(String o1, String o2) {
				int o1reqindex = indexOFieldInArray(required, o1);
				int o2reqindex = indexOFieldInArray(required, o2);
				int o1optindex = indexOFieldInArray(optional, o1);
				int o2optindex = indexOFieldInArray(optional, o2);

				if (o1reqindex > -1 && o2reqindex == -1)
					return -1;
				else if (o1reqindex == -1 && o2reqindex > -1)
					return 1;
				else if (o1reqindex > -1 && o2reqindex > -1)
					return o1reqindex - o2reqindex;
				else {
					if (o1optindex > -1 && o2optindex == -1)
						return -1;
					else if (o1optindex == -1 && o2optindex > -1)
						return 1;
					else if (o1optindex > -1 && o2optindex > -1)
						return o1optindex - o2optindex;
					else {
						return o1.compareTo(o2);
					}
				}
			}

			/**
			 * Returns the index of the given field in the given {@link List} of
			 * strings.
			 * 
			 * @param list
			 *            the list of strings to retrieve the index from.
			 * @param field
			 *            field to retrieve the index of from the list.
			 * @return
			 */
			private int indexOFieldInArray(List<String> list, String field) {
				int index = 0;
				for (String required : list) {
					for (String requiredSplit : required.split(" */ *"))
						if (field.equals(requiredSplit))
							return index;
						else
							++index;
				}
				return -1;
			}
		};
	}

	/**
	 * Writes the contents of this {@link BibTexEntry} on the given
	 * {@link PrintStream}.
	 * 
	 * @param stream
	 *            the {@link PrintStream} to write this {@link BibTexEntry} on.
	 * @throws NullPointerException
	 *             when the given stream is null.
	 */
	public void write(PrintStream stream) throws NullPointerException {
		if (stream == null)
			throw new NullPointerException("the given stream is null!");
		stream.print(toString());
	}

	/**
	 * Writes the contents of this {@link BibTexEntry} to the given {@link File}
	 * .
	 * 
	 * @param file
	 *            the {@link File} to write to.
	 * @throws NullPointerException
	 *             when the given file is null.
	 * @throws IOException
	 *             when an exception occurs during the writing of the file.
	 */
	public void write(File file) throws NullPointerException, IOException {
		try (FileWriter writer = new FileWriter(file);
				BufferedWriter w = new BufferedWriter(writer)) {
			w.write(toString());
		}
	}

	/**
	 * Writes the contents of this {@link BibTexEntry} to the file with the
	 * given filename.
	 * 
	 * @param filename
	 *            the filename of the file.
	 * @throws NullPointerException
	 *             when the given filename is null.
	 * @throws IOException
	 *             when an exception occurs during the writing of the file.
	 */
	public void write(String filename) throws NullPointerException, IOException {
		write(new File(filename));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("@%s{%s,", getType(), getCitationKey()));

		int longestKeyLength = 0;

		for (String key : getKeys())
			longestKeyLength = Math.max(longestKeyLength, key.length());

		int counter = 0;
		for (Entry<String, String> entry : this) {
			String key = entry.getKey();
			builder.append(String.format("\n  %s", key));

			for (int i = 0; i < longestKeyLength - key.length(); ++i)
				builder.append(String.format(" "));
			builder.append(String.format(" = { %s }", entry.getValue()));

			if (counter < fields.size() - 1)
				builder.append(",");
			++counter;
		}
		builder.append(String.format("\n}"));
		return builder.toString();
	}
}
