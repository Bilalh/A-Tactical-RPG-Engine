package engine.map;

import common.Location;
import common.LocationInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import util.Args;
import util.Logf;
import view.notifications.*;

import common.interfaces.INotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import config.Config;
import config.xml.*;
import engine.IMutableUnit;
import engine.Player;
import engine.Unit;
import engine.UnitImages;
import engine.PathfindingEx.AStarPathFinder;
import engine.PathfindingEx.Mover;
import engine.PathfindingEx.TileBasedMap;
import engine.ai.AIPlayer;
import engine.ai.AIUnit;
import engine.pathfinding.PathFinder;

/**
 * @author bilalh
 */
public class Map extends BasicMap implements IMap {
	private static final Logger log = Logger.getLogger(Map.class);
	private Player playerinfo;
	
	private AIPlayer ai;
	private MapPlayer player;
	
	//	Cached movement data 
	private HashMap<IMutableMapUnit, PathFinder> paths;
	// Turn ordering
	private PriorityQueue<IMutableMapUnit> order;
	
	/** @category Constructor */
	public Map(String name, Player player) {
		this.playerinfo = player;
		loadSettings(name);
		setUpAI();
		
		paths = new HashMap<IMutableMapUnit, PathFinder>();
		order = new PriorityQueue<IMutableMapUnit>(16,new DefaultTurnComparator());
		
	}

	@Override
	public void start() {
		INotification n = new ChooseUnitsNotifications(playerinfo.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	
	private IMutableMapUnit current;
	
	@Override
	public void setUsersUnits(HashMap<IMutableUnit, Location> selected) {

		ArrayList<IMutableMapUnit> units = new ArrayList<IMutableMapUnit>();
		
		for (Entry<IMutableUnit, Location> e : selected.entrySet()) {
			IMutableMapUnit u = new MapUnit(e.getKey(),e.getValue(),player); 
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
			units.add(u);
			order.add(u);
		}
		player = new MapPlayer(units);
		
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
	public void moveUnit(IMutableMapUnit u, Location p) {
		
		Collection<LocationInfo> path  =  paths.get(u).getMovementPath(p);
		field[u.getGridX()][u.getGridY()].setCurrentUnit(null);
		u.setLocation(p);
		field[p.x][p.y].setCurrentUnit(u);
		
		//FIXME events
		for (LocationInfo l : path) {
//			field[l.x][l.y].event(u)
		}

		u.setReadiness(u.getReadiness() - 60);
		order.add(u);
		INotification n = new UserMovedNotification(u,path);
		paths.remove(u);
		
		setChanged();
		notifyObservers(n);
		sendNextUnit();
	}


	public void sendNextUnit(){
		current = order.remove();
		if (current.getReadiness() == 0){
			order.clear();
			for (IMutableMapUnit u : player.getUnits()) {
				u.setReadiness(100);
				order.add(u);
			}
//			for (IMutableMapUnit u : ai.getUnits()) {
//				u.setReadiness(100);
//				order.add(u);
//			}
			
		}
		setChanged();
		INotification n = new UnitTurnNotification(current);
		notifyObservers(n);
	}
	
	@Override
	public Collection<LocationInfo> getMovementRange(IMutableMapUnit u){
		Args.nullCheck(u);
		PathFinder pf;
		if ((pf = paths.get(u)) != null){
			return pf.getMovementRange();
		}
		
		pf = new PathFinder(u, this);
		paths.put(u, pf);
		
		return pf.getMovementRange();
	}
	
	
	@Override
	public ArrayList<IMapUnit> getUnits() {
		 return new ArrayList<IMapUnit>(player.getUnits());
	}

	private void setUpAI() {
		ArrayList<IMutableMapUnit> aiUnits = new ArrayList<IMutableMapUnit>();
		
		
		Unit u        = new Unit();
		UnitImages ui = new UnitImages(); 
		u.setName("ai-1");
		u.setMove(3);
		u.setSpeed(20);
		u.setStrength(30);
		u.setDefence(20);
		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		AIUnit au = new AIUnit(u,new Location( width - 1, 0), ai);
		aiUnits.add(au);
		field[width - 1][0].setCurrentUnit(au);
		
		u  = new Unit();
		ui = new UnitImages(); 
		u.setName("ai-2");
		u.setMove(4);
		u.setSpeed(60);
		u.setStrength(10);
		u.setDefence(10);
		ui.setSpriteSheetLocation("images/characters/Elena.png");
		u.setImageData(ui);
		au = new AIUnit(u,new Location( width - 1, 1), ai);
		aiUnits.add(au);
		field[width - 1][1].setCurrentUnit(au);
		
		ai = new AIPlayer(aiUnits);
	}

	private void loadSettings(String name) {
		loadMap(name);
//		loadFromSpaceSepFile("test.txt");
//		testing();
		assert(field != null);
		assert(width  > 0);
		assert(height > 0);
	}

	/** @category unused**/
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
				field[i][j] = new Tile(first, first,"grass");
				// field[i][j] = new Tile(1,1);
			}
		}
	}

	/** @category unused**/
	private void loadFromSpaceSepFile(String name) {
		tileMapping = Config.defaultMapping();
		try {
			File f = new java.io.File(name);
			Scanner sc = new Scanner(f);
			width = sc.nextInt();
			height = sc.nextInt();
			// width = 8;
			// height = 8;
			field = new Tile[width][height];
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					int a = sc.nextInt() + 2;
					field[i][j] = new Tile(a, a,"grass");
				}
			}
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
}
