package editor.prototype;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import util.Logf;
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
import config.assets.AssetStore;
import config.assets.AssetsLocations;
import config.xml.MapSettings;

import editor.map.EditorMapPanel;
import editor.map.others.OthersMap;
import editor.map.others.OthersUnit;
import editor.spritesheet.MutableSprite;
import editor.util.Prefs;
import editor.util.Resources;
import engine.items.MeleeWeapon;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * Editor for weapons
 * @category Protype 
 * @author Bilal Hussain
 */
public class WeaponsEditor extends AbstactMapEditor {
	private static final Logger log = Logger.getLogger(WeaponsEditor.class);
	private static final long serialVersionUID = -7965140392208111973L;

	// Unit
	private IMutableMapUnit mapUnit;
	private OthersUnit guiUnit;
	
	// Weapons
	private IWeapon weapon;
	private WeaponTypes currentType;
	private Collection<Location> attackRange = new ArrayList<Location>(1);

	// Infopanel controls
	private JLabel     infoIcon;
	private JTextField infoName;
	private JComboBox  infoType;
	private JSpinner   infoStrength;
	private JSpinner   infoRange;
	private JSpinner   infoInnerRange;
	private JLabel     infoRangeL;
	private JLabel     infoInnerRangeL;
	
	
	public WeaponsEditor(IWeaponListener w) {
		super("Weapons Editor", "Weapons", 11, 11);

		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");		
		
		String path = "panels/unitSprites.png";
		
			SpriteSheet ss = new SpriteSheet(Resources.getImage(path), 
					Resources.getFileAsStream("panels/unitSprites.xml"));
			OthersUnit.setSpriteSheet(ss);
		
		Unit u  = new Unit();
		u.setWeapon(new MeleeWeapon());
		SpriteSheetData ui = new SpriteSheetData();
		ui.setSpriteSheetLocation(path);
		u.setImageData(path, ui);
		
		Location l = new Location(5,5);
		mapUnit = new MapUnit(u, l, new MapPlayer());
		
		guiUnit = new OthersUnit(l.x, l.y, u);
		guiUnit.setMapUnit(mapUnit);
		
		map.getGuiField()[5][5].setUnit(guiUnit);
		map.setUnitAt(l, guiUnit);
		
		IWeapon ww= new Spear(10,3);
		ww.setName("New Weapon");
		setWeapon(ww);
	}

	@Override
	protected void createMap() {
		MapSettings m = MapSettings.defaults();
		m.tileDiagonal = 40;
		map = new OthersMap(mapWidth,mapHeight, m);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),m);
	} 
	
	Collection<Location> getAttackRange(){
		assert weapon   != null;
		assert guiUnit  != null;
		assert mapWidth  > 0;
		assert mapHeight > 0;
		return weapon.getAttackRange(guiUnit.getLocation(), mapWidth, mapHeight);
	}
	
	// Displays the attack range.
	void showAttackRange(){
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.NONE);
		}
		attackRange = getAttackRange();
		Logf.trace(log,"attack range %s of %s +", attackRange ,weapon);
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.ATTACK_RANGE);
		}
		editorMapPanel.repaintMap();
	}
	
	private void changeStength(int intValue) {
		weapon.setStrength(intValue);
	}
	
	private void changeRange(int intValue) {
		weapon.setRange(intValue);
		showAttackRange();
	}

	private void changeInnerRange(int intValue) {
		assert currentType == WeaponTypes.RANGED;
		assert weapon instanceof RangedWeapon;
		((RangedWeapon) weapon).setInnerRange(intValue);
		showAttackRange();
	}
	
	private void changeType(WeaponTypes t) {
		if (t == currentType) return;
		currentType = t;
		weapon = t.newWeapon(this);
		showAttackRange();
	}
	
	public IWeapon getWeapon() {
		return weapon;
	}

	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
		this.mapUnit.setWeapon(weapon);
		if (weapon instanceof RangedWeapon){
			currentType = WeaponTypes.RANGED;
			WeaponTypes.RANGED.updateEditor(this, weapon);
		}else if (weapon instanceof MeleeWeapon){
			currentType = WeaponTypes.MELEE;
			WeaponTypes.MELEE.updateEditor(this, weapon);
		}else if (weapon instanceof Spear){
			currentType = WeaponTypes.SPEAR;
			WeaponTypes.SPEAR.updateEditor(this, weapon);
		}
		showAttackRange();
	}
	
	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("Icon:"), "gap 4");
		p.add((infoIcon = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Name:"), "gap 4");
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New Weapon");
		
		infoType = new JComboBox(WeaponTypes.values());
		infoType.setEditable(false);
		p.add(new JLabel("Type:"), "gap 4");
		infoType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				WeaponTypes t= (WeaponTypes) e.getItem();
				changeType(t);
			}
		});
		p.add(infoType, "span, growx");

		
		p.add(new JLabel("Strength:"), "gap 4");
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		
		p.add(infoStrength, "alignx leading, span, wrap");
		
		p.add((infoRangeL = new JLabel("Range:")), "gap 4");
		infoRange = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
		
		infoRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeRange(((Number)infoRange.getValue()).intValue());
			}
		});
		p.add(infoRange, "alignx leading, span, wrap");

		
		p.add((infoInnerRangeL = new JLabel("Inner Range:")), "gap 4");
		infoInnerRange = new JSpinner(new SpinnerNumberModel(0, 0, 8, 1));
		infoInnerRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int inner = ((Number)infoInnerRange.getValue()).intValue();
				int outer = ((Number)infoRange.getValue()).intValue();
				if (inner >= outer){
					inner  = outer-1;
					infoInnerRange.setValue(inner);
					return;
				}
				changeInnerRange(inner);
			}

		});
		p.add(infoInnerRange, "alignx leading, span, wrap");
		
		return p;
	}


	@Override
	public void select(List<MutableSprite> selection) {
		super.select(selection);
		if (currentIconImage == null) return;
		infoIcon.setIcon(new ImageIcon(currentIconImage.getImage()));
		weapon.setImageRef(currentIconImage.getName());
	}
	
	@Override
	protected void onQuit() {
		super.onQuit();
	}

	// The current Weapon type. Also has methods for updating the editor and creating a new weapon.
	static enum WeaponTypes {
		MELEE("Melee") {
			@Override
			IWeapon newWeapon(WeaponsEditor we) {
				MeleeWeapon w = new MeleeWeapon();
				w.setName(we.infoName.getText());
				
				we.infoRange.setVisible(false);
				we.infoRangeL.setVisible(false);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
				return w;
			}

			@Override
			void updateEditor(WeaponsEditor we, IWeapon w) {
				updateCommon(we, w);
				we.infoRange.setVisible(false);
				we.infoRangeL.setVisible(false);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
			}
		},
		
		RANGED("Ranged") {
			@Override
			IWeapon newWeapon(WeaponsEditor we) {
				RangedWeapon w = new RangedWeapon();
				w.setName(we.infoName.getText());
				
				int range = ((Number) we.infoRange.getValue()).intValue();
				w.setRange(range);
				int inner = ((Number) we.infoInnerRange.getValue()).intValue();
				w.setInnerRange(inner < range ? inner : range-1);
				
				we.infoInnerRange.setVisible(true);
				we.infoInnerRangeL.setVisible(true);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
				
				return w;
			}

			@Override
			void updateEditor(WeaponsEditor we, IWeapon w) {
				updateCommon(we, w);
				RangedWeapon ww = (RangedWeapon) w;
				we.infoInnerRange.setValue(ww.getInnerRange());
				we.infoInnerRange.setVisible(true);
				we.infoInnerRangeL.setVisible(true);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
			}
		},
		
		SPEAR("Spear") {
			@Override
			IWeapon newWeapon(WeaponsEditor we) {
				Spear w = new Spear();
				w.setName(we.infoName.getText());
				
				int range = ((Number) we.infoRange.getValue()).intValue();
				w.setRange(range);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
				return w;
			}

			@Override
			void updateEditor(WeaponsEditor we, IWeapon w) {
				updateCommon(we, w);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
			}
		};

		private final String name;

		@Override
		public String toString() {
			return name;
		}

		public void updateCommon(WeaponsEditor we, IWeapon w){
			we.infoRange.setValue(w.getRange());
			we.infoName.setText(w.getName());
			we.infoStrength.setValue(w.getStrength());
			ItemListener il = we.infoType.getItemListeners()[0];
			we.infoType.removeItemListener(il);
			we.infoType.setSelectedItem(this);
			we.infoType.addItemListener(il);
		}
		
		abstract IWeapon newWeapon(WeaponsEditor we);
		abstract void updateEditor(WeaponsEditor we, IWeapon w);

		private WeaponTypes(String name) {
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new WeaponsEditor(new IWeaponListener() {
			@Override
			public void editingFinished(IWeapon weapon) {
			}
		}).setVisible(true);
	}

}
