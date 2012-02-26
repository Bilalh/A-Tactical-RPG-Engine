package editor;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledEditorKit.BoldAction;

import config.xml.MapSettings;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import editor.WeaponsEditor.WeaponTypes;
import editor.map.EditorMapPanel;
import editor.map.others.OthersMap;

/**
 * @author Bilal Hussain
 */
public class WeaponsPanel extends AbstactMapEditorPanel {
	private static final long serialVersionUID = -4663464965056461703L;
	
	// Infopanel controls
	private JLabel     infoIcon;
	private JTextField infoName;
	private JComboBox  infoType;
	private JSpinner   infoStrength;
	private JSpinner   infoRange;
	private JSpinner   infoInnerRange;
	private JLabel     infoRangeL;
	private JLabel     infoInnerRangeL;
	private JList list;
	
	public WeaponsPanel(){
		super("Weapons Editor", "Weapons", 11, 11);
	}
	
	@Override
	protected void createMap() {
		MapSettings m = MapSettings.defaults();
		m.tileDiagonal = 40;
		map = new OthersMap(mapWidth,mapHeight, m);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),m);
	} 
	

	private LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4).align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);
		return new MigLayout(layC, colC, rowC);
	}

	
	@Override
	protected  JComponent createLeft(){
		list = new JList(new String[] {"Mouse, Mickey"});
		JScrollPane slist = new JScrollPane(list);
		return slist;
	}
	
	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		list = new JList(new String[] {"Mouse, Mickey"});
		JScrollPane slist = new JScrollPane(list);
//		p.add(slist,  new CC().spanY().growY().minWidth("150").gapX(null, "10")		 );
//		this.add(slist, BorderLayout.WEST);
		
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
		
		p.add(new JPanel());
		return p;
	}
	
	
	
}
