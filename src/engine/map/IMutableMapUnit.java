package engine.map;

import common.Location;

import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
import engine.IMutableUnit;


/**
 * @author Bilal Hussain
 */
public interface IMutableMapUnit extends IMapUnit, IMutableUnit {

	void setCurrentHp(int currentHp);

	void setGridX(int gridX);

	void setGridY(int gridY);

	void setLocation(ILocation p);
	
}