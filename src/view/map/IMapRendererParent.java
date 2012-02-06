package view.map;


import view.AnimatedUnit;

/**
 * Interface that allow the map render to get the infomation it needs
 * @author Bilal Hussain
 */
public interface IMapRendererParent {
	
	boolean isMouseMoving();
	public int getDrawX();
	public int getDrawY();
	
}
