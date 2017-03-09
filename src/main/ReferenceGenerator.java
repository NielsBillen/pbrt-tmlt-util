package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pbrt.PBRTParser;
import pbrt.PBRTProperty;
import pbrt.PBRTScene;
import core.RenderJob;

/**
 * Generator for the scripts which generate the reference images.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class ReferenceGenerator {
	private static final int resolution = 1024;
	private static final int repetition = 10;
	private static final int[] samples = { 1, 4, 8, 16, 32, 64, 128, 256, 1024,
			2048, 4096, 8192 };
	private static final int reference = 131072;

	/**
	 * 
	 * @param scenes
	 * @throws IOException
	 */
	public static void main(String[] scenes) throws IOException {
		for (String scene : scenes) {
			List<RenderJob> bdpt = bdpt(scene);
			script("bdpt.sh", bdpt);
			List<RenderJob> mlt = mlt(scene);
			script("mlt.sh", mlt);
		}
	}

	/**
	 * 
	 * @param filename
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public static File script(String filename, List<RenderJob> list)
			throws IOException {
		Collections.sort(list);

		long total = 0;
		for (RenderJob job : list)
			total += job.samples;

		File file = new File(filename);
		Path path = file.toPath();

		long sum = 0;
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write("#!/bin/sh");
			writer.newLine();

			for (RenderJob job : list) {
				writer.write("mkdir -p results/" + job.sceneName);
				writer.newLine();

				writer.write(String.format(
						"echo \"[% 3.1f%% ] started rendering %s ... \"",
						(100.0 * sum) / total, job.file.getName()));
				writer.newLine();

				sum += job.samples;

				PBRTScene scene = PBRTParser.parse(job.file);
				String output = scene.findSetting("Film.filename");

				writer.write(String.format(
						"{ time ./pbrt --quiet \"%s\" ; } 2> \"%s\"", "scenes/"
								+ job.sceneName + "/" + job.file.getName(),
						output + "-time.txt"));
				writer.newLine();
				writer.write(String.format(
						"echo \"[% 4.1f%% ] finished rendering %s!\"",
						(100.0 * sum) / total, job.file.getName()));
				writer.newLine();

			}

		}

		return null;

	}

	/**
	 * @throws IOException
	 * 
	 */
	public static List<RenderJob> bdpt(String sceneName) throws IOException {
		List<RenderJob> result = new ArrayList<RenderJob>();

		File scenes = new File("scenes");
		if (!scenes.exists())
			if (!scenes.mkdir())
				throw new IOException("coudl not make \"scenes\" folder!");
		File sceneFolder = new File(scenes, sceneName);
		if (!sceneFolder.exists())
			if (!sceneFolder.mkdir())
				throw new IOException("could not make \"" + sceneName
						+ "\" folder!");

		// Generate the references
		for (int i = 0; i < repetition; ++i) {
			for (int sample : samples) {
				String filename = String.format("%s-bdpt-%06dspp-%02d",
						sceneName, sample, i);
				File file = new File(sceneFolder, filename.concat(".pbrt"));

				PBRTScene scene = new PBRTScene(sceneName);

				scene.addChild(new PBRTProperty("Film")
						.setValue("image")
						.setIntegerSetting("xresolution", resolution)
						.setIntegerSetting("yresolution", resolution)
						.setStringSetting("filename",
								"results/" + sceneName + "/" + filename));

				scene.addChild(new PBRTProperty("Sampler")
						.setValue("lowdiscrepancy")
						.setIntegerSetting("pixelsamples", sample)
						.setIntegerSetting("seed", i));

				scene.addChild(new PBRTProperty("Integrator").setValue("bdpt")
						.setIntegerSetting("maxdepth", 16));

				scene.addChild(new PBRTProperty("Include")
						.setValue("common.pbrt"));

				scene.print(file);

				result.add(new RenderJob(file, sceneName, sample));
			}
		}

		{
			String filename = String.format("%s-bdpt-%06dspp", sceneName,
					reference);
			File file = new File(sceneFolder, filename.concat(".pbrt"));

			PBRTScene scene = new PBRTScene(sceneName);

			scene.addChild(new PBRTProperty("Film")
					.setValue("image")
					.setIntegerSetting("xresolution", resolution)
					.setIntegerSetting("yresolution", resolution)
					.setStringSetting("filename",
							"results/" + sceneName + "/" + filename));

			scene.addChild(new PBRTProperty("Sampler")
					.setValue("lowdiscrepancy")
					.setIntegerSetting("pixelsamples", reference)
					.setIntegerSetting("seed", 0));

			scene.addChild(new PBRTProperty("Integrator").setValue("bdpt")
					.setIntegerSetting("maxdepth", 16));

			scene.addChild(new PBRTProperty("Include").setValue("common.pbrt"));
			scene.print(file);

			result.add(new RenderJob(file, sceneName, reference));

		}

		return result;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public static List<RenderJob> mlt(String sceneName) throws IOException {
		List<RenderJob> result = new ArrayList<RenderJob>();

		File scenes = new File("scenes");
		if (!scenes.exists())
			if (!scenes.mkdir())
				throw new IOException("coudl not make \"scenes\" folder!");
		File sceneFolder = new File(scenes, sceneName);
		if (!sceneFolder.exists())
			if (!sceneFolder.mkdir())
				throw new IOException("could not make \"" + sceneName
						+ "\" folder!");

		// Generate the references
		for (int i = 0; i < repetition; ++i) {
			for (int sample : samples) {
				String filename = String.format("%s-mlt-%06dspp-%02d",
						sceneName, sample, i);
				File file = new File(sceneFolder, filename.concat(".pbrt"));

				PBRTScene scene = new PBRTScene(sceneName);

				scene.addChild(new PBRTProperty("Film")
						.setValue("image")
						.setIntegerSetting("xresolution", resolution)
						.setIntegerSetting("yresolution", resolution)
						.setStringSetting("filename",
								"results/" + sceneName + "/" + filename));

				scene.addChild(new PBRTProperty("Integrator").setValue("mlt")
						.setIntegerSetting("maxdepth", 16)
						.setIntegerSetting("mutationsperpixel", sample)
						.setIntegerSetting("seed", repetition));

				scene.addChild(new PBRTProperty("Include")
						.setValue("common.pbrt"));

				scene.print(file);

				result.add(new RenderJob(file, sceneName, sample));
			}
		}

		return result;
	}
}
