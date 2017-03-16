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

	// private static final Pattern entryPattern = Pattern
	// .compile("[\\{|\"][a-z|A-Z|0-9|\\\\|'|\\:|\"|\\{|\\}|/|\\-|\\_ |,|\\.]+[\\}|\"]");
	private static final Pattern entryPattern = Pattern
			.compile(" *([a-z]+) *= *[\\{|\"]([^=]+)[\\}|\"] *[, *]{0,1}");
	private static final Pattern bibtexPattern = Pattern
			.compile("@([a-z]+) *\\{ *([a-z]+) *, *(("
					+ entryPattern.toString() + ")*) *\\}");

	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<BibTexEntry> parser(File file)
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

	/**
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<BibTexEntry> parse(File file)
			throws FileNotFoundException {
		List<BibTexEntry> result = new ArrayList<BibTexEntry>();

		String s = " {0,1000}";

		/************************************************
		 * read the entire contents of the bibtex file.
		 ***********************************************/

		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(file);
		while (scanner.hasNext())
			builder.append(scanner.nextLine()).append(" ");
		scanner.close();
		String bibtex = builder.toString().replaceAll("\t+", " ")
				.replaceAll("  *", " ").trim();

		/*************************************************
		 * find the entry type of the bibtex file.
		 *************************************************/

		Pattern entryPattern = Pattern
				.compile("(?<=@)[a-z|A-Z]+(?=[ {0,100}|\\{])");
		Matcher entryMatcher = entryPattern.matcher(bibtex);

		while (entryMatcher.find()) {
			String entryType = entryMatcher.group();
			int offset = entryMatcher.start();

			/*************************************************
			 * retrieve the contents of the bibtex file.
			 *************************************************/

			Pattern contentsPattern = Pattern.compile(String.format(
					"(?<=@%s%s%s\\{)%s.+%s(,%s.+%s=%s\\{%s.+%s\\}%s)*(?=\\})",
					s, entryType, s, s, s, s, s, s, s, s, s));
			Matcher contentsMatcher = contentsPattern.matcher(bibtex);
			if (!contentsMatcher.find(offset))
				throw new IllegalStateException(
						"no citation key could be found in'"
								+ file.getAbsolutePath() + "'!");
			String content = contentsMatcher.group();

			System.out.println(content);
			String[] split = content.split(String.format(
					"%s,%s(?=[a-z|A-Z]+%s=%s\\{%s.*%s\\}%s)", s, s, s, s, s, s,
					s));
			if (split.length == 0)
				throw new IllegalStateException(
						"no citation key could be found in'"
								+ file.getAbsolutePath() + "'!");

			/*************************************************
			 * retrieve the key of the bibtex
			 *************************************************/
			String citationKey = split[0].trim();

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

			/*************************************************
			 * Parse the remaining entries
			 *************************************************/

			for (int i = 1; i < split.length; ++i) {
				String[] keyValueSplit = split[i].split(String.format(
						"(?<=%s[a-z|A-Z]{0,100000})%s=%s(?=\\{.{0,100000}\\})",
						s, s, s));
				if (keyValueSplit.length != 2)
					throw new IllegalStateException(
							"could not parse a key-value pair from '"
									+ split[i] + "' in file '"
									+ file.getAbsolutePath()
									+ "'! Split length was "
									+ keyValueSplit.length);

				String key = keyValueSplit[0].trim();
				String value = keyValueSplit[1];
				value = value.substring(value.indexOf("{") + 1,
						value.lastIndexOf("}")).trim();

				bibTexFile.addField(key, value);
			}

			result.add(bibTexFile);
		}
		return result;
	}
}
