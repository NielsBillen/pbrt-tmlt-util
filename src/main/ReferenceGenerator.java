package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pbrt.PBRTParser;
import pbrt.PBRTProperty;
import pbrt.PBRTScene;
import util.FileUtil;
import core.RenderJob;

/**
 * Generator for the scripts which generate the reference images.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class ReferenceGenerator {
	private static final Random random = new Random();
	private static final int resolution = 512;
	private static final int repetition = 4;
	private static final int testSamples = 1024;
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
			List<RenderJob> mlt_tune = mlt_tuning(scene);
			script("mlt_tune.sh", mlt_tune);
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
						"{ time ./pbrt --quiet \"%s\" ; } 2> \"%s\"",
						job.file.getPath(), output + "-time.txt"));
				writer.newLine();
				writer.write(String.format(
						"echo \"[% 4.1f%% ] finished rendering %s!\"",
						(100.0 * sum) / total, job.file.getName()));
				writer.newLine();

			}

		}

		return file;

	}

	/**
	 * @throws IOException
	 * 
	 */
	public static List<RenderJob> bdpt(String sceneName) throws IOException {
		List<RenderJob> result = new ArrayList<RenderJob>();

		File bdptFolder = new File("test/" + sceneName + "/bdpt");
		FileUtil.mkdirs(bdptFolder);

		// Generate the references
		for (int i = 0; i < repetition; ++i) {
			for (int sample : samples) {
				String filename = String.format("%s-bdpt-%06dspp-%02d",
						sceneName, sample, i);
				File file = new File(bdptFolder, filename.concat(".pbrt"));

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
						.setIntegerSetting("seed",
								random.nextInt(Integer.MAX_VALUE)));

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
			File file = new File(bdptFolder, filename.concat(".pbrt"));

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
					.setIntegerSetting("seed",
							random.nextInt(Integer.MAX_VALUE)));

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

		File mltFolder = new File("test/" + sceneName + "/mlt");
		FileUtil.mkdirs(mltFolder);

		// Generate the references
		for (int i = 0; i < repetition; ++i) {
			for (int sample : samples) {
				String filename = String.format("%s-mlt-%06dspp-%02d",
						sceneName, sample, i);
				File file = new File(mltFolder, filename.concat(".pbrt"));

				PBRTScene scene = new PBRTScene(sceneName);

				scene.addChild(new PBRTProperty("Film")
						.setValue("image")
						.setIntegerSetting("xresolution", resolution)
						.setIntegerSetting("yresolution", resolution)
						.setStringSetting("filename",
								"results/" + sceneName + "/" + filename));

				scene.addChild(new PBRTProperty("Integrator")
						.setValue("pssmlt")
						.setIntegerSetting("maxdepth", 16)
						.setIntegerSetting("mutationsperpixel", sample)
						.setIntegerSetting("seed",
								random.nextInt(Integer.MAX_VALUE)));

				scene.addChild(new PBRTProperty("Include")
						.setValue("common.pbrt"));

				scene.print(file);

				result.add(new RenderJob(file, sceneName, sample));
			}
		}

		return result;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public static List<RenderJob> mlt_tuning(String sceneName)
			throws IOException {
		List<RenderJob> result = new ArrayList<RenderJob>();

		File mltFolder = new File("test/" + sceneName + "/mlt-tuning");
		FileUtil.rm(mltFolder);
		FileUtil.mkdirs(mltFolder);

		// Generate the references
		for (int i = 0; i < repetition; ++i) {
			for (float large = 0.05f; large < 0.5f; large += 0.05f) {
				for (float sigma = 0.002f;; sigma *= 1.5f) {
					String filename = String.format(
							"%s-mlttune-spp-%06d-sigma-%.4f-step-%.2f-%02d",
							sceneName, testSamples, sigma, large, i);
					File file = new File(mltFolder, filename.concat(".pbrt"));

					PBRTScene scene = new PBRTScene(sceneName);

					scene.addChild(new PBRTProperty("Film")
							.setValue("image")
							.setIntegerSetting("xresolution", resolution)
							.setIntegerSetting("yresolution", resolution)
							.setStringSetting("filename",
									"results/" + sceneName + "/" + filename));

					scene.addChild(new PBRTProperty("Integrator")
							.setValue("pssmlt")
							.setIntegerSetting("maxdepth", 16)
							.setIntegerSetting("mutationsperpixel", testSamples)
							.setFloatSetting("sigma", sigma)
							.setFloatSetting("largestepprobability", large)
							.setIntegerSetting("seed",
									random.nextInt(Integer.MAX_VALUE)));

					scene.addChild(new PBRTProperty("Include")
							.setValue("common.pbrt"));

					scene.print(file);

					result.add(new RenderJob(file, sceneName, testSamples));

					if (sigma > 1)
						break;
				}
			}
		}

		return result;
	}
}
