package pfm;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import util.Statistics;

/**
 * The PFMImage describes an image in the Portable Float Map format.
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
public class PFMImage {
	private final float[] floats;
	public final int width;
	public final int height;

	/**
	 * 
	 * @param width
	 * @param height
	 * @param color
	 */
	public PFMImage(int width, int height) {
		this.width = width;
		this.height = height;
		this.floats = new float[width * height * 3];
	}

	/**
	 * Creates a new image with the given dimensions using the given array of
	 * floats.
	 * 
	 * Let r = width*height, then the length of the float array can either be
	 * equal to r or 3*r.
	 * 
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @param floats
	 *            The floats to construct the image.
	 * @throws IllegalArgumentException
	 *             When the width or height are smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             When the length of the float array is not equal to
	 *             width*height or 3*width*height.
	 * @throws NullPointerException
	 *             When the given float array is null.
	 */
	public PFMImage(int width, int height, float[] floats)
			throws IllegalArgumentException, NullPointerException {
		if (width <= 0)
			throw new IllegalArgumentException(
					"the width has to be larger than zero!");
		if (height <= 0)
			throw new IllegalArgumentException(
					"the height has to be larger than zero!");
		if (floats == null)
			throw new NullPointerException("the given float array is null!");

		final int resolution = width * height;
		final boolean gray;

		if (floats.length == resolution)
			gray = true;
		else if (floats.length == 3.0 * resolution)
			gray = false;
		else
			throw new IllegalArgumentException(
					"the number of floats must either be equal to "
							+ resolution + " or " + (3 * resolution)
							+ " but was " + floats.length);

		// allocate the floats
		this.width = width;
		this.height = height;
		this.floats = new float[3 * resolution];

		// copy the floats
		if (gray) {
			for (int i = 0; i < floats.length; ++i) {
				float value = floats[i];
				for (int j = 0; j < 3; ++j)
					setFloat(3 * i + j, value);
			}
		} else {
			for (int i = 0; i < floats.length; ++i) {
				setFloat(i, floats[i]);
			}
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public PFMImage scale(float value) {
		final int n = nbOfFloats();
		final PFMImage result = new PFMImage(width, height);

		for (int i = 0; i < n; ++i)
			result.setFloat(i, getFloat(i) * value);
		return result;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public PFMImage divide(float value) {
		if (value == 0)
			throw new ArithmeticException("dividing by zero!");
		return scale(1.f / value);
	}

	/**
	 * 
	 * @return
	 */
	public Statistics getStatistics() {
		Statistics statistics = new Statistics();

		for (int i = 0; i < nbOfFloats(); ++i)
			statistics.add(getFloat(i));

		return statistics;

	}

	/**
	 * Returns the i'th float of the image.
	 * 
	 * @param i
	 *            the index of the float we wish to access.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given index is out of bounds.
	 * @return the i'th float of the image.
	 */
	public float getFloat(int i) throws ArrayIndexOutOfBoundsException {
		return floats[i];
	}

	/**
	 * Sets the i'th float of the image.
	 * 
	 * @param i
	 *            index of the float we wish to set.
	 * @param value
	 *            the value for the float.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given index is out of bounds.
	 */
	public void setFloat(int i, float value)
			throws ArrayIndexOutOfBoundsException {
		if (value < 0) {
			int j = i / 3;
			int k = i % 3;
			int x = j % width;
			int y = j / width;
			String color;
			if (k == 0)
				color = "red";
			else if (k == 1)
				color = "green";
			else
				color = "blue";

			System.err.format("Warning: pixel at coordinate (%d, %d) has "
					+ "negative value %.16f for the %s color component!", x, y,
					value, color);
		}
		floats[i] = value;
	}

	/**
	 * Returns the number of floats in this image.
	 * 
	 * @return the number of floats in this image.
	 */
	public int nbOfFloats() {
		return floats.length;
	}

	/**
	 * Returns an array of length 3 consisting of the red, green and blue color
	 * channel at the specified pixel in the image.
	 * 
	 * When the image is grayscale, the three color components will be equal to
	 * each other.
	 * 
	 * @param x
	 *            x position in the image.
	 * @param y
	 *            y position in the image.
	 * @throws IllegalArgumentException
	 *             when the given pixel coordinates are out of range.
	 * @return an array consisting of the red, green and blue color channel at
	 *         the specified pixel in the image.
	 */
	public float[] getColorAt(int x, int y) {
		final int o = 3 * (y * width + x);
		return Arrays.copyOfRange(floats, o, o + 3);
	}

	/**
	 * Sets the color at the given position to the given color.
	 * 
	 * @param x
	 *            the horizontal position of the pixel to set.
	 * @param y
	 *            the vertical position of the pixel to set.
	 * @param color
	 *            array containing the red, green and blue color components for
	 *            the pixel.
	 * @throws NullPointerException
	 *             when the given color is null.
	 * @throws IllegalArgumentException
	 *             when the given array of colors does not have length 3.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the given coordinate position is out of bounds.
	 * 
	 */
	public void setColorAt(int x, int y, float... color)
			throws NullPointerException, IllegalArgumentException {
		if (color == null)
			throw new NullPointerException("the given color is null!");
		if (color.length != 3)
			throw new IllegalArgumentException(
					"the given array of colors does not have length 3!");

		final int o = 3 * (y * width + x);
		for (int i = 0; i < 3; ++i)
			setFloat(o + i, color[i]);

	}

	/**
	 * Converts this Portable Float Map image to a BufferedImage.
	 * 
	 * @param gamma
	 *            The gamma correction factor.
	 * @return a Buffered Image representation of this image.
	 */
	public BufferedImage toBufferedImage(double gamma) {
		BufferedImage result = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		int[] rgba = new int[] { 0, 0, 0, 255 };

		for (int i = 0; i < width * height; ++i) {
			rgba[0] = toInt(getFloat(3 * i), gamma);
			rgba[1] = toInt(getFloat(3 * i + 1), gamma);
			rgba[2] = toInt(getFloat(3 * i + 2), gamma);
			result.getRaster()
					.setPixel(i % width, height - 1 - i / width, rgba);

		}

		return result;
	}

	/**
	 * Converts this Portable Float Map image to a BufferedImage where all the
	 * floats in the image are first scaled to values between [0,1].
	 * 
	 * @param gamma
	 *            The gamma correction factor.
	 * @return a Buffered Image representation of this image.
	 */
	public BufferedImage toScaledBufferedImage(double gamma) {
		BufferedImage result = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		int[] rgba = new int[] { 0, 0, 0, 255 };

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < width * height; ++i) {
			floats[i] = (float) Math.pow(floats[i], gamma);
			if (floats[i] < min)
				min = floats[i];
			if (floats[i] > max)
				max = floats[i];
		}

		float[] f = new float[nbOfFloats()];
		double inv_range = 1.0 / (max - min);
		for (int i = 0; i < width * height; ++i)
			f[i] = (float) ((floats[i] - min) * inv_range);

		for (int i = 0; i < width * height; ++i) {
			rgba[0] = clamp((int) (255.f * f[3 * i]), 0, 255);
			rgba[1] = clamp((int) (255.f * f[3 * i + 1]), 0, 255);
			rgba[2] = clamp((int) (255.f * f[3 * i + 2]), 0, 255);
			result.getRaster()
					.setPixel(i % width, height - 1 - i / width, rgba);
		}

		return result;
	}

	/**
	 * Clamps the given value between the given minimum and maximum.
	 * 
	 * @param value
	 *            the value to clamp.
	 * @param min
	 *            the minimum.
	 * @param max
	 *            the maximum.
	 * @return the given value clamped between to the interval [min,max].
	 */
	public static int clamp(int value, int min, int max) {
		if (value < min)
			return min;
		else if (value > max)
			return max;
		else
			return value;
	}

	/**
	 * Returns a gamma corrected float within a valid RGB range.
	 * 
	 * @param f
	 *            The float within the range [-Infinity,Infinity]
	 * @param gamma
	 *            The inverse of the gamma. (I pass the inverse to avoid
	 *            repeatefiled divisions).
	 * @return the gamma corrected float within the range [0,255].
	 */
	public static int toInt(double f, double gamma) {
		if (f < 0.0)
			return 0;
		else if (f > 1.0)
			return 255;
		else
			return clamp((int) (255.0 * Math.pow(f, 1.0 / gamma)), 0, 255);
	}

	/**
	 * 
	 * @param filename
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public void write(String filename) throws NullPointerException,
			IllegalArgumentException, IOException {
		if (filename == null)
			throw new NullPointerException("the given filename is null!");
		write(new File(filename));
	}

	/**
	 * 
	 * @param filename
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public void write(String filename, double gamma)
			throws NullPointerException, IllegalArgumentException, IOException {
		if (filename == null)
			throw new NullPointerException("the given filename is null!");
		write(new File(filename), gamma);
	}

	/**
	 * 
	 * @param file
	 * @param gamma
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void write(File file) throws NullPointerException,
			IllegalArgumentException, IOException {
		write(file, 2.2);
	}

	/**
	 * 
	 * @param file
	 * @param gamma
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void write(File file, double gamma) throws NullPointerException,
			IllegalArgumentException, IOException {
		if (file == null)
			throw new NullPointerException("the given file is null!");

		// Get the extension
		String filename = file.getName();
		int index = filename.lastIndexOf(".");
		if (index < 0)
			throw new IllegalArgumentException("unspecified extension!");
		String extension = filename.substring(index + 1);

		// Allocate the required directories
		File absolute = file.getAbsoluteFile();
		File parent = absolute.getParentFile();

		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs())
				throw new IllegalArgumentException(
						"could not allocate the required directory!");
		}

		BufferedImage image = toBufferedImage(gamma);
		ImageIO.write(image, extension, file);
	}
}