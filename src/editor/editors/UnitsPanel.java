package editor.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.DefaultFormatter;

import org.apache.log4j.Logger;
import org.jvnet.inflector.Noun;

import sun.util.logging.resources.logging;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.*;

import common.enums.Direction;
import common.gui.ResourceManager;
import common.interfaces.IWeapon;
import common.spritesheet.SpriteSheet;
import config.Config;
import config.assets.*;

import editor.Editor;
import editor.map.EditorSpriteSheet;
import editor.map.MapEditor;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import editor.util.EditorResources;
import engine.skills.ISkill;
import engine.unit.AiUnit;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * Editor for units
 * @author Bilal Hussain
 */
public class UnitsPanel<E extends IMutableUnit>  extends JPanel implements IRefreshable {
	private static final Logger log = Logger.getLogger(UnitsPanel.class);
	private static final long serialVersionUID = 6590057554995017334L;

	protected JList unitsList;
	protected DefaultListModel unitsListModel;
	protected E currentUnit;
	
	// Infopanel controls
	protected JTextField infoName;
	protected JComboBox  infoWeapon;
	
	protected JSpinner   infoStrength;
	protected JSpinner   infoDefence;
	
	protected JSpinner   infoSpeed;
	protected JSpinner   infoMove;

	protected JSpinner   infoHp;
//	protected JSpinner   infoMp;
	
	protected JList skillsList;
	protected DefaultListModel skillsListModel;

	protected JList allSkillsList;
	protected DefaultListModel allSkillsListModel;
	
	protected JComboBox  infoSpriteSheet;
	protected JLabel[]   infoSprites;

	protected java.util.Map<UUID, EditorSpriteSheet> spriteSheets;
	protected SpriteSheet unitSprites;
	
	protected boolean listOnLeft;
	protected String displayName;
	
	public UnitsPanel(java.util.Map<UUID, EditorSpriteSheet> spriteSheets){
		this(spriteSheets, true, "Unit");
	}

	public UnitsPanel(java.util.Map<UUID, EditorSpriteSheet> spriteSheets, boolean listOnLeft, String displayName){
		super(new BorderLayout());
		this.spriteSheets = spriteSheets;
		this.listOnLeft   = listOnLeft;
		this.displayName  = displayName;
		createMainPane();
	}
	
	public Units getUnits(){
		Units ws = new Units();
		for (Object o: unitsListModel.toArray()) {
			ws.put((IMutableUnit) o);
		}
		return ws;
	}
	
	public synchronized void setUnits(Units ws){
		ListSelectionListener lsl =  unitsList.getListSelectionListeners()[0];
		unitsList.removeListSelectionListener(lsl);
		unitsListModel.clear();
		for (IMutableUnit u : ws.values()) {
			unitsListModel.addElement(u);
		}
		unitsList.addListSelectionListener(lsl);
		unitsList.setSelectedIndex(0);
	}
	
	public void setCurrentUnit(E u){		
		assert u != null;
		
		currentUnit = u;
		infoName.setText(u.getName());
		infoWeapon.setSelectedItem(u.getWeapon());
		infoStrength.setValue(u.getStrength());
		infoDefence.setValue(u.getDefence());
		infoSpeed.setValue(u.getSpeed());
		infoMove.setValue(u.getMove());
		infoHp.setValue(u.getMaxHp());

		for (int i = 0; i < skillsListModel.size(); i++) {
			Object o = skillsListModel.get(i);
			if (!allSkillsListModel.contains(o)) allSkillsListModel.addElement(o);
		}
		
		skillsListModel.clear();
		assert currentUnit != null;
		assert currentUnit.getSkills() != null : currentUnit;
		for (ISkill	s : currentUnit.getSkills()) {
			skillsListModel.addElement(s);
			allSkillsListModel.removeElement(s);
		}

		SpriteSheetData ui = currentUnit.getImageData();
		log.debug(u);
		log.debug(spriteSheets);
		EditorSpriteSheet ess;
		if (ui == null || (ess = spriteSheets.get(ui.getUuid())) == null){
			assert false : spriteSheets.size() + " "+ ui.getUuid() + "\n" + spriteSheets + "\n" + currentUnit;
			throw new IllegalStateException("No spritesheets");
		}else{
			unitSprites = ess.getSpriteSheet();
			infoSpriteSheet.setSelectedItem(ui);
		}
		
		assert infoSprites != null;
		loadUnitImages();
	}
	
	private void changeName(){
		currentUnit.setName(infoName.getText());
		unitsList.repaint();
	}	
	
	private void changeWeapon(IWeapon w){
		assert currentUnit != null;
		currentUnit.setWeapon(w);
	}	 

	private void changeStrength(int value){
		currentUnit.setStrength(value);
	}	
	
	private void changeDefence(int value){
		currentUnit.setDefence(value);
	}	

	private void changeSpeed(int value){
		currentUnit.setSpeed(value);
	}	

	private void changeMove(int value){
		currentUnit.setMove(value);
	}	

	private void changeHp(int value){
		currentUnit.setMaxHp(value);
	}	
	
	
	private void changeSkills(){
		ArrayList<ISkill> skills =new ArrayList<ISkill>();
		for (int i = 0; i < skillsListModel.size(); i++) {
			skills.add((ISkill) skillsListModel.get(i));
		}
		currentUnit.setSkills(skills);
	}

	private void changeUnitImages(SpriteSheetData images){
		
		String path = images.getAnimationPath();
		currentUnit.setImageData(path,images);
		
		unitSprites = spriteSheets.get(images.getUuid()).getSpriteSheet();
		loadUnitImages();
	}
	
	private void loadUnitImages(){
		for (ImageDirection d : ImageDirection.values()) {
			assert unitSprites.getSpriteImage(d.getImageRef()) != null;
			assert infoSprites[d.ordinal()] != null;
			infoSprites[d.ordinal()].setIcon(new ImageIcon(unitSprites.getSpriteImage(d.getImageRef())));
		}
	}
	
	@Override
	public synchronized void panelSelected(Editor editor) {
		spriteSheets = editor.getUnitsSprites();
		
		ItemListener il =  infoWeapon.getItemListeners()[0];
		infoWeapon.removeItemListener(il);
		infoWeapon.removeAllItems();
		
		Weapons ww = editor.getWeapons();
		AssetStore.instance().loadWeapons(ww); // TODO chanage?
		List<IWeapon> s = new ArrayList<IWeapon>(ww.values());
		
		Collections.sort(s,new Comparator<IWeapon>() {
			@Override
			public int compare(IWeapon o1, IWeapon o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		for (IWeapon w : s) {
			infoWeapon.addItem(w);
		}
		
		infoWeapon.addItemListener(il);
		if (currentUnit != null){
			setCurrentUnit(currentUnit);
		}
		
		allSkillsListModel.removeAllElements();
		
		Skills ss = editor.getSkills();
		AssetStore.instance().loadSkill(ss);
		List<ISkill> l = new ArrayList<ISkill>(ss.values());
		
		Collections.sort(l,new Comparator<ISkill>() {
			@Override
			public int compare(ISkill o1, ISkill o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		for (ISkill is :l) {
			allSkillsListModel.addElement(is);
		}
		
		il =  infoSpriteSheet.getItemListeners()[0];
		infoSpriteSheet.removeItemListener(il);
		infoSpriteSheet.removeAllItems();
		SpriteSheetsData images = editor.getUnitImages();
		
		List<SpriteSheetData> ll = AbstractSpriteSheetOrganiser.sortedSprites(images.values());
		for (SpriteSheetData ui :ll) {
			infoSpriteSheet.addItem(ui);
		}
		infoSpriteSheet.setSelectedItem(currentUnit.getImageData());
		infoSpriteSheet.addItemListener(il);
		
		assert currentUnit.getImageData().equals(infoSpriteSheet.getSelectedItem());
	}

	protected void createMainPane() {
		JPanel p = createInfoPanel();
		JSplitPane mainSplit;
		if (listOnLeft){
			mainSplit = new JSplitPane(
					JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), p);
			mainSplit.setResizeWeight(0.05);
		}else{
			mainSplit = new JSplitPane(
					JSplitPane.HORIZONTAL_SPLIT, true, p, createLeftPane());
			mainSplit.setResizeWeight(0.95);			
		}
		
		
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setBorder(null);
		this.add(mainSplit, BorderLayout.CENTER);
	}

	protected JComponent createLeftPane(){
		unitsListModel = new DefaultListModel();
		
		unitsList = new JList(unitsListModel);
		unitsList.setCellRenderer(new UnitListRenderer());
		unitsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				E u =  (E) unitsList.getSelectedValue();
				if (u == null){
					log.debug(displayName + " "+ u);
					return;
				}
				setCurrentUnit(u);
			}
		});
//		unitsList.setSelectedIndex(0);

		unitsList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (unitsListModel.size() <= 1) return;

				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					deleteFromList(unitsList.getSelectedIndex());
				}
			}
		});

		JScrollPane slist = new JScrollPane(unitsList);
		slist.setColumnHeaderView(createHeader("All " + Noun.pluralOf(displayName)));
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		
		return p;
	}
	
	private void deleteFromList(int index){
		assert unitsListModel.size() >=2;
		assert index != -1;
		int nextIndex = index == 0 ? unitsListModel.size()-1 : index - 1;
		System.out.printf("(%d,%d)\n", index, nextIndex );
		unitsList.setSelectedIndex(nextIndex);
		unitsListModel.remove(index);
	}
	
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public DeleteAction() {
			putValue(NAME, "Delete the selected " + displayName);
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListRemoveIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Must have at lest one unit
			if (unitsListModel.size() <= 1) {
				return;
			}
			deleteFromList(unitsList.getSelectedIndex());
		}
	}

	private class AddAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public AddAction() {
			putValue(NAME, "Add a new " + displayName);
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IMutableUnit w = createUnit(currentUnit);
			int index = unitsListModel.size();
			w.setName("New " + displayName + " " + (index + 1));
			unitsListModel.addElement(w);
			unitsList.setSelectedIndex(index);
		}
	}

	protected IMutableUnit createUnit(IMutableUnit old){
		return new Unit(old);
	}
	
	public static LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}

	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		addSeparator(p,"General");
		p.add(new JLabel("Name:"));
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New " + displayName);
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
		
		p.add(new JLabel("Weapon:"));
		infoWeapon = new JComboBox(new IWeapon[]{});
		infoWeapon.setRenderer(new WeaponDropDownListRenderer());
		infoWeapon.setEditable(false);
		infoWeapon.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoWeapon.getItemCount() <= 0) return;
				
				IWeapon w= (IWeapon) e.getItem();
				changeWeapon(w);
			}
		});
		p.add(infoWeapon, "span, growx");

		
		p.add(new JLabel("Strength:"));
		infoStrength = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeStrength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70"));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoStrength); 
		
		p.add(new JLabel("Defence:"), "gap unrelated");
		infoDefence = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoDefence);

		infoDefence.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeDefence(((Number)infoDefence.getValue()).intValue());
			}
		});
		p.add(infoDefence, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add(new JLabel("Speed:"));
		infoSpeed = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoSpeed);

		
		infoSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeSpeed(((Number)infoSpeed.getValue()).intValue());
			}
		});
		p.add(infoSpeed, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Move:"), "gap unrelated");
		infoMove = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoMove);
		
		infoMove.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeMove(((Number)infoMove.getValue()).intValue());
			}
		});
		p.add(infoMove, new CC().alignX("leading").maxWidth("70").wrap());

		
		p.add(new JLabel("Hp:"));
		infoHp = new JSpinner(new SpinnerNumberModel(20, 1, 10000, 10));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoHp);
		
		infoHp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeHp(((Number)infoHp.getValue()).intValue());
			}
		});
		p.add(infoHp, new CC().alignX("leading").maxWidth("70").wrap());
		
		extraComponents(p);
		
		addSeparator(p,"Sprites");
		
		infoSprites = new JLabel[ImageDirection.values().length];
		for (ImageDirection d : ImageDirection.values()) {
			String s = d.toString().toLowerCase();
			infoSprites[d.ordinal()] = new JLabel(Character.toUpperCase(s.charAt(0)) + s.substring(1));
			
			infoSprites[d.ordinal()].setHorizontalTextPosition(SwingConstants.CENTER);
			infoSprites[d.ordinal()].setVerticalTextPosition(SwingConstants.BOTTOM);
			p.add(infoSprites[d.ordinal()]);
		}
		
		
		infoSpriteSheet = new JComboBox(new IWeapon[]{});
		infoSpriteSheet.setEditable(false);
		infoSpriteSheet.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoSpriteSheet.getItemCount() <= 0) return;
				
				SpriteSheetData w= (SpriteSheetData) e.getItem();
				changeUnitImages(w);
			}
		});
		
		p.add(infoSpriteSheet, "span, growx, wrap");
		
		addSeparator(p,"Skills");
		skillsListModel = new DefaultListModel();
		skillsList      = new JList(skillsListModel);
		skillsList.setCellRenderer(new SkillsPanel.SkillListRenderer());

		JScrollPane ssp = new JScrollPane(skillsList);
		ssp.setColumnHeaderView(createHeader("Unit's Skills"));
		p.add(ssp, new CC().alignX("leading").spanX(2).grow());
		
		p.add(new TButton(new AddSkillAction()),new CC().alignY("center").alignX("center").split(2).flowY());
		p.add(new TButton(new RemoveSkillAction()),new CC().alignY("center").alignX("center"));
		
		allSkillsListModel = new DefaultListModel();
		allSkillsList      = new JList(allSkillsListModel);
		allSkillsList.setCellRenderer(new SkillsPanel.SkillListRenderer());
		
		JScrollPane assp = new JScrollPane(allSkillsList);
		assp.setColumnHeaderView(createHeader("All Skills"));
		p.add(assp, new CC().gap("unrelated").alignX("leading").spanX(2).grow().wrap());
		
		p.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border
		return p;
	}
	
	protected void extraComponents(JPanel p) {
		
	}

	private class AddSkillAction extends AbstractAction {
		private static final long serialVersionUID = -6538170935544736252L;

		public AddSkillAction() {
			putValue(NAME, "Add to " +  displayName + "'s Skill");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new GoPreviousIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ISkill s = (ISkill) allSkillsList.getSelectedValue();
			if (s == null) return;
			skillsListModel.addElement(s);
			allSkillsListModel.removeElement(s);
			changeSkills();
		}
	}

	private class RemoveSkillAction extends AbstractAction {
		private static final long serialVersionUID = -8604147798296984257L;

		public RemoveSkillAction() {
			putValue(NAME, "Remove " +  displayName + "'s Skill");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new GoNextIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ISkill s = (ISkill) skillsList.getSelectedValue();
			if (s == null) return;
			
			allSkillsListModel.addElement(s);
			skillsListModel.removeElement(s);
			changeSkills();
		}
	}
	
	
	void addSeparator(JPanel p, String title){
		JLabel pTitle = new JLabel(title);
		pTitle.setForeground(Color.BLUE.brighter());
		
		p.add(pTitle, new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));		
	}
	
	// creates a header for the panel.
	protected JPanel createHeader(String text){
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}
	
	// Gui elements to display information about a asset. 
	
	enum ImageDirection {
		NORTH ("north0"),
		SOUTH ("south0"),
		
		EAST  ("east0"),
		WEST  ("west0");
		
		final String imageRef;
		
		private ImageDirection(String name) {
			this.imageRef = name;
		}

		public String getImageRef() {
			return imageRef;
		}
		
	}
	
	public static class WeaponDropDownListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7730726867980301916L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			IWeapon w    = (IWeapon) value;
			label.setText(String.format("%s - Strength:%s Range:%s", w.getName(), w.getStrength(), w.getDetails() ));
			label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}

	public static class UnitListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			if (value == null){
				label.setText("None");
			}else{
				IMutableUnit w= (IMutableUnit) value;
				label.setText(w.getName());	
			}
			return label;
		}
	}
	
	
}
