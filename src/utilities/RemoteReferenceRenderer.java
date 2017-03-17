package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import pbrt.PBRTArray;
import pbrt.PBRTProperty;
import pbrt.PBRTScene;
import ssh.RemotePC;
import ssh.RemotePCCluster;
import ssh.SSHUserInfo;
import core.Tile;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemoteReferenceRenderer {
	private static final String directory = "/home/niels/workspace/pbrt-tmlt/scenes";
	private static final String[] scenes = new String[] { "mirror-balls",
			"mirror-ring", "kitchen", "volume-caustic" };
	private static final int resolution = 1024;
	private static final int tileSize = 16;
	private static Random random = new Random();
	private static final int samples = 1048576;

	// private static final int samples = 4;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/***********************************************************************
		 * Connect to the computers
		 **********************************************************************/

		SSHUserInfo info = new SSHUserInfo("u0093806");
		List<RemotePC> cluster = RemotePCCluster.getCluster(info);

		for (String scene : scenes)
			render(scene, cluster);

	}

	/**
	 * 
	 * @param sceneName
	 */
	public static void render(final String sceneName, List<RemotePC> cluster) {
		/***********************************************************************
		 * Construct the required tiles
		 **********************************************************************/

		final ReentrantLock lock = new ReentrantLock();
		final List<Tile> tiles = new ArrayList<Tile>();
		for (int x = 0; x < resolution; x += tileSize) {
			for (int y = 0; y < resolution; y += tileSize) {
				Tile tile = new Tile(x, y, x + tileSize, y + tileSize);
				tiles.add(tile);
			}
		}

		final List<RemotePC> available = new ArrayList<RemotePC>(cluster);
		final List<RemotePC> busy = new ArrayList<RemotePC>();

		/***********************************************************************
		 * Continue to schedule tasks until all tiles have been rendered
		 **********************************************************************/

		while (true) {
			lock.lock();

			// we are done!
			if (tiles.size() == 0) {
				lock.unlock();
				break;
			}

			// no pc's are available
			if (available.size() == 0) {
				lock.unlock();
				continue;
			}

			// get a tile and a remote pc
			final Tile tile = tiles.remove(tiles.size() - 1);
			final RemotePC pc = available.remove(available.size() - 1);
			final long seed = 1 + random.nextInt(Integer.MAX_VALUE - 1);
			busy.add(pc);

			// construct the thread
			Thread thread = new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				public void run() {
					execute(sceneName, pc, tile, seed);
					lock.lock();
					available.add(pc);
					busy.remove(pc);
					lock.unlock();
				};
			};

			lock.unlock();

			// start the thread after the lock has been released.
			thread.start();
		}
	}

	/**
	 * 
	 * @param pc
	 * @param tile
	 */
	public static boolean execute(String sceneName, RemotePC pc, Tile tile,
			long seed) {

		/***********************************************************************
		 * Create the scene parameters
		 **********************************************************************/

		String filename = String.format(
				"%s-samples-%d-tile-%d-%d-%d-%d-seed-%d", sceneName, samples,
				tile.minX, tile.minY, tile.maxX, tile.maxY, seed);
		String tmpFilename = "/tmp/" + sceneName + "/" + filename;

		final double inv = 1.0 / (double) resolution;
		PBRTScene scene = new PBRTScene();
		PBRTProperty film = new PBRTProperty("Film")
				.setValue("image")
				.setIntegerSetting("xresolution", resolution)
				.setIntegerSetting("yresolution", resolution)
				.setFloatArraySetting(
						"float",
						"cropwindow",
						new PBRTArray<Float>((float) (tile.minX * inv),
								(float) (tile.maxX * inv),
								(float) (tile.minY * inv),
								(float) (tile.maxY * inv)))
				.setStringSetting("filename", tmpFilename);
		PBRTProperty sampler = new PBRTProperty("Sampler").setValue(
				"lowdiscrepancy").setIntegerSetting("pixelsamples", samples);
		PBRTProperty integrator = new PBRTProperty("Integrator").setValue(
				"bdpt").setIntegerSetting("maxdepth",
				sceneName.contains("volume") ? 100 : 8);
		PBRTProperty include = new PBRTProperty("Include")
				.setValue("common.pbrt");
		scene.addChild(film);
		scene.addChild(sampler);
		scene.addChild(integrator);
		scene.addChild(include);

		/***********************************************************************
		 * Write scene to disc
		 **********************************************************************/

		File sceneFile = null;
		try {
			sceneFile = scene.print(filename.concat(".pbrt"));
			sceneFile.deleteOnExit();
		} catch (Exception e) {
			return false;
		}

		/***********************************************************************
		 * Copy the file to remote pc's rendering location
		 **********************************************************************/

		String sceneFilename = "scenes/" + sceneName + "/" + filename + ".pbrt";
		String remoteSceneFilename = "/home/u0093806/workspace/pbrt-tmlt/"
				+ sceneFilename;

		if (!pc.cp(sceneFile, remoteSceneFilename)) {
			System.err.println("could not copy file to remote server!");
			return false;
		}

		sceneFile.delete();

		/***********************************************************************
		 * 
		 **********************************************************************/

		if (!pc.execute("cd workspace/pbrt-tmlt", "mkdir -p /tmp/" + sceneName,
				"./pbrt " + sceneFilename + " --seed " + seed, "exit")) {
			System.err.println("could not execute command on remote server!");
			return false;
		}

		/***********************************************************************
		 * Get the rendered results
		 **********************************************************************/

		File file = new File(sceneName);
		file.mkdirs();

		pc.get(tmpFilename + ".pfm", sceneName + "/" + filename + ".pfm");
		pc.get(tmpFilename + ".png", sceneName + "/" + filename + ".png");
		pc.get(tmpFilename + ".exr", sceneName + "/" + filename + ".exr");
		pc.get(tmpFilename + ".txt", sceneName + "/" + filename + ".txt");

		/***********************************************************************
		 * Remove the file from the remote's pc's rendering location
		 **********************************************************************/

		if (!pc.rm(remoteSceneFilename)) {
			System.err.println("could not remove scene from remote server!");
			return false;
		}

		return true;

	}
}
