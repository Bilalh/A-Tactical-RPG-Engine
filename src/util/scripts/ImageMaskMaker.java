package util.scripts;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import common.gui.Sprite;
import common.gui.ResourceManager;


/**
 * Makes the tile mask at various sizes
 * @author Bilal Hussain
 */
public class ImageMaskMaker {

	static String filename = "Resources/images/tiles/mask.png";
	static String dstBase  = "Resources/images/tiles/mask-";

	public static void main(String[] args) throws IOException {
		config.Config.loadLoggingProperties();
		Sprite s = ResourceManager.instance().getSpriteFromClassPath(filename);
		
		int[]diagonals = {16,32,60,64,80,100,128,192,200,256};
		
		for (int diagonal : diagonals) {
			
			int mheight =  diagonal/2;
			Image sourceImage = s.getImage().getScaledInstance(diagonal,mheight, Image.SCALE_SMOOTH);

			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage  image = gc.createCompatibleImage(sourceImage.getWidth(null),sourceImage.getHeight(null),Transparency.BITMASK);
			image.getGraphics().drawImage(sourceImage,0,0,null);
			
			ImageIO.write(image, "png", new File(dstBase + (diagonal) + ".png"));	
		}
		
	}
	
}
