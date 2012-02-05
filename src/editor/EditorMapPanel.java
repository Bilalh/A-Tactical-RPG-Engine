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

import editor.map.EditorIsoTile;

import util.Logf;
import view.map.IsoTile;
import view.map.IsomertricMapRenderer;
import view.map.MapSettings;

class EditorMapPanel extends JPanel {
	private static final Logger log = Logger.getLogger(EditorMapPanel.class);

	private static final long serialVersionUID = 3779345216980490025L;

	private Editor editor;
	private IsomertricMapRenderer mapRender;
	private EditorIsoTile[][] field;

	private int heightOffset;
	private int bufferWidth;
	private int bufferHeight;

	private Image buffer;

	private boolean drawn = false;
	private int frameDuration = 100 * 1000000;
	private int frameChange = 0;

	private Rectangle mouseArea;
	private Point mouseStart;
	
	
	private void selection(){
		Set<EditorIsoTile> selection = new HashSet<EditorIsoTile>();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (mouseArea.contains(field[i][j].getBounds())){
					selection.add(field[i][j]);
				}
			}
		}
		tile
		
		selection.clear();
		repaint(50);
	}
	
	public EditorMapPanel(final Editor editor, EditorIsoTile[][] field) {
		this.editor = editor;
		setMap(field);
		
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
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
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (editor.getState() == State.SELECTION) {
					selection();
				}
				mouseArea = null;
			}

		});

	}

	public synchronized void setMap(EditorIsoTile[][] field){
		this.field    = field;
		
        //FIXME hack calculate real size of bufffer
        int max = Math.max(field.length, field[0].length);

		heightOffset  = (MapSettings.tileDiagonal);
		bufferWidth   = MapSettings.tileDiagonal * max + 5;
		bufferHeight  = (int) (MapSettings.tileDiagonal / 2f *max + heightOffset);
		
		setPreferredSize(new Dimension(bufferWidth, bufferHeight));

		int startX = bufferWidth / 2;
		int startY = heightOffset;
		buffer     = null;
		mapRender  = new IsomertricMapRenderer(field, editor, startX, startY);
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
		if (editor.getState() == State.SELECTION && mouseArea != null){
			Color old = g.getColor();
			g.setColor(Color.BLUE);
			g.drawRect(mouseArea.x, mouseArea.y, mouseArea.width, mouseArea.height);
			g.setColor(old);
		}
	}
	
	public  void repaintMap(){
		log.trace("repainting Map");
		drawn = false;
		repaint();
	}
	
}