package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing a part of a book, usually untitled.
 * May be a chapter (or section, etc.) and/or a range of pages.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexInBook extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexInBook} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexInBook}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexInBook(String citationKey) throws NullPointerException,
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
				"chapter/pages", "publisher", "year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "volume/number", "series", "type",
				"address", "edition", "month", "note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "inbook";
	}
}
