package editor.map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.GuiMap;
import view.map.IsomertricMapRenderer;
import view.units.AnimatedUnit;
import view.util.BufferSize;

import common.Location;
import config.xml.MapSettings;


/**
 * Contains the view of the map and handles mouse input for the map.
 * @author Bilal Hussain
 */
public class EditorMapPanel extends JPanel {
	private static final long serialVersionUID = 3779345216980490025L;
	private static final Logger log = Logger.getLogger(EditorMapPanel.class);

	// For quick state checks
	private static EnumSet<MapState> drawable = EnumSet.of(MapState.DRAW,MapState.DRAW_INFO,MapState.LEFT_WALL,MapState.RIGHT_WALL);

	private IEditorMapPanelListener editor;
	private IsomertricMapRenderer mapRender;
	private EditorIsoTile[][] field;
	private MapSettings settings;
	
	private int bufferWidth  =-2;
	private int bufferHeight =-3;

	private Image buffer;

	private boolean drawn = false;
	private int frameDuration = 100 * 1000000;
	private int frameChange = 0;

	private Rectangle mouseArea;
	private Point mouseStart;
	private Point old;
	
	private ArrayList<EditorIsoTile> selection(){
		ArrayList<EditorIsoTile> selection = new ArrayList<EditorIsoTile>();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (mouseArea.contains(field[i][j].getBounds())){
					selection.add(field[i][j]);
				}
			}
		}
		return selection;
	}
	
	private ArrayList<EditorIsoTile> selected = new ArrayList<EditorIsoTile>();
	private EditorIsoTile current;
	
	private static final int[][] dirs = {
		{ 0, 1 },  // up
		{ 0, -1 }, // down
		{ -1, 0 }, // left
		{ 1, 0 },  // right
	};

		
	public EditorMapPanel(final IEditorMapPanelListener editor, final EditorIsoTile[][] field, MapSettings settings) {
		this.editor = editor;
		setMap(field,settings);
		
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (old ==null){
					old = e.getPoint();
				}
				if (current ==null){
					if (findCurrent(e.getPoint())==null) return;
				}
				
				findNext(e.getPoint(), false);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (current!=null && (drawable.contains(editor.getEditorState()))){
					findNext(e.getPoint(),true);
				}
				
				if (editor.getEditorState() != MapState.SELECTION) return;
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
				if (drawable.contains(editor.getEditorState())){
					findCurrent(mouseStart);
					old = mouseStart;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (mouseArea != null && editor.getEditorState() == MapState.SELECTION) {
					ArrayList<EditorIsoTile> selection = selection();
					editor.tilesSelected(selection,e.isShiftDown());
				}
				mouseArea = null;
			}
		});

	}

	private EditorIsoTile findCurrent(Point p) {
		int xIndex = -1, yIndex = -1;
		double highest = 0.0;

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[0].length; j++) {
				if (field[i][j].contains(p)) {
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
        }else{
        	current=null;
        }
        return current;
	}
	
	private void findNext(Point e, boolean sendClicked) {
		assert current !=null : "current null";
		if (sendClicked) editor.tileClicked(current);
		else             editor.tileEntered(current);
		
		if (e.distance(old) > settings.tileDiagonal / 6) {
			if (findCurrent(e) ==null) return;
			if (sendClicked) editor.tileClicked(current);
			else             editor.tileEntered(current);
		}

		
		Location start = current.getLocation().copy();
		for (int[] ii : dirs) {
			Location l = start.copy().translate(ii[0], ii[1]);

			if (l.x < 0 || l.x >= field.length || l.y < 0 || l.y >= field[0].length) {
				continue;
			}

			if (field[l.x][l.y].contains(e)) {
				if (sendClicked) editor.tileClicked(current);
				else             editor.tileEntered(current);
				break;
			}
		}
		if (sendClicked) editor.tileClicked(current);
		else             editor.tileEntered(current);
		
	}

	public synchronized void setMap(EditorIsoTile[][] field, MapSettings settings){
		this.field    = field;
		this.settings = settings;
		
		buffer     = null;
		mapRender  = new IsomertricMapRenderer(field, editor, 1.1f, settings);
		BufferSize s = mapRender.getMapDimensions();
		s.height*= 1.5f;
		setPreferredSize(mapRender.getMapDimensions());
		bufferWidth  = s.width;
		bufferHeight = s.height;
	}
	
	public void mousePressed(MouseEvent e) {
		if (editor.getEditorState()==MapState.SELECTION) return;
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
			Logf.trace(log, "Selected %s", current.getLocation());
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
		if (editor.getEditorState() == MapState.SELECTION && mouseArea != null){
			Color old = _g.getColor();
			_g.setColor(Color.BLUE);
			_g.drawRect(mouseArea.x, mouseArea.y, mouseArea.width, mouseArea.height);
			_g.setColor(old);
		}
	}
	
	public  void repaintMap(){
		log.debug("repainting Map");
		drawn = false;
		repaint();
	}
	
	public void invalidateMap(){
		mapRender.invaildate();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j].invaildate(settings);
			}
		}
		repaintMap();
	}
	
	public void rotate(Collection<AnimatedUnit> units){
		mapRender.rotateMap();
		for (AnimatedUnit au : units) {
			GuiMap.translateDirectionOnRotation(au, mapRender);
		}
		invalidateMap();
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