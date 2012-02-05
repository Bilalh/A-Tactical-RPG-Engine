package editor.map;

import engine.map.IMutableMapUnit;
import engine.map.Tile;

/**
 * @author Bilal Hussain
 */
public class EditorTile extends Tile {

	public EditorTile(Tile t) {
		super(t.getStartHeight(), t.getEndHeight(), t.getType());
	}

	/** @category Generated */
	public void setCurrent(IMutableMapUnit current) {
		this.current = current;
	}

	/** @category Generated */
	public void setStartHeight(int startHeight) {
		this.startHeight = startHeight;
	}

	/** @category Generated */
	public void setEndHeight(int endHeight) {
		this.endHeight = endHeight;
	}

	/** @category Generated */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/** @category Generated */
	public void setType(String type) {
		this.type = type;
	}

}
