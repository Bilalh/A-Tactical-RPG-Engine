package common.interfaces;

import common.Location;
import java.util.Collection;

import engine.map.UnitStatus;

/**
 * @author Bilal Hussain
 */
public interface IMapUnit extends IUnit {

	int getCurrentHp();

	int getGridX();

	int getGridY();

	Location getLocation();

	boolean hasStatus(UnitStatus s);

	boolean isMoved();
	
}