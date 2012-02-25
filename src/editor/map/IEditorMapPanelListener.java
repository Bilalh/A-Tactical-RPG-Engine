package editor.map;

import java.util.ArrayList;

import view.map.interfaces.IMapRendererParent;
import editor.MapEditor;

/**
 * @author Bilal Hussain
 */
public interface IEditorMapPanelListener extends IMapRendererParent {

	MapState getEditorState();
	
	void tileClicked(EditorIsoTile tile);
	
	void tileEntered(EditorIsoTile tile);

	void tilesSelected(ArrayList<EditorIsoTile> tiles, boolean shiftDown);

}