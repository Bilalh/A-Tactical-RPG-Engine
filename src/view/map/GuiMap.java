package view.map;

import static util.Args.assetNonNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import javax.swing.text.DefaultEditorKit.CutAction;

import openal.Music;
import openal.SlickException;

import org.apache.log4j.Logger;

import util.Logf;
import view.Gui;
import view.map.IsoTile.TileState;
import view.ui.Dialog;
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
import common.interfaces.ILocation;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;

import config.xml.TileImageData;
import controller.MapController;
import engine.map.Tile;

/**
 * The view
 * 
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
	private AnimatedUnit[] plunits;
	private AnimatedUnit[] aiUnits;

	private HashMap<UUID, AnimatedUnit> unitMapping;

	private Menu menu     = new Menu();
	private Dialog dialog = new Dialog(0, 0);
	
	final MenuInput menuInput           = new MenuInput(this, menu);
	final DialogHandler dialogHandler   = new DialogHandler(this, dialog);
	private UnitInfoDisplay infoDisplay = new UnitInfoDisplay();

	// The classes that with handed the input
	private MousePoxy  MousePoxy;
	private MapActions currentAction;
	
	// Handles the input fpr each state
	final private MapActions[] actions = {
			new Movement(this), dialogHandler,
			new MapActions(this), menuInput };

	static enum ActionsEnum {
		MOVEMENT, DIALOG, NONE, MENU
	}

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
	private int frameDuration = 750 * 1000000;
	private int frameChange = 0;
	// When there are two units on the same tile keep a referance to the 
	// unit replaced so that it does not get lost.
	private AnimatedUnit replaced;

	/** @category Constructor */
	public GuiMap(MapController mapController, Component parent) {
		assert actions.length == ActionsEnum.values().length;

		this.parent = parent;
		this.mapController = mapController;

		final Tile grid[][] = mapController.getGrid();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;

		this.field = new IsoTile[fieldWidth][fieldHeight];
		this.mapRenderer = new IsomertricMapRenderer(field, this);
		this.unitMapping = new HashMap<UUID, AnimatedUnit>();
		this.pathIterator = new ArrayDeque<LocationInfo>(0).iterator();

		BufferSize s = mapRenderer.getMapDimensions();
		bufferWidth = s.width;
		bufferHeight = s.height;

		currentAction = getActionHandler(ActionsEnum.MOVEMENT);
		MousePoxy = new MousePoxy();
		setActionHandler(ActionsEnum.MOVEMENT);

		dialog.setWidth(665);
		dialog.setHeight(70);
		UnitState.setMap(this);

		// Load the tile images
		ResourceManager.instance().loadSpriteSheetFromResources(mapController.getTileSheetLocation());

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

		selectedTile = field[0][0];
		selectedTile.setSelected(true);

		mapController.addMapObserver(this);
		mapController.startMap();

		try {
			Gui.getMusicThread().replaceMusic(new Music("music/1-19 Fight It Out!.ogg", true));
			Gui.getMusicThread().pause();
		} catch (SlickException e) {
			e.printStackTrace();
		}

		// setActionHandler(ActionsEnum.MENU);
	}

	public void makeImageBuffer() {
		mapBuffer = parent.createImage(bufferWidth, bufferHeight);
	}

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
			g.setColor(Color.BLUE.brighter());
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			setDrawn(mapRenderer.draw(g, width, height));
			// Logf.trace(log, "%s %s %s %s %s %s %s %s %s %s ",bufferWidth,bufferHeight, 0, 0, width, height, drawX, drawY, drawX + width,
			// drawY + height);
		}

		_g.drawImage(mapBuffer, 0, 0, width, height, drawX, drawY, drawX + width, drawY + height, null);

		if (selectedTile.getUnit() != null) {
			infoDisplay.setUnit(selectedTile.getUnit().getUnit());
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

	private Location getDrawLocation(Location l) {
		int x = mapRenderer.getStartX() - (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - l.y - 1f));
		int y = mapRenderer.getStartX() + (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom * (fieldHeight - l.y - 1));
		x += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * l.x;
		y += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom) * l.x;
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
		Gui.console().println(notification);
		((IMapNotification) notification).process(this);
	}

	public void chooseUnits(ArrayList<? extends IUnit> allPlayerUnits, ArrayList<? extends IMapUnit> aiUnits) {
		assetNonNull(allPlayerUnits, aiUnits);

		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		HashMap<IUnit, Location> selectedPostions = new HashMap<IUnit, Location>();
		for (int i = 0; i < newUnits.length; i++) {
			// //FIXME indies
			final IUnit u = allPlayerUnits.get(i);
			assert u != null;
			Location p = new Location(2, i + 2);
			newUnits[i] = new AnimatedUnit(p.x, p.y, u);
			selectedPostions.put(u, p);
			unitMapping.put(u.getUuid(), newUnits[i]);
			field[p.x][p.y].setUnit(newUnits[i]);
		}
		this.plunits = newUnits;

		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			final IMapUnit u = aiUnits.get(i);
			newAiUnits[i] = new AnimatedUnit(u.getGridX(), u.getGridY(), u);
			newAiUnits[i].setMapUnit(u);
			field[u.getGridX()][u.getGridY()].setUnit(newAiUnits[i]);
			unitMapping.put(u.getUuid(), newAiUnits[i]);
		}
		this.aiUnits = newAiUnits;

		mapController.setUsersUnits(selectedPostions);
	}

	public void unitsChoosen(ArrayList<IMapUnit> units) {
		for (IMapUnit u : units) {
			field[u.getGridX()][u.getGridY()].getUnit().setMapUnit(u);
		}
	}

	public void unitsTurn(IMapUnit unit) {
		currentUnit = getTile(unit.getLocation()).getUnit();
		assert currentUnit != null;

		changeState(UnitState.WAITING);
		setSelectedTile(currentUnit.getGridX(), currentUnit.getGridY());
		
		
		Location drawLocation  = getDrawLocation(currentUnit.getLocation());
		//FIXME why add MapSettings.tileDiagonal?
		int diffY = drawY + mapRenderer.getMapDimensions().heightOffset + MapSettings.tileDiagonal;
		drawLocation.translate(-drawX, -diffY);
		
		//FIXME need testing on larger maps
		Location bottom  = drawLocation.copy().translate(-parent.getWidth(), -parent.getHeight());
//		System.out.printf("(%d,%d)\n", parent.getWidth(), parent.getHeight());
//		System.out.println(drawLocation);
//		System.out.println(bottom);
		bottom.limitLower(0, 0);
		setDrawLocation(drawX+bottom.y, drawY+bottom.y);			
		
	}

	// To allow the user to see the ai's move
	Timer timer = new Timer();

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
			}, 1000);
		}

		log.debug(u);
		log.debug(movingUnit);
		Logf.info(log, "%s moved from %s to %s with path:%s", u.getName(), movingUnit.getLocation(), u.getLocation(), path);
		setDrawn(false);
	}

	private void finishedMoving(AnimatedUnit u) {
		if (nextState == null) nextState = UnitState.WAITING;
		changeState(nextState);
		nextState = null;
		mapController.finishedMoving(u.getUnit());
	}

	Collection<LocationInfo> othersRange;

	void tileSelected() {
		if (othersRange != null) return;

		IsoTile selected = getSelectedTile();
		final AnimatedUnit u = getSelectedTile().getUnit();

		if (u != currentUnit && state == UnitState.WAITING && othersRange == null) {
			if (u != null) othersRange = highlightRange(u);
			return;
		}

		switch (state) {
			case WAITING:
				changeState(state.exec(null, null));
				break;
			case MOVEMENT_RANGE:
				Logf.info(log, "exec: %s", state);
				nextState = state.exec(currentUnit, selected);
				break;
		}

	}

	void waitingCancel() {
		if (othersRange != null) {
			removeRange(othersRange);
			othersRange = null;
		}
	}

	Collection<LocationInfo> highlightRange(AnimatedUnit u) {
		Collection<LocationInfo> inRange = mapController.getMovementRange(u.getUnit());
		for (LocationInfo p : inRange) {
			getTile(p).setState(u == currentUnit ? TileState.MOVEMENT_RANGE : TileState.OTHERS_RANGE);
		}
		return inRange;
	}

	void removeRange(Iterable<LocationInfo> range) {
		for (LocationInfo l : range) {
			getTile(l).setState(TileState.NONE);
		}
	}

	/** @category unused **/
	public void playersTurn() {
		displayMessage("Player's Turn");
	}

	void changeState(UnitState newState) {
		assert newState != null;

		Logf.info(log, "State %s -> %s", state, newState);
		state = newState;
		Logf.info(log, "stateEntered: %s", state);
		state.stateEntered(null);
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
			translateDirectionOnRotation(u);
		}

		for (AnimatedUnit u : aiUnits) {
			translateDirectionOnRotation(u);
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

	public void translateDirectionOnRotation(AnimatedUnit u) {
		Rotation r = mapRenderer.getRotation();
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
		Point p = new Point(x, y);
		x += drawX;
		y += drawY;
		int xIndex = -1, yIndex = -1;
		
		// Logf.info(log,"p:(%d,%d)\n", x, y);
		for (int i = 0; i < fieldWidth; i++) {
			for (int j = 0; j < fieldHeight; j++) {
				if (field[i][j].getOrientation() != Orientation.EMPTY && field[i][j].contains(p)) {
					// Logf.info(log,"Clicked(%d,%d)\n", i, j);
					if (field[i][j].getHeight() > highest) {
						// log.info("\t highest");
						highest = field[i][j].getHeight();
						xIndex = i;
						yIndex = j;
					}

				}
			}
		}
		if (xIndex > -1 && yIndex > -1) {
			this.setSelectedTile(xIndex, yIndex);
			// Logf.info(log,"(%d,%d)\n\n", xIndex, yIndex);
			return new Location(xIndex, yIndex);
		} else {
			// Logf.info(log,"(%d,%d)\n\n", xIndex, yIndex);
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

		setSelectedTile(l.x, l.y);
	}

	public IsoTile getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(int x, int y) {
		if (x < 0 || y < 0 || x >= fieldWidth || y >= fieldHeight) {
			assert false : "should not happen";
			return;
		}

		if (selectedTile != null) {
			selectedTile.setSelected(false);
		}

		selectedTile = field[x][y];
		selectedTile.setSelected(true);
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
				t.invaildate();
			}
		}
		setDrawn(false);
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
				if (MapSettings.zoom <= 0.8) break;
				MapSettings.zoom -= 0.2;
				log.info(MapSettings.zoom);
				afterMapSettingsChange();
				break;

			case KeyEvent.VK_EQUALS:
				if (MapSettings.zoom > 1.2) break;
				MapSettings.zoom += 0.2;
				log.info(MapSettings.zoom);
				afterMapSettingsChange();
				break;

			case KeyEvent.VK_COMMA:
				if (MapSettings.pitch < 0.6) break;
				MapSettings.pitch *= 0.8;
				MapSettings.pitch = Math.round(MapSettings.pitch * 10f) / 10f;
				log.info(MapSettings.pitch);
				afterMapSettingsChange();
				break;
			case KeyEvent.VK_PERIOD:
				if (MapSettings.pitch > 0.8) break;
				MapSettings.pitch *= 1.2;
				MapSettings.pitch = Math.round(MapSettings.pitch * 10f) / 10f;
				log.info(MapSettings.pitch);
				afterMapSettingsChange();
				break;

			case KeyEvent.VK_L:
				mapController.mapFinished();
				break;

			case KeyEvent.VK_Y:
				menu.reset();
				setActionHandler(ActionsEnum.MENU);
				break;
			case KeyEvent.VK_T:
				dialog.setPicture(ResourceManager.instance().getSpriteFromClassPath("assets/gui/mage.png"));
				dialog.setName("Mage");
				dialog.setText(
						"Many people believe that Vincent van Gogh painted his best works " +
								"during the two-year period he spent in Provence. Here is where he " +
								"painted The Starry Night--which some consider to be his greatest " +
								"work of all. However, as his artistic brilliance reached new " +
								"heights in Provence, his ysical and mental health plummeted. ");
				setActionHandler(ActionsEnum.DIALOG);
				break;
			case KeyEvent.VK_I:
				Logf.info(log, "draw (%d,%d) selected %s unit:%s", drawX, drawY, selectedTile, selectedTile.getUnit());
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
	AnimatedUnit[] getPlayersUnits() {
		return plunits;
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

	/** @category Generated */
	public UnitState getState() {
		return state;
	}

	/** @category Generated */
	public Menu getMenu() {
		return menu;
	}

}
