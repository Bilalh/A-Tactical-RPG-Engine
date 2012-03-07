package editor.editors;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.log4j.Logger;

import net.miginfocom.layout.CC;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import common.gui.ResourceManager;
import common.interfaces.IWeapon;
import config.Config;
import config.assets.Units;

import editor.Editor;
import editor.editors.UnitsPanel.*;
import editor.spritesheet.*;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * Editor for units images
 * @author Bilal Hussain
 */
public class TilesetPanel extends AbstractSpriteSheetOrganiser {
	static final Logger log = Logger.getLogger(TilesetPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	public TilesetPanel(Editor editor){
		super(editor, new BorderLayout());
		
		showAnimations  = false;
		makeTileMapping = true;
		
		spriteSheetHelpString = "A tile's height should be half its width (isometric)";
	}

	@Override
	protected SpriteSheetData defaultImages() {
		return Config.loadPreference("images/tilesets/default-animations.xml");
	}

	@Override
	protected String infoPanelTitle() {
		return "Tiles";
	}
	
	@Override
	protected String spriteSheetBasePath() {
		return "images/tilesets/";
	}
	
}
