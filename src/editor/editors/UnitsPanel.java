package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import engine.assets.Units;
import engine.assets.Weapons;
import engine.unit.IMutableUnit;
import engine.unit.Unit;

/**
 * Editor for untis
 * @author Bilal Hussain
 */
public class UnitsPanel extends JPanel implements IRefreshable {
	private static final long serialVersionUID = 6590057554995017334L;

	private JList unitList;
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
	
	public void setUnits(Units ws, Editor editor){
		ListSelectionListener lsl =  unitList.getListSelectionListeners()[0];
		unitList.removeListSelectionListener(lsl);
		unitsListModel.clear();
		for (IMutableUnit w : ws.values()) {
			unitsListModel.addElement(w);
		}
		unitList.addListSelectionListener(lsl);
		unitList.setSelectedIndex(0);
	}
	
	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method
		DefaultComboBoxModel cbm = new DefaultComboBoxModel(editor.getWeapons().values().toArray());
		infoWeapon.removeAllItems();
		
		Weapons ww = editor.getWeapons();
		AssertStore.instance().loadWeapons(ww);
		List s = new ArrayList<IWeapon>(ww.values());
		
		Collections.sort(s,new Comparator<IWeapon>() {
			@Override
			public int compare(IWeapon o1, IWeapon o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		for (IWeapon w : editor.getWeapons().values()) {
			infoWeapon.addItem(w);
		}
	}

	protected void createMainPane() {
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), createInfoPanel());
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(0.05);
		mainSplit.setBorder(null);
		this.add(mainSplit, BorderLayout.CENTER);
	}

	protected JComponent createLeftPane(){
		IMutableUnit uu = new Unit();
		uu.setName("New Unit");
		
		unitsListModel = new DefaultListModel();
		unitList = new JList(unitsListModel);
		unitList.setCellRenderer(new UnitListRenderer());
		unitsListModel.addElement(uu);
		unitList.setSelectedIndex(0);
		unitList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				IMutableUnit w =  (IMutableUnit) unitList.getSelectedValue();
				if (w == null) return;
//				setCurrentWeapon(w);
			}
		});
		
		JScrollPane slist = new JScrollPane(unitList);
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		p.add(createHeader("All Units"),BorderLayout.NORTH);
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
				// Must have at lest one unit
				if (unitsListModel.size() <=1){
					return;
				}
				int index = unitList.getSelectedIndex();
				assert index != -1;
				int nextIndex = index == 0 ? unitsListModel.size()-1 : index -1;
				unitList.setSelectedIndex(nextIndex);
				unitsListModel.remove(index);
				
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
				IMutableUnit w  = new Unit();
				int index = unitsListModel.size();
				w.setName("New Unit " +index);
				unitsListModel.addElement(w);
				unitList.setSelectedIndex(index);
			}
		}

	protected LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);
		return new MigLayout(layC, colC, rowC);
	}

	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		p.add(new JLabel("Name:"), "gap 4");
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New Weapon");
		infoName.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
//				changeName();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
//				changeName();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
//				changeName();
			}
		});
		
		infoWeapon = new JComboBox(new IWeapon[]{});
		infoWeapon.setRenderer(new WeaponDropDownList());
		infoWeapon.setEditable(false);
		p.add(new JLabel("Weapon:"), "gap 4");
		infoWeapon.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				IWeapon w= (IWeapon) e.getItem();
//				changeType(t);
			}
		});
		p.add(infoWeapon, "span, growx");

		
		p.add(new JLabel("Strength:"));
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Defence:"));
		infoDefence = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoDefence.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoDefence, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add(new JLabel("Speed:"));
		infoSpeed = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoSpeed, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Move:"));
		infoMove = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoMove.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoMove, new CC().alignX("leading").maxWidth("70").wrap());

		
		p.add(new JLabel("Hp:"));
		infoHp = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		
		infoHp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		p.add(infoHp, new CC().alignX("leading").maxWidth("70").wrap());
	
		
		
		return p;
	}
	
	// creates a header for the panel.
	protected JPanel createHeader(String text){
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}
	
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
