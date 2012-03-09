package editor.editors;

import static editor.editors.AbstractSpriteSheetOrganiser.sortedSprites;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import common.interfaces.IUnit;
import common.interfaces.IWeapon;
import config.assets.DialogPart;
import config.assets.DialogParts;
import config.assets.UnitPlacement;
import editor.Editor;
import editor.editors.AbstractResourcesPanel.AbstractListRenderer;
import editor.spritesheet.IDragFinishedListener;
import editor.spritesheet.ReorderableJList;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.IMutableUnit;
import engine.unit.SpriteSheetData;
import engine.unit.Unit;

/**
 * Editor for a dialog
 * @author Bilal Hussain
 */
public class MapDialogPanel extends AbstractResourcesPanel<DialogPart, DialogParts> implements IDragFinishedListener {
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
		if (current != null) infoSpeaker.setSelectedItem(current.getUnitId());
	}

	
	private void saveToCurrent(){
		current.setText(infoText.getText());
		current.setUnitId((IUnit) infoSpeaker.getSelectedItem());
	}
	
	@Override
	protected void addToList() {
		infoText.setEnabled(true);
		infoSpeaker.setEnabled(true);
		DialogPart dp = new DialogPart();
		int index = resourceListModel.size();
		dp.setText("New Dialogue "+ (index + 1));
		
		resourceListModel.addElement(dp);
		resourceList.setSelectedIndex(index);
	}

	@Override
	public DialogParts getResouces() {
		if (current != null){
			saveToCurrent();
		}
		
		DialogParts ws = createAssetInstance();
		for (int i = 0; i < resourceListModel.size(); i++) {
			DialogPart dp = (DialogPart) resourceListModel.get(i);
			dp.setPartNo(i);
			ws.put(dp);
		}
		return ws;
	}

	@Override
	protected  void addResourcesToList(DialogParts assets){
		for (DialogPart u : assets.sortedValues()) {
			assert u != null;
			resourceListModel.addElement(u);
		}
	}
	
	@Override
	public synchronized void setResources(DialogParts assets) {
		super.setResources(assets);
		if (assets.size() == 0){
			infoText.setText("");
			infoText.setEnabled(false);
			infoSpeaker.setEnabled(false);
			current = null;
		}else{
			infoText.setEnabled(true);
			infoSpeaker.setEnabled(true);
		}
	}

	
	@Override
	protected void setCurrentResource(DialogPart dp) {
		if (current != null){
			saveToCurrent();
		}
		current = dp;
		infoText.setText(dp.getText());
		UUID uuid =  dp.getUnitId();
		assert infoSpeaker != null;
		if(uuid != null && enemies != null) {
			infoSpeaker.setSelectedItem(enemies.getUnit(uuid));
		}else{ 
			infoSpeaker.setSelectedItem(null);
		}
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
		infoText = new JTextArea("abc");
		infoText.setLineWrap(true);
		infoText.setWrapStyleWord(true);
		p.add(new JScrollPane(infoText), new CC().spanX().spanY().grow());
		
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

	@Override
	protected JList createJList(ListModel model) {
		ReorderableJList l = new ReorderableJList(model, this);
		l.setCellRenderer(new ReorderableJList.ReorderableListCellRenderer() {
			private static final long serialVersionUID = 5631531917215157509L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				DialogPart r = (DialogPart) value;
				label.setText(resourceDisplayName(r, index));
				return label;
			}
		});
		return l;
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde, int oldIndex, int newIndex) {
//		System.out.println("dragDropEnd  " + oldIndex + " " + newIndex);
		resourceList.setSelectedIndex(newIndex);
	}


	@Override
	protected boolean shouldDelete() {
		return resourceListModel.size() > 0;
	}

	@Override
	protected void deleteFromList(int index) {
		super.deleteFromList(index);
		if (resourceListModel.size() == 0){
			infoText.setText("");
			infoText.setEnabled(false);
			infoSpeaker.setEnabled(false);
			current = null;
		}
	}
	
}
