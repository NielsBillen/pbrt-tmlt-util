package ssh;

import java.util.ArrayList;
import java.util.List;

import computer.RemotePC;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemotePCCluster {
	// baba bebe bibi bobo hakuna heverlee
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

	// private static String[] names = { "dinant" };

	/**
	 * 
	 * @return
	 */
	public static List<RemotePC> getCluster(final SSHUserInfo info) {
		List<RemotePC> result = new ArrayList<RemotePC>();

		for (final String name : names) {
			String host = name.concat(".cs.kotnet.kuleuven.be");
			result.add(new RemotePC(host, info));
		}

		return result;

	}
}
