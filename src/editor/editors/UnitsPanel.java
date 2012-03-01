package editor.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;
import common.gui.ResourceManager;
import common.interfaces.IWeapon;

import editor.Editor;
import editor.MapEditor;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.assets.AssertStore;
import engine.assets.Skills;
import engine.assets.Units;
import engine.assets.Weapons;
import engine.skills.ISkill;
import engine.unit.IMutableUnit;
import engine.unit.Unit;

/**
 * Editor for units
 * @author Bilal Hussain
 */
public class UnitsPanel extends JPanel implements IRefreshable {
	private static final long serialVersionUID = 6590057554995017334L;

	private JList unitsList;
	private DefaultListModel unitsListModel;
	private IMutableUnit currentUnit;
	
	// Infopanel controls
	private JTextField infoName;
	private JComboBox  infoWeapon;
	
	private JSpinner   infoStrength;
	private JSpinner   infoDefence;
	
	private JSpinner   infoSpeed;
	private JSpinner   infoMove;

	private JSpinner   infoHp;
//	private JSpinner   infoMp;
	
	private JList skillsList;
	private DefaultListModel skillsListModel;

	private JList allSkillsList;
	private DefaultListModel allSkillsListModel;
	
	private JComboBox  infoSpriteSheet;


	public UnitsPanel(){
		super(new BorderLayout());
		createMainPane();
//		panelSelected();
	}
	
	public Units getUnits(){
		Units ws = new Units();
		for (Object o: unitsListModel.toArray()) {
			ws.put((IMutableUnit) o);
		}
		return ws;
	}
	
	public void setUnits(Units ws){
		ListSelectionListener lsl =  unitsList.getListSelectionListeners()[0];
		unitsList.removeListSelectionListener(lsl);
		unitsListModel.clear();
		for (IMutableUnit u : ws.values()) {
			unitsListModel.addElement(u);
		}
		unitsList.addListSelectionListener(lsl);
		unitsList.setSelectedIndex(0);
	}
	
	public void setCurrentUnit(IMutableUnit u){
		currentUnit = u;
		infoName.setText(u.getName());
		infoWeapon.setSelectedItem(u.getWeapon());
		infoStrength.setValue(u.getStrength());
		infoDefence.setValue(u.getDefence());
		infoSpeed.setValue(u.getSpeed());
		infoMove.setValue(u.getMove());
		infoHp.setValue(u.getMaxHp());
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
	
	
	@Override
	public synchronized void panelSelected(Editor editor) {
		ItemListener il =  infoWeapon.getItemListeners()[0];
		infoWeapon.removeItemListener(il);
		infoWeapon.removeAllItems();
		
		Weapons ww = editor.getWeapons();
		AssertStore.instance().loadWeapons(ww); // TODO chanage
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
		AssertStore.instance().loadSkill(ss);
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
		
	}

	protected void createMainPane() {
		JPanel p = createInfoPanel();
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), p);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(0.05);
		mainSplit.setBorder(null);
		this.add(mainSplit, BorderLayout.CENTER);
	}

	protected JComponent createLeftPane(){
		IMutableUnit uu = new Unit();
		uu.setName("New Unit");
		
		unitsListModel = new DefaultListModel();
		
		unitsList = new JList(unitsListModel);
		unitsList.setCellRenderer(new UnitListRenderer());
		unitsListModel.addElement(uu);
		unitsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				IMutableUnit u =  (IMutableUnit) unitsList.getSelectedValue();
				if (u == null) return;
				setCurrentUnit(u);
			}
		});
		unitsList.setSelectedIndex(0);

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
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		p.add(createHeader("All Units"),BorderLayout.NORTH);
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
			putValue(NAME, "Delete the selected Unit");
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
			putValue(NAME, "Add a new Unit");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IMutableUnit w = new Unit();
			int index = unitsListModel.size();
			w.setName("New Unit " + (index + 1));
			unitsListModel.addElement(w);
			unitsList.setSelectedIndex(index);
		}
	}

	protected LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 9).gap("15!", 9).grow(100, 9);
		return new MigLayout(layC, colC, rowC);
	}

	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		addSeparator(p,"General");
		p.add(new JLabel("Name:"), "gap 4");
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New Unit");
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
		
		infoWeapon = new JComboBox(new IWeapon[]{});
		infoWeapon.setRenderer(new WeaponDropDownList());
		infoWeapon.setEditable(false);
		p.add(new JLabel("Weapon:"), "gap 4");
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
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeStrength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Defence:"), "gap unrelated");
		infoDefence = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoDefence.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeDefence(((Number)infoDefence.getValue()).intValue());
			}
		});
		p.add(infoDefence, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add(new JLabel("Speed:"));
		infoSpeed = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeSpeed(((Number)infoSpeed.getValue()).intValue());
			}
		});
		p.add(infoSpeed, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Move:"), "gap unrelated");
		infoMove = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoMove.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeMove(((Number)infoMove.getValue()).intValue());
			}
		});
		p.add(infoMove, new CC().alignX("leading").maxWidth("70").wrap());

		
		p.add(new JLabel("Hp:"));
		infoHp = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoHp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeHp(((Number)infoHp.getValue()).intValue());
			}
		});
		p.add(infoHp, new CC().alignX("leading").maxWidth("70").wrap());
		
		addSeparator(p,"Sprites");
		infoSpriteSheet = new JComboBox(new IWeapon[]{});
		infoSpriteSheet.setEditable(false);
		p.add(new JLabel("Weapon:"), "gap 4");
		p.add(infoSpriteSheet, "span, growx, wrap");
		
		addSeparator(p,"Skills");
		skillsListModel = new DefaultListModel();
		skillsList      = new JList(skillsListModel);
		
		p.add(skillsList, new CC().alignX("leading").spanX(2).grow());
		
		allSkillsListModel = new DefaultListModel();
		allSkillsList      = new JList(allSkillsListModel);
		allSkillsList.setCellRenderer(new SkillsPanel.SkillListRenderer());
		
		p.add(allSkillsList, new CC().gap("unrelated").alignX("leading").spanX(2).grow().wrap());
		
		
		p.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border
		return p;
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
	
	class WeaponDropDownList extends DefaultListCellRenderer {
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
	
	class UnitListRenderer extends  DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			IMutableUnit w= (IMutableUnit) value;
			label.setText(w.getName());
//			label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}
	
}
