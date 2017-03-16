package bibtex.types;

import java.util.Arrays;
import java.util.List;

import bibtex.BibTexEntry;

/**
 * An extension of a BibTexFile used when nothing else fits the content.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexMisc extends BibTexEntry {
	/**
	 * Creates a new {@link BibTexMisc} with the given citation key.
	 * 
	 * @param citationKey
	 *            citation key for the {@link BibTexMisc}.
	 * @throws NullPointerException
	 *             when the given citation key is null.
	 * @throws IllegalArgumentException
	 *             when the citation key is empty.
	 */
	public BibTexMisc(String citationKey) throws NullPointerException,
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
		return Arrays.asList(new String[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#optionalFields()
	 */
	@Override
	public List<String> optionalFields() {
		return Arrays.asList(new String[] { "author", "title", "howpublished",
				"month", "year", "note", "key" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bibtex.BibTexFile#getType()
	 */
	@Override
	public String getType() {
		return "misc";
	}

}
