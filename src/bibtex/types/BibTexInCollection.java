package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing a part of a book having its own
 * title.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexInCollection extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexInCollection} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexInCollection}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexInCollection(String citationKey) throws NullPointerException,
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
		return Arrays.asList(new String[] { "author", "title", "booktitle",
				"publisher", "year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "editor", "volume/number",
				"series", "type", "chapter", "pages", "address", "edition",
				"month", "note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "incollection";
	}
}
