package engine.ai;

import common.Location;
import common.LocationInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import notifications.*;
import notifications.map.*;

import org.apache.log4j.Logger;

import util.Args;
import util.Logf;

import common.enums.Direction;
import common.enums.Orientation;
import common.interfaces.*;
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

	private void loadSettings(String name) {
			loadMap(name);
	//		for (int i = 0; i < width; i++) {
	//			for (int j = 0; j < height; j++) {
	//				field[i][j] = new Tile(1, 1, field[i][j].getType(), Orientation.UP_TO_EAST);
	//			}
	//		}
			assert (field != null);
			assert (width > 0);
			assert (height > 0);
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

	ArrayList<IMutableMapUnit> getPlayerUnits() {
		return player.getUnits();
	}

	private void sendNotification(IMapNotification n){
		setChanged();
		notifyObservers(n);
	}
	
	@Override
	public void start() {
		sendNotification(new ChooseUnitsNotifications(playerinfo.getUnits(), ai.getUnits()));
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
		
		sendNotification(new UnitsChosenNotification(new ArrayList<IMapUnit>(units)));
		
		Logf.debug(log, "ordering %s", order);
		setChanged();
		current = order.remove();
		
		sendNotification(new UnitTurnNotification(current));
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
		paths.remove(u);

		sendNotification(new UnitMovedNotification(u, path));
	}


	public void finishedMoving(IMutableMapUnit u) {
		if (u.isAI()){
			log.info("AI moved a unit");
			IMutableMapUnit target = ai.getTarget(u);
			
			if (target == null){
				unitTurnFinished(u);
			}else{
				targetChosen(u, target);
			}
		}
	}

	public void unitTurnFinished(IMutableMapUnit u) {
		if (!checkForFinished()){
			sendNextUnit();
		}
	}
	
	// returns true if the map is finished.
	public boolean checkForFinished(){
		if (ai.getUnits().isEmpty()) {
			System.err.println("PLAYERS WINS");	
			sendNotification(new PlayerWonNotification());
			return true;
		}
		if (player.getUnits().isEmpty()) {
			System.err.println("AI WINS");
			sendNotification(new PlayerLostNotification());
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tell the  Observers the next unit to move
	 */
	void sendNextUnit() {
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

		sendNotification(new UnitTurnNotification(current));
		
		if (current.isAI()) {
			log.info("Next Unit is ai:" + current);
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

	// Returns the units that the specifed units can attack
	public Collection<Location> getVaildTargets(IMutableMapUnit u) {
		assert u != null;
		
		Location l = u.getLocation();
		Tile t = field[l.x][l.y];
		Collection<Location> results = new ArrayList<Location>();
		boolean opponent = !u.isAI();

		for (Direction d : Direction.values()) {
			final int nx = l.x + d.x, ny = l.y + d.y;
			if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;

			final Tile tt = field[nx][ny];
			final IMutableMapUnit v = tt.getCurrentUnit();
			if (Math.abs(tt.getEndHeight() - t.getEndHeight()) <= 2 && v != null && v.isAI() == opponent) {
				results.add(new Location(nx, ny));
			}
		}
		return results;
	}

	// Peforms the attack and notifies the Observers what the results were 
	public void targetChosen(IMutableMapUnit u, IMutableMapUnit target){
		
		Battle battle = new Battle(u, target);
		battle.performBattle();
		
		if (!battle.isTargetAlive()){
			if (target.isAI()){
				ai.unitDied(target);
			}else{
				player.unitDied(target);
			}
			order.remove(target);
			paths.remove(target);
			getTile(target.getLocation()).setCurrentUnit(null);
		}
		
		sendNotification(new BattleNotification(battle));
	}

}
