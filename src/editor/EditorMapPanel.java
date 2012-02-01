package editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import view.map.GuiTile;
import view.map.MapRenderer;
import view.map.MapSettings;

class EditorMapPanel extends JPanel {

	private static final long serialVersionUID = 3779345216980490025L;

	private Editor editor;
	private MapRenderer mapRender;
	private GuiTile[][] field;

	private int heightOffset;
	private int bufferWidth;
	private int bufferHeight;

	private Image buffer;

	private boolean drawn = false;
	private int frameDuration = 100 * 1000000;
	private int frameChange = 0;

	public EditorMapPanel(Editor editor, GuiTile[][] field) {
		this.editor = editor;
		this.field = field;

		heightOffset = (MapSettings.tileDiagonal);
		bufferWidth = MapSettings.tileDiagonal * field.length + 5;
		bufferHeight = (int) (MapSettings.tileDiagonal / 2f * field[0].length + heightOffset);
		setPreferredSize(new Dimension(bufferWidth, bufferHeight));

		int startX = bufferWidth / 2;
		int startY = heightOffset;

		buffer = createImage(bufferWidth, bufferHeight);
		mapRender = new MapRenderer(field, editor, startX, startY);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				EditorMapPanel.this.mousePressed(e);
			}
		});
	}

	public void mousePressed(MouseEvent e) {
		GuiTile current = field[0][0];
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j].wasClickedOn(e.getPoint()) && field[i][j].getHeight() > current.getHeight()) {
					current = field[i][j];
				}
			}
		}
		if (current != null) {
//			current.setSelected(true);
//			repaint(current.getBounds());
			editor.tileClicked(current);
		}

	}

	long before = System.nanoTime();
	@Override
	protected void paintComponent(Graphics g) {
		g.fillRect(0, 0, getWidth(), getHeight());
		mapRender.draw(g, bufferWidth, bufferHeight);
		// long temp = System.nanoTime();
		// frameChange += (temp-before);
		// if (frameChange > frameDuration) {
		// frameChange = 0;
		// drawn =false;
		// }
		// if (!drawn){
		// g.fillRect(0, 0, getWidth(), getHeight());
		// mapRender.draw(g, bufferWidth, bufferHeight);
		// g.drawImage(buffer, 0, 0, null);
		// }
		// before = temp;
	}
}