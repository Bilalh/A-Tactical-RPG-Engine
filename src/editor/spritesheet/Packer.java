package editor.spritesheet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import config.XMLUtil;

public class Packer {


	/**
	 * Packs the sprites into a image of the specifed size then writes the image to a file.
	 */
	public Sheet packImages(ArrayList<MutableSprite> images, int width, int height, int border, File out) throws IOException {
		Sheet sheet = packImages(images, width, height, border);

		assert out !=null;
		PrintStream pout = new PrintStream(new FileOutputStream(new File(out.getParentFile(),
				out.getName().replaceAll("\\.png$", "") + ".xml")));
		pout.print(XMLUtil.makeFormattedXml(images.toArray(new MutableSprite[0])));
		pout.close();
		ImageIO.write(sheet.getSheetImage(), "PNG", out);

		return sheet;
	}

	/**
	 * Packs the sprites into a image of the specifed size.
	 */
	public Sheet packImages(ArrayList<MutableSprite> images, int width, int height, int border) {
		Collections.sort(images);
		int x = 0, y = 0;

		BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = buf.getGraphics();
		int rowHeight = 0;

		for (MutableSprite current : images) {
			if (x + current.getWidth() > width) {
				x = 0;
				y += rowHeight;
				rowHeight = 0;
			}

			if (rowHeight == 0) {
				rowHeight = current.getHeight() + border;
			}

			current.setPositionInSheet(x, y);
			g.drawImage(current.getImage(), x, y, null);
			x += current.getWidth() + border;
		}

		g.dispose();
		return new Sheet(buf, images);
	}

	
	public ISpriteSheet pack(ArrayList<File> files, int width, int height, int border, File out) throws IOException {
		ArrayList<MutableSprite> images = new ArrayList<MutableSprite>();

		for (File f : files) {
			MutableSprite sprite = new MutableSprite(f.getName(), ImageIO.read(f));
			images.add(sprite);
		}
		
		return packImages(images, width, height, border, out);
	}

	
	/**
	 * Packs all the image in the current directory into output.png.
	 */
	public static void main(String[] args) throws IOException {
		File dir = new File(".");
		ArrayList list = new ArrayList();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".png")) {
				if (!files[i].getName().startsWith("output")) {
					list.add(files[i]);
				}
			}
		}

		Packer packer = new Packer();
		packer.pack(list, 512, 512, 1, new File(dir, "output.png"));
		System.out.println("Saved as output.png");
	}
}
