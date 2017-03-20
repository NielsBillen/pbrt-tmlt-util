package utilities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import util.CLI;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class ImageComposer {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<String> arguments = new LinkedList<String>();
		arguments.add("mirror-balls");
		compose(arguments);
	}

	/**
	 * 
	 * @param arguments
	 */
	public static void compose(LinkedList<String> arguments) {
		int xresolution = 1024;
		int yresolution = 1024;

		while (!arguments.isEmpty()) {
			String token = arguments.removeFirst();

			if (token.equals("-resolution") || token.equals("--resolution")) {
				int resolution = CLI.nextInteger(token, arguments);
				xresolution = resolution;
				yresolution = resolution;
			} else if (token.equals("-xresolution")
					|| token.equals("--xresolution")) {
				xresolution = CLI.nextInteger(token, arguments);
			} else if (token.equals("-yresolution")
					|| token.equals("--yresolution")) {
				yresolution = CLI.nextInteger(token, arguments);
			} else {
				File folder = new File(token);
				BufferedImage image = new BufferedImage(xresolution,
						yresolution, BufferedImage.TYPE_INT_ARGB);

				File[] tiles = folder.listFiles(new FilenameFilter() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see java.io.FilenameFilter#accept(java.io.File,
					 * java.lang.String)
					 */
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".png");
					}
				});

				Graphics g = image.getGraphics();
				Pattern pattern = Pattern
						.compile("tile.([0-9]+).([0-9]+).([0-9]+).([0-9]+)");

				int count = 0;
				System.out.format("found %d tiles\n", tiles.length);
				for (File tile : tiles) {
					try {
						BufferedImage i = ImageIO.read(tile);
						Matcher matcher = pattern.matcher(tile.getName());

						if (matcher.find()) {
							int x = Integer.parseInt(matcher.group(1));
							int y = Integer.parseInt(matcher.group(2));
							g.drawImage(i, x, y, null);
							++count;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.format("%d had valid tile identifiers ...\n", count);

				try {
					ImageIO.write(image, "png", new File("merged.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
