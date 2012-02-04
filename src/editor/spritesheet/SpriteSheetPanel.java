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
import java.util.List;

import javax.swing.JPanel;

import common.gui.Sprite;
import common.spritesheet.SpriteInfo;

public class SpriteSheetPanel<E extends SpriteInfo> extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int width;
	private int height;
	
	// for transparency
	private TexturePaint background;
	// generated sheet
	private ISpriteSheet sheet;

	private List<E> selected = new ArrayList<E>();
	private ISpriteProvider<E> spriteProvider;

	public SpriteSheetPanel(ISpriteProvider<E> spriteProvider) {
		this.spriteProvider = spriteProvider;
		initGui();
		addMouse();
		addKeys();
	}

	protected void initGui(){
		Color base = Color.gray;
		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.setColor(base);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.setColor(base.darker());
		g.fillRect(image.getWidth() / 2, 0,  image.getWidth() / 2, image.getHeight() / 2);
		g.fillRect(0, image.getHeight() / 2, image.getWidth() / 2, image.getHeight() / 2);
		
		background = new TexturePaint(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()));

	}
	
	protected void addMouse(){
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				SpriteSheetPanel.this.requestFocusInWindow();
				E sprite = spriteProvider.getSpriteAt(e.getX(), e.getY());
				if (sprite != null) {
					ArrayList<E> selection = new ArrayList<E>();
					if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
						selection.addAll(selected);
					}
					if (selection.contains(sprite)){
						selection.remove(sprite);
					}else{
						selection.add(sprite);
					}
					spriteProvider.select(selection);
				}
			}
		});
	}
	
	protected void addKeys(){
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!selected.isEmpty() && (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					spriteProvider.delete(selected);
				}
			}
		});
	}
	
	public void setSelectedSprites(List<E> selection) {
		this.selected = selection;
		repaint(0);
	}

	public void setSheetSize(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setSize(new Dimension(width, height));
		this.width = width;
		this.height = height;
	}

	public void setSpriteSheet(ISpriteSheet sheet) {
		this.sheet = sheet;
		this.image = sheet.getSheetImage();
		repaint(0);
	}

	@Override
	public void paint(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;

		g.setPaint(background);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.RED);
		g.drawRect(0, 0, width, height);

		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}

		g.setColor(Color.BLUE);
		for (int i = 0; i < selected.size(); i++) {
			E sprite =selected.get(i);
			g.drawRect(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		}
	}
}
