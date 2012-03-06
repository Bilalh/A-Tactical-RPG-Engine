package editor.map;

import util.Args;

import common.Location;
import common.assets.UnitPlacement;
import common.enums.Orientation;
import common.gui.ResourceManager;

import config.Config;
import config.xml.SavedMap;
import config.xml.TileImageData;
import editor.spritesheet.MutableSprite;
import editor.util.Resources;
import engine.map.BasicMap;
import engine.map.Tile;

/**
 * Stores the map and keeps the model and gui map in sync.
 * @author Bilal Hussain
 */
public class EditorMap extends BasicMap {

	protected EditorIsoTile[][] guiField;
	protected EditorTile[][] editorField;

	protected EditorSpriteSheet tiles;
	protected EditorSpriteSheet textures;
	
	public EditorMap(String name) {
		this.loadMap(name);
	}

	@Override
	public void loadMap(String name) {
		super.loadMap(name);
		loadEditorSettings();
	}

	private void loadEditorSettings() {
		ResourceManager.instance().loadTileSheetFromResources(getTileSheetLocation());
		ResourceManager.instance().loadTextureSheetFromResources(getTexturesLocation());
		
		guiField    = new EditorIsoTile[width][height];
		editorField = new EditorTile[width][height];
		tiles    = new EditorSpriteSheet(ResourceManager.instance().getCurrentTileSheet(),false);
		textures = new EditorSpriteSheet(ResourceManager.instance().getCurrentTextureSheet(),true);
		
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				TileImageData d = getTileImageData(i, j);
				assert d !=null :  String.format("TileImageData not found (%s,%s) %s",i,j, field[i][j]);
				editorField[i][j] = new EditorTile(field[i][j]);
				field[i][j]       = editorField[i][j];
				guiField[i][j]    = new EditorIsoTile(editorField[i][j].getOrientation(),
						editorField[i][j].getStartingHeight(),
						editorField[i][j].getEndHeight(), i, j,
						tiles.getSprite(d.getLocation()), 
						d.getType(),mapSettings,
						editorField[i][j].getLeftWallName(),
						editorField[i][j].getRightWallName());
			}
		}
		field = editorField;
		tileMapping = new MutableTileMapping(tileMapping);
	}

	public void setTileSprite(Location p, MutableSprite sprite) {
		editorField[p.x][p.y].setType(sprite.getName());
		guiField[p.x][p.y].setSprite(sprite);		
	}
	
	public void setHeight(Location p, int height){
		editorField[p.x][p.y].setStartHeight(height);
		editorField[p.x][p.y].setEndHeight(height);
		guiField[p.x][p.y].setHeight(height);
		guiField[p.x][p.y].invaildate(getMapSettings());
	}
	
	public void setOrientation(Location p, Orientation o) {
		editorField[p.x][p.y].setOrientation(o);
		guiField[p.x][p.y].setOrientation(o);
		guiField[p.x][p.y].invaildate(getMapSettings());
	}
	
	public EditorIsoTile[][] getGuiField() {
		return guiField;
	}

	public EditorSpriteSheet getTileset() {
		return tiles;
	}

	public EditorSpriteSheet getTextures() {
		return textures;
	}

	public UnitPlacement getEnemies() {
		return enemies;
	}

}
