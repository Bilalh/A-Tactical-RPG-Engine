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

import editor.Editor;
import editor.editors.UnitsPanel.*;
import editor.spritesheet.*;
import engine.assets.Units;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * Editor for units images
 * @author Bilal Hussain
 */
public class TilesetPanel extends AbstractSpriteSheetOrganiser {
	static final Logger log = Logger.getLogger(TilesetPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	public TilesetPanel(Editor editor){
		super(editor, new BorderLayout());
		showAnimations = false;
	}

	@Override
	protected UnitImages defaultImages() {
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
