package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

import editor.editors.WeaponsPanel.WeaponTypes;
import editor.editors.WeaponsPanel.WeaponsListRenderer;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.items.MeleeWeapon;
import engine.items.Spear;

/**
 * Editor for untis
 * @author Bilal Hussain
 */
public class UnitPanel extends JPanel {
	private static final long serialVersionUID = 6590057554995017334L;

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
	private JLabel     infoAbout;

	public UnitPanel(){
		super(new BorderLayout());
		createMainPane();
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
		IWeapon ww= new Spear(10,3);
		ww.setName("New Weapon");
		weaponslistModel = new DefaultListModel();
		weaponslist = new JList(weaponslistModel);
		weaponslist.setCellRenderer(new WeaponsListRenderer());
		weaponslistModel.addElement(ww);
		weaponslist.setSelectedIndex(0);
		weaponslist.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				IWeapon w =  (IWeapon) weaponslist.getSelectedValue();
				if (w == null) return;
//				setCurrentWeapon(w);
			}
		});
		
		JScrollPane slist = new JScrollPane(weaponslist);
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		p.add(createHeader("All Weapons"),BorderLayout.NORTH);
		return p;
	}
	
	protected LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4).align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);
		return new MigLayout(layC, colC, rowC);
	}

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
		
		infoType = new JComboBox(WeaponTypes.values());
		infoType.setEditable(false);
		p.add(new JLabel("Type:"), "gap 4");
		infoType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				WeaponTypes t= (WeaponTypes) e.getItem();
//				changeType(t);
			}
		});
		p.add(infoType, "span, growx");

		
		p.add(new JLabel("Strength:"), "gap 4");
		infoStrength = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		
		infoStrength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeStength(((Number)infoStrength.getValue()).intValue());
			}
		});
		
		p.add(infoStrength, new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add((infoRangeL = new JLabel("Range:")), "gap 4");
		infoRange = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
		
		infoRange.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				changeRange(((Number)infoRange.getValue()).intValue());
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
//				changeInnerRange(inner);
			}

		});
		p.add(infoInnerRange,  new CC().alignX("leading").maxWidth("70").wrap());
		

		p.add(new JLabel("Infomation:"),  new CC().newline("30px").gap("4"));
		p.add((infoAbout = new JLabel("Info")), new CC().gap("4"));

		p.add(new JPanel());
		return p;
	}
	
	// creates a header for the panel.
	protected JPanel createHeader(String text){
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
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
			// Must have at lest one weapon
			if (weaponslistModel.size() <=1){
				return;
			}
			int index = weaponslist.getSelectedIndex();
			assert index != -1;
			int nextIndex = index == 0 ? weaponslistModel.size()-1 : index -1;
			weaponslist.setSelectedIndex(nextIndex);
			weaponslistModel.remove(index);
			
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
			MeleeWeapon w  = new MeleeWeapon();
			int index = weaponslistModel.size();
			w.setName("New Weapon " +index);
			weaponslistModel.addElement(w);
			weaponslist.setSelectedIndex(index);
		}
	}
	
	class WeaponsListRenderer extends  DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			IWeapon w= (IWeapon) value;
			label.setText(w.getName());
			label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}
	
}
