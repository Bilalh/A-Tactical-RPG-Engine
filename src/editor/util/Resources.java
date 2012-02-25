package editor.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import editor.MapEditor;

/**
 * @author Bilal Hussain
 */
public final class Resources {

	private Resources() {
	}
	
	public static BufferedImage getImage(String filename) throws IOException, IllegalArgumentException {
		return ImageIO.read(MapEditor.class.getResourceAsStream("resources/" + filename));
	}

	public static ImageIcon getIcon(String filename) {
		try {
			return new ImageIcon(getImage(filename));
		} catch (IOException e) {
			System.err.println("Failed to load  image: " + filename);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Failed to load resource: " + filename);
			e.printStackTrace();
		}
		return null;
	}

}
