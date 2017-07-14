package main;

import java.io.IOException;

import pfm.PFMImage;
import pfm.PFMReader;
import util.Statistics;

public class Test {
	public static void main(String[] args) throws IOException {
		PFMImage image = PFMReader
				.read("/home/niels/workspace/exrmse/frame0.pfm");
		image.write("frame0.png", 2.2);
		
		Statistics statistics = new Statistics();
		for(int i = 0;i< image.nbOfFloats(); ++i)
			statistics.add(image.getFloat(i));
		System.out.println(statistics);
	}
}
