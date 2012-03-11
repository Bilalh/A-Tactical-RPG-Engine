package view.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.interfaces.IActions;

/**
 * Stub for all actions and mouse input
 * @author Bilal Hussain
 */
public class ActionsAdapter implements IActions, MouseListener, MouseMotionListener {
	private static final Logger log = Logger.getLogger(ActionsAdapter.class);
	
	
	@Override
	public void keyComfirm() {

	}

	@Override
	public void keyCancel() {

	}

	@Override
	public void keyUp() {

	}

	@Override
	public void keyDown() {

	}

	@Override
	public void keyLeft() {

	}

	@Override
	public void keyRight() {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3){
			Logf.info(log, "mouse cancel: %s");
			keyCancel();
			return;
		}else{
			keyComfirm();
		}
	}

	@Override
	public void keyOther1() {
		
	}

	@Override
	public void keyOther2() {

	}

	@Override
	public void keyOther3() {
	}

}
