package engine.map;

import common.Location;

import common.interfaces.IUnit;


/**
 * @author Bilal Hussain
 */
public interface IModelUnit extends IUnit {

	void setMaxHp(int maxHp);

	void setCurrentHp(int currentHp);

	void setMove(int move);

	void setStrength(int strength);

	void setGridX(int gridX);

	void setGridY(int gridY);

	void setLocation(Location p);

	Location getLocation();

	int getCost(Tile old, Tile New);
	
}