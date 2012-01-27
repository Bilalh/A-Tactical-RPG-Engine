package common.interfaces;

import common.Location;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Bilal Hussain
 */
public interface IUnit {

	int getMaxHp();

	int getCurrentHp();

	int getMove();

	int getStrength();

	String getName();

	int getGridX();

	int getGridY();

	UUID getUuid();
	
	Location getLocation();
	

}