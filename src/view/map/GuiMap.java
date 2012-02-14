package view.map;
/**
 * 
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import openal.Music;
import openal.SlickException;

import org.apache.log4j.Logger;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import util.Args;
import util.Logf;
import view.AnimatedUnit;
import view.Gui;
import view.GuiUnit;
import view.interfaces.IActions;
import view.ui.Dialog;
import view.ui.UnitInfoDisplay;
import view.util.MapActions;
import view.util.MousePoxy;

import common.Location;
import common.LocationInfo;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.interfaces.ILocation;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;

import config.xml.TileImageData;
import controller.MapController;
import engine.map.Tile;

import static util.Args.*;

/**
 * The view 
 * @author bilalh
 */
public class GuiMap implements Observer, IMapRendererParent {
	private static final Logger log = Logger.getLogger(GuiMap.class);
		
    private Component parent;
	private MapController mapController; 
	
	private IsomertricMapRenderer mapRenderer;
	private IsoTile[][] field;

	private int fieldWidth, fieldHeight;
	private static IsoTile selectedTile;
    
	// The units
    private AnimatedUnit[] punits;
    private AnimatedUnit[] aiUnits;

    private HashMap<UUID,AnimatedUnit> unitMapping;

    private Music music;
    Dialog dialog;
    boolean showDialog = false;
        
    UnitInfoDisplay infoDisplay = new UnitInfoDisplay();
    
    // The Class that with handed the input 
    private MapActions currentAction;
    private MousePoxy MousePoxy;
    
    // Buffer for drawing the map.    	
    private Image mapBuffer;
    private final int bufferWidth;
	private final int bufferHeight;

    private int drawX,drawY;
    
    final private MapActions[] actions = {new Movement(this), new DialogHandler(this), new MapActions(this)};
    private GuiUnit currentUnit;
	enum ActionsEnum {
    	MOVEMENT, DIALOG,NONE
    }

	// For unit movement
	private Iterator<LocationInfo> pathIterator;
	private LocationInfo lastLocation;
	private MapActions oldAction = null;
	
	// For drawing 
	private boolean drawn = false;
	private int frameDuration = 750 * 1000000;
	private int frameChange = 0;
	// When there are two units on the same tile keep a referance to the unit replaced 
	// so that it does not get lost.
	private AnimatedUnit replaced = null;
	
    /** @category Constructor */
	public GuiMap(MapController mapController, Component parent) {
		assert actions.length == ActionsEnum.values().length;
		
		this.parent        = parent;
		this.mapController = mapController;
		
		final Tile grid[][] = mapController.getGrid();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
		
        this.field = new IsoTile[fieldWidth][fieldHeight];
        this.mapRenderer = new IsomertricMapRenderer(field, this);
        
		BufferSize  s  = mapRenderer.getMapDimensions();
		bufferWidth  = s.width;
		bufferHeight = s.height;
        
        currentAction = getActionHandler(ActionsEnum.MOVEMENT);
        MousePoxy = new MousePoxy();
        setActionHandler(ActionsEnum.MOVEMENT);
		
        ResourceManager.instance().loadSpriteSheetFromResources(mapController.getTileSheetLocation());
        try {
			music = new Music("music/1-19 Fight It Out!.ogg", true);
//			music.loop();
			musicPlaying = false;
		} catch (SlickException e) {
			// FIXME catch block in GuiMap
			e.printStackTrace();
		}
        
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	TileImageData d = mapController.getTileImageData(i, j);
            	field[i][j] = new IsoTile(
            			grid[i][j].getOrientation(),
            			grid[i][j].getStartHeight(),
            			grid[i][j].getEndHeight(), 
            			i, j,
            			d.getLocation(), d.getType());
            }
        }


        unitMapping = new HashMap<UUID, AnimatedUnit>();
        dialog = new Dialog(665, 70, "mage", ResourceManager.instance().getSpriteFromClassPath("assets/gui/mage.png"));
        
        selectedTile = field[0][0];
        selectedTile.setSelected(true);
        
        mapController.addMapObserver(this);
        mapController.startMap();
        
        pathIterator = new ArrayDeque<LocationInfo>(0).iterator();
	}
	
	public void makeImageBuffer(){
		mapBuffer = parent.createImage(bufferWidth,bufferHeight);
	}
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		Music.poll((int) (timeDiff/1000000));
		Graphics g = mapBuffer.getGraphics();
		
		// Handled the animated movement
		if (!currentAction.isMouseMoving()) {
			frameChange += timeDiff;
			if (frameChange > frameDuration) {
				frameChange = 0;
				// Animated moving.
				if(pathIterator.hasNext()){
					AnimatedUnit u = getTile(lastLocation).removeUnit();

					if (replaced != null){
						getTile(lastLocation).setUnit(replaced);
						log.trace("over set "+ replaced);
						replaced = null;
					}
					lastLocation = pathIterator.next();
					replaced = getTile(lastLocation).getUnit();
					log.trace("over "+ replaced);
					
					u.setLocation(lastLocation);
					u.setDirection(lastLocation.getDirection());
					getTile(lastLocation).setUnit(u);
					
					log.debug("Moved to" + getTile(lastLocation));
					
					if (!pathIterator.hasNext()){
						setActionHandler(oldAction);
						mapController.finishedMoving(u.getUnit());
					}
				}
				setDrawn(false);
			}
		}

		if (!isDrawn()){
			g.setColor(Color.BLUE.brighter());
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			setDrawn(mapRenderer.draw(g, width, height));
//			Logf.trace(log, "%s %s %s %s %s %s %s %s %s %s ",bufferWidth,bufferHeight, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height);
		}
		
		_g.drawImage(mapBuffer, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height, null);

		if (selectedTile.getUnit() != null){
			infoDisplay.draw((Graphics2D) _g, width-100, 100, selectedTile.getUnit().getUnit());
		}
		if (showDialog) dialog.draw((Graphics2D) _g, 5, height - dialog.getHeight() - 5);
	}
	
	private Location getDrawLocation(int startX, int startY, int gridX, int gridY){
		int x = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - gridY-1f));
		int y = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - gridY-1));
		x += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * gridX;
		y += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom)* gridX;
		return new Location(x,y);
	}

	
	@Override
	public void update(Observable map, Object notification) {
		Gui.console().println(notification);
		((IMapNotification) notification).process(this);
	}
	
	public void chooseUnits(ArrayList<? extends IUnit> allPlayerUnits, ArrayList<? extends IMapUnit> aiUnits) {
		assetNonNull(allPlayerUnits,aiUnits);
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		HashMap<IUnit, Location> selectedPostions = new HashMap<IUnit, Location>();
		for (int i = 0; i < newUnits.length; i++) {
//			//FIXME indies
				final IUnit u = allPlayerUnits.get(i);
				assert u != null;
				Location p = new Location(2,i+2); 
				newUnits[i] = new AnimatedUnit(p.x, p.y,u);
				selectedPostions.put(u, p);
				unitMapping.put(u.getUuid(), newUnits[i]);
				field[p.x][p.y].setUnit(newUnits[i]);
		}
		this.punits = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			final IMapUnit u = aiUnits.get(i);
			newAiUnits[i] = new AnimatedUnit(u.getGridX(), u.getGridY(),u);
			newAiUnits[i].setMapUnit(u);
			field[u.getGridX()][u.getGridY()].setUnit(newAiUnits[i]);
			unitMapping.put(u.getUuid(), newUnits[i]);
		}
		this.aiUnits = newAiUnits;
		
		mapController.setUsersUnits(selectedPostions);
	}
	
	public void unitsChoosen(ArrayList<IMapUnit> units){
		for (IMapUnit u : units) {
//			FIXME todo unit choosen 
			field[u.getGridX()][u.getGridY()].getUnit().setMapUnit(u);
		}
	}
	
	// To allow the user to see the ai's move 
	Timer timer = new Timer();
	public void unitMoved(final IMapUnit u, Collection<LocationInfo> path){
		assert u != null;
		assert path != null;
		Logf.info(log, "Unit: %s",u);
		AnimatedUnit movingUnit =  unitMapping.get(u.getUuid());
		assert movingUnit != null;
		
		pathIterator = path.iterator();
		lastLocation = pathIterator.next();
		oldAction = currentAction;
		
		// Disable input when a unit is moving
		if (pathIterator.hasNext()) {
			setActionHandler(ActionsEnum.NONE);
		}else{
			// Allows the player to see the ai moves
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					mapController.finishedMoving(u);
				}
			}, 1000);
		}
		
		
		Logf.info(log, "%s moved from %s to %s with path:s%s", u.getName(),  movingUnit.getLocation(), u.getLocation(), path );
		setDrawn(false);
	}
	
	public void unitsTurn(IMapUnit unit) {
		currentUnit = getTile(unit.getLocation()).getUnit();
		log.debug(unit);
		log.debug(getTile(unit.getLocation()));
		assert(currentUnit != null);
		setSelectedTile(currentUnit.getGridX(), currentUnit.getGridY());
	}
	
	/** @category unused **/
	public void playersTurn(){
		displayMessage("Player's Turn");
	}
	
	private void displayMessage(String text){
		dialog.setPicture(null);
		dialog.setName(null);
		dialog.setText(text);
		showDialog = true;
		setActionHandler(ActionsEnum.DIALOG);
	}
		
	
	/**
     * Select the file that is under the mouse click
	 */
    public ILocation findAndSelectTile(int x, int y) {
        double highest = 0.0;
        x += drawX;
        y += drawY;
        int xIndex = -1, yIndex = -1;
        
//        Logf.info(log,"p:(%d,%d)\n", x, y);
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                if (field[i][j].contains(new Point(x, y))) {
//                	Logf.info(log,"Clicked(%d,%d)\n", i, j);
                	
                	if (field[i][j].getHeight() > highest){
//                		log.info("\t highest");
                        highest = field[i][j].getHeight();
                        xIndex = i;
                        yIndex = j;	
                	}
                    
                }
            }
        }
        if (xIndex > -1 && yIndex > -1) {
            this.setSelectedTile(xIndex, yIndex);
//            Logf.info(log,"(%d,%d)\n\n", xIndex, yIndex);
            return new Location(xIndex, yIndex);
        } else {
//        	Logf.info(log,"(%d,%d)\n\n", xIndex, yIndex);
            return null;
        }
    }
    
	public IsoTile getSelectedTile() {
		return selectedTile;
	}

	public void moveSelectedTile(int dx, int dy) {
		setSelectedTile(selectedTile.getX()+dx, selectedTile.getY()+dy);
	}
	
	public void setSelectedTile(int x, int y) {
        if (x < 0  || y < 0  || x >= fieldWidth || y >= fieldHeight) return; 
        
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }
        
        selectedTile = field[x][y];
        selectedTile.setSelected(true);
    }

	public IActions getActionHandler() {
		return currentAction;
	}
	
	public MouseListener getMouseListener(){
		return MousePoxy;
	}
	
	public MouseMotionListener getMouseMotionListener(){
		return MousePoxy;
	}
	
	private MapActions getActionHandler(ActionsEnum num){
		return actions[num.ordinal()];
	}
	
	void setActionHandler(ActionsEnum num){
		final MapActions aa = actions[num.ordinal()];
		setActionHandler(aa);
	}
	
	private void setActionHandler(MapActions aa){
		Logf.info(log, "Action changed to %s", aa);
		currentAction = aa;
		MousePoxy.setMouseListener(aa);
		MousePoxy.setMouseMotionListener(aa);
	}
	
    public void setDrawLocation(int x, int y) {
    	if (x >= 0){
        	if (x + parent.getWidth()  <= bufferWidth){
        		drawX = x;
        	}else{
        		drawX = bufferWidth - parent.getWidth();
        	}
    	}else{
    		drawX = 0;
    	}    	
    	
    	if (y >= 0){
        	if (y + parent.getHeight()  <= bufferHeight){
        		drawY = y;
        	}
        	else{
        		drawY = bufferHeight - parent.getHeight();
        	}
    	}else{
    		drawY = 0;
    	}    
    	
    }

    public IsoTile getTile(ILocation l){
    	return field[l.getX()][l.getY()];
    }
    
    boolean musicPlaying = true;
    public void otherKeys(KeyEvent e){
		switch (e.getKeyCode()) {
			case KeyEvent.VK_L:
				mapController.mapFinished();
				break;
			case KeyEvent.VK_1:
				mapRenderer.toggleNumbering();
				break;
			case KeyEvent.VK_M:
				if (musicPlaying){
					music.stop();
				}else{
					music.loop();
				}
				musicPlaying = !musicPlaying;
				break;
			case KeyEvent.VK_T:
				setActionHandler(ActionsEnum.DIALOG);
				dialog.setPicture(ResourceManager.instance().getSpriteFromClassPath("assets/gui/mage.png"));
				dialog.setName("Mage");
				dialog.setText(
						"Many people believe that Vincent van Gogh painted his best works " +
						"during the two-year period he spent in Provence. Here is where he " +
						"painted The Starry Night--which some consider to be his greatest " +
						"work of all. However, as his artistic brilliance reached new " +
						"heights in Provence, his ysical and mental health plummeted. ");
				showDialog = true;
				break;
			case KeyEvent.VK_MINUS:
				if ( MapSettings.zoom <=0.6) break;
				MapSettings.zoom -= 0.2;
//				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				log.info(MapSettings.zoom * MapSettings.tileDiagonal);
//				if ((MapSettings.zoom * MapSettings.tileDiagonal) % 2 !=0){
//					log.info("Odd");
//				}
				
				log.info(MapSettings.zoom);
				setDrawn(false);
				break;
			case KeyEvent.VK_EQUALS:
				if ( MapSettings.zoom >1.2) break;
				MapSettings.zoom += 0.2;
//				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				log.info(MapSettings.zoom * MapSettings.tileDiagonal);
				log.info(MapSettings.zoom);
				setDrawn(false);
				break;
			case KeyEvent.VK_COMMA:
				if ( MapSettings.pitch <0.6) break;
				MapSettings.pitch *= 0.8;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				log.info(MapSettings.pitch);
				setDrawn(false);
				break;
			case KeyEvent.VK_PERIOD:
				if ( MapSettings.pitch >0.8) break;
				MapSettings.pitch *= 1.2;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				log.info(MapSettings.pitch);
				setDrawn(false);
				break;
			case KeyEvent.VK_I:
				Logf.info(log,"draw (%d,%d) selected %s unit:%s", drawX, drawY, selectedTile, selectedTile.getUnit());
				break;
		}
		
	}
    /** @category Generated */
	public boolean isShowDialog() {
		return showDialog;
	}

	/** @category Generated */
	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

	/** @category Generated Getter */
	@Override
	public boolean isMouseMoving() {
		return currentAction.isMouseMoving();
	}

	/** @category Generated Getter */
	@Override
	public int getDrawX() {
		return drawX;
	}

	/** @category Generated Getter */
	@Override
	public int getDrawY() {
		return drawY;
	}
	
	/** @category Generated */
	AnimatedUnit[] getPlayersUnits() {
		return punits;
	}
	
	/** @category Generated */
	AnimatedUnit[] getAIUnits() {
		return aiUnits;
	}
	
	/** @category Generated */
	GuiUnit getCurrentUnit() {
		return currentUnit;
	}

	/** @category Generated */
	MapController getMapController() {
		return mapController;
	}


	/** @category Generated */
	boolean isDrawn() {
		return drawn;
	}

	/** @category Generated */
	void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

}
