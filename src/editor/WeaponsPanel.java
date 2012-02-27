package editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.log4j.Logger;

import com.javarichclient.icon.tango.actions.*;

import util.Logf;
import view.map.IsoTile.TileState;

import common.Location;
import common.gui.ResourceManager;
import common.interfaces.IWeapon;
import common.spritesheet.SpriteSheet;

import config.Config;
import config.xml.MapSettings;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import editor.WeaponsEditor.WeaponTypes;
import editor.map.EditorMapPanel;
import editor.map.others.OthersMap;
import editor.map.others.OthersUnit;
import editor.spritesheet.MutableSprite;
import editor.spritesheet.ReorderableJList.ReorderableListCellRenderer;
import editor.ui.TButton;
import editor.util.IWeaponListener;
import editor.util.Resources;
import engine.items.MeleeWeapon;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public class WeaponsPanel extends AbstactMapEditorPanel {
	private static final long serialVersionUID = -4663464965056461703L;
	private static final Logger log = Logger.getLogger(WeaponsPanel.class);

	// Unit
	private IMutableMapUnit mapUnit;
	private OthersUnit guiUnit;
	
	// Weapons
	private IWeapon currentWeapon;
	private WeaponTypes currentType;
	private Collection<Location> attackRange = new ArrayList<Location>(1);

	private JList weaponslist;
	private DefaultListModel weaponslistModel;
	
	// Infopanel controls
	private JLabel     infoIcon;
	private JTextField infoName;
	private JComboBox  infoType;
	private JSpinner   infoStrength;
	private JSpinner   infoRange;
	private JSpinner   infoInnerRange;
	private JLabel     infoRangeL;
	private JLabel     infoInnerRangeL;
	
	public WeaponsPanel(){
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
		
		setWeapon((IWeapon) weaponslistModel.getElementAt(0));
	}
	
	@Override
	protected void createMap() {
		MapSettings m = MapSettings.defaults();
		m.tileDiagonal = 45;
		map = new OthersMap(mapWidth,mapHeight, m);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),m);
	} 
	
	Collection<Location> getAttackRange(){
		assert currentWeapon   != null;
		assert guiUnit  != null;
		assert mapWidth  > 0;
		assert mapHeight > 0;
		return currentWeapon.getAttackRange(guiUnit.getLocation(), mapWidth, mapHeight);
	}
	
	// Displays the attack range.
	void showAttackRange(){
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.NONE);
		}
		attackRange = getAttackRange();
		Logf.trace(log,"attack range %s of %s +", attackRange ,currentWeapon);
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.ATTACK_RANGE);
		}
		editorMapPanel.repaintMap();
	}
	
	private void changeStength(int intValue) {
		currentWeapon.setStrength(intValue);
	}
	
	private void changeRange(int intValue) {
		currentWeapon.setRange(intValue);
		showAttackRange();
	}

	private void changeInnerRange(int intValue) {
		assert currentType == WeaponTypes.RANGED;
		assert currentWeapon instanceof RangedWeapon;
		((RangedWeapon) currentWeapon).setInnerRange(intValue);
		showAttackRange();
	}
	
	private void changeType(WeaponTypes t) {
		if (t == currentType) return;
		currentType = t;
		currentWeapon = t.newWeapon(this);
		showAttackRange();
	}
	
	private void changeName(){
		currentWeapon.setName(infoName.getText());
		weaponslist.repaint();
	}
	
	/** @category unused**/
	public IWeapon getWeapon() {
		return currentWeapon;
	}

	public void setWeapon(IWeapon weapon) {
		this.currentWeapon = weapon;
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
	public void select(List<MutableSprite> selection) {
		super.select(selection);
		if (currentIconImage == null) return;
		infoIcon.setIcon(new ImageIcon(currentIconImage.getImage()));
		currentWeapon.setImageRef(currentIconImage.getName());
		weaponslist.repaint();
	}

	private LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4).align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);
		return new MigLayout(layC, colC, rowC);
	}

	
	@Override
	protected  JComponent createLeftPane(){
		IWeapon ww= new Spear(10,3);
		ww.setName("New Weapon");
		weaponslistModel = new DefaultListModel();
		weaponslist = new JList(weaponslistModel);
		weaponslist.setCellRenderer(new WeaponsListRenderer());
		weaponslistModel.addElement(ww);
		
		JScrollPane slist = new JScrollPane(weaponslist);
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buutons  =new JPanel();
		buutons.add(new TButton(new DeleteAction()));
		buutons.add(new TButton(new AddAction()));

		p.add(buutons, BorderLayout.SOUTH);
		return p;
	}
	
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public DeleteAction() {
			putValue(NAME, "Delete the selected weapon");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON,new ListRemoveIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private class AddAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public AddAction() {
			putValue(NAME, "Add a new Weapon");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON,new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		p.add(new JLabel("Icon:"), "gap 4");
		p.add((infoIcon = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Name:"), "gap 4");
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New Weapon");
		infoName.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changeName();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				changeName();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				changeName();
			}
		});
		
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
		
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add((infoRangeL = new JLabel("Range:")), "gap 4");
		infoRange = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
		
		infoRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeRange(((Number)infoRange.getValue()).intValue());
			}
		});
		p.add(infoRange,new CC().alignX("leading").maxWidth("70").wrap());

		
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
		p.add(infoInnerRange,  new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add(new JPanel());
		return p;
	}
	
	// The current Weapon type. Also has methods for updating the editor and creating a new weapon.
	static enum WeaponTypes {
		MELEE("Melee") {
			@Override
			IWeapon newWeapon(WeaponsPanel we) {
				MeleeWeapon w = new MeleeWeapon();
				w.setName(we.infoName.getText());
				
				we.infoRange.setVisible(false);
				we.infoRangeL.setVisible(false);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
				return w;
			}

			@Override
			void updateEditor(WeaponsPanel we, IWeapon w) {
				updateCommon(we, w);
				we.infoRange.setVisible(false);
				we.infoRangeL.setVisible(false);
				we.infoInnerRange.setVisible(false);
				we.infoInnerRangeL.setVisible(false);
			}
		},
		
		RANGED("Ranged") {
			@Override
			IWeapon newWeapon(WeaponsPanel we) {
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
			void updateEditor(WeaponsPanel we, IWeapon w) {
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
			IWeapon newWeapon(WeaponsPanel we) {
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
			void updateEditor(WeaponsPanel we, IWeapon w) {
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

		public void updateCommon(WeaponsPanel we, IWeapon w){
			we.infoRange.setValue(w.getRange());
			we.infoName.setText(w.getName());
			we.infoStrength.setValue(w.getStrength());
			ItemListener il = we.infoType.getItemListeners()[0];
			we.infoType.removeItemListener(il);
			we.infoType.setSelectedItem(this);
			we.infoType.addItemListener(il);
		}
		
		abstract IWeapon newWeapon(WeaponsPanel we);
		abstract void updateEditor(WeaponsPanel we, IWeapon w);

		private WeaponTypes(String name) {
			this.name = name;
		}
	}
	
	class WeaponsListRenderer extends  DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			IWeapon w= (IWeapon) value;
			label.setText(w.getName());
			label.setIcon(infoIcon.getIcon());
			return label;
		}
	}
	
}
