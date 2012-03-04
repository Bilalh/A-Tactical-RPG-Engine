package editor.editors;

import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.log4j.Logger;

import net.miginfocom.layout.CC;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import common.assets.Units;
import common.gui.ResourceManager;
import common.interfaces.IWeapon;
import config.Config;

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
public class UnitsImagesPanel extends AbstractSpriteSheetOrganiser {
	static final Logger log = Logger.getLogger(UnitsImagesPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	public UnitsImagesPanel(Editor editor){
		super(editor, new BorderLayout());
		validationForUnits = true;
	}

	@Override
	protected SpriteSheetData defaultImages() {
		return Config.loadPreference("images/characters/default-animations.xml");
	}

	@Override
	protected String infoPanelTitle() {
		return "Sprites";
	}
	
	@Override
	protected String spriteSheetBasePath() {
		return "images/characters/";
	}
	
}
