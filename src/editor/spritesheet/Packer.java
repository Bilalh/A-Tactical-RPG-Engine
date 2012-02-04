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

	public ISpriteSheet pack(ArrayList<File> files, int width, int height, int border, File out) throws IOException {
		ArrayList<MutableSprite> images = new ArrayList<MutableSprite>();

		try {
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				MutableSprite sprite = new MutableSprite(file.getName(), ImageIO.read(file));

				images.add(sprite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packImages(images, width, height, border, out);
	}

	public Sheet packImages(ArrayList<MutableSprite> images, int width, int height, int border){
		try {
			return packImages(images,width,height,border,null);
		} catch (IOException e) {
			// should never happen
			e.printStackTrace();
		}
		return null;
	}
	
	public Sheet packImages(ArrayList<MutableSprite> images, int width, int height, int border, File out) throws IOException {
		Collections.sort(images);

		int x = 0, y = 0;

		BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = buf.getGraphics();
		int rowHeight = 0;

		try {
			PrintStream pout = null;
			if (out != null) {
				pout = new PrintStream(new FileOutputStream(new File(out.getParentFile(), out.getName().replaceAll("\\.png$", "") + ".xml")));
				pout.println("<sprite-array>");
			}

			for (MutableSprite current : images) {
				if (x + current.getWidth() > width) {
					x = 0;
					y += rowHeight;
					rowHeight = 0;
				}

				if (rowHeight == 0) {
					rowHeight = current.getHeight() + border;
				}

				if (out != null) {
					pout.println(XMLUtil.makeFormattedXml(current));
				}

				current.setPositionInSheet(x, y);
				g.drawImage(current.getImage(), x, y, null);
				x += current.getWidth() + border;
			}
			
			g.dispose();

			if (out != null) {
				pout.println("</sprite-array>");
				pout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed writing sheet's xml", e);
		}

		if (out != null) {
			try {
				ImageIO.write(buf, "PNG", out);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException("Failed writing Image", e);
			}
		}

		return new Sheet(buf, images);
	}

	public static void main(String[] argv) throws IOException {
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
