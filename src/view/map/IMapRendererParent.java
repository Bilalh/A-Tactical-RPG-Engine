package view.map;


import view.AnimatedUnit;

/**
 * @author Bilal Hussain
 */
public interface IMapRendererParent {
	
	boolean isMouseMoving();
	AnimatedUnit getUnit(GuiTile tile);
	
	public int getDrawX();
	public int getDrawY();
}
