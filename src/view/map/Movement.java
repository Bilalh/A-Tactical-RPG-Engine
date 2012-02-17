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
	
	private Collection<LocationInfo> inRange = null;
    private Point mouseStart, mouseEnd;
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
		selectMoveUnit();
	}
	
	private AnimatedUnit selected = null;
	private void selectMoveUnit() {
		assert(map.getCurrentUnit() != null);
		if (selected != null){
			if (selected != map.getCurrentUnit()){
				Logf.info(log, "Not %s's turn its %s turn", map.getSelectedTile().getUnit(), map.getCurrentUnit() );
				return; 
			}
			log.info("selected " + selected);
			
			if (!map.getSelectedTile().isSelected() ) return;
			
			if (!inRange.contains(map.getSelectedTile().getFieldLocation())){
				Logf.info(log, "%s not in range", map.getSelectedTile());
				return;
			}
			
			map.getMapController().moveUnit(selected.getUnit(), map.getSelectedTile().getFieldLocation());
			for (LocationInfo p : inRange) {
				map.getTile(p).setState(TileState.NONE);
			}
			selected = null;
			inRange = null;
			log.info("Selected unit move finished");
			return;
		}

		AnimatedUnit unitS = null;
		IsoTile t = map.getSelectedTile();
		
		for (AnimatedUnit u : map.getPlayersUnits()) {
			if (u.getGridX() == t.getFieldLocation().x && u.getGridY() == t.getFieldLocation().y){
				unitS = u;
				break;
			}
		}
		
		if (unitS == null){
			for (AnimatedUnit u : map.getAIUnits()) {
				if (u.getGridX() == t.getFieldLocation().x && u.getGridY() == t.getFieldLocation().y){
					unitS = u;
					break;
				}
			}
		}
		
		if(unitS == null) return; 
		
		if (unitS != selected){
			if (inRange != null){
				for (LocationInfo p : inRange) {
					map.getTile(p).setState(TileState.NONE);
				}	
			}
			inRange = null;
			selected = unitS;
		}
		
		inRange =  map.getMapController().getMovementRange(unitS.getUnit());
		for (LocationInfo p : inRange) {
			map.getTile(p).setState(selected == map.getCurrentUnit() ? TileState.MOVEMENT_RANGE : TileState.OTHERS_RANGE);
		}
	}
	
	@Override
	public void keyCancel() {
		selected = null;
		if (inRange != null){
			for (LocationInfo p : inRange) {
				map.getTile(p).setState(TileState.NONE);
			}
			inRange = null;
		}
		
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
        	
        	if (l != null){
        		map.setSelectedTile(l.x, l.y);
        	}else{
	            map.findAndSelectTile(e.getX(), e.getY());
        	}
        	
            selectMoveUnit();
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