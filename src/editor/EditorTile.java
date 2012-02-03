package editor;

import common.enums.ImageType;

import view.map.GuiTile;

/**
 * @author Bilal Hussain
 */
public class EditorTile extends GuiTile {

	private String ref;
	
	/** @category Generated */
	public EditorTile(Orientation orientation, float startHeight, float endHeight, 
			int x, int y, String ref, ImageType type) {
		super(orientation, startHeight, endHeight, x, y, ref, type);
		this.ref = ref;
	}

	
}
