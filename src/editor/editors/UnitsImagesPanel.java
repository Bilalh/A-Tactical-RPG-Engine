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

import editor.Editor;
import editor.editors.UnitsPanel.*;
import editor.spritesheet.*;
import engine.assets.Units;
import engine.unit.IMutableUnit;
import engine.unit.Unit;

/**
 * Editor for units images
 * @author Bilal Hussain
 */
public class UnitsImagesPanel extends AbstractSpriteSheetOrganiser {
	static final Logger log = Logger.getLogger(UnitsImagesPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	public UnitsImagesPanel(Editor editor){
		super(editor, new BorderLayout());
	}
	
}
