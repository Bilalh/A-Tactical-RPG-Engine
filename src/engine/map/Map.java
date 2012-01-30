package engine.map;

import common.Location;
import common.LocationInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import util.Args;
import view.notifications.*;

import common.interfaces.INotification;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import config.Config;
import config.xml.SavedMap;
import config.xml.SavedTile;
import config.xml.TileImageData;
import config.xml.TileMapping;
import engine.IMutableUnit;
import engine.Player;
import engine.Unit;
import engine.PathfindingEx.AStarPathFinder;
import engine.PathfindingEx.Mover;
import engine.PathfindingEx.TileBasedMap;
import engine.ai.AIPlayer;
import engine.ai.AIUnit;
import engine.pathfinding.PathFinder;

/**
 * @author bilalh
 */
public class Map extends Observable implements IMap {

	private static final Logger log = Logger.getLogger(Map.class);
	
	private Tile[][] field;
	private TileMapping tileMapping;
	
	private int width;
	private int height;

	private Player player;
	
	private AIPlayer ai;
	private MapPlayer mplayer;
	
	//	Cached movement data 
	private HashMap<IMutableMapUnit, PathFinder> paths;
	
	private boolean playersTurn; 
	
	/** @category Constructor */
	public Map(String name, Player player) {
		this.player = player;
		loadSettings(name);
		setUpAI();
		
		playersTurn = true;
		paths = new HashMap<IMutableMapUnit, PathFinder>();
	}

	private void setUpAI() {
		ArrayList<IMutableMapUnit> aiUnits = new ArrayList<IMutableMapUnit>();
		
		AIUnit u = new AIUnit("ai-1", width - 1, 0);
		aiUnits.add(u);
		field[width - 1][0].setCurrentUnit(u);

		u = new AIUnit("ai-2", width - 1, 1);
		aiUnits.add(u);
		field[width - 1][1].setCurrentUnit(u);
		
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

	void loadMap(String name){
		SavedMap smap = Config.loadMap(name);
		
		width  = smap.getFieldWidth();
		height = smap.getFieldHeight();
		field  = new Tile[width][height];
		
		for (SavedTile t : smap.getTiles()) {
			field[t.getX()][t.getY()] = new Tile(t.getHeight(), t.getHeight(), t.getType());
		}
		
		String mappingLocation = smap.getTileMappinglocation();
		if (mappingLocation == null){
			tileMapping = Config.defaultMapping();	
		}else{
			tileMapping = Config.loadMap(mappingLocation);
		}
		
	}
	
	@Override
	public void start() {
		INotification n = new ChooseUnitsNotifications(player.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	@Override
	public void setUsersUnits(HashMap<IMutableUnit, Location> selected) {
		System.out.println(selected);

		ArrayList<IMutableMapUnit> units = new ArrayList<IMutableMapUnit>();
		
		for (Entry<IMutableUnit, Location> e : selected.entrySet()) {
			IMutableMapUnit u = new MapUnit(e.getKey(),e.getValue()); 
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
			units.add(u);
		}
		mplayer = new MapPlayer(units);
		
		INotification n = new UnitsChosenNotification(new ArrayList<IMapUnit>(units));
		setChanged();
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
		
		INotification n = new UserMovedNotification(u,path);
		paths.remove(u);
		
		setChanged();
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
		 return new ArrayList<IMapUnit>(mplayer.getUnits());
	}

	@Override
	public Tile getTile(int x, int y){
		return field[x][y];
	}
	
	@Override
	public TileImageData getTileImageData(int x, int y){
		return tileMapping.getTileImageData(field[x][y].getType());
	}
	
	/** @category Generated */
	public Tile[][] getField() {
		return field;
	}

	/** @category Generated */
	@Override
	public boolean isPlayersTurn() {
		return playersTurn;
	}

	/** @category Generated Getter */
	@Override
	public int getFieldWidth() {
		return width;
	}

	/** @category Generated Getter */
	@Override
	public int getFieldHeight() {
		return height;
	}

	
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
				field[i][j] = new Tile(first, first);
				// field[i][j] = new Tile(1,1);
			}
		}
	}

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
					field[i][j] = new Tile(a, a);
				}
			}
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
