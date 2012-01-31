package editor.spritesheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class SpriteSheetPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Image image;
	private int width;
	private int height;
	
	// for transparency
	private TexturePaint background;
	// generated sheet
	private Sheet sheet;

	private ArrayList<Spritee> selected = new ArrayList<Spritee>();
	private SpriteSheetEditor editor;

	public SpriteSheetPanel(SpriteSheetEditor e) {
		this.editor = e;

		Color base = Color.gray;
		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.setColor(base);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(base.darker());
		g.fillRect(image.getWidth() / 2, 0,  image.getWidth() / 2, image.getHeight() / 2);
		g.fillRect(0, image.getHeight() / 2, image.getWidth() / 2, image.getHeight() / 2);
		
		background = new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				SpriteSheetPanel.this.requestFocusInWindow();
				Spritee sprite = editor.getSpriteAt(e.getX(), e.getY());
				if (sprite != null) {
					ArrayList<Spritee> selection = new ArrayList<Spritee>();
					if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
						selection.addAll(selected);
					}
					selection.add(sprite);
					editor.select(selection);
				}
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!selected.isEmpty() && (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					editor.delete(selected);
				}
			}
		});
		
	}

	public void setSelectedSprites(ArrayList sprites) {
		this.selected = sprites;
		repaint(0);
	}

	public void setSheetSize(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
	}

	public void setImage(Sheet sheet) {
		this.sheet = sheet;
		this.image = sheet.getImage();
		repaint(0);
	}

	@Override
	public void paint(Graphics g1d) {
		Graphics2D g = (Graphics2D) g1d;

		g.setPaint(background);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.RED);
		g.drawRect(0, 0, width, height);

		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}

		g.setColor(Color.BLUE);
		for (int i = 0; i < selected.size(); i++) {
			Spritee sprite =selected.get(i);
			g.drawRect(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		}
	}
}
