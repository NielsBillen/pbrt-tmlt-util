package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing an article in a conference
 * proceedings.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexConference extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexConference} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexConference}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexConference(String citationKey) throws NullPointerException,
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
				"year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "editor", "volume/number",
				"series", "pages", "address", "month", "organization",
				"publisher", "note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "conference";
	}
}
