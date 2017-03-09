package pfm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementation of a class capable of reading Portable Float Map images.
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
public class PFMWriter {
	/**
	 * Reads a Portable Float Map from the file specified by the given filename.
	 * 
	 * @param filename
	 *            name of the file to read the Portable Float Map from.
	 * @throws IOException
	 *             when an exception occurs during the reading of the file.
	 * @return an object containing the Portable Float Map image.
	 */
	public static void write(String filename, PFMImage image)
			throws IOException {
		write(new File(filename), image);
	}

	/**
	 * Reads a Portable Float Map from the given file.
	 * 
	 * @param file
	 *            file to read the Portable Float Map from.
	 * @throws IOException
	 *             when an exception occurs during the reading of the file.
	 * @return an object containing the Portable Float Map image.
	 */
	public static void write(File file, PFMImage image) throws IOException {
		{
			FileWriter writer = new FileWriter(file);
			BufferedWriter w = new BufferedWriter(writer);
			w.write("PF\n");
			w.write(image.width + " " + image.height + "\n");
			w.write("1.0\n");
			w.flush();
			w.close();
			writer.close();
		}

		{
			ByteBuffer buffer = ByteBuffer.allocate(4 * image.nbOfFloats());
			FileOutputStream stream = new FileOutputStream(file, true);
			for (int i = 0; i < image.nbOfFloats(); ++i) {
				float f = image.getFloat(i);
				buffer = buffer.putFloat(f);
			}
			stream.write(buffer.array());
			stream.flush();
			stream.close();
		}
	}
}