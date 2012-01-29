package common.interfaces;

import common.Location;
import java.util.Collection;

/**
 * @author Bilal Hussain
 */
public interface IMapUnit extends IUnit {

	int getCurrentHp();

	int getGridX();

	int getGridY();

	Location getLocation();
	
}