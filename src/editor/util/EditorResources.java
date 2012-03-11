package editor.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import editor.Editor;
import editor.map.MapEditor;

/**
 * Gets editor resources
 * @author Bilal Hussain
 */
public final class EditorResources {

	private EditorResources() {
	}

	
	public static InputStream getFileAsStream(String filename) {
		return Editor.class.getResourceAsStream("resources/" + filename);
	}
	
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(Editor.class.getResourceAsStream("resources/" + filename));
		} catch (IOException e) {
			System.err.println("Failed to load  image: " + filename);
			e.printStackTrace();
			assert false;
		}
		return null;
	}

	public static ImageIcon getIcon(String filename) {
		return new ImageIcon(getImage(filename));
	}

}
