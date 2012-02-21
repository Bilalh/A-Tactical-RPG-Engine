package view.map.interfaces;

import view.units.AnimatedUnit;

/**
 * Interface that allow the map renderer to get the infomation it needs.
 * @author Bilal Hussain
 */
public interface IMapRendererParent {
	
	boolean isMouseMoving();
	int getDrawX();
	int getDrawY();
	
}
