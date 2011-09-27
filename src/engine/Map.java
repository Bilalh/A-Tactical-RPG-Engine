package engine;

import java.util.ArrayList;
import java.util.Observable;

import engine.notifications.ChooseUnitsNotifications;
import engine.notifications.Notification;

/**
 * @author bilalh
 */
public class Map extends Observable {

	Tile[][] field;
	AIPlayer ai;
	Player player;
	ArrayList<Unit> selectedUnits;
	
	boolean turn; // false player

	/** @category Constructor */
	public Map(String name, Player player) {
		this.player = player;
		loadSettings(name);
		setUpAI();
		turn = false;
	}

	private void setUpAI() {
		AIPlayer ai = new AIPlayer();
		ai.addUnit(new Unit("ai-1", 20, 4, 4));
		ai.addUnit(new Unit("ai-2", 10, 2, 10));
		this.ai = ai;
	}

	public void setUsersUnits(ArrayList<Unit> units){
		selectedUnits= units;
	}

	private void loadSettings(String name) {
		field = new Tile[5][5];
		
	}

	/** @category Generated Getter */
	public Tile[][] getField() {
		return field;
	}

	public void start() {
		Notification n =  new ChooseUnitsNotifications(player.getUnits());
		setChanged();
		notifyObservers(n);
	}

}
