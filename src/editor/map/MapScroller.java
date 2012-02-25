package editor.map;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;


public class MapScroller extends MouseAdapter {
	private final Cursor normal   = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private final Cursor movement = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private final Point start = new Point();

	private JViewport mapViewport;
	private EditorMapPanel editorMapPanel;
	private IEditorMapPanelListener parent;

	/** @category Generated */
	public MapScroller(JViewport mapViewport, EditorMapPanel editorMapPanel, IEditorMapPanelListener parent) {
		this.mapViewport = mapViewport;
		this.editorMapPanel = editorMapPanel;
		this.parent = parent;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (parent.getEditorState() != MapState.MOVE) return;
		Point p = e.getPoint();
		Point vp = mapViewport.getViewPosition();
		vp.translate(start.x - p.x, start.y - p.y);
		editorMapPanel.scrollRectToVisible(new Rectangle(vp, mapViewport.getSize()));
		start.setLocation(p);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (parent.getEditorState() != MapState.MOVE) return;
		((JComponent) e.getSource()).setCursor(movement);
		start.setLocation(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (parent.getEditorState() != MapState.MOVE) return;
		((JComponent) e.getSource()).setCursor(normal);
	}
}