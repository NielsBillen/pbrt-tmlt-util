package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing a proceedings of a conference.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexProceedings extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexProceedings} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexProceedings}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexProceedings(String citationKey) throws NullPointerException,
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
		return Arrays.asList(new String[] { "title", "year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "editor", "volume/number",
				"series", "address", "month", "publisher", "organization",
				"note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "proceedings";
	}

}
