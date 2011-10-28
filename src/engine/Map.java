package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;

import view.notifications.ChooseUnitsNotifications;
import view.notifications.PlayersTurnNotification;
import view.notifications.UserMovedNotification;

import common.interfaces.INotification;



/**
 * @author bilalh
 */
public class Map extends Observable {

	Tile[][] field;
	AIPlayer ai;
	Player player;
	ArrayList<Unit> selectedUnits;
	
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
		ai.addUnit(new Unit("ai-1", 20, 4, 4));
		ai.addUnit(new Unit("ai-2", 10, 2, 10));
		this.ai = ai;
	}

	public void setUsersUnits(ArrayList<Unit> units){
		selectedUnits= units;
		INotification n =  new PlayersTurnNotification();
		setChanged();
		notifyObservers(n);
	}

	private void loadSettings(String name) {
		field = new Tile[5][5];
//		Random r = new Random();
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
//				int first = r.nextInt(3)+1;
//				field[i][j] = new Tile(first, first + r.nextInt(2));
				field[i][j] = new Tile(1,1);
			}
		}
	}

	/** @category Generated Getter */
	public Tile[][] getField() {
		return field;
	}

	public void start() {
		INotification n =  new ChooseUnitsNotifications(player.getUnits(), ai.getUnits());
		setChanged();
		notifyObservers(n);
	}

	/** @category Generated Getter */
	public boolean isPlayersTurn() {
		return playersTurn;
	}

	public void moveUnit(Unit u, int gridX, int gridY){
		u = selectedUnits.get(0);
		u.setGridX(gridX);
		u.setGridY(gridY);
		setChanged();
		INotification n =  new UserMovedNotification(u);
		setChanged();
		notifyObservers(n);
	}
	
}
