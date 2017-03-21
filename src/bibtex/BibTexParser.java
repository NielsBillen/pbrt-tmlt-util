package bibtex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Parser which parses a file with {@link BibTexEntry}s.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class BibTexParser {
	private static final Pattern entryPattern = Pattern
			.compile(" *([a-z|_]+) *= *[\\{|\"]([^=]+)[\\}|\"] *?[, *]");
	private static final Pattern bibtexPattern = Pattern
			.compile("@([^=|^ ]+) *\\{ *([^=]+) *, *(("
					+ entryPattern.toString() + ")*) *\\}");

	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<BibTexEntry> parse(File file)
			throws FileNotFoundException {
		final List<BibTexEntry> result = new ArrayList<BibTexEntry>();

		/************************************************
		 * read the entire contents of the bibtex file.
		 ***********************************************/

		StringBuilder builder = new StringBuilder();
		Scanner fileScanner = new Scanner(file);
		while (fileScanner.hasNext())
			builder.append(fileScanner.nextLine()).append(" ");
		fileScanner.close();
		String bibtex = builder.toString().replaceAll("\t+", " ")
				.replaceAll("  *", " ").trim();
		

		/***********************************************************************
		 * Scan the bibtex
		 **********************************************************************/

		Matcher matcher = bibtexPattern.matcher(bibtex);
		while (matcher.find()) {
			String entryType = matcher.group(1);
			String citationKey = matcher.group(2);

			BibTexEntry bibTexFile;

			if (entryType.equals("article"))
				bibTexFile = new BibTexArticle(citationKey);
			else if (entryType.equals("book"))
				bibTexFile = new BibTexBook(citationKey);
			else if (entryType.equals("booklet"))
				bibTexFile = new BibTexBooklet(citationKey);
			else if (entryType.equals("conference"))
				bibTexFile = new BibTexConference(citationKey);
			else if (entryType.equals("inbook"))
				bibTexFile = new BibTexInBook(citationKey);
			else if (entryType.equals("incollection"))
				bibTexFile = new BibTexInCollection(citationKey);
			else if (entryType.equals("inproceedings"))
				bibTexFile = new BibTexInProceedings(citationKey);
			else if (entryType.equals("misc"))
				bibTexFile = new BibTexMisc(citationKey);
			else if (entryType.equals("phdthesis"))
				bibTexFile = new BibTexPhDThesis(citationKey);
			else if (entryType.equals("proceedings"))
				bibTexFile = new BibTexProceedings(citationKey);
			else if (entryType.equals("techreport"))
				bibTexFile = new BibTexTechReport(citationKey);
			else
				throw new IllegalStateException("unknown bibtex entry type '"
						+ entryType + "'!");

			Matcher entryMatcher = entryPattern.matcher(matcher.group(3));
			while (entryMatcher.find()) {
				bibTexFile.addField(entryMatcher.group(1),
						entryMatcher.group(2));
			}

			result.add(bibTexFile);
		}

		return result;
	}
}
