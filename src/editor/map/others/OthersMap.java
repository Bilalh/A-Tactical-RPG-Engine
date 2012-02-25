package editor.map.others;

import java.awt.image.BufferedImage;
import java.io.IOException;

import common.gui.ResourceManager;

import config.xml.TileImageData;

import editor.map.EditorIsoTile;
import editor.util.Resources;
import engine.map.BasicMap;

/**
 * @author Bilal Hussain
 */
public class OthersMap extends BasicMap {

	protected EditorIsoTile[][] guiField;
	protected BufferedImage tileImage;

	public OthersMap(String name) {
		try {
			tileImage = Resources.getImage("defaults/tile.png");
			OthersIsoTile.setTileImage(tileImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.loadMap(name);
	}

	@Override
	public void loadMap(String name) {
		super.loadMap(name);
		loadOtherSettings();
	}

	private void loadOtherSettings() {
		guiField = new EditorIsoTile[width][height];

		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				guiField[i][j] = new OthersIsoTile(field[i][j].getOrientation(),
						field[i][j].getStartHeight(),
						field[i][j].getEndHeight(), i, j);
			}
		}
	}

	public EditorIsoTile[][] getGuiField() {
		return guiField;
	}

}
