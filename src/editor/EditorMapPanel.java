package editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.GuiTile;
import view.map.MapRenderer;
import view.map.MapSettings;

class EditorMapPanel extends JPanel {
	private static final Logger log = Logger.getLogger(EditorMapPanel.class);

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

		mapRender = new MapRenderer(field, editor, startX, startY);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				EditorMapPanel.this.mousePressed(e);
			}
		});

	}

	public void mousePressed(MouseEvent e) {
		GuiTile current = null;
		double highest = 0.0;

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j].wasClickedOn(e.getPoint())) {
					Logf.trace(log, "\t%s (%s,%s)", e.getPoint(), i, j);
				}
				if (field[i][j].wasClickedOn(e.getPoint()) && field[i][j].getHeight() > highest) {
					highest = field[i][j].getHeight();
					current = field[i][j];
				}
			}
		}
		if (current != null) {
			Logf.debug(log, "Selected %s", current.getFieldLocation());
			editor.tileClicked(current);
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		if (buffer == null) {
			buffer = createImage(bufferWidth, bufferHeight);
		}

		Graphics _g = buffer.getGraphics();
		if (!drawn) {
			mapRender.draw(_g, bufferWidth, bufferHeight);
			drawn = true;
		}
		g.drawImage(buffer, 0, 0, null);
		_g.dispose();
	}
	
	public  void repaintMap(){
		drawn = false;
		repaint();
	}
	
}