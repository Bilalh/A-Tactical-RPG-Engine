package engine.map;

import common.Location;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.apache.log4j.Logger;

import view.notifications.ChooseUnitsNotifications;
import view.notifications.PlayersTurnNotification;
import view.notifications.UserMovedNotification;

import common.interfaces.INotification;
import common.interfaces.IUnit;
import engine.PathfindingEx.AStarPathFinder;
import engine.PathfindingEx.Mover;
import engine.PathfindingEx.TileBasedMap;
import engine.ai.AIPlayer;
import engine.ai.AIUnit;
import engine.pathfinding.LocationInfo;
import engine.pathfinding.PathFinder;

/**
 * @author bilalh
 */
public class Map extends Observable implements IMap {

	private static final Logger log = Logger.getLogger(Map.class);
	
	Tile[][] field;
	private int width;
	private int height;

	private AIPlayer ai;
	private Player player;
	
	
	private ArrayList<IModelUnit> selectedUnits;
	private HashMap<IModelUnit, PathFinder> paths;
	
	boolean playersTurn; // false aiplayer

	/** @category Constructor */
	public Map(String name, Player player) {
		this.player = player;
		loadSettings(name);
		setUpAI();
		
		playersTurn = true;
		paths = new HashMap<IModelUnit, PathFinder>();
	}

	private void setUpAI() {
		AIPlayer ai = new AIPlayer();
		AIUnit u = new AIUnit("ai-1", 20, 4, 4);
		u.setGridX(width - 1);
		u.setGridY(0);
		ai.addUnit(u);
		field[width - 1][0].setCurrentUnit(u);

		u = new AIUnit("ai-2", 10, 3, 10);
		u.setGridX(width - 1);
		u.setGridY(1);
		ai.addUnit(u);
		field[width - 1][1].setCurrentUnit(u);

		this.ai = ai;
	}

	private void loadFromSpaceSepFile(String name) {
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

	private void loadSettings(String name) {
//		loadFromSpaceSepFile("test.txt");
		testing();
	}

	void testing() {
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
		// field[2][6] = new Tile(3, 3);
		//
		// field[2][7] = new Tile(2, 2);
		// field[1][4] = new Tile(2, 2);
		//
		//
		// field[3][5] = new Tile(2, 2);
		// field[2][4] = new Tile(2, 2);
		//
		// field[1][6] = new Tile(2, 2);
		// field[10][5] = new Tile(7, 7);
		// field[10][4] = new Tile(7, 7);
		//
		// for(int i =0; i < 8;i+=2){
		// field[6][i] = new Tile(i,i);
		// }

		// for (int i = 8; i < 14; i++) {
		// for (int j = 8; j < 14; j++) {
		// int first = r.nextInt(5)+1;
		// field[i][j] = new Tile(first, first);
		// }
		// }
	}

	@Override
	public void start() {
		INotification n = new ChooseUnitsNotifications(player.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	@Override
	public void setUsersUnits(ArrayList<IModelUnit> selected) {
		selectedUnits = selected;
		System.out.println(selected);
		for (IModelUnit u : selected) {
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
		}

		INotification n = new PlayersTurnNotification();
		setChanged();
		notifyObservers(n);
	}

	
	// Precondtion getMovementRange must have been called first
	@Override
	public void moveUnit(IModelUnit u, Location p) {
		
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

	public ArrayList<LocationInfo> getMovementRange(IModelUnit u){
		PathFinder pf;
		if ((pf = paths.get(u)) != null){
			return pf.getMovementRange();
		}
		
		pf = new PathFinder(u, this);
		paths.put(u, pf);
		
		return pf.getMovementRange();
	}
	
	
	@Override
	public ArrayList<Unit> getUnits() {
		return player.getUnits();
	}

	@Override
	public Tile getTile(int x, int y){
		return field[x][y];
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
	
}
