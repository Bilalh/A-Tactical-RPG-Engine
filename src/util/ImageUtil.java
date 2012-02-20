package util;

import java.awt.*;
import java.awt.image.*;

/**
 * @author Bilal Hussain
 */
public class ImageUtil {

	public static Image setColorToTransparent(BufferedImage img, final Color c) {
		ImageFilter filter = new ColorToTransparent(c);
		ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
	
	public static BufferedImage createBufferedImage(Image sourceImage) {
		final int width = sourceImage.getWidth(null), height = sourceImage.getHeight(null);
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bimg = gc.createCompatibleImage(width, height, Transparency.BITMASK);
		Graphics g = bimg.getGraphics();
		g.drawImage(sourceImage, 0, 0, width, height, null);
		g.dispose();
		return bimg;

	}

	static class ColorToTransparent extends RGBImageFilter{
		
		private Color colour;
		private int rgbMask;
		
		public ColorToTransparent(Color colour) {
			this.colour = colour;
			rgbMask = colour.getRGB() | 0xFF000000;
		}
		
		@Override
		public final int filterRGB(int x, int y, int rgb) {
			if ((rgb | 0xFF000000) == rgbMask) {
				// Mark the alpha bits as zero meaning transparent
				return 0x00FFFFFF & rgb;
			}
			return rgb;
		}
	}
	
}
