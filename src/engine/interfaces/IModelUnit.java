package engine.interfaces;

import java.awt.Point;

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

	void setLocation(Point p);

	Point getLocation();

}