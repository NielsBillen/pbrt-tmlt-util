package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile representing a PhD thesis.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexPhDThesis extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexPhDThesis} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexPhDThesis}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexPhDThesis(String citationKey) throws NullPointerException,
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
		return Arrays
				.asList(new String[] { "author", "title", "school", "year" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "type", "address", "month", "note",
				"key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "phdthesis";
	}

}
