package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing a book with an explicit publisher.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexBook extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexBook} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexBook}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexBook(String citationKey) throws NullPointerException,
			IllegalArgumentException {
		super(citationKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#requiredFields()
	 */
	@Override
	public List<String> requiredFields() {
		return Arrays.asList(new String[] { "author/editor", "title",
				"publisher", "year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "volume/number", "series",
				"address", "edition", "month", "note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "book";
	}
}
