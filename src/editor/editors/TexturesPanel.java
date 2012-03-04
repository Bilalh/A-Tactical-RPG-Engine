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
 * Editor for textures
 * @author Bilal Hussain
 */
public class TexturesPanel extends AbstractSpriteSheetOrganiser {
	static final Logger log = Logger.getLogger(TexturesPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	public TexturesPanel(Editor editor){
		super(editor, new BorderLayout());
		showAnimations = false;
		spriteSheetHelpString = "The texures should be square. The width and the height should be a power of 2";
	}

	@Override
	protected UnitImages defaultImages() {
		return Config.loadPreference("images/textures/default-animations.xml");
	}

	@Override
	protected String infoPanelTitle() {
		return "Textures";
	}
	
	@Override
	protected String spriteSheetBasePath() {
		return "images/textures/";
	}
	
}
