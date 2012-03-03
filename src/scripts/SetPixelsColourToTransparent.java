package scripts;

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
	JFileChooser chooser;
	public SetPixelsColourToTransparent() {
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chooser = new JFileChooser("Resources/images");
		chooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		chooser.setMultiSelectionEnabled(true);
	}

	public void makeTranspanarent(int x, int y) throws IOException {
		int rst = chooser.showOpenDialog(f);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File dir = new File(chooser.getSelectedFile().getParentFile(), "results");
			dir.mkdirs();
			for (File f : chooser.getSelectedFiles()) {
				BufferedImage source = ImageIO.read(f);
				int color = source.getRGB(x, y);
				Image image = ImageUtil.setColorToTransparent(source, new Color(color));
				BufferedImage transparent = ImageUtil.createBufferedImage(image);
				File out = new File(dir, f.getName());
				ImageIO.write(transparent, "PNG", out);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		SetPixelsColourToTransparent s = new SetPixelsColourToTransparent();
		s.makeTranspanarent(0, 0);
	}

}