package common.interfaces;

import java.util.UUID;

import engine.map.Tile;

/**
 * @author Bilal Hussain
 */
public interface IUnit {

	int getMaxHp();

	int getMove();

	int getStrength();

	String getName();

	UUID getUuid();

	int getExp();

	int getLevel();

	int getDefence();

	int getCost(Tile old, Tile next);

	int getSpeed();
	
}