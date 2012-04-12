package editor.map;

import common.enums.Orientation;

import engine.map.Tile;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class EditorTile extends Tile {

	public EditorTile(Tile t) {
		super(t);
	}


	public void setCurrent(IMutableMapUnit current) {
		this.current = current;
	}


	public void setStartingHeight(int startHeight) {
		this.startHeight = startHeight;
	}


	public void setEndHeight(int endHeight) {
		this.endHeight = endHeight;
	}


	public void setCost(int cost) {
		this.cost = cost;
	}


	public void setType(String type) {
		this.type = type;
	}

	public void setOrientation(Orientation o) {
		this.orientation =o;
	}

}
