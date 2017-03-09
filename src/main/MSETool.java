package main;

import java.io.IOException;

import pfm.PFMImage;
import pfm.PFMReader;
import pfm.PFMUtil;

public class MSETool {
	public static void main(String[] args) throws IOException {
		PFMImage reference = PFMReader
				.read("/home/niels/workspace/pbrt-tmlt/results/"
						+ "caustic-glass/caustic-glass-bdpt-131072spp.pfm");
		PFMImage pssmlt = PFMReader
				.read("/home/niels/workspace/pbrt-tmlt/results/"
						+ "caustic-glass/caustic-glass-pssmlt.pfm");
		PFMImage tmlt = PFMReader
				.read("/home/niels/workspace/pbrt-tmlt/results/"
						+ "caustic-glass/caustic-glass-tmlt.pfm");

		System.out.format(
				"[ Mean squared error]\npssmlt: %.10f\ntmlt:   %.10f\n",
				PFMUtil.MSE(reference, pssmlt), PFMUtil.MSE(reference, tmlt));
	}
}
