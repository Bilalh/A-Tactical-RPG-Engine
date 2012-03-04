package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import util.IOUtil;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;
import common.interfaces.IWeapon;

import config.Config;
import config.xml.SavedMap;

import editor.Editor;
import editor.editors.UnitsPanel.WeaponDropDownListRenderer;
import engine.assets.Maps;
import engine.assets.Units;
import engine.assets.SpriteSheetsData;
import engine.map.Map;
import engine.unit.IMutableUnit;
import engine.unit.SpriteSheetData;

import static editor.editors.AbstractSpriteSheetOrganiser.*;

/**
 * @author Bilal Hussain
 */
public class MapsPanel extends AbstractResourcesPanel<SavedMap, Maps> {
	private static final long serialVersionUID = -6885584804612641268L;
	private static final Logger log = Logger.getLogger(MapsPanel.class);

	private JTextField infoName;

	private JSpinner infoWidth;
	private JSpinner infoHeight;
	
	private JSpinner infoTileDiagonal;
	private JSpinner infoTileHeight;	

	private JComboBox  infoTileset;
	private JComboBox  infoTextures;

	
	public MapsPanel(Editor editor) {
		super(editor);
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method
		ItemListener il =  infoTileset.getItemListeners()[0];
		infoTileset.removeItemListener(il);
		infoTileset.removeAllItems();


		for (SpriteSheetData u : sortedSprites(editor.getTilesets().values())) {
			infoTileset.addItem(u);
		}
		infoTileset.addItemListener(il);

		il =  infoTextures.getItemListeners()[0];
		infoTextures.removeItemListener(il);
		infoTextures.removeAllItems();

		for (SpriteSheetData u : sortedSprites(editor.getTextures().values())) {
			infoTextures.addItem(u);
		}
		infoTextures.addItemListener(il);
		
	}

	@Override
	protected void setCurrentResource(SavedMap map) {
		infoName.setText(map.getUuid().toString());
		
		infoWidth.setValue(map.getFieldWidth());
		infoHeight.setValue(map.getFieldHeight());
		
		infoTileDiagonal.setValue(map.getMapSettings().tileDiagonal);
		infoTileHeight.setValue(map.getMapSettings().tileHeight);
	}

	@Override
	protected void addToList() {
		
	}

	private void changeName(){
//		currentUnit.setName(infoName.getText());
		resourceList.repaint();
	}	
	
	private void changeTileset(SpriteSheetData ui){
		
	}

	private void changeTextures(SpriteSheetData ui){
		
	}
	
	private void editMap(){
		
	}
	
	class ImageComboBoxRenderer extends BasicComboBoxRenderer{
		private static final long serialVersionUID = 5419860419890213604L;
		
        @Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
            
            SpriteSheetData w= (SpriteSheetData) value;
            label.setText(IOUtil.removeExtension(new File(w.getSpriteSheetLocation()).getName()));
            return label;
        }
		
	}
	
	@Override
	protected JPanel createInfoPanel() {
	    JPanel p = new JPanel(defaultInfoPanelLayout());
	    
		addSeparator(p,"General");
		p.add(new JLabel("Name:"), new CC().alignX("leading"));
		p.add((infoName = new JTextField(15)), "span, growx");
		infoName.setText("New Map");
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
		
		p.add(new JLabel("Map Width:"),new CC().alignX("leading"));
		infoWidth = new JSpinner(new SpinnerNumberModel(15, 5, 50, 1));
		p.add(infoWidth, new CC().alignX("leading").maxWidth("70"));

		
		p.add(new JLabel("Map Height:"), "gap unrelated");
		infoHeight = new JSpinner(new SpinnerNumberModel(15, 5, 50, 1));
		p.add(infoHeight, new CC().alignX("leading").maxWidth("70").wrap());
		
		addSeparator(p,"Tiles");

		p.add(new JLabel("Tile Diagonal:"),new CC().alignX("leading"));
		infoTileDiagonal = new JSpinner(new SpinnerNumberModel(60, 40, 256, 5));
		p.add(infoTileDiagonal, new CC().alignX("leading").maxWidth("70"));

		p.add(new JLabel("Tile Height:"), "gap unrelated");
		infoTileHeight = new JSpinner(new SpinnerNumberModel(10, 10, 50, 1));
		p.add(infoTileHeight, new CC().alignX("leading").maxWidth("70").wrap());
		
		
		infoTileset = new JComboBox(new SpriteSheetData[]{});
		System.out.println(infoTileset.getRenderer());
		
		infoTileset.setEditable(false);
		p.add(new JLabel("Tileset:"), new CC().alignX("leading"));
		infoTileset.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoTileset.getItemCount() <= 0) return;
				SpriteSheetData w= (SpriteSheetData) e.getItem();
				changeTileset(w);
			}
		});
		p.add(infoTileset, "span, growx");
		
		infoTextures = new JComboBox(new IWeapon[]{});
		infoTextures.setEditable(false);
		p.add(new JLabel("Textures:"), new CC().alignX("leading"));
		infoTextures.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoTileset.getItemCount() <= 0) return;
				SpriteSheetData w= (SpriteSheetData) e.getItem();
				changeTextures(w);
			}
		});
		p.add(infoTextures, "span, growx");
		
		
		addSeparator(p,"Editing");

		p.add(new JButton(new EditAction()));
		
		return p;
	}

	protected class EditAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public EditAction() {
			putValue(NAME, "Edit Map");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			// putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			editMap();
		}
	}
	
	@Override
	protected String resourceDisplayName(SavedMap map){
		return map.getUuid().toString();
	}
	
	@Override
	protected String resourceName() {
		return "Map";
	}

	@Override
	protected Maps createAssetsInstance() {
		return new Maps();
	}

	@Override
	protected SavedMap defaultResource() {
		return Config.loadPreference("maps/default.xml");
	}

}
