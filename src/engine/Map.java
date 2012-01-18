package engine;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


import view.notifications.ChooseUnitsNotifications;
import view.notifications.PlayersTurnNotification;
import view.notifications.UserMovedNotification;

import common.interfaces.INotification;
import common.interfaces.IUnit;
import engine.interfaces.IModelUnit;



/**
 * @author bilalh
 */
public class Map extends Observable {

	private Tile[][] field;
	private int width;
	private int height;
	
	private AIPlayer ai;
	private Player player;
	private ArrayList<IModelUnit> selectedUnits;
	
	boolean playersTurn; // false aiplayer

	/** @category Constructor */
	public Map(String name, Player player) {
		this.player = player;
		loadSettings(name);
		setUpAI();
		playersTurn = true;
	}

	private void setUpAI() {
		AIPlayer ai = new AIPlayer();
		AIUnit u = new AIUnit("ai-1", 20, 4, 4);
		u.setGridX(width-1);
		u.setGridY(0);
		ai.addUnit(u);
		field[width-1][0].setCurrentUnit(u);
		
		
		u = new AIUnit("ai-2", 10, 3, 10);
		u.setGridX(width-1);
		u.setGridY(1);
		ai.addUnit(u);
		field[width-1][1].setCurrentUnit(u);
		
		
		this.ai = ai;
	}

	public void setUsersUnits(ArrayList<IModelUnit> selected){
		selectedUnits= selected;
		System.out.println(selected);
		for (IModelUnit u: selected) {
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
		}
		
		INotification n =  new PlayersTurnNotification();
		setChanged();
		notifyObservers(n);
	}

	private void loadFromSpaceSepFile(String name){
		try {
			File f = new java.io.File(name);
			Scanner sc = new Scanner(f);
			width  = sc.nextInt();
			height = sc.nextInt();
//			width = 8; 
//			height = 8;
			field = new Tile[width][height];
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					int a= sc.nextInt() +2;
					field[i][j] = new Tile(a,a);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	private void loadSettings(String name) {
		loadFromSpaceSepFile("test.txt");
//		testing();
	}

	void testing(){
		width = 16; 
		height =16;
		field = new Tile[width][height];
		
		long seed = 654645l;
		Random r = new Random(seed);
		System.out.println("seed" + " " + seed);
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				int first = r.nextInt(3)+1;
				first=1;
				field[i][j] = new Tile(first, first);
//				field[i][j] = new Tile(1,1);
			}
		}
//			field[2][6] = new Tile(3, 3);
//			
//			field[2][7] = new Tile(2, 2);
//			field[1][4] = new Tile(2, 2);
//			
//			
//			field[3][5] = new Tile(2, 2);
//			field[2][4] = new Tile(2, 2);
//			
//			field[1][6] = new Tile(2, 2);
//			field[10][5] = new Tile(7, 7);
//			field[10][4] = new Tile(7, 7);
//	
//			for(int i =0; i < 8;i+=2){
//				field[6][i] = new Tile(i,i);
//			}
		
		
//		for (int i = 8; i < 14; i++) {
//			for (int j = 8; j < 14; j++) {
//				int first = r.nextInt(5)+1;
//				field[i][j] = new Tile(first, first);
//			}
//		}
	}

	public void start() {
		INotification n =  new ChooseUnitsNotifications(player.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	public void moveUnit(IModelUnit u, Point p){
		field[u.getGridX()][u.getGridY()].setCurrentUnit(null);
		u.setLocation(p);
		field[p.x][p.y].setCurrentUnit(u);
		setChanged();
		INotification n =  new UserMovedNotification(u);
		setChanged();
		notifyObservers(n);
	}
	
	public ArrayList<Unit> getUnits(){
		return player.getUnits();
	}

	/** @category Generated */
	public Tile[][] getField() {
		return field;
	}
	
	/** @category Generated */
	public boolean isPlayersTurn() {
		return playersTurn;
	}

	HashSet<Point> points  = new HashSet<Point>();
	static final int[][] dirs = {
			{0,1},   // up 
			{0,-1},  // down
			{-1,0},  // left
			{1,0},   // right
	};		
	
	private void checkAround(Point p, int movmentLeft){
//		System.out.println(p + " l:"+ movmentLeft + " c:" + field[x][y].getCost()  + " r:" +  ( field[x][y].getCost() - movmentLeft) +  "\t" + points );

		if (field[p.x][p.y].getCost() - movmentLeft >0 || field[p.x][p.y].getCurrentUnit() instanceof AIUnit) return;
		if (!(field[p.x][p.y].getCurrentUnit() instanceof IModelUnit)){
			points.add(p);
		}
		
		movmentLeft -=field[p.x][p.y].getCost(); 
		
		for (final int[] is : dirs) { 
			Point pp = new Point(p);
			pp.translate(is[0], is[1]);
			if (pp.x  >= 0 &&  pp.x < width && pp.y >= 0 &&  pp.y < height ){
				checkAround(pp, movmentLeft);
			}
		}
		
	}
	
	public synchronized Collection<Point> getMovementRange(IModelUnit u) {
		points.clear();
		System.out.println("Finding moves for " + u);
		checkAround(u.getLocation(), u.getMove()+ field[u.getGridX()][u.getGridY()].getCost() );
		System.out.println("Found moves for " + u +  " " + points);
		return points;
	}
	
}

