package core;

import java.io.IOException;

import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;

public class Image {
	public static void main(String[] args) throws IOException {
		String scene = "mirror-balls";
		PFMImage reference = PFMReader.read(String.format(
				"/home/niels/workspace/pbrt-tmlt-util/"
						+ "references/%s/%s-pssmlt-mutations-1048576-"
						+ "maxdepth-8-sigma-0.01-step-0.3-seed-0-0.pfm", scene,
				scene));

		PFMImage pssmlt = PFMReader.read(String.format(
				"/home/niels/workspace/pbrt-tmlt/results/%s/%s-pssmlt.pfm",
				scene, scene));
		PFMImage tpssmlt = PFMReader.read(String.format(
				"/home/niels/workspace/pbrt-tmlt/results/%s/%s-tpssmlt.pfm",
				scene, scene));

		System.out.format("[ MSE %s]\n", scene);
		System.out.format("pssmlt:  %f\n", PFMUtil.MSE(reference, pssmlt));
		System.out.format("tpssmlt: %f\n", PFMUtil.MSE(reference, tpssmlt));
	}
}
