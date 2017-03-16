package bibtex;

/**
 * Represents an author of a {@link BibTexEntry}.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexAuthor {
	public final String firstName;
	public final String lastName;

	/**
	 * Creates a new {@link BibTexAuthor} with the given name.
	 * 
	 * @param firstName
	 *            the first name of the {@link BibTexAuthor}.
	 * @param lastName
	 *            the last name of the {@link BibTexAuthor}.
	 * @throws NullPointerException
	 *             when the first name or last name are null.
	 * @throws IllegalArgumentException
	 *             when both the first and last name are empty.
	 */
	public BibTexAuthor(String firstName, String lastName)
			throws NullPointerException, IllegalArgumentException {
		if (firstName == null)
			throw new NullPointerException("the first name cannot be null!");
		if (lastName == null)
			throw new NullPointerException("the last name cannot be null!");
		if (firstName.isEmpty() && lastName.isEmpty())
			throw new IllegalArgumentException(
					"the names cannot be both empty!");
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return firstName.hashCode() + 31 * lastName.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		BibTexAuthor other = (BibTexAuthor) obj;

		return firstName.equals(other.firstName)
				&& lastName.equals(other.lastName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s, %s", firstName, lastName);
	}
}
