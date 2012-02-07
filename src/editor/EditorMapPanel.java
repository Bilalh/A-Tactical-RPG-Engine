package editor;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import common.Location;

import editor.map.EditorIsoTile;

import util.Logf;
import view.map.BufferSize;
import view.map.IsoTile;
import view.map.IsomertricMapRenderer;
import view.map.MapSettings;

/**
 * Contains the view of the map and handles mouse input for the map.
 * @author Bilal Hussain
 */
class EditorMapPanel extends JPanel {
	private static final Logger log = Logger.getLogger(EditorMapPanel.class);

	private static final long serialVersionUID = 3779345216980490025L;

	private Editor editor;
	private IsomertricMapRenderer mapRender;
	private EditorIsoTile[][] field;

	private int bufferWidth  =-2;
	private int bufferHeight =-3;

	private Image buffer;

	private boolean drawn = false;
	private int frameDuration = 100 * 1000000;
	private int frameChange = 0;

	private Rectangle mouseArea;
	private Point mouseStart;
	private Point old;
	
	private void selection(){
		ArrayList<EditorIsoTile> selection = new ArrayList<EditorIsoTile>();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (mouseArea.contains(field[i][j].getBounds())){
					selection.add(field[i][j]);
				}
			}
		}
		
		editor.tilesSelected(selection);
		repaint(50);
	}
	
	private ArrayList<EditorIsoTile> selected = new ArrayList<EditorIsoTile>();
	private EditorIsoTile current;
	
	private static final int[][] dirs = {
		{ 0, 1 },  // up
		{ 0, -1 }, // down
		{ -1, 0 }, // left
		{ 1, 0 },  // right
};

	
	public EditorMapPanel(final Editor editor, final EditorIsoTile[][] field) {
		this.editor = editor;
		setMap(field);
		
		this.addMouseMotionListener(new MouseMotionAdapter() {

			
			@Override
			public void mouseDragged(MouseEvent e) {
				Location start = current.getFieldLocation().copy();
				if (editor.getState() == State.DRAW) {
					for (int[] ii : dirs) {
						Location l = start.copy().translate(ii[0], ii[1]);

						if (l.x < 0 || l.x >= field.length || l.y < 0 || l.y >= field[0].length) {
							continue;
						}

						if (field[l.x][l.y].contains(e.getPoint())) {
							editor.tileClicked(field[l.x][l.y]);
							current = field[l.x][l.y];
							break;
						}
						
					}
				}
				
				if (editor.getState() != State.SELECTION) return;
				Point end = e.getPoint();
				int y_max = Math.max(mouseStart.y, end.y);
				int y_min = Math.min(mouseStart.y, end.y);
				
				int x_max = Math.max(mouseStart.x, end.x);
				int x_min = Math.min(mouseStart.x, end.x);
				
				mouseArea = new Rectangle(x_min,y_min, x_max - x_min, y_max-y_min);
				repaint(10);
			}
			
		});
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				EditorMapPanel.this.mousePressed(e);
				mouseStart = e.getPoint();
				if (editor.getState() == State.DRAW){
					findSelected();
					old = mouseStart;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (mouseArea != null && editor.getState() == State.SELECTION) {
					selection();
				}
				mouseArea = null;
			}

		});

	}

	private void findSelected() {
		int xIndex = -1, yIndex = -1;
		double highest = 0.0;

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				if (field[i][j].contains(mouseStart)) {
					if (field[i][j].getHeight() > highest) {
						highest = field[i][j].getHeight();
						xIndex = i;
						yIndex = j;
					}
				}
			}
		}
        if (xIndex > -1 && yIndex > -1) {
        	current = field[xIndex][yIndex];
        }
	}
	
	public synchronized void setMap(EditorIsoTile[][] field){
		this.field    = field;
		buffer     = null;
		mapRender  = new IsomertricMapRenderer(field, editor);
		BufferSize s = mapRender.getMapDimensions();
		setPreferredSize(mapRender.getMapDimensions());
		bufferWidth  = s.width;
		bufferHeight = s.height;
	}
	
	public void mousePressed(MouseEvent e) {
		EditorIsoTile current = null;
		double highest = 0.0;

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j].contains(e.getPoint())) {
					Logf.trace(log, "\t%s (%s,%s)", e.getPoint(), i, j);
				}
				if (field[i][j].contains(e.getPoint()) && field[i][j].getHeight() > highest) {
					highest = field[i][j].getHeight();
					current = field[i][j];
				}
			}
		}
		if (current != null) {
			Logf.trace(log, "Selected %s", current.getFieldLocation());
			editor.tileClicked(current);
		}

	}

	@Override
	protected void paintComponent(Graphics _g) {
		if (buffer == null) {
			buffer = createImage(bufferWidth, bufferHeight);
		}

		Graphics g = buffer.getGraphics();
		if (!drawn) {
			Color old =  _g.getColor();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			g.setColor(old);
			mapRender.draw(g, bufferWidth, bufferHeight);
			drawn = true;
		}
		_g.drawImage(buffer, 0, 0, null);
		g.dispose();
		if (editor.getState() == State.SELECTION && mouseArea != null){
			Color old = _g.getColor();
			_g.setColor(Color.BLUE);
			_g.drawRect(mouseArea.x, mouseArea.y, mouseArea.width, mouseArea.height);
			_g.setColor(old);
		}
	}
	
	public  void repaintMap(){
//		log.debug("repainting Map");
		drawn = false;
		repaint();
	}

	/** @category Generated */
	public void toggleNumbering() {
		mapRender.toggleNumbering();
	}

	/** @category Generated */
	public boolean hasNumbering() {
		return mapRender.hasNumbering();
	}
	
}