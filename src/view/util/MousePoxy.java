package view.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Bilal Hussain
 */
public class MousePoxy implements MouseListener, MouseMotionListener {

	private MouseListener ml;
	private MouseMotionListener mml;

	@Override
	public void mouseDragged(MouseEvent e) {
		mml.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mml.mouseMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		ml.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		ml.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		ml.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ml.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ml.mouseReleased(e);
	}

	/** @category Generated Setter */
	public void setMouseListener(MouseListener ml) {
		this.ml = ml;
	}

	/** @category Generated Setter */
	public void setMouseMotionListener(MouseMotionListener mml) {
		this.mml = mml;
	}

}
