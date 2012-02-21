package util.scripts;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import common.enums.ImageType;
import common.enums.Orientation;
import common.gui.ResourceManager;

import view.map.IsoTile;

/**
 * @author Bilal Hussain
 */
public class A extends JFrame {

	IsoTile t; 
	
	A(){
		
		t = new IsoTile(Orientation.UP_TO_EAST, 1, 1, 0, 1, "2", ImageType.TEXTURED);
	}
	
	@Override
	public void paint(Graphics g) {
		t.draw(50, 50, g, true, true);
	}
	
	public static void main(String[] args) throws IOException {
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage  image = gc.createCompatibleImage(70,40,Transparency.BITMASK);
		Graphics g = image.getGraphics();
		
		ResourceManager.instance().loadSpriteSheetFromResources("images/tilesets/fft2.png");
		IsoTile t = new IsoTile(Orientation.UP_TO_EAST, 1, 1, 0, 1, "2", ImageType.TEXTURED);
		t.draw(40, 20, g, true, true);
		System.out.println(t);
		ImageIO.write(image, "PNG", new File("out2.png"));
		
//		JFrame a = new A();
//		a.setSize(400,400);
//		a.setVisible(true);
	}
	
}
