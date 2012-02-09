package engine.map;

import common.Location;

import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
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

}