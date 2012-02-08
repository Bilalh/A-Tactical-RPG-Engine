package util.scripts;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.spritesheet.SpriteSheetEditor;

import util.ImageUtil;


/**
 * Set all pixels of the colour specifed (by the selected pixel) to transparent.
 * @author Bilal Hussain
 */
public class SetPixelsColourToTransparent {
	private JFrame f = new JFrame();

	public SetPixelsColourToTransparent(int x, int y) throws IOException {
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JFileChooser chooser = new JFileChooser("Resources/images");
		chooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		chooser.setMultiSelectionEnabled(true);
		int rst = chooser.showOpenDialog(f);
		if (rst == JFileChooser.APPROVE_OPTION) {
			for (File f : chooser.getSelectedFiles()) {
				BufferedImage source = ImageIO.read(f);
				int color = source.getRGB(x, y);
				Image image = ImageUtil.setColorToTransparent(source, new Color(color));
				BufferedImage transparent = ImageUtil.createBufferedImage(image);
				File out = new File(f.getPath().replaceAll("\\.png", "-rst.png"));
				ImageIO.write(transparent, "PNG", out);
			}
		}

	}

	public static void main(String[] args) throws Exception {
		SetPixelsColourToTransparent s = new SetPixelsColourToTransparent(0, 0);
	}

}