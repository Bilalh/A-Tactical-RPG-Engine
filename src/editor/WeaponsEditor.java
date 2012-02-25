package editor;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import view.map.IsoTile.TileState;
import view.units.AnimatedUnit;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import common.Location;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import common.interfaces.IWeapon;
import common.spritesheet.SpriteSheet;
import config.Config;

import editor.map.others.AbstactMapEditor;
import editor.map.others.OthersUnit;
import editor.util.Prefs;
import editor.util.Resources;
import engine.assets.AssertStore;
import engine.assets.AssetsLocations;
import engine.items.MeleeWeapon;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * Editor for weapons
 * @author Bilal Hussain
 */
public class WeaponsEditor extends AbstactMapEditor {
	private static final Logger log = Logger.getLogger(WeaponsEditor.class);
	private static final long serialVersionUID = -7965140392208111973L;

	private IMutableMapUnit mapUnit;
	private OthersUnit guiUnit;
	
	private IWeapon weapon;
	private Collection<Location> attackRange = new ArrayList<Location>(1);

	
	// Infopanel controls
	private JLabel     infoIcon;
	private JTextField infoName;
	private JComboBox  infoType;
	private JSpinner   infoStrength;
	private JSpinner   infoRange;
	
	
	static enum WeaponTypes {
		RANGED("Ranged"),
		MELEE("Melee"),
		SPEAR("Spear");

		private final String name;

		@Override
		public String toString() {
			return name;
		}

		/** @category Generated */
		private WeaponTypes(String name) {
			this.name = name;
		}
	}
	
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
		
		weapon = new MeleeWeapon(10);
		u.setWeapon(weapon);
		
		Location l = new Location(5,5);
		mapUnit = new MapUnit(u, l, new MapPlayer());
		
		guiUnit = new OthersUnit(l.x, l.y, u);
		guiUnit.setMapUnit(mapUnit);
		
		map.getGuiField()[5][5].setUnit(guiUnit);
		map.setUnitAt(l, guiUnit);
		showAttackRange();
	}

	Collection<Location> getAttackRange(){
		return weapon.getAttackRange(guiUnit.getLocation(), mapWidth, mapHeight);
	}
	
	// Displays the attack range.
	void showAttackRange(){
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.NONE);
		}
		attackRange = getAttackRange();
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.ATTACK_RANGE);
		}		
	}
	
	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));


		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoIcon = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Name:"), "gap 4");
		p.add((infoName = new JTextField(15)), "span, growx");
		

		infoType = new JComboBox(WeaponTypes.values());
		infoType.setEditable(false);
		p.add(new JLabel("Type:"), "gap 4");
		infoType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO
			}
		});
		p.add(infoType, "span, growx");

		
		p.add(new JLabel("Strength:"), "gap 4");
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO
			}
		});
		
		p.add(infoStrength, "alignx leading, span, wrap");
		
		p.add(new JLabel("Range:"), "gap 4");
		infoRange = new JSpinner(new SpinnerNumberModel(1, 0, 9, 1));
		
		infoRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO
			}
		});
		p.add(infoRange, "alignx leading, span, wrap");

		return p;
	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new WeaponsEditor().setVisible(true);
	}

}
