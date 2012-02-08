package util.scripts;
/**
 * @author Bilal Hussain
 */
import java.awt.*;
import java.awt.image.*;
import java.io.File;

import javax.imageio.ImageIO;

import util.ImageUtil;

public class SetFirstPixelsColourToTransparent
{
	public static void main(String[] args) throws Exception {

		String path = "Resources/images/characters/NW.png";

		File in = new File(path);
		BufferedImage source = ImageIO.read(in);
		int color = source.getRGB(0, 0);
		Image image = ImageUtil.setColorToTransparent(source, new Color(color));
		BufferedImage transparent = ImageUtil.createBufferedImage(image);

		File out = new File(path.replaceAll("\\.png", "-rst.png"));
		ImageIO.write(transparent, "PNG", out);

	}

}