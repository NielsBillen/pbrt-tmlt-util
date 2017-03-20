package distributed;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author niels
 * 
 */
public class RemoteCluster {
	/**
	 * List of names of the pc's at the ground floor of Celestijnenlaan 200A.
	 */
	private static String[] names = { "aalst", "aarlen", "alken", "andenne",
			"asse", "aubel", "balen", "bastogne", "beringen", "bevekom",
			"bierbeek", "bilzen", "binche", "borgworm", "charleroi", "chimay",
			"ciney", "couvin", "diest", "dilbeek", "dinant", "doornik", "dour",
			"durbuy", "eupen", "fleurus", "geel", "genk", "gent", "hamme",
			"hastiere", "heers", "heist", "herent", "herstal", "hove", "ieper",
			"jemeppe", "kaprijke", "knokke", "komen", "kortrijk", "laarne",
			"lanaken", "lier", "lommel", "maaseik", "malle", "marche",
			"mechelen", "mol", "musson", "nijvel", "ninove", "olen",
			"overpelt", "peer", "riemst", "ronse", "schoten", "seraing", "spa",
			"stavelot", "temse", "terhulpen", "tienen", "torhout", "tremelo",
			"tubize", "turnhout", "verviers", "veurne", "vielsalm",
			"vilvoorde", "virton", "voeren", "waterloo", "waver", "yvoir",
			"zwalm" };

	/**
	 * Returns the computers at the ground floor of the computer science
	 * department.
	 * 
	 * @param nCores
	 *            the number of cores to use on the machines (0 = all).
	 * @param consoleAuthentication
	 *            whether to ask for login information via the console or a
	 *            graphical user interface.
	 * @return the computers at the ground floor of the computer science
	 *         department.
	 */
	public static List<RemoteComputer> getCluster(int nCores,
			boolean consoleAuthentication) {
		RemoteAuthentication authentication = new RemoteAuthentication(
				"u0093806", consoleAuthentication);
		List<RemoteComputer> result = new ArrayList<RemoteComputer>();

		for (String name : names) {
			RemoteComputer computer = new RemoteComputer(
					name.concat(".cs.kotnet.kuleuven.be"), authentication,
					nCores);
			result.add(computer);
		}
		return result;
	}
}
