package pbrt.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class PBRTStatistics {
	private Pattern integerPattern = Pattern.compile("(.+) ([0-9]+)$");
	private Pattern ratioPattern = Pattern
			.compile("(.+) ([0-9]+) / ([0-9]+) \\((.+)%\\)");
	private Pattern timePattern = Pattern
			.compile("(.+) +(.+)% +\\( *(.+) *\\)$");
	// private Pattern ratioPattern = Pattern
	// .compile("(.)+ ([0-9]+) / ([0-9]+) \\([0-9]*(\\.[0-9]*)?)%\\)");

	/**
	 * 
	 */
	public final TreeMap<String, Double> doubles = new TreeMap<String, Double>();

	/**
	 * 
	 */
	public final TreeMap<String, Long> times = new TreeMap<String, Long>();

	/**
	 * 
	 */
	public final TreeMap<String, Integer[]> ratios = new TreeMap<String, Integer[]>();

	/**
	 * 
	 */
	public final TreeMap<String, Integer> integers = new TreeMap<String, Integer>();

	/**
	 * 
	 * @param filename
	 * @throws NullPointerException
	 */
	public PBRTStatistics(String filename) throws NullPointerException {
		this(new File(filename));
	}

	/**
	 * 
	 * @param file
	 * @throws NullPointerException
	 */
	public PBRTStatistics(File file) throws NullPointerException {
		this(file.toPath());
	}

	/**
	 * 
	 * @param path
	 * @throws NullPointerException
	 */
	public PBRTStatistics(Path path) throws NullPointerException {
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim().replaceAll(" +", " ");

				Matcher integerMatcher = integerPattern.matcher(line);
				if (integerMatcher.find()) {
					String name = integerMatcher.group(1);
					Integer value = Integer.parseInt(integerMatcher.group(2));
					integers.put(name, value);
					System.out.format("%-40s %d\n", name, value);
					continue;
				}

				Matcher ratioMatcher = ratioPattern.matcher(line);
				if (ratioMatcher.find()) {
					String name = ratioMatcher.group(1);
					Integer first = Integer.parseInt(ratioMatcher.group(2));
					Integer second = Integer.parseInt(ratioMatcher.group(3));
					double percentage = Double.parseDouble(ratioMatcher
							.group(4));
					System.out.format("%-40s %d / %d (%f%%)\n", name, first,
							second, percentage);

					ratios.put(name, new Integer[] { first, second });
					continue;
				}

				Matcher timeMatcher = timePattern.matcher(line);
				if (timeMatcher.find()) {
					String name = timeMatcher.group(1);
					String time = timeMatcher.group(3);
					long millis = toMillis(time);
					System.out.format("%-40s %s - %d\n", name, time, millis);
					times.put(name, millis);
					continue;
				}
				System.err.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param time
	 * @return
	 */
	private static long toMillis(String time) {
		Pattern pattern = Pattern
				.compile("([0-9]+):([0-9]+):([0-9]+)\\.([0-9]+)");
		Matcher matcher = pattern.matcher(time);

		if (matcher.find()) {
			long hours = Long.parseLong(matcher.group(1));
			long minutes = Long.parseLong(matcher.group(2));
			long seconds = Long.parseLong(matcher.group(3));
			long millis = Long.parseLong(matcher.group(4)) * 10L;

			return millis + 1000L * seconds + 60000L * minutes + 3600000L
					* hours;
		} else
			return 0;

	}

}
