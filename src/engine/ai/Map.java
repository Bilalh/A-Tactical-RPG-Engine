package engine.ai;

import common.Location;
import common.LocationInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import notifications.*;
import notifications.map.ChooseUnitsNotifications;
import notifications.map.UnitMovedNotification;
import notifications.map.UnitTurnNotification;
import notifications.map.UnitsChosenNotification;

import org.apache.log4j.Logger;

import util.Args;
import util.Logf;

import common.enums.Orientation;
import common.interfaces.ILocation;
import common.interfaces.INotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import config.Config;
import config.xml.*;
import engine.Player;
import engine.PathfindingEx.AStarPathFinder;
import engine.PathfindingEx.Mover;
import engine.PathfindingEx.TileBasedMap;
import engine.map.*;
import engine.pathfinding.PathFinder;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * @author bilalh
 */
public class Map extends BasicMap implements IMap {
	private static final Logger log = Logger.getLogger(Map.class);
	private Player playerinfo;

	private AIPlayer ai;
	private MapPlayer player;

	// Cached movement data
	private HashMap<IMutableMapUnit, PathFinder> paths;
	// Turn ordering
	private PriorityQueue<IMutableMapUnit> order;

	private IMutableMapUnit current;

	/** @category Constructor */
	public Map(String name, Player player) {
		this.playerinfo = player;
		loadSettings(name);
		paths = new HashMap<IMutableMapUnit, PathFinder>();
		order = new PriorityQueue<IMutableMapUnit>(16, new DefaultTurnComparator());

		setUpAI();
	}

	@Override
	public void start() {
		INotification n = new ChooseUnitsNotifications(playerinfo.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	@Override
	public void setUsersUnits(HashMap<IMutableUnit, Location> selected) {

		ArrayList<IMutableMapUnit> units = new ArrayList<IMutableMapUnit>();

		player = new MapPlayer();
		for (Entry<IMutableUnit, Location> e : selected.entrySet()) {
			IMutableMapUnit u = new MapUnit(e.getKey(), e.getValue(), player);
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
			units.add(u);
			order.add(u);
		}
		player.setUnits(units);
		
		INotification n = new UnitsChosenNotification(new ArrayList<IMapUnit>(units));
		setChanged();
		notifyObservers(n);

		Logf.info(log, "ordering %s", order);
		setChanged();
		current = order.remove();
		n = new UnitTurnNotification(current);
		notifyObservers(n);
	}

	// Precondition getMovementRange must have been called first
	@Override
	public void moveUnit(IMutableMapUnit u, ILocation p) {

		Collection<LocationInfo> path = paths.get(u).getMovementPath(p);
		field[u.getGridX()][u.getGridY()].setCurrentUnit(null);
		u.setLocation(p);
		field[p.getX()][p.getY()].setCurrentUnit(u);

		// FIXME events
		for (LocationInfo l : path) {
			// field[l.x][l.y].event(u)
		}

		u.setReadiness(u.getReadiness() - 60);
		order.add(u);
		INotification n = new UnitMovedNotification(u, path);
		paths.remove(u);

		setChanged();
		notifyObservers(n);
	}


	public void finishedMoving(IMutableMapUnit u) {
		if (!(u instanceof AIUnit)){
			log.info("Player moved a unit");
//			return;
		}
		sendNextUnit();
	}
	
	
	public void sendNextUnit() {
		current = order.remove();
		if (current.getReadiness() <= 0) {
			order.clear();
			for (IMutableMapUnit u : player.getUnits()) {
				u.setReadiness(100);
				order.add(u);
			}
			for (IMutableMapUnit u : ai.getUnits()) {
				u.setReadiness(100);
				order.add(u);
			}

		}

		setChanged();
		INotification n = new UnitTurnNotification(current);
		notifyObservers(n);
		if (current instanceof AIUnit) {
			ILocation l = ai.getMoveLocation((AIUnit) current);
			moveUnit(current, l);
		}
	}

	@Override
	public Collection<LocationInfo> getMovementRange(IMutableMapUnit u) {
		Args.nullCheck(u);
		PathFinder pf;
		if ((pf = paths.get(u)) != null) {
			return pf.getMovementRange();
		}

		pf = new PathFinder(u, this);
		paths.put(u, pf);

		return pf.getMovementRange();
	}

	public ArrayList<IMutableMapUnit> getPlayerUnits() {
		return player.getUnits();
	}

	private void setUpAI() {
		ArrayList<IMutableMapUnit> aiUnits = new ArrayList<IMutableMapUnit>();

		Unit u = new Unit();
		UnitImages ui = new UnitImages();
		u.setName("ai-1");
		u.setMove(5);
		u.setSpeed(5);
		u.setStrength(30);
		u.setDefence(20);
		u.setMaxHp(40);
		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		AIUnit au = new AIUnit(u, new Location(width - 1, 5), ai);
		aiUnits.add(au);
		field[width - 1][0].setCurrentUnit(au);

		u = new Unit();
		ui = new UnitImages();
		u.setName("ai-2");
		u.setMove(6);
		u.setSpeed(10);
		u.setStrength(10);
		u.setDefence(10);
		u.setMaxHp(30);
		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		au = new AIUnit(u, new Location(width - 1, 4), ai);
		aiUnits.add(au);
		field[width - 1][1].setCurrentUnit(au);

		ai = new AIPlayer(this, aiUnits);

		assert order != null;
		for (IMutableMapUnit aiu : aiUnits) {
			assert aiu != null;
			order.add(aiu);
		}
	}

	private void loadSettings(String name) {
		loadMap(name);
		// testing();
		assert (field != null);
		assert (width > 0);
		assert (height > 0);
	}

	/** @category unused **/
	void testing() {
		tileMapping = Config.defaultMapping();
		width = 17;
		height = 17;
		field = new Tile[width][height];

		long seed = 654645l;
		Random r = new Random(seed);
		log.info("seed" + " " + seed);
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				int first = r.nextInt(3) + 1;
				field[i][j] = new Tile(first, first, "grass",Orientation.UP_TO_EAST);
				// field[i][j] = new Tile(1,1);
			}
		}
	}

}
