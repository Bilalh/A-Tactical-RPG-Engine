package view.map;
/**
 * 
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import org.apache.log4j.Logger;

import util.Logf;
import view.AnimatedUnit;
import view.Gui;
import view.GuiUnit;
import view.interfaces.IActions;
import view.ui.Dialog;
import view.util.MapActions;
import view.util.MousePoxy;

import common.Location;
import common.LocationInfo;
import common.gui.ResourceManager;
import common.interfaces.ILocation;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;

import config.xml.TileImageData;
import controller.MapController;
import engine.map.Tile;

/**
 * @author bilalh
 */
public class GuiMap implements Observer, IMapRendererParent {

	private static final Logger log = Logger.getLogger(GuiMap.class);
	
	private MapController mapController; 
	
    private IsoTile[][] field;
	private IsomertricMapRenderer mapRenderer;

	private int fieldWidth, fieldHeight;
	private static IsoTile selectedTile;
    
    
    private AnimatedUnit[] units;
    private AnimatedUnit[] aiUnits;
    
    private Dialog dialog;
    private boolean showDialog = false;
        
    // The Class that with handed the input 
    private MapActions current;
    
    private MousePoxy MousePoxy;
    
    private HashMap<UUID,AnimatedUnit> unitMapping;

    // Buffer for drawing the map.    	
    private Image mapBuffer;
    private final int bufferWidth;
	private final int bufferHeight;

    private final int startX, startY;
    private int drawX,drawY;
    
    final private MapActions[] actions = {new Movement(this), new DialogHandler(this), new MapActions(this)};
    enum ActionsEnum {
    	MOVEMENT, DIALOG,NONE
    }

    private final int heightOffset;
    
    private Component parent;
    
	/** @category Constructor */
	public GuiMap(MapController mapController, Component parent) {
		assert actions.length == ActionsEnum.values().length;
		
		this.parent        = parent;
		this.mapController = mapController;
		
		final Tile grid[][] = mapController.getGrid();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
		
        field = new IsoTile[fieldWidth][fieldHeight];
        current = getActionHandler(ActionsEnum.MOVEMENT);
        MousePoxy = new MousePoxy();
        setActionHandler(ActionsEnum.MOVEMENT);
		
        ResourceManager.instance().loadSpriteSheetFromResources(mapController.getTileSheetLocation());
        
        //FIXME heights?
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	TileImageData d = mapController.getTileImageData(i, j);
            	field[i][j] = new IsoTile(IsoTile.Orientation.UP_TO_EAST,
            			grid[i][j].getStartHeight(),
            			grid[i][j].getEndHeight(), i, j,
            			d.getLocation(), d.getType());
            }
        }
        
        //FIXME hack calculate real size of bufffer
//        
//        heightOffset = (MapSettings.tileDiagonal);
//        bufferWidth  =  MapSettings.tileDiagonal*fieldWidth +5;  
//		bufferHeight = (int) (MapSettings.tileDiagonal/2f*fieldHeight +heightOffset);
	
        int max = Math.max(fieldHeight, fieldWidth);
        heightOffset = (MapSettings.tileDiagonal);
        bufferWidth  =  MapSettings.tileDiagonal*max +5;  
		bufferHeight = (int) (MapSettings.tileDiagonal/2f*max +heightOffset);
        
        drawX = 0;
        drawY = bufferHeight/2 - Gui.HEIGHT/2;

        startX = bufferWidth/2;
        startY = heightOffset;
        
        unitMapping = new HashMap<UUID, AnimatedUnit>();
        dialog = new Dialog(665, 70, "mage", ResourceManager.instance().getSpriteFromClassPath("assets/gui/mage.png"));
        
        selectedTile = field[0][0];
        selectedTile.setSelected(true);
        
        mapController.addMapObserver(this);
        mapController.startMap();
        path = new ArrayDeque<LocationInfo>(0);
        pathIterator = path.iterator();
	}
	
	public void makeImageBuffer(Component parent){
		mapBuffer = parent.createImage(bufferWidth,bufferHeight);
		
        final int heightOffset = (MapSettings.tileDiagonal);
		mapRenderer = new IsomertricMapRenderer(
				field, this, 
				startX, startY);
	}

	private boolean drawn = false;
	private int frameDuration = 750 * 1000000;
	private int frameChange = 0;
	private AnimatedUnit over = null;
	
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		Graphics g = mapBuffer.getGraphics();
		
		if (!current.isMouseMoving()) {
			frameChange += timeDiff;
			if (frameChange > frameDuration) {
				frameChange = 0;
				// Animated moving.
				if(pathIterator.hasNext()){
					AnimatedUnit u = getTile(lastLocation).removeUnit();
					lastLocation = pathIterator.next();
					u.setLocation(lastLocation);
					
					if (over != null){
						getTile(lastLocation).setUnit(over);
						over = null;
					}
					over = getTile(lastLocation).getUnit();
					
					getTile(lastLocation).setUnit(u);
					log.trace("Moved to" + getTile(lastLocation));
					
					if (!pathIterator.hasNext()){
						setActionHandler(oldAction);
					}
				}
				setDrawn(false);
			}
		}

		if (!isDrawn()){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			setDrawn(mapRenderer.draw(g, width, height));
//			Logf.trace(log, "%s %s %s %s %s %s %s %s %s %s ",bufferWidth,bufferHeight, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height);
		}
		
		_g.drawImage(mapBuffer, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height, null);

		if (showDialog) dialog.draw((Graphics2D) _g, 5, height - dialog.getHeight() - 5);
	}
	
	Location getDrawLocation(int startX, int startY, int gridX, int gridY){
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
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		HashMap<IUnit, Location> selectedPostions = new HashMap<IUnit, Location>();
		for (int i = 0; i < newUnits.length; i++) {
//			//FIXME indies
				final IUnit u = allPlayerUnits.get(i);
				Location p = new Location(2,i+3); 
				newUnits[i] = new AnimatedUnit(p.x, p.y, new String[]{"assets/gui/Archer.png"});
				selectedPostions.put(u, p);
				unitMapping.put(u.getUuid(), newUnits[i]);
				field[p.x][p.y].setUnit(newUnits[i]);
		}
		mapController.setUsersUnits(selectedPostions);
		this.units = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			final IMapUnit u = aiUnits.get(i);
			newAiUnits[i] = new AnimatedUnit(u.getGridX(), u.getGridY(), 
					new String[]{"assets/gui/alien.gif", "assets/gui/alien2.gif", "assets/gui/alien3.gif"});
			newAiUnits[i].setMapUnit(u);
			field[u.getGridX()][u.getGridY()].setUnit(newAiUnits[i]);
		}
		this.aiUnits = newAiUnits;
	}
	
	public void unitsChoosen(ArrayList<IMapUnit> units){
		for (IMapUnit u : units) {
//			FIXME todo unit choosen 
			field[u.getGridX()][u.getGridY()].getUnit().setMapUnit(u);
		}
	}
	
	/** @category unused **/
	private Collection<LocationInfo> path;
	
	private Iterator<LocationInfo> pathIterator;
	private LocationInfo lastLocation;
	private MapActions oldAction = null;
	
	public void unitMoved(IMapUnit u, Collection<LocationInfo> path){
		AnimatedUnit movingUnit =  unitMapping.get(u.getUuid());
		this.path    = path;
		pathIterator = path.iterator();
		lastLocation = pathIterator.next();
		oldAction = current;
		
		// Disable input when a unit is moving
		if (pathIterator.hasNext()) setActionHandler(ActionsEnum.NONE);
		
		Logf.info(log, "%s moved from %s to %s with path:s%s", u.getName(),  movingUnit.getLocation(), u.getLocation(), path );
		setDrawn(false);
	}
	
	private GuiUnit currentUnit;
	public void unitsTurn(IMapUnit unit) {
		currentUnit = getTile(unit.getLocation()).getUnit();
		assert(currentUnit != null);
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
		
	private class DialogHandler extends MapActions{

		public DialogHandler(GuiMap map) {
			super(map);
		}

		@Override
		public void keyComfirm() {
			nextPage();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			nextPage();
		}
		
		private void nextPage(){
			if (!dialog.nextPage()){
				showDialog = false;
				setActionHandler(ActionsEnum.MOVEMENT);
			}
		}
	}

	public void otherKeys(KeyEvent e){
		switch (e.getKeyCode()) {
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
				if ((MapSettings.zoom * MapSettings.tileDiagonal) % 2 !=0){
//					log.info("Odd");
				}
				
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
		return current;
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
	
	private void setActionHandler(ActionsEnum num){
		final MapActions aa = actions[num.ordinal()];
		setActionHandler(aa);
	}
	
	private void setActionHandler(MapActions aa){
		Logf.info(log, "Action changed to %s", aa);
		current = aa;
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
		return current.isMouseMoving();
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
	AnimatedUnit[] getUnits() {
		return units;
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
	void setUnits(AnimatedUnit[] units) {
		this.units = units;
	}

	boolean isDrawn() {
		return drawn;
	}

	void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

}
