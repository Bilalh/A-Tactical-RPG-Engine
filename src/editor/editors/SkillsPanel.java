package editor.editors;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
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
import config.assets.Skills;
import config.assets.Weapons;
import config.xml.MapSettings;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import editor.Editor;
import editor.map.EditorIsoTile;
import editor.map.EditorMapPanel;
import editor.map.others.OthersMap;
import editor.map.others.OthersUnit;
import editor.spritesheet.MutableSprite;
import editor.spritesheet.ReorderableJList.ReorderableListCellRenderer;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import editor.util.EditorResources;
import engine.items.Around;
import engine.items.MeleeWeapon;
import engine.items.RangedWeapon;
import engine.items.Spear;
import engine.map.MapPlayer;
import engine.map.MapUnit;
import engine.map.interfaces.IMutableMapUnit;
import engine.skills.ISkill;
import engine.skills.RangedSkill;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * Editor for skills
 * @author Bilal Hussain
 */
public class SkillsPanel extends AbstactMapEditorPanel {
	private static final long serialVersionUID = -4663464965056461703L;
	private static final Logger log = Logger.getLogger(SkillsPanel.class);

	// Unit
	private IMutableMapUnit mapUnit;
	private OthersUnit guiUnit;

	// Weapons
	private Location currentLocation;
	private ISkill currentSkill;
	private SkillTypes currentType;
	private Collection<Location> attackArea   = new ArrayList<Location>(1);
	private Collection<Location> attackRange  = new ArrayList<Location>(1);

	private JList skillList;
	private DefaultListModel skillListModel;
	
	// Infopanel controls
	private JLabel     infoIcon;
	private JTextField infoName;
	private JComboBox  infoType;
	private JSpinner   infoStrength;
	private JSpinner   infoRange;
	private JSpinner   infoArea;
	private JLabel     infoRangeL;
	private JLabel     infoAreaL;
	
	private JComboBox  infoIncludeCaster;
	private JLabel     infoAbout;

	public SkillsPanel(){
		super("Skill", 11, 11);

		String path = "panels/unitSprites.xml";
		
			SpriteSheet ss = new SpriteSheet(EditorResources.getImage("panels/unitSprites.png"), 
					EditorResources.getFileAsStream(path));
			OthersUnit.setSpriteSheet(ss);
		
		Unit u  = new Unit();
		SpriteSheetData ui = new SpriteSheetData();
		ui.setSpriteSheetLocation("panels/unitSprites.png");
		u.setImageData(path, ui);
		u.setWeapon(new MeleeWeapon(1));
		
		currentLocation = new Location(5,5);
		mapUnit = new MapUnit(u, currentLocation, new MapPlayer());
		
		guiUnit = new OthersUnit(currentLocation.x, currentLocation.y, u);
		guiUnit.setMapUnit(mapUnit);
		
		map.getGuiField()[5][5].setUnit(guiUnit);
		map.setUnitAt(currentLocation, guiUnit);
		
		setCurrentSkill((ISkill) skillListModel.getElementAt(0));
		tilesetPanelWithHeader.setVisible(false);
	}
	
	@Override
	protected void createMap() {
		MapSettings m = MapSettings.defaults();
		m.tileDiagonal = 40;
		map = new OthersMap(mapWidth,mapHeight, m);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),m);
	} 
	
	Collection<Location> getAttackRange(){
		assert currentSkill   != null;
		assert guiUnit        != null;
		assert mapWidth  > 0;
		assert mapHeight > 0;
		return currentSkill.getAttackRange(guiUnit.getLocation(), mapWidth, mapHeight);
	}
	
	Collection<Location> getAttackArea(){
		assert currentSkill   != null;
		assert guiUnit        != null;
		assert mapWidth  > 0;
		assert mapHeight > 0;
		if (!attackRange.contains(currentLocation)){
			return Collections.emptySet();
		}
		return currentSkill.getArea(currentLocation, mapWidth, mapHeight);
	}
	
	// Displays the attack range.
	void showAttackRangeAndArea(){
		
		for (Location l : attackArea) {
			map.getGuiTile(l).setState(TileState.NONE);
		}
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.NONE);
		}
		
		attackRange = getAttackRange();
		Logf.trace(log,"attack range %s of %s +", attackRange ,currentSkill);
		for (Location l : attackRange) {
			map.getGuiTile(l).setState(TileState.ATTACK_RANGE);
		}
		
		attackArea = getAttackArea();
		Logf.trace(log,"attack area %s of %s +", attackArea ,currentSkill);
		for (Location l : attackArea) {
			map.getGuiTile(l).setState(TileState.ATTACK_AREA);
		}
		
		editorMapPanel.repaintMap();
	}
	
	private void changeStrength(int intValue) {
		currentSkill.setPower(intValue);
	}
	
	private void changeRange(int intValue) {
		assert currentSkill instanceof RangedSkill;
		((RangedSkill) currentSkill).setRange(intValue);
		showAttackRangeAndArea();
	}

	private void changeArea(int intValue) {
		assert currentType == SkillTypes.RANGED;
		assert currentSkill instanceof RangedSkill;
		((RangedSkill) currentSkill).setArea(intValue);
		showAttackRangeAndArea();
	}
	
	private void changeType(SkillTypes t) {
		if (t == currentType) return;
		currentType = t;
		int index =skillList.getSelectedIndex();
		currentSkill = t.newSkill(this);
		skillListModel.remove(index);
		skillListModel.add(index, currentSkill);
		skillList.setSelectedIndex(index);
		showAttackRangeAndArea();
	}
	
	private void changeName(){
		currentSkill.setName(infoName.getText());
		skillList.repaint();
	}
	
	private void changeIncludeCaster(boolean b) {
		currentSkill.setIncludeCaster(b);
		showAttackRangeAndArea();
	}

	public Skills getSkills(){
		Skills ws = new Skills();
		for (Object o: skillListModel.toArray()) {
			ws.put((ISkill) o);
		}
		return ws;
	}
	
	public void setSkills(Skills ws){
		ListSelectionListener lsl =  skillList.getListSelectionListeners()[0];
		skillList.removeListSelectionListener(lsl);
		skillListModel.clear();
		for (ISkill w : ws.values()) {
			skillListModel.addElement(w);
		}
		skillList.addListSelectionListener(lsl);
		skillList.setSelectedIndex(0);
	}
	
	public void setCurrentSkill(ISkill skill) {
		this.currentSkill = skill;
		
		ArrayList<ISkill> list = new ArrayList<ISkill>();
		this.mapUnit.setSkills(list);
		if (skill instanceof RangedSkill){
			currentType = SkillTypes.RANGED;
			SkillTypes.RANGED.updateEditor(this, skill);
		}
		showAttackRangeAndArea();
	}
	
	@Override
	public void select(List<MutableSprite> selection) {
		super.select(selection);
		if (currentIconImage == null) return;
		infoIcon.setIcon(new ImageIcon(currentIconImage.getImage()));
//		currentWeapon.setImageRef(currentIconImage.getName());
		skillList.repaint();
	}

	@Override
	public void tileClicked(EditorIsoTile tile) {
		map.getGuiTile(currentLocation).setSelected(false);
		tile.setSelected(true);
		currentLocation = tile.getLocation();
		showAttackRangeAndArea();
	}

	@Override
	public void panelSelected(Editor editor) {
		// TODO panelSelected method
		
	}
	
	private LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4).align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);
		return new MigLayout(layC, colC, rowC);
	}
	
	@Override
	protected  JComponent createLeftPane(){
		ISkill ww= new RangedSkill("New Skill", 10, 5, 2, true, false);
		skillListModel = new DefaultListModel();
		skillList = new JList(skillListModel);
		skillList.setCellRenderer(new SkillListRenderer());
		skillListModel.addElement(ww);
		skillList.setSelectedIndex(0);
		skillList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ISkill w =  (ISkill) skillList.getSelectedValue();
				if (w == null) return;
				setCurrentSkill(w);
			}
		});
		
		skillList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (skillListModel.size() <= 1) return;

				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					deleteFromList(skillList.getSelectedIndex());
				}
			}
		});
		
		JScrollPane slist = new JScrollPane(skillList);
		slist.setColumnHeaderView(createHeader("All Skills"));

		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);

		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		return p;
	}
	
	
	private void deleteFromList(int index){
		assert index != -1;
		int nextIndex = index == 0 ? skillListModel.size()-1 : index -1;
		skillList.setSelectedIndex(nextIndex);
		skillListModel.remove(index);
	}
	
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public DeleteAction() {
			putValue(NAME, "Delete the selected Skill");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON,new ListRemoveIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Must have at lest one skill
			deleteFromList(skillList.getSelectedIndex());
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
			RangedSkill w  = new RangedSkill();
			int index = skillListModel.size();
			w.setName("New Skill " + (index +1));
			skillListModel.addElement(w);
			skillList.setSelectedIndex(index);
		}
	}
	
	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		infoIcon = new JLabel("        ");
//		p.add(new JLabel("Icon:"), "gap 4");
//		p.add(infoIcon, "span, growx");
		
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
		
		infoType = new JComboBox(SkillTypes.values());
		infoType.setEditable(false);
		p.add(new JLabel("Type:"), "gap 4");
		infoType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				SkillTypes t= (SkillTypes) e.getItem();
				changeType(t);
			}
		});
		p.add(infoType, "span, growx");

		
		p.add(new JLabel("Strength:"), "gap 4");
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoStrength);
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeStrength(((Number)infoStrength.getValue()).intValue());
			}
		});
		
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add((infoRangeL = new JLabel("Range:")), "gap 4");
		infoRange = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoRange);
		
		infoRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeRange(((Number)infoRange.getValue()).intValue());
			}
		});
		p.add(infoRange,new CC().alignX("leading").maxWidth("70").wrap());

		
		p.add((infoAreaL = new JLabel("Area:")), "gap 4");
		infoArea = new JSpinner(new SpinnerNumberModel(1, 0, 8, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoArea);
		
		infoArea.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int inner = ((Number)infoArea.getValue()).intValue();
				changeArea(inner);
			}

		});
		p.add(infoArea,  new CC().alignX("leading").maxWidth("70").wrap());
		
		infoIncludeCaster = new JComboBox(new Boolean[]{Boolean.FALSE, Boolean.TRUE});
		infoIncludeCaster.setEditable(false);
		p.add(new JLabel("Include Caster:"), "gap 4");
		infoIncludeCaster.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean b= (Boolean) e.getItem();
				changeIncludeCaster(b);
			}
		});
		p.add(infoIncludeCaster,  new CC().alignX("leading").maxWidth("200").wrap());


		p.add(new JLabel("Infomation:"),  new CC().newline("30px").gap("4"));
		p.add((infoAbout = new JLabel("Info")), new CC().gap("4"));

		p.add(new JPanel());
		return p;
	}
	
	/**
	 * The current Skill type. Also has methods for updating the editor and creating a new skill.
	 * @author Bilal Hussain
	 */
	static enum SkillTypes {
		RANGED("Ranged") {
			@Override
			ISkill newSkill(SkillsPanel we) {
				RangedSkill w = new RangedSkill();
				w.setName(we.infoName.getText());
				
				int range = ((Number) we.infoRange.getValue()).intValue();
				w.setRange(range);
				int area = ((Number) we.infoArea.getValue()).intValue();
				w.setArea(area);
				
				we.infoArea.setVisible(true);
				we.infoAreaL.setVisible(true);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
				
				return w;
			}

			@Override
			void updateEditor(SkillsPanel we, ISkill w) {
				updateCommon(we, w);
				RangedSkill ww = (RangedSkill) w;
				we.infoRange.setValue(ww.getRange());
				we.infoArea.setValue(ww.getArea());
				we.infoArea.setVisible(true);
				we.infoAreaL.setVisible(true);
				we.infoRange.setVisible(true);
				we.infoRangeL.setVisible(true);
			}
			
			@Override
			String getInfo() {
				return "Damages all the targets in the attack area";
			}
			
		};		
		private final String name;

		@Override
		public String toString() {
			return name;
		}

		public void updateCommon(SkillsPanel we, ISkill w){
			we.infoName.setText(w.getName());
			we.infoStrength.setValue(w.getPower());
			we.infoAbout.setText(this.getInfo());
			
			ItemListener il = we.infoType.getItemListeners()[0];
			we.infoType.removeItemListener(il);
			we.infoType.setSelectedItem(this);
			we.infoType.addItemListener(il);
		}
		
		abstract ISkill newSkill(SkillsPanel we);
		abstract String getInfo();
		abstract void updateEditor(SkillsPanel we, ISkill w);

		private SkillTypes(String name) {
			this.name = name;
		}
	}
	
	public static class SkillListRenderer extends  DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			ISkill s= (ISkill) value;
			label.setText(s.getName());
//			label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}
	
}
