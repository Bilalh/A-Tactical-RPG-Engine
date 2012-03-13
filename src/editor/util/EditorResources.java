package editor.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import editor.Editor;
import editor.map.MapEditor;

/**
 * Gets editor resources
 * @author Bilal Hussain
 */
public final class EditorResources {
	private static final Logger log = Logger.getLogger(EditorResources.class);
	
	private EditorResources() {
	}

	
	public static InputStream getFileAsStream(String filename) {
		InputStream s =  Editor.class.getResourceAsStream("/editor/resources/" + filename);
		log.info(filename);
		if (s == null) throw new IllegalArgumentException(filename + "not found");
		return s;
	}
	
	public static BufferedImage getImage(String filename) {
		try {
			return ImageIO.read(getFileAsStream(filename));
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
