package editor.map.others;

import java.awt.image.BufferedImage;

import common.enums.Orientation;
import editor.map.EditorIsoTile;

/**
 * Uses the default image as the tile's images 
 * @author Bilal Hussain
 */
public class OthersIsoTile extends EditorIsoTile {

	protected static BufferedImage allTiles = null;

	public OthersIsoTile(Orientation orientation, float startHeight, float endHeight, int x, int y) {
		super(orientation, startHeight, endHeight, x, y);
	}

	@Override
	protected BufferedImage getTileImage(int horizontal, int vertical) {
		assert allTiles != null;
		return allTiles;
	}

	public static void setTileImage(BufferedImage allTiles) {
		OthersIsoTile.allTiles = allTiles;
	}

}
