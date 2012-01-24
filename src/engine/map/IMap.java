package engine.map;

import common.Location;
import java.util.ArrayList;

/**
 * @author Bilal Hussain
 */
public interface IMap {

	public abstract void start();

	public abstract void moveUnit(IModelUnit u, Location p);

	public abstract void setUsersUnits(ArrayList<IModelUnit> selected);

	public Tile getTile(int x, int y);
	
	public abstract int getFieldHeight();

	public abstract int getFieldWidth();

	public abstract boolean isPlayersTurn();

	public abstract ArrayList<Unit> getUnits();

}
