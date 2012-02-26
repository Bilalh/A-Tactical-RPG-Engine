package editor.map.others;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import view.units.AnimatedUnit;

import common.Location;
import common.enums.Orientation;
import common.gui.ResourceManager;

import config.xml.MapData;
import config.xml.MapSettings;
import config.xml.TileImageData;
import config.xml.TileMapping;

import editor.map.EditorIsoTile;
import editor.util.Resources;
import engine.map.BasicMap;
import engine.map.Tile;

/**
 * Provides a map of a specifed size, where the tiles have a height of 1 and the default tile image. 
 * @author Bilal Hussain
 */
public class OthersMap extends BasicMap {

	protected EditorIsoTile[][] guiField;
	protected BufferedImage tileImage;
	protected MapSettings settings;
	
	public OthersMap(int width, int height, MapSettings settings) {
		try {
			tileImage = Resources.getImage("defaults/tile.png");
			OthersIsoTile.setTileImage(tileImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.width    = width;
		this.height   = height;
		this.settings = settings;
		loadMap("none");
	}

	@Override
	public void loadMap(String name) {
		
		field = new Tile[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				field[i][j] = new Tile(1, 1, "none", Orientation.UP_TO_EAST);
			}
		}
		mapSettings = MapSettings.defaults();
		tileMapping = new TileMapping("none", new HashMap<String, TileImageData>());
		data = new MapData("none");
		loadOtherSettings();
	}

	private void loadOtherSettings() {
		guiField = new EditorIsoTile[width][height];

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				guiField[i][j] = new OthersIsoTile(field[i][j].getOrientation(),
						field[i][j].getStartHeight(),
						field[i][j].getEndHeight(), i, j, settings);
			}
		}
	}

	public EditorIsoTile[][] getGuiField() {
		return guiField;
	}

	public EditorIsoTile getGuiTile(Location l) {
		return guiField[l.x][l.y];
	}
	
	public void setUnitAt(Location l, AnimatedUnit au){
		getGuiTile(l).setUnit(au);
		au.setLocation(l);
	}
	
}
