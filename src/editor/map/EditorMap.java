package editor.map;

import util.Args;

import common.Location;
import common.enums.Orientation;
import common.gui.ResourceManager;

import config.Config;
import config.xml.SavedMap;
import config.xml.TileImageData;
import editor.Resources;
import editor.spritesheet.MutableSprite;
import engine.map.BasicMap;

/**
 * Stores the map and keeps the model and gui map in sync.
 * @author Bilal Hussain
 */
public class EditorMap extends BasicMap {

	protected EditorIsoTile[][] guiField;
	protected EditorSpriteSheet spriteSheet;
	protected EditorTile[][] editorField;
	
	public EditorMap(){
		//FIXME copy defaults 
		this.loadMap("maps/default.xml");
	}
	
	public EditorMap(String name) {
		this.loadMap(name);
	}

	@Override
	public void loadMap(String name) {
		super.loadMap(name);
		loadEditorSettings();
	}

	private void loadEditorSettings() {
		guiField    = new EditorIsoTile[width][height];
		editorField = new EditorTile[width][height];
		ResourceManager.instance().loadSpriteSheetFromResources(getTileSheetLocation());
		spriteSheet = new EditorSpriteSheet(ResourceManager.instance().getCurrentTileSheet());

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				TileImageData d = getTileImageData(i, j);
				assert d !=null :  String.format("TileImageData not found (%s,%s) %s",i,j, field[i][j]);
				editorField[i][j] = new EditorTile(field[i][j]);				
				guiField[i][j]    = new EditorIsoTile(editorField[i][j].getOrientation(),
						editorField[i][j].getStartHeight(),
						editorField[i][j].getEndHeight(), i, j,
						spriteSheet.getSprite(d.getLocation()), d.getType());
			}
		}
		field = editorField;
		tileMapping = new MutableTileMapping(tileMapping);
	}

	public void setSprite(Location p, MutableSprite sprite) {
		editorField[p.x][p.y].setType(sprite.getName());
		guiField[p.x][p.y].setSprite(sprite);		
	}
	
	public void setHeight(Location p, int height){
		editorField[p.x][p.y].setStartHeight(height);
		editorField[p.x][p.y].setEndHeight(height);
		guiField[p.x][p.y].setHeight(height);
	}
	
	public void setOrientation(Location p, Orientation o) {
		editorField[p.x][p.y].setOrientation(o);
		guiField[p.x][p.y].setOrientation(o);
	}
	
	/** @category unused**/
	public EditorIsoTile getGuiTile(int x, int y) {
		return guiField[x][y];
	}

	/** @category unused**/
	public MutableSprite getSpriteAt(int x, int y) {
		Args.assetNonNull(guiField,spriteSheet);
		return spriteSheet.getSpriteAt(x, y);
	}
	
	/** @category Generated */
	public EditorIsoTile[][] getGuiField() {
		return guiField;
	}

	
	/** @category Generated */
	public EditorSpriteSheet getSpriteSheet() {
		return spriteSheet;
	}

}
