package engine.map.interfaces;

import common.Location;

import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
import engine.map.MapPlayer;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
public interface IMutableMapUnit extends IMapUnit, IMutableUnit {

	void setCurrentHp(int currentHp);

	void setGridX(int gridX);

	void setGridY(int gridY);

	void setLocation(ILocation p);

	void setStatus(UnitStatus s);

	void setMoved();

	MapPlayer getPlayer();

	int getReadiness();

	void setReadiness(int readiness);

	/** Removes the specifed number of hit points from the unit
	 * @return True if the unit still alive otherwise false'
	 */
	boolean removeHp(int value);

	void addExp(int value);
	
	void levelUp();
	
}