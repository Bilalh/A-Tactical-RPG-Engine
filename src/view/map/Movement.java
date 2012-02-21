package view.map;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collection;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.IsoTile.TileState;
import view.units.AnimatedUnit;
import view.util.MapActions;

import common.Location;
import common.LocationInfo;
import common.enums.Direction;

/**
 * @author Bilal Hussain
 */
public  class Movement extends MapActions{
	private static final Logger log = Logger.getLogger(Movement.class);
	
    private Point mouseStart = new Point(), mouseEnd = new Point();
    private int offsetX, offsetY;
	
	public Movement(GuiMap map) {
		super(map);
	}

	@Override
	public void keyUp() {
		map.moveSelectedTile(Direction.NORTH);
	}

	@Override
	public void keyDown() {
		map.moveSelectedTile(Direction.SOUTH);
	}

	@Override
	public void keyLeft() {
		map.moveSelectedTile(Direction.WEST);
	}

	@Override
	public void keyRight() {
		map.moveSelectedTile(Direction.EAST);
	}
	
	@Override
	public void keyComfirm() {
		tileSelected();
	}
	
	private void tileSelected() {
		assert map.getCurrentUnit() != null;
		map.tileSelected();
	}
	
	@Override
	public void keyCancel() {
		Logf.info(log, "cancel: %s",map.getState());
		map.changeState(map.getState().cancel(null));
	}
	
    @Override
	public void mousePressed(MouseEvent e) {
        mouseStart = e.getPoint();
        offsetX = e.getX() - map.getDrawX();
        offsetY = e.getY() - map.getDrawY();
        Logf.debug(log,"MousePressed MouseMoving:%s map.drawn:%s", mouseMoving,map.isDrawn());
    }

	@Override
	public void mouseReleased(MouseEvent e) {
    	mouseMoving = false;
    	log.trace("MousrReleased start");
        mouseEnd = e.getPoint();
        int a = Math.abs((int) (mouseEnd.getX() - mouseStart.getX()));
        int b = Math.abs((int) (mouseEnd.getY() - mouseStart.getY()));
        if (Math.sqrt(a * a + b * b) > 3) {
            
        } else {
 
        	System.out.println(e.getPoint());
        	Location l = null;
        	for (AnimatedUnit u : map.getPlayersUnits()) {
        		if (l ==null){
        			if(u.isIntersecting(map.getTile(u.getLocation()), e.getX(), e.getY())){
        				l = u.getLocation();
        			}
        		}else{
        			l = null;
        			break;
        		}
			}
        	
        	if (l != null || false){
        		map.setSelectedTile(l);
        	}else{
	            if (map.findAndSelectTile(e.getX(), e.getY()) == null){
	            	return;
	            }
	            System.out.println(map.getSelectedTile());
        	}
        	
            tileSelected();
        }
        Logf.debug(log,"MouseReleased MouseMoving:%s map.drawn:%s", mouseMoving,map.isDrawn());
    }

    @Override
	public void mouseDragged(MouseEvent e) {
    	if (!mouseMoving){
    		mouseMoving =true;
    		log.debug("mouseDragged ");
	    	map.setDrawn(false);	
    	}
    	
        Point current = e.getPoint();
        map.setDrawLocation(e.getX() - offsetX, e.getY() - offsetY);
//        System.out.print((map.drawn ? "T" : "F") +  (mouseMoving ? ":" : "@"));
    }
	
}