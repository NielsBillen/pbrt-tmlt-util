package pfm;

import util.Statistics;

/**
 * Utility methods for operations on Portable Float Map images.
 * 
 * @author Niels Billen
 * @version 1.0
 * 
 *          Redistribution and use in source and binary forms, with or without
 *          modification, are permitted provided that the following conditions
 *          are met:
 * 
 *          - Redistributions of source code must retain the above copyright
 *          notice, this list of conditions and the following disclaimer.
 * 
 *          - Redistributions in binary form must reproduce the above copyright
 *          notice, this list of conditions and the following disclaimer in the
 *          documentation and/or other materials provided with the distribution.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *          LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *          FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *          COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *          INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *          BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *          LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *          CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *          ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *          POSSIBILITY OF SUCH DAMAGE.
 */
public class PFMUtil {
	/**
	 * 
	 * @param image
	 * @return
	 */
	public static PFMImage normalizeByAverage(PFMImage image) {
		double average = getAverage(image);
		double inv = 1.0 / average;

		PFMImage result = new PFMImage(image.width, image.height);
		for (int i = 0; i < image.nbOfFloats(); ++i) {
			result.setFloat(i, (float) (image.getFloat(i) * inv));
		}
		return result;
	}

	/**
	 * Returns the average value of the color components in the image.
	 * 
	 * @param image
	 *            the image to get the average value of .
	 * @return the average value of the color components in the image.
	 */
	public static double getAverage(PFMImage image) throws NullPointerException {
		if (image == null)
			throw new NullPointerException("the given image is null!");

		Statistics statistic = new Statistics();

		for (int i = 0; i < image.nbOfFloats(); ++i)
			statistic.add(image.getFloat(i));

		return statistic.getAverage();
	}

	/**
	 * Computes the mean squared error between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param image2
	 *            the second image.
	 * @param gamma
	 *            the gamma correction to apply to the colors of the images.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static double
			getRMSE(PFMImage image1, PFMImage image2, double gamma)
					throws IllegalArgumentException {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (image2 == null)
			throw new NullPointerException("the second image is null!");
		if (gamma <= 0)
			throw new IllegalArgumentException("the gamma must be positive!");
		if (image1.width != image2.width || image1.height != image2.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!" + image1.width
							+ "x" + image1.height + " vs " + image2.width + "x"
							+ image2.height);

		final int n = image1.nbOfFloats();
		final Statistics statistic = new Statistics();
		final double invGamma = 1.0 / gamma;

		for (int i = 0; i < n; i++) {
			float c1 = image1.getFloat(i);
			float c2 = image2.getFloat(i);

			if (c1 < 0.f)
				continue;
			if (c2 < 0.f)
				continue;

			double g1 = Math.pow(c1, invGamma);
			double g2 = Math.pow(c2, invGamma);

			if (g2 == 0)
				continue;
			double difference = (g1 - g2) / g2;
			double squared = difference * difference;

			statistic.add(squared);
		}

		return statistic.getAverage();
	}

	/**
	 * Computes the mean squared error between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param image2
	 *            the second image.
	 * @param gamma
	 *            the gamma correction to apply to the colors of the images.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static double getMSE(PFMImage image1, PFMImage image2, double gamma)
			throws IllegalArgumentException {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (image2 == null)
			throw new NullPointerException("the second image is null!");
		if (gamma <= 0)
			throw new IllegalArgumentException("the gamma must be positive!");
		if (image1.width != image2.width || image1.height != image2.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!" + image1.width
							+ "x" + image1.height + " vs " + image2.width + "x"
							+ image2.height);

		final int n = image1.nbOfFloats();
		final Statistics statistic = new Statistics();
		final double invGamma = 1.0 / gamma;

		for (int i = 0; i < n; i++) {
			float c1 = image1.getFloat(i);
			float c2 = image2.getFloat(i);

			if (c1 < 0.f)
				continue;
			if (c2 < 0.f)
				continue;

			double g1 = Math.pow(c1, invGamma);
			double g2 = Math.pow(c2, invGamma);
			double difference = g1 - g2;
			double squared = difference * difference;

			statistic.add(squared);

		}

		return statistic.getAverage();
	}

	/**
	 * Computes the mean squared error between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param image2
	 *            the second image.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static PFMImage getMSEImage(PFMImage image1, PFMImage image2,
			double gamma) throws IllegalArgumentException {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (image2 == null)
			throw new NullPointerException("the second image is null!");
		if (image1.width != image2.width || image1.height != image2.height)
			throw new IllegalArgumentException(String.format(
					"the images do not have matching size! %dx%d vs %dx%d",
					image1.width, image1.height, image2.width, image2.height));

		final int n = image1.nbOfFloats();
		final PFMImage result = new PFMImage(image1.width, image1.height);
		final double invGamma = 1.0 / gamma;

		for (int i = 0; i < n; ++i) {
			float c1 = image1.getFloat(i);
			float c2 = image2.getFloat(i);

			if (c1 < 0 || c2 < 0)
				result.setFloat(i, 0.f);
			else {
				double g1 = Math.pow(c1, invGamma);
				double g2 = Math.pow(c2, invGamma);
				double difference = g1 - g2;
				double squared = difference * difference;
				result.setFloat(i, (float) squared);

			}
		}

		return result;
	}

	/**
	 * Returns the difference between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param image2
	 *            the second image.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static PFMImage difference(PFMImage image1, PFMImage image2) {
		return difference(image1, image2, 1);
	}

	/**
	 * Returns the difference between the two given images scaled by the given
	 * amount.
	 * 
	 * @param image1
	 *            the first image.
	 * @param image2
	 *            the second image.
	 * @param scale
	 *            the scale used to scale the difference.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static PFMImage difference(PFMImage image1, PFMImage image2,
			float scale) {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (image2 == null)
			throw new NullPointerException("the second image is null!");
		if (image1.width != image2.width || image1.height != image2.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!");

		final int n = image1.nbOfFloats();
		final PFMImage result = new PFMImage(image1.width, image1.height);
		for (int i = 0; i < n; ++i) {
			float c1 = image1.getFloat(i);
			float c2 = image2.getFloat(i);
			float difference = scale * Math.abs(c1 - c2);
			result.setFloat(i, difference);
		}

		return result;
	}
}