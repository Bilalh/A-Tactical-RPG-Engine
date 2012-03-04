package editor.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import config.Config;
import config.xml.SavedMap;

import editor.Editor;
import editor.editors.AbstractSpriteSheetOrganiser.AddAction;
import editor.editors.AbstractSpriteSheetOrganiser.DeleteAction;
import engine.assets.Maps;
import engine.assets.Units;
import engine.assets.UnitsImages;
import engine.map.Map;
import engine.unit.IMutableUnit;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public class MapsPanel extends AbstractResourcesPanel<SavedMap, Maps> {
	private static final long serialVersionUID = -6885584804612641268L;
	private static final Logger log = Logger.getLogger(MapsPanel.class);

	public MapsPanel(Editor editor) {
		super(editor);
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method

	}

	@Override
	protected void setCurrentResource(SavedMap map) {

	}

	@Override
	protected SavedMap defaultResource() {
		return Config.loadPreference("maps/default.xml");
	}

	@Override
	protected String resourceDisplayName(SavedMap map){
		return map.getUuid().toString();
	}
	
	@Override
	protected String resourceName() {
		return "Map";
	}

	@Override
	protected void addToList() {
		
	}

	@Override
	protected Maps createAssetsInstance() {
		return new Maps();
	}

}
