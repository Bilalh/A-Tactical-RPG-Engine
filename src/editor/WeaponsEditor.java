package editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.units.AnimatedUnit;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import common.Location;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import common.spritesheet.SpriteSheet;
import config.Config;

import editor.map.others.AbstactMapEditor;
import editor.map.others.OthersUnit;
import editor.util.Resources;
import engine.assets.AssertStore;
import engine.assets.AssetsLocations;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public class WeaponsEditor extends AbstactMapEditor {
	private static final long serialVersionUID = -7965140392208111973L;

	// Infopanel controls
	private JLabel infoLocation;
	private JTextField infoType;
	private JComboBox infoOrientation;
	private JSpinner infoHeight;

	private IMutableMapUnit mapUnit;
	private OthersUnit guiUnit;
	
	public WeaponsEditor() {
		super("Weapons Editor", "Weapons", 11, 11);
		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");		
		
		
		String path = "defaults/Boy.xml";
		
		try {
			SpriteSheet ss = new SpriteSheet(Resources.getImage("defaults/Boy.png"), 
					Resources.getFileAsStream(path));
			OthersUnit.setSpriteSheet(ss);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Unit u  = new Unit();
		UnitImages ui = new UnitImages();
		ui.setSpriteSheetLocation(path);
		u.setImageData(path, ui);
		
		
		
		Location l = new Location(5,5);
		mapUnit = new MapUnit(u, l, new MapPlayer());
		
		guiUnit = new OthersUnit(l.x, l.y, u);
		guiUnit.setMapUnit(mapUnit);
		
		map.getGuiField()[5][5].setUnit(guiUnit);
		map.setUnitAt(l, guiUnit);
	}

	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		infoOrientation = new JComboBox(Orientation.values());
		infoHeight = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));

		p.add(new JLabel("General"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoLocation = new JLabel("        ")), "span, growx");

		p.add(new JLabel("Type:"), "gap 4");
		p.add((infoType = new JTextField(15)), "span, growx");

		infoOrientation.setEditable(false);
		p.add(new JLabel("Orientation:"), "gap 4");
		infoOrientation.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO
			}
		});
		p.add(infoOrientation, "span, growx");

		p.add(new JLabel("Height:"), "gap 4");
		infoHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO
			}
		});
		p.add(infoHeight, "alignx leading, span, wrap");

		return p;
	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new WeaponsEditor().setVisible(true);
	}

}
