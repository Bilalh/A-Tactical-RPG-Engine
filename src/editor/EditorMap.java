package editor;

import java.util.HashMap;

import common.Location;
import common.gui.ResourceManager;

import config.xml.TileImageData;
import util.Args;
import view.map.GuiTile;
import editor.spritesheet.MutableSprite;
import engine.map.BasicMap;

/**
 * @author Bilal Hussain
 */
public class EditorMap extends BasicMap {

	protected EditorTile[][] guiField;
	protected EditorSpriteSheet spriteSheet;

	public EditorMap(String name) {
		this.loadMap(name);
		tileMapping = new MutableTileMapping(tileMapping);
	}

	@Override
	public void loadMap(String name) {
		super.loadMap(name);
		guiField = new EditorTile[width][height];

		ResourceManager.instance().loadSpriteSheetFromResources(getTileSheetLocation());
		spriteSheet = new EditorSpriteSheet(ResourceManager.instance().getCurrentTileSheet());

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				TileImageData d = getTileImageData(i, j);
				guiField[i][j] = new EditorTile(GuiTile.Orientation.UP_TO_EAST,
						field[i][j].getStartHeight(),
						field[i][j].getEndHeight(), i, j,
						spriteSheet.getSprite(d.getLocation()), d.getType());
			}
		}

	}

	public void setSprite(Location p, MutableSprite sprite) {
		field[p.x][p.y].setType(sprite.getName());
		guiField[p.x][p.y].setSprite(sprite);		
	}
	
	/** @category unused**/
	public EditorTile getGuiTile(int x, int y) {
		return guiField[x][y];
	}

	/** @category unused**/
	public MutableSprite getSpriteAt(int x, int y) {
		Args.assetNonNull(guiField,spriteSheet);
		return spriteSheet.getSpriteAt(x, y);
	}
	
	/** @category Generated */
	public EditorTile[][] getGuiField() {
		return guiField;
	}

	
	/** @category Generated */
	public EditorSpriteSheet getSpriteSheet() {
		return spriteSheet;
	}


}
