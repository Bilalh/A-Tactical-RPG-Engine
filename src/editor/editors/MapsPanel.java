package editor.editors;

import static editor.editors.AbstractSpriteSheetOrganiser.sortedSprites;
import static util.IOUtil.replaceExtension;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.layout.CC;

import org.apache.log4j.Logger;

import common.interfaces.IWeapon;

import config.Config;
import config.assets.DeferredMap;
import config.assets.Maps;
import config.assets.UnitPlacement;
import config.xml.*;
import editor.Editor;
import editor.map.IMapEditorListener;
import editor.map.MapEditor;
import engine.unit.SpriteSheetData;

/**
 * Editor for maps 
 * @author Bilal Hussain
 */
public class MapsPanel extends AbstractResourcesPanel<DeferredMap, Maps> implements IMapEditorListener {
	private static final long serialVersionUID = -6885584804612641268L;
	private static final Logger log = Logger.getLogger(MapsPanel.class);

	private JTextField infoName;

	private JSpinner infoWidth;
	private JSpinner infoHeight;
	
	private JSpinner infoTileDiagonal;
	private JSpinner infoTileHeight;	

	private JComboBox infoTileset;
	private JComboBox infoTextures;

	private DeferredMap   currentMap;	
	private ITileMapping  currentMapping;
	private UnitPlacement currentUnitPlacement;
	private MapEvents     currentEvent;
	private MapMusic      currentMusic;
	
	private JTabbedPane    infoTabs;
	private UnitsPanel     enemiesPanel;
	private MapDialogPanel dialogStartPanel;
	private MapDialogPanel dialogEndPanel;
	private MapMusicPanel  musicPanel;
	
	public MapsPanel(Editor editor) {
		super(editor);
	}

	@Override
	public void panelSelected(Editor editor) {
		editor.loadAssets();
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
		
		setCurrentResource(currentMap);
		enemiesPanel.panelSelected(editor);
		dialogStartPanel.panelSelected(editor);
		dialogEndPanel.panelSelected(editor);
		musicPanel.panelSelected(editor);
		musicPanel.setMapMusic(editor.getMusic(), editor.getSounds(), currentMusic);
	}

	@Override
	public Maps getResouces() {
		saveExternal();
		return super.getResouces();
	}

	// Save data in external files
	private void saveExternal(){
		if (currentMap == null) return;
		Config.savePreferences(currentMapping, currentMap.getAsset().getMapData().getTileMappingLocation());	
		currentUnitPlacement.setUnits(enemiesPanel.getUnits());
		Config.savePreferences(currentUnitPlacement, currentMap.getAsset().getMapData().getEnemiesLocation());	
		Config.savePreferences(new MapEvents(dialogStartPanel.getResouces(), dialogEndPanel.getResouces()) , currentMap.getAsset().getMapData().getEventsLocation());	
		Config.savePreferences(currentMusic , currentMap.getAsset().getMapData().getMusicLocation());	
	}

	protected UnitPlacement getEnemies(){
		return currentUnitPlacement;
	}
	
	@Override
	protected void setCurrentResource(DeferredMap _map) {
		// Makes sures are the data from the old map was changed
		if (_map != currentMap && _map != null) saveExternal();
		
		currentMap = _map;
		SavedMap map = currentMap.getAsset();
		assert map != null;
		assert map.getMapData().getName() != null;
		
		infoName.setText(map.getMapData().getName());
		
		infoWidth.setValue(map.getFieldWidth());
		infoHeight.setValue(map.getFieldHeight());
		
		infoTileDiagonal.setValue(map.getMapSettings().tileDiagonal);
		infoTileHeight.setValue(map.getMapSettings().tileHeight);
		
		SpriteSheetData d =  Config.loadPreference(replaceExtension(map.getMapData().getTexturesLocation(),"-animations.xml"));
		assert d != null;
		infoTextures.setSelectedItem(d);

		currentMapping= Config.loadPreference(map.getMapData().getTileMappingLocation());
		d = Config.loadPreference(replaceExtension(currentMapping.getSpriteSheetLocation(),"-animations.xml"));
		assert d != null;
		infoTileset.setSelectedItem(d);
		
		currentUnitPlacement = Config.loadPreference(map.getMapData().getEnemiesLocation());
		enemiesPanel.setUnits(currentUnitPlacement.getUnits());
		
		currentMusic = Config.loadPreference(map.getMapData().getMusicLocation());
		
		currentEvent = Config.loadPreference(map.getMapData().getEventsLocation());
		dialogStartPanel.setResources(currentEvent.getStartDialog());
		dialogEndPanel.setResources(currentEvent.getEndDialog());
		dialogStartPanel.panelSelected(editor);
		dialogEndPanel.panelSelected(editor);
		musicPanel.panelSelected(editor);
		musicPanel.setMapMusic(editor.getMusic(), editor.getSounds(), currentMusic);
		
	}

	@Override
	protected void addToList() {
		//FIXME addToList
	}

	private void changeName(){
		SavedMap m = currentMap.getAsset(); 
		m.setMapData(m.getMapData().changeName(infoName.getText()));
		resourceList.repaint();
	}	
	
	private void changeTileset(SpriteSheetData ui){
		currentMapping = new TileMapping(ui.getSpriteSheetLocation(), currentMapping.getTilemapping());
	}

	private void changeTextures(SpriteSheetData ui){
		SavedMap m = currentMap.getAsset(); 
		m.setMapData(m.getMapData().changeTexturesLocation(ui.getSpriteSheetLocation()));
	}


	private void changeTileDiagonal(int value) {
		currentMap.getAsset().getMapSettings().tileDiagonal = value;
	}
	
	private void changeTileHeight(int value) {
		currentMap.getAsset().getMapSettings().tileHeight = value;
	}
	
	@SuppressWarnings("unused")
	private void editMap(){
		editor.setVisible(false);
		saveExternal();
		currentMap.saveAsset();
		new MapEditor(WindowConstants.DO_NOTHING_ON_CLOSE,  currentMap.getResouceLocation(), this);
		
	}

	@Override
	public void mapEditingFinished() {
		currentMap.reloadAsset();
		setCurrentResource(currentMap);
		editor.setVisible(true);
	}
	
	@Override
	protected JComponent createInfoPanel() {
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
		infoWidth.setEnabled(false);
		
		p.add(new JLabel("Map Height:"), "gap unrelated");
		infoHeight = new JSpinner(new SpinnerNumberModel(15, 5, 50, 1));
		p.add(infoHeight, new CC().alignX("leading").maxWidth("70").wrap());
		infoHeight.setEnabled(false);
		
		addSeparator(p,"Tiles");

		p.add(new JLabel("Tile Diagonal:"),new CC().alignX("leading"));
		infoTileDiagonal = new JSpinner(new SpinnerNumberModel(60, 40, 256, 5));
		p.add(infoTileDiagonal, new CC().alignX("leading").maxWidth("70"));

		infoTileDiagonal.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeTileDiagonal(((Number)infoTileDiagonal.getValue()).intValue());
			}
		});
		
		p.add(new JLabel("Tile Height:"), "gap unrelated");
		infoTileHeight = new JSpinner(new SpinnerNumberModel(10, 10, 50, 1));
		p.add(infoTileHeight, new CC().alignX("leading").maxWidth("70").wrap());
		
		infoTileHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeTileHeight(((Number)infoTileHeight.getValue()).intValue());
			}
		});
		
		infoTileset = new JComboBox(new SpriteSheetData[]{});
		
		infoTileset.setEditable(false);
		infoTileset.setEnabled(false);
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
		infoTextures.setEnabled(false);
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
		p.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border

		infoTabs = new JTabbedPane();
		infoTabs.addTab("Details", p);
		infoTabs.addTab("Enemies ",      enemiesPanel = new UnitsPanel(editor.getUnitsSprites(), false, "Enemy"));
		infoTabs.addTab("Start Dialog",  dialogStartPanel = new MapDialogPanel(this, editor));
		infoTabs.addTab("Finish Dialog", dialogEndPanel = new MapDialogPanel(this, editor));
		infoTabs.addTab("Music",         musicPanel = new MapMusicPanel());
		
		return infoTabs;
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
	protected String resourceDisplayName(DeferredMap map, int index){
		assert map != null;
		return map.getAsset().getMapData().getName();
	}
	
	@Override
	protected String resourceName() {
		return "Map";
	}

	@Override
	protected Maps createAssetInstance() {
		return new Maps();
	}

	@Override
	protected DeferredMap defaultResource() {
		return new DeferredMap(Config.<SavedMap>loadPreference("maps/default.xml"),"maps/default.xml");
	}


}
