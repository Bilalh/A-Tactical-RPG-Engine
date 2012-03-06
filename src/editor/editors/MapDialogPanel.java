package editor.editors;

import static editor.editors.AbstractSpriteSheetOrganiser.sortedSprites;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;


import common.assets.DialogPart;
import common.assets.DialogParts;
import common.assets.UnitPlacement;
import common.interfaces.IUnit;
import common.interfaces.IWeapon;
import editor.Editor;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.IMutableUnit;
import engine.unit.SpriteSheetData;
import engine.unit.Unit;

/**
 * @author Bilal Hussain
 */
public class MapDialogPanel extends AbstractResourcesPanel<DialogPart, DialogParts> {
	private static final long serialVersionUID = 4626052435769578123L;
	private static final Logger log = Logger.getLogger(MapDialogPanel.class);

	private MapsPanel mapPanel;
	private JComboBox infoSpeaker;
	private JTextArea infoText;
	
	private UnitPlacement enemies;
	
	private DialogPart current;
	
	public MapDialogPanel(MapsPanel mapPanel, Editor editor) {
		super(editor,false);
		this.mapPanel = mapPanel;
	}


	@Override
	public void panelSelected(Editor editor) {
		enemies = mapPanel.getEnemies();
		
		ItemListener il =  infoSpeaker.getItemListeners()[0];
		infoSpeaker.removeItemListener(il);
		infoSpeaker.removeAllItems(); 

		
		for (UUID uuid : enemies.getUnitPlacement().keySet()) {
			IMutableUnit m = enemies.getUnit(uuid);
			infoSpeaker.addItem(m);
		}
		
		infoSpeaker.addItem(null);
		infoSpeaker.addItemListener(il);
		infoSpeaker.setSelectedItem(current.getUnitId());
	}

	@Override
	public DialogParts getResouces() {
		return super.getResouces();
	}
	
	private void saveToCurrent(){
		current.setText(infoText.getText());
		current.setUnitId((IUnit) infoSpeaker.getSelectedItem());
	}
	
	@Override
	protected void addToList() {
		DialogPart dp = new DialogPart();
		int index = resourceListModel.size();
		dp.setText("New Dialogue "+ (index + 1));
		
		resourceListModel.addElement(dp);
		resourceList.setSelectedIndex(index);
	}

	@Override
	protected void setCurrentResource(DialogPart dp) {
		if (current != null){
			saveToCurrent();
		}
		current = dp;
		infoText.setText(dp.getText());
		infoSpeaker.setSelectedItem(dp.getUnitId());
	}
	
	@Override
	protected JComponent createInfoPanel() {
		JPanel p = new JPanel(defaultInfoPanelLayout());
		
		infoSpeaker = new JComboBox();
		p.add(new JLabel("Speaker: "));
		p.add(infoSpeaker, "span, growx");
		infoSpeaker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoSpeaker.getItemCount() <= 0) return;
				
			}
		});
		infoSpeaker.setRenderer(new TickComboBoxCellRender(infoSpeaker){
			private static final long serialVersionUID = 53080362973790034L;
			
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
		});
				
		p.add(new JLabel("Text:"), new CC().wrap());
		p.add(infoText = new JTextArea("abc"), new CC().grow().spanX().spanY());
		
		return p;
	}

	@Override
	protected String resourceDisplayName(DialogPart resource, int index) {
		return "Part " + (index + 1) ;
	}

	@Override
	protected String resourceName() {
		return "Dialog";
	}

	@Override
	protected DialogParts createAssetInstance() {
		return new DialogParts();
	}

	@Override
	protected DialogPart defaultResource() {
		return new DialogPart("",null);
	}

}
