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
	 * Computes the mean squared error between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param reference
	 *            the second image.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static double MSE(PFMImage image1, PFMImage reference)
			throws IllegalArgumentException {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (reference == null)
			throw new NullPointerException("the second image is null!");
		if (image1.width != reference.width
				|| image1.height != reference.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!" + image1.width
							+ "x" + image1.height + " vs " + reference.width
							+ "x" + reference.height);

		Statistics statistic = new Statistics();

		double s1 = 0;
		double s2 = 0;
		double inv = 1.0 / 3.0;

		for (int y = 0; y < image1.height; ++y) {
			for (int x = 0; x < image1.width; ++x) {
				float[] c1 = image1.getColorAt(x, y);
				float[] c2 = reference.getColorAt(x, y);

				for (int i = 0; i < 3; ++i) {
					s1 += c1[i] * inv;
					s2 += c2[i] * inv;
				}
			}
		}
		// double invs1 = (image1.height * image1.width) / s1;
		// double invs2 = (image1.height * image1.width) / s2;
		double invs1 = 1.0;
		double invs2 = 1.0;

		for (int y = 0; y < image1.height; ++y) {
			for (int x = 0; x < image1.width; ++x) {
				float[] c1 = image1.getColorAt(x, y);
				float[] c2 = reference.getColorAt(x, y);

				for (int i = 0; i < 3; ++i) {
					if (c1[i] < 0)
						continue;
					if (c2[i] < 0)
						continue;
					double bc1 = Math.pow(c1[i], 1.0 / 2.2) * invs1;
					double bc2 = Math.pow(c2[i], 1.0 / 2.2) * invs2;

					// double bc1 = c1[i] * invs1;
					// double bc2 = c2[i] * invs2;
					double difference = bc1 - bc2;
					double squared = difference * difference;

					statistic.add(squared);
				}
			}
		}

		return statistic.getAverage();
	}

	/**
	 * Computes the mean squared error between the two given images.
	 * 
	 * @param image1
	 *            the first image.
	 * @param reference
	 *            the second image.
	 * @throws NullPointerException
	 *             when one of the images is null.
	 * @throws IllegalArgumentException
	 *             when the sizes of the images do not match.
	 * @return the mean squared error between the two given images.
	 */
	public static PFMImage mseImage(PFMImage image1, PFMImage reference)
			throws IllegalArgumentException {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (reference == null)
			throw new NullPointerException("the second image is null!");
		if (image1.width != reference.width
				|| image1.height != reference.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!" + image1.width
							+ "x" + image1.height + " vs " + reference.width
							+ "x" + reference.height);

		double s1 = 0;
		double s2 = 0;
		double inv = 1.0 / 3.0;

		for (int y = 0; y < image1.height; ++y) {
			for (int x = 0; x < image1.width; ++x) {
				float[] c1 = image1.getColorAt(x, y);
				float[] c2 = reference.getColorAt(x, y);

				for (int i = 0; i < 3; ++i) {
					s1 += c1[i] * inv;
					s2 += c2[i] * inv;
				}
			}
		}
		double invs1 = (image1.height * image1.width) / s1;
		double invs2 = (image1.height * image1.width) / s2;
		final int resolution = image1.width * image1.height;
		final int nbOfFloats = resolution * 3;
		float[] floats = new float[nbOfFloats];
		int index = 0;

		for (int y = 0; y < image1.height; ++y) {
			for (int x = 0; x < image1.width; ++x) {
				float[] c1 = image1.getColorAt(x, y);
				float[] c2 = reference.getColorAt(x, y);

				for (int i = 0; i < 3; ++i) {
					double bc1 = Math.pow(c1[i], 1.0 / 2.2) * invs1;
					double bc2 = Math.pow(c2[i], 1.0 / 2.2) * invs2;
					double difference = (bc1 - bc2);
					floats[index++] = (float) (difference * difference);
				}
			}
		}

		return new PFMImage(image1.width, image1.height, floats);
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

		final int resolution = image1.width * image1.height;
		final int nbOfFloats = 3 * resolution;
		float[] floats = new float[nbOfFloats];
		float[] c1, c2;

		int index = 0;
		for (int y = 0; y < image1.height; ++y)
			for (int x = 0; x < image1.width; ++x) {
				c1 = image1.getColorAt(x, y);
				c2 = image2.getColorAt(x, y);

				for (int i = 0; i < 3; ++i)
					floats[index++] = scale * Math.abs(c1[i] - c2[i]);
			}

		return new PFMImage(image1.width, image1.height, floats);
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
	public static PFMImage
			divide(PFMImage image1, PFMImage image2, float scale) {
		if (image1 == null)
			throw new NullPointerException("the first image is null!");
		if (image2 == null)
			throw new NullPointerException("the second image is null!");
		if (image1.width != image2.width || image1.height != image2.height)
			throw new IllegalArgumentException(
					"the images do not have matching size!");

		final int resolution = image1.width * image1.height;
		final int nbOfFloats = 3 * resolution;
		float[] floats = new float[nbOfFloats];
		float[] c1, c2;
		float average = 0;

		for (int y = 0; y < image1.height; ++y)
			for (int x = 0; x < image1.width; ++x) {
				c1 = image1.getColorAt(x, y);
				c2 = image2.getColorAt(x, y);
				int index = 3 * (image1.height * y + x);

				for (int i = 0; i < 3; ++i) {
					if (c2[i] == 0.0) {
						floats[index + i] = 1;
						average += 1;
					} else {
						float s = scale * (c1[i] / c2[i]);
						floats[index + i] = s;
						average += s;
					}
				}
			}

		System.out.println(average / (3.0 * image1.width * image2.height));
		return new PFMImage(image1.width, image1.height, floats);
	}
}