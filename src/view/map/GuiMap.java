package view.map;

import static util.Args.assetNonNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.*;

import javax.swing.text.DefaultEditorKit.CutAction;


import org.apache.log4j.Logger;

import com.sun.tools.example.debug.gui.GUI;


import util.Logf;
import util.openal.Music;
import util.openal.SlickException;
import util.openal.Sound;
import view.Gui;
import view.map.IsoTile.TileState;
import view.map.interfaces.IActions;
import view.map.interfaces.IMapRendererParent;
import view.ui.GuiDialog;
import view.ui.Menu;
import view.ui.UnitInfoDisplay;
import view.ui.interfaces.IMenuItem;
import view.units.AnimatedUnit;
import view.units.GuiUnit;
import view.util.BufferSize;
import view.util.MapActions;
import view.util.MousePoxy;

import common.Location;
import common.LocationInfo;
import common.enums.Direction;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.interfaces.*;

import config.Config;
import config.assets.DialogParts;
import config.xml.MapConditions;
import config.xml.MapEvents;
import config.xml.MapSettings;
import config.xml.TileImageData;
import controller.MapController;
import engine.map.Tile;

/**
 * The view
 * 
 * @author Bilal Hussain
 */
public class GuiMap implements Observer, IMapRendererParent {
	private static final Logger log = Logger.getLogger(GuiMap.class);

	private Component parent;
	private MapController mapController;

	// The map, it renderer and its data
	private IsomertricMapRenderer mapRenderer;
	private IsoTile[][] field;

	private config.xml.MapSettings mapSettings;
	private int fieldWidth, fieldHeight;
	private static IsoTile selectedTile;

	// The units
	private ArrayList<AnimatedUnit> plunits;
	private ArrayList<AnimatedUnit> aiUnits;
	
	private HashMap<UUID, AnimatedUnit> unitMapping;
	private HashMap<UUID, AnimatedUnit> died;
	
	// UI elements
	private Menu menu        = new Menu();
	private GuiDialog dialog = new GuiDialog(0, 0);
	
	// input handlers
	private final MenuInput menuInput            = new MenuInput(this, menu);
	private final DialogHandler dialogHandler    = new DialogHandler(this, dialog);
	private final UnitInfoDisplay infoDisplay    = new UnitInfoDisplay();
	private final MapFinishedHandler mapFinished = new MapFinishedHandler(this);
	private final MapStartHandler mapStart       = new MapStartHandler(this);

	private final SkillMovement skillMovement    = new SkillMovement(this);
	
	// The classes that with handed the input
	private MousePoxy  MousePoxy;
	private MapActions currentAction;
	
	// Handles the input fpr each state
	final private MapActions[] actions = {
			new Movement(this), dialogHandler, new MapActions(this), 
			menuInput, mapFinished, skillMovement, mapStart};

	static enum ActionsEnum {
		MOVEMENT, DIALOG, NONE, MENU, FINISHED, SKILL_MOVEMENT, START
	}

	// The state of the current units state machine.
	private UnitState state = UnitState.WAITING;

	// For unit movement
	private AnimatedUnit currentUnit;
	private Iterator<LocationInfo> pathIterator;
	private LocationInfo lastLocation;
	private MapActions oldAction;
	private UnitState nextState;

	// Buffer for drawing the map.
	private Image mapBuffer;
	private final int bufferWidth;
	private final int bufferHeight;

	// When to draw from
	private int drawX, drawY;

	// For drawing
	private boolean drawn = false;
	private int frameDuration = 500 * 1000000;
	private int frameChange = 0;
	// When there are two units on the same tile keep a referance to the 
	// unit replaced so that it does not get lost.
	private AnimatedUnit replaced;

	// Used to delay events
	Timer timer = new Timer();
	boolean justStarted = true;
	
	public GuiMap(MapController mapController, Component parent) {
		assert actions.length == ActionsEnum.values().length;

		this.parent = parent;
		this.mapController = mapController;

		final Tile grid[][] = mapController.getField();
		this.fieldWidth  = grid.length;
		this.fieldHeight = grid[0].length;
		this.mapSettings = mapController.getMapSettings();
		
		this.field = new IsoTile[fieldWidth][fieldHeight];
		this.unitMapping  = new HashMap<UUID, AnimatedUnit>();
		this.died         = new HashMap<UUID, AnimatedUnit>();
		this.pathIterator = new ArrayDeque<LocationInfo>(0).iterator();
		
		float multiplier = 1.3f;
		this.mapRenderer  = new IsomertricMapRenderer(this.field, this, multiplier, mapSettings);
		
		BufferSize s = mapRenderer.getMapDimensions();
		bufferWidth  = (int) (s.width  * multiplier);
		bufferHeight = (int) (s.height * multiplier) + mapSettings.tileDiagonal;

		drawX = (int) (Math.max(multiplier-1, 0) * s.width)/2;
//		drawY = (int) (Math.max(multiplier-1, 0) * s.height)/2;
		
		currentAction = getActionHandler(ActionsEnum.MOVEMENT);
		MousePoxy     = new MousePoxy();
		setActionHandler(ActionsEnum.MOVEMENT);

		dialog.setWidth(parent.getWidth());
		dialog.setHeight(75);
		// Set up state machine
		UnitState.setMap(this);

		// Load the tiles and items images
		ResourceManager.instance().loadTileSheetFromResources(mapController.getTileSheetLocation());
		ResourceManager.instance().loadTextureSheetFromResources(mapController.getTexturesLocation());
		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");

		boolean outlines = mapController.hasOutlines();
		
		// create tiles
		for (int i = 0; i < fieldWidth; i++) {
			for (int j = 0; j < fieldHeight; j++) {
				TileImageData d = mapController.getTileImageData(i, j);
				field[i][j] = new IsoTile(
						grid[i][j].getOrientation(),
						grid[i][j].getStartingHeight(),
						grid[i][j].getEndHeight(),
						i, j,
						d.getLocation(), 
						d.getType(), 
						mapSettings,
						grid[i][j].getLeftWallName(),
						grid[i][j].getRightWallName());
				if (outlines){
					field[i][j].setOutline(true);
				}
			}
		}

		selectedTile = field[0][0];
		selectedTile.setSelected(true);
		
		mapController.addMapObserver(this);
		mapController.startMap();

		// Start music
		try {
			Gui.getMusicThread().replaceMusic(new Music(mapController.getMusic().getBackground()));
			Gui.getMusicThread().pause();
			Gui.getMusicThread().toggleMusic();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Makes the buffer to draw the map
	 */
	public void makeImageBuffer() {
		mapBuffer = parent.createImage(bufferWidth, bufferHeight);
	}

	/**
	 * renderers the map
	 */
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		Graphics g = mapBuffer.getGraphics();

		if (!currentAction.isMouseMoving()) {
			frameChange += timeDiff;
			if (frameChange > frameDuration) {
				frameChange = 0;
				animatedMovement();
				setDrawn(false);
			}
		}

		if (!isDrawn()) {
			g.setColor(Color.BLUE.darker());
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			setDrawn(mapRenderer.draw(g, width, height));
		}

		_g.drawImage(mapBuffer, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height, null);

		if (selectedTile.getUnit() != null) {
			infoDisplay.setUnit(selectedTile.getUnit());
			infoDisplay.draw((Graphics2D) _g, width - 100, 100);
		}

		currentAction.draw((Graphics2D) _g, width, height);
	}

	// Animated any movement left in the current path.
	private void animatedMovement() {
		if (pathIterator.hasNext()) {
			AnimatedUnit u = getTile(lastLocation).removeUnit();

			if (replaced != null) {
				getTile(lastLocation).setUnit(replaced);
				log.info("over set " + replaced);
				replaced = null;
			}
			lastLocation = pathIterator.next();
			replaced = getTile(lastLocation).getUnit();
			log.trace("over " + replaced);

			u.setLocation(lastLocation);
			u.setDirection(mapRenderer.traslateDirection(lastLocation.getDirection()));
			getTile(lastLocation).setUnit(u);

			log.trace("Moved to" + getTile(lastLocation));

			if (!pathIterator.hasNext()) {
				Logf.debug(log, "Finished moving au:%s", u);
				setActionHandler(oldAction);
				finishedMoving(u);
			}
		}
	}

	private Location getDrawLocation(ILocation l) {
		int x = mapRenderer.getStartX() - (int) (mapSettings.tileDiagonal / 2f * mapSettings.zoom * (fieldHeight - l.getY() - 1f));
		int y = mapRenderer.getStartX() + (int) (mapSettings.tileDiagonal / 2f * mapSettings.pitch * mapSettings.zoom * (fieldHeight - l.getY() - 1));
		x += (int) (mapSettings.tileDiagonal / 2f * mapSettings.zoom) * l.getX();
		y += (int) (mapSettings.tileDiagonal / 2f * mapSettings.pitch * mapSettings.zoom) * l.getX();
		return new Location(x, y);
	}

	public void setDrawLocation(int x, int y) {
		if (x >= 0) {
			if (x + parent.getWidth() <= bufferWidth) {
				drawX = x;
			} else {
				drawX = bufferWidth - parent.getWidth();
			}
		} else {
			drawX = 0;
		}

		if (y >= 0) {
			if (y + parent.getHeight() <= bufferHeight) {
				drawY = y;
			}
			else {
				drawY = bufferHeight - parent.getHeight();
			}
		} else {
			drawY = 0;
		}

	}

	@Override
	public void update(Observable map, Object notification) {
		log.info(notification);
		IMapNotification n = (IMapNotification) notification;
		String s = n.readableInfo();
		if (s != null) Gui.console().println(s);
		n.process(this);
	}

	String winCondition = "";
	/**
	 * Places the units on the map
	 */
	public void chooseUnits(ArrayList<? extends IUnit> allPlayerUnits, ArrayList<? extends IMapUnit> allAiUnits) {
		assetNonNull(allPlayerUnits, allAiUnits);

		plunits = new ArrayList<AnimatedUnit>();
		aiUnits = new ArrayList<AnimatedUnit>();
		HashMap<IUnit, Location> selectedPostions = new HashMap<IUnit, Location>();

		
		ArrayList<IUnit> all =new ArrayList<IUnit>();
		all.addAll(allAiUnits);
		all.addAll(allPlayerUnits);
		winCondition = mapController.getConditions().getWinCondition().info(all);
		Gui.console().printf("Win Condition: %s", winCondition);
		
		for (IMapUnit u : allAiUnits) {
			assert u != null;
			
			AnimatedUnit au = new AnimatedUnit(u.getGridX(), u.getGridY(), u);
			au.setMapUnit(u);
			aiUnits.add(au);
			
			field[u.getGridX()][u.getGridY()].setUnit(au);
			unitMapping.put(u.getUuid(), au);
		}

		MapConditions con = mapController.getConditions();
		ArrayList<Location> vaildLocations = con.getVaildStartLocations();
		
		if (vaildLocations == null || vaildLocations.isEmpty()){
			Location start = con.getDefaultStartLocation();
			if (start == null){
				start = new Location(0,0);
			}else{
				start.abs().limitUpper(fieldWidth, fieldHeight);
			}
			setDefaultLocations(start, allPlayerUnits, selectedPostions);
		}else{
			Iterator<? extends IUnit> it = allPlayerUnits.iterator();
			for (Location l : vaildLocations) {
				IUnit u = it.next();
				if (u == null) break;
				placeUnit(selectedPostions, u, l);
			}
		}

		mapController.setUsersUnits(selectedPostions);
	}

	/**
	 * Finds vaild locations for the player's units and place the units on these locations.
	 */
	private void setDefaultLocations(Location start, ArrayList<? extends IUnit> allPlayerUnits, HashMap<IUnit, Location> selectedPostions) {
		start.translate(0, -1);
		for (IUnit u : allPlayerUnits) {
			assert u != null;
			do {
				start.translate(0, 1);
				if (start.y >= fieldHeight) start.translate(1,0).y = 0;
				if (start.x >= fieldWidth) return;
			} while (!isVaildTile(start));

			placeUnit(selectedPostions, u, start);
		}
	}
	
	private boolean isVaildTile(ILocation start){
		IsoTile t = getTile(start);
		return (t.getUnit() == null && t.getOrientation() != Orientation.EMPTY);
	}
	
	private void placeUnit(HashMap<IUnit, Location> selectedPostions, IUnit u, Location p){
		p = p.copy();
		AnimatedUnit au = new AnimatedUnit(p.x, p.y, u);
		plunits.add(au);
		selectedPostions.put(u, p);
		unitMapping.put(u.getUuid(), au);
		field[p.x][p.y].setUnit(au);
	}
	
	/**
	 * Set the chosen units
	 */
	public void unitsChoosen(ArrayList<IMapUnit> units) {
		for (IMapUnit u : units) {
			field[u.getGridX()][u.getGridY()].getUnit().setMapUnit(u);
		}
	}

	/**
	 * When its a unit's turn
	 */
	public void unitsTurn(IMapUnit unit) {
		currentUnit = getTile(unit.getLocation()).getUnit();
		assert currentUnit != null;
		infoDisplay.setCurrentUnit(currentUnit);

		changeState(UnitState.WAITING);
		if (unit.isAI()) setActionHandler(ActionsEnum.NONE);
		
		setSelectedTile(currentUnit.getLocation());
	}

	/**
	 * Start the movement animation
	 */
	public void unitMoved(final IMapUnit u, Collection<LocationInfo> path) {
		assert u    != null;
		assert path != null;
		final AnimatedUnit movingUnit = unitMapping.get(u.getUuid());
		assert movingUnit != null;
		assert movingUnit.getUnit().getUuid() == u.getUuid();
		
		pathIterator = path.iterator();
		lastLocation = pathIterator.next();
		oldAction = currentAction;

		// Disable input when a unit is moving
		if (pathIterator.hasNext()) {
			setActionHandler(ActionsEnum.NONE);
		} else {
			// Allows the player to see the ai moves
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					finishedMoving(movingUnit);
				}
			}, 1500);
		}

		log.debug(u);
		log.debug(movingUnit);
		Logf.info(log, "%s moved from %s to %s with path:%s", u.getName(), movingUnit.getLocation(), u.getLocation(), path);
		setDrawn(false);
	}

	// Tells the controller that the unit has finished moving
	private void finishedMoving(AnimatedUnit u) {
		if (nextState == null) nextState = UnitState.WAITING;
		changeState(nextState);
		nextState = null;
		mapController.finishedMoving(u.getUnit());
	}


	/**
	 * Shows a battle between the specifed units 
	 */
	public void unitsBattle(final IBattleInfo battleInfo) {
		log.info(battleInfo);
		
		for (IBattleResult battle : battleInfo.getResults()) {
			AnimatedUnit atarget = unitMapping.get(battle.getTarget().getUuid());
			atarget.setDamage(battle.getDamage());
			setSelectedTile(battle.getTarget().getLocation());
		}
		
		// End unit's turn
		timer.schedule(new TimerTask() {
			@Override
			public  void run() {
				int playLose = 0, playGain = 0;
				boolean diedd = false;
				
				for (IBattleResult battle : battleInfo.getResults()) {
					AnimatedUnit au =  unitMapping.get(battle.getTarget().getUuid());
					au.removeDamage();
					
					if (battle.isTargetDead()){
						diedd = true;
						Logf.info(log, "Died:%s", battle.getTarget());
						AnimatedUnit aau =  unitMapping.remove(battle.getTarget().getUuid());
						died.put(aau.getUnit().getUuid(), aau);
						
						getTile(au.getLocation()).removeUnit();
						if (battle.getTarget().isAI()){
							aiUnits.remove(au);
							playGain++;
						}else{
							plunits.remove(au);
							playLose++;
						}
					}
				}

				if (battleInfo.hasLeveledUp()){
					IMapUnit m =  battleInfo.getAttacker();
					Gui.console().printf("%s's %s has leveled up!", m.isAI() ? "Ai" : "Player", m.getName() );
					Sound s = Gui.getMusicThread().getSound(mapController.getMusic().getLevelUpSound());
					Gui.getMusicThread().playSound(s);
					// Make sure the sound has finished playing
					while (s.playing()){
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
				}
				
				int lose =  playLose - playGain;
				if (playLose != 0 || playGain != 0){
					if (lose >= 0){
						Gui.getMusicThread().playSound(mapController.getMusic().getLoseUnitSound());
					}else{
						Gui.getMusicThread().playSound(mapController.getMusic().getDefeatUnitSound());
					}
				}
				
				if (diedd){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}	
				}
				changeState(UnitState.FINISHED);
			}
		}, 1500);
		
		setDrawn(false);
		Gui.getMusicThread().playSound(mapController.getMusic().getAttackSound());
	}

	public void playerWon() {
		Gui.getMusicThread().removeMusic();
		Gui.getMusicThread().playSound(mapController.getMusic().getWinMapSound());
		mapFinished.setup(true);
		setActionHandler(ActionsEnum.FINISHED);
	}

	public void playerLost() {
		Gui.getMusicThread().removeMusic();
		Gui.getMusicThread().playSound(mapController.getMusic().getLoseUnitSound());
		mapFinished.setup(false);
		setActionHandler(ActionsEnum.FINISHED);
	}
	

	public void displayDialog(DialogParts dialog) {
		Gui.hideConsole();
		dialogHandler.setDialog(dialog.sortedValues());
	}
	
	void dialogFinished(ActionsEnum nextAction){
		setActionHandler(nextAction);
		if (justStarted){
			mapStart.setup(winCondition);
			setActionHandler(ActionsEnum.START);
			justStarted = false;
		}else{
			mapController.dialogFinished();
		}
	}
	
	
	public void showMovementRange(){
		final AnimatedUnit u = getSelectedTile().getUnit();
		if (u == null) return;
		if (othersRange == null) {
			 othersRange = highlightRange(u, TileState.OTHERS_RANGE);
		}
	}
	
	// Shows the movement range/attack range of other units.
	Collection<? extends ILocation> othersRange;
	void  tileSelected() {
		IsoTile selected = getSelectedTile();
		final AnimatedUnit u = getSelectedTile().getUnit();
		
		if (othersRange != null){
			if (currentUnit != u) return;
			waitingCancel();
		}
		
		if (u != currentUnit && state == UnitState.WAITING){
			showMovementRange();
			return;
		}
		
		switch (state) {
			case WAITING:
				Logf.info(log, "exec: %s", state);
				changeState(state.exec());
				break;
			case MOVEMENT_RANGE:
				Logf.info(log, "exec: %s", state);
				nextState = state.exec();
				break;

			case SHOW_ATTACK_TARGETS_QUICK:
			case SHOW_ATTACK_TARGETS:
			case SHOW_SKILL_TARGETS:
				Logf.info(log, "exec: %s", state);
				UnitState s = state.exec();
				if (s != null) changeState(s);
				break;
			default:
				assert false :"Not done yet" + state;
		}

	}

	// Removes any highlight ranges
	void waitingCancel() {
		if (othersRange != null) {
			removeRange(othersRange);
			othersRange = null;
		}
	}

	void showAttackRange(){
		AnimatedUnit u;
		if (othersRange == null && (u=selectedTile.getUnit()) != null) {
			othersRange = u.getAttackRange(fieldWidth, fieldHeight);
			for (ILocation p : othersRange) {
				getTile(p).setState(TileState.ATTACK_RANGE);
			}
			
		}
	}
	
	Collection<LocationInfo> highlightRange(GuiUnit u, TileState tileState) {
		Collection<LocationInfo> inRange = mapController.getMovementRange(u.getUnit());
		for (LocationInfo p : inRange) {
			getTile(p).setState(u == currentUnit ? TileState.MOVEMENT_RANGE : tileState);
		}
		return inRange;
	}

	void removeRange(Iterable<? extends ILocation> range) {
		for (ILocation l : range) {
			getTile(l).setState(TileState.NONE);
		}
	}

	/** @category unused **/
	public void playersTurn() {
		displayMessage("Player's Turn");
	}

	void changeState(UnitState newState) {
		assert newState != null;
        
//		Logf.info(log, "State %s -> %s from %s", state, newState, Logf.getCallers(3));
		Logf.info(log, "State %s -> %s", state, newState);
		state = newState;
		Logf.info(log, "stateEntered: %s", state);
		state.stateEntered();
	}

	private void displayMessage(String text) {
		dialog.setPicture(null);
		dialog.setName(null);
		dialog.setText(text);
		setActionHandler(ActionsEnum.DIALOG);
	}

	public void rotateMap() {
		mapRenderer.rotateMap();

		// Fixes the way the units faces.
		for (AnimatedUnit u : plunits) {
			translateDirectionOnRotation(u,mapRenderer);
		}

		for (AnimatedUnit u : aiUnits) {
			translateDirectionOnRotation(u,mapRenderer);
		}

		for (IsoTile[] ts : field) {
			for (IsoTile t : ts) {
				translateOrientationOnRotation(t,mapRenderer);
			}
		}
		afterMapSettingsChange();
	}


	public static void translateOrientationOnRotation(IsoTile u, IsomertricMapRenderer renderer) {
		Rotation r = renderer.getRotation();
		Orientation o = u.getOrientation();
		switch (r) {
			case NORTH:
				if (o == Orientation.TO_NORTH || o == Orientation.TO_SOUTH){
					u.inverseOrientation();
				}
				break;
			case EAST:
				if (o == Orientation.TO_EAST || o == Orientation.TO_WEST){
					u.inverseOrientation();
				}
				break;
			case SOUTH:
				if (o == Orientation.TO_NORTH || o == Orientation.TO_SOUTH){
					u.inverseOrientation();
				}
				break;
			case WEST:
				if (o == Orientation.TO_EAST || o == Orientation.TO_WEST){
					u.inverseOrientation();
				}		
				break;
		}
	}
	
	private Direction translateDirectionOnRotation(Direction d) {
		Rotation r = mapRenderer.getRotation();
		switch (d) {
			case EAST:
				if (r == Rotation.EAST) return d.inverse();
			case NORTH:
				if (r == Rotation.SOUTH) return d.inverse();
			case SOUTH:
				if (r == Rotation.NORTH) return d.inverse();
			case WEST:
				if (r == Rotation.WEST) return d.inverse();
			default:
				return d;
		}
	}

	public static void translateDirectionOnRotation(AnimatedUnit u, IsomertricMapRenderer renderer) {
		Rotation r = renderer.getRotation();
		switch (u.getDirection()) {
			case EAST:
				if (r == Rotation.EAST) {
					u.inverseDirection();
				}
				break;
			case NORTH:
				if (r == Rotation.SOUTH) {
					u.inverseDirection();
				}
				break;
			case SOUTH:
				if (r == Rotation.NORTH) {
					u.inverseDirection();
				}
				break;
			case WEST:
				if (r == Rotation.WEST) {
					u.inverseDirection();
				}
				break;
		}
	}

	/**
	 * Select the file that is under the mouse click
	 */
	public ILocation findAndSelectTile(int x, int y) {
		float highest = 0.0f;
		x += drawX;
		y += drawY;
		Point p = new Point(x, y);
//		Logf.info(log, "p:(%d,%d)\n", x, y);
		int xIndex = -1, yIndex = -1;
		
		for (int i = 0; i < fieldWidth; i++) {
			for (int j = 0; j < fieldHeight; j++) {
				if (field[i][j].getOrientation() != Orientation.EMPTY && field[i][j].contains(p)) {
//					Logf.info(log, "Clicked(%d,%d)\n", i, j);
					if (field[i][j].getHeight() > highest) {
//						log.info("\t highest");
						highest = field[i][j].getHeight();
						xIndex = i;
						yIndex = j;
					}

				}
			}
		}
		
		if (xIndex > -1 && yIndex > -1) {
			Location l = new Location(xIndex, yIndex);
			setSelectedTile(l);
			return l;
		} else {
			return null;
		}
	}

	public void moveSelectedTile(Direction d) {
		d = mapRenderer.traslateDirection(d);

		Location l = selectedTile.fieldLocation.copy();
		do {
			l.translate(d.x, d.y);
			if (l.x < 0 || l.y < 0 || l.x >= fieldWidth || l.y >= fieldHeight) return;
		} while (getTile(l).getOrientation() == Orientation.EMPTY);

		setSelectedTile(l);
	}

	public IsoTile getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(ILocation l) {
		if (l.getX() < 0 || l.getY() < 0 || l.getX() >= fieldWidth || l.getY() >= fieldHeight) {
			assert false : "should not happen";
			return;
		}

		if (selectedTile != null) {
			selectedTile.setSelected(false);
		}

		selectedTile = field[l.getX()][l.getY()];
		selectedTile.setSelected(true);
		scrollToLocation(l);
	}

	// Scroll the map so that the location on the map is visible.
	void scrollToLocation(ILocation l){
			if (mapSettings.tileDiagonal * field.length < parent.getWidth()  && mapSettings.tileDiagonal/1.5f * field[0].length < parent.getHeight() ){
				return;
			}
		
			Location drawLocation  = getDrawLocation(l);
			//FIXME why add MapSettings.tileDiagonal/2?
			int diffY = drawY + parent.getHeight() -  mapSettings.tileDiagonal/2;
			drawLocation.translate(-drawX, -diffY);

//			System.out.printf("(%d,%d)\n", parent.getWidth(), parent.getHeight());
//			System.out.println(drawLocation);
			boolean check = true;


			Location topLeft = drawLocation.copy().translate(-mapSettings.tileDiagonal, (int) (-mapSettings.tileDiagonal*1.5));
			check = topLeft.checkUpper(1, 1);
//			System.out.println("tl:"+ topLeft);
			
			if (check) {
				setDrawLocation(drawX+topLeft.x, drawY+topLeft.y);
			}else{
				//FIXME need testing on larger maps  -- works?{
				Location bottomRight  = drawLocation.copy().translate(-parent.getWidth()+ mapSettings.tileDiagonal, -parent.getHeight()+ mapSettings.tileDiagonal/2);
//				System.out.println("br:"+ bottomRight);
				check =  bottomRight.checkLower(0, 0);
				if (check) setDrawLocation(drawX+bottomRight.x, drawY+bottomRight.y);
			}
//			System.out.println();
		}

	public IsoTile getTile(ILocation l) {
		return field[l.getX()][l.getY()];
	}

	public IActions getActionHandler() {
		return currentAction;
	}

	public MouseListener getMouseListener() {
		return MousePoxy;
	}

	public MouseMotionListener getMouseMotionListener() {
		return MousePoxy;
	}

	private MapActions getActionHandler(ActionsEnum num) {
		return actions[num.ordinal()];
	}

	void setActionHandler(ActionsEnum num) {
		final MapActions aa = actions[num.ordinal()];
		setActionHandler(aa);
	}

	private void setActionHandler(MapActions aa) {
		Logf.info(log, "Action changed to %s", aa);
		currentAction = aa;
		MousePoxy.setMouseListener(aa);
		MousePoxy.setMouseMotionListener(aa);
	}

	// Reloads the images of all tiles
	private void afterMapSettingsChange() {
		mapRenderer.invaildate();
		for (IsoTile[] arr : field) {
			for (IsoTile t : arr) {
				t.invaildate(mapSettings);
			}
		}
		setDrawn(false);
	}

	public AnimatedUnit getUnit(UUID uuid){
		return unitMapping.get(uuid);
	}
	
	public AnimatedUnit getUnitInculdingDied(UUID uuid){
		AnimatedUnit au    = unitMapping.get(uuid);
		if (au == null) au = died.get(uuid);
		return au;
	}
	
	private void toggleTileOutlines() {
		for (IsoTile[] tiles : field) {
			for (IsoTile t : tiles) {
				t.toggleOutline();
			}
		}
	}

	public void otherKeys(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_1:
				mapRenderer.toggleNumbering();
				break;
			case KeyEvent.VK_R:
				rotateMap();
				break;
			case KeyEvent.VK_M: {
				Gui.getMusicThread().toggleMusic();
				break;
			}
			case KeyEvent.VK_MINUS:
				if (mapSettings.zoom <= 0.8) break;
				mapSettings.zoom -= 0.2;
				log.info(mapSettings.zoom);
				afterMapSettingsChange();
				break;

			case KeyEvent.VK_EQUALS:
				if (mapSettings.zoom > 1.2) break;
				mapSettings.zoom += 0.2;
				log.info(mapSettings.zoom);
				afterMapSettingsChange();
				break;

			case KeyEvent.VK_COMMA:
				if (mapSettings.pitch <= 0.5) break;
				mapSettings.pitch -= 0.1;
//				MapSettings.pitch = Math.round(MapSettings.pitch * 10f) / 10f;
				log.info(mapSettings.pitch);
				afterMapSettingsChange();
				break;
			case KeyEvent.VK_PERIOD:
				if (mapSettings.pitch >= 0.7) break;
				mapSettings.pitch += 0.1;
//				MapSettings.pitch = Math.round(MapSettings.pitch * 10f) / 10f;
				log.info(mapSettings.pitch);
				afterMapSettingsChange();
				break;
			case KeyEvent.VK_O:
				toggleTileOutlines();
				break;
			case KeyEvent.VK_I:
				Logf.info(log, "way %s selected %s", mapRenderer.getRotation(), selectedTile);
				break;
		}

	}

	/** @category Generated */
	@Override
	public boolean isMouseMoving() {
		return currentAction.isMouseMoving();
	}

	/** @category Generated */
	@Override
	public int getDrawX() {
		return drawX;
	}

	/** @category Generated */
	@Override
	public int getDrawY() {
		return drawY;
	}

	/** @category Generated */
	ArrayList<AnimatedUnit> getPlayersUnits() {
		return plunits;
	}

	/** @category Generated */
	ArrayList<AnimatedUnit> getAIUnits() {
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

	/** @category Generated */
	public UnitState getState() {
		return state;
	}

	/** @category Generated */
	public Menu getMenu() {
		return menu;
	}

	/** @category Generated */
	public int getFieldWidth() {
		return fieldWidth;
	}

	/** @category Generated */
	public int getFieldHeight() {
		return fieldHeight;
	}

	/** @category Generated */
	SkillMovement getSkillMovement() {
		return skillMovement;
	}

}
