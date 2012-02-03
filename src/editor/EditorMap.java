package editor;

import common.gui.ResourceManager;

import config.xml.TileImageData;
import view.map.GuiTile;
import engine.map.BasicMap;

/**
 * @author Bilal Hussain
 */
public class EditorMap extends BasicMap {

	protected EditorTile[][] guiField;

	public EditorMap(String name) {
		this.loadMap(name);
	}

	@Override
	public void loadMap(String name) {
		super.loadMap(name);
		guiField = new EditorTile[width][height];

		ResourceManager.instance().loadSpriteSheetFromResources(getTileSheetLocation());

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				TileImageData d = getTileImageData(i, j);
				guiField[i][j] = new EditorTile(GuiTile.Orientation.UP_TO_EAST,
						field[i][j].getStartHeight(),
						field[i][j].getEndHeight(), i, j,
						d.getLocation(), d.getType());
			}
		}

	}

	
	public EditorTile getGuiTile(int x, int y) {
		return guiField[x][y];
	}

	/** @category Generated */
	public EditorTile[][] getGuiField() {
		return guiField;
	}

}
