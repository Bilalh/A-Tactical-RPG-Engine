package editor.editors;

import static editor.editors.AbstractSpriteSheetOrganiser.sortedSprites;
import static util.IOUtil.replaceExtension;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.*;

import net.miginfocom.layout.CC;

import org.apache.log4j.Logger;

import util.IOUtil;

import common.Location;
import common.enums.Orientation;
import common.interfaces.IWeapon;
import common.spritesheet.Sprites;

import config.Config;
import config.assets.*;
import config.xml.*;
import editor.Editor;
import editor.map.IMapEditorListener;
import editor.map.MapEditor;
import engine.map.win.DefeatAllUnitsCondition;
import engine.unit.SpriteSheetData;
import engine.unit.Unit;

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

	private JButton infoEdit, infoCreate, infoCancel;
	
	private DeferredMap   currentMap;	
	private ITileMapping  currentMapping;
	private UnitPlacement currentUnitPlacement;
	private MapEvents     currentEvent;
	private MapMusic      currentMusic;
	private MapConditions currentConditions;
	
	private JTabbedPane    infoTabs;
	private AiUnitPanel    enemiesPanel;
	private MapDialogPanel dialogStartPanel;
	private MapDialogPanel dialogEndPanel;
	private MapMusicPanel  musicPanel;
	
	private MapConditionsPanel conditionsPanel;
	
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
		
		assert currentConditions != null;
		
		//FIXME needed?
		enemiesPanel.panelSelected(editor);
		dialogStartPanel.panelSelected(editor);
		dialogEndPanel.panelSelected(editor);
		musicPanel.panelSelected(editor);
		musicPanel.setMapMusic(editor.getMusic(), editor.getSounds(), currentMusic);
		conditionsPanel.panelSelected(editor);
		conditionsPanel.setMapConditions(currentConditions);
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
		assert currentConditions.getWinCondition() != null;
		Config.savePreferences(currentConditions , currentMap.getAsset().getMapData().getConditionsLocation());
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
		
		currentMusic      = Config.loadPreference(map.getMapData().getMusicLocation());
		currentEvent      = Config.loadPreference(map.getMapData().getEventsLocation());
		currentConditions = Config.loadPreference(map.getMapData().getConditionsLocation());
		
		assert currentMusic != null;
		assert currentEvent != null;
		assert currentConditions != null;
		
		enemiesPanel.panelSelected(editor);
		dialogStartPanel.setResources(currentEvent.getStartDialog());
		dialogEndPanel.setResources(currentEvent.getEndDialog());
		dialogStartPanel.panelSelected(editor);
		dialogEndPanel.panelSelected(editor);
		musicPanel.panelSelected(editor);
		musicPanel.setMapMusic(editor.getMusic(), editor.getSounds(), currentMusic);
		conditionsPanel.panelSelected(editor);
		conditionsPanel.setMapConditions(currentConditions);
		
	}

	private void changeName(){
		SavedMap m = currentMap.getAsset(); 
		m.setMapData(m.getMapData().changeName(infoName.getText()));
		resourceList.repaint();
	}	
	
//	private void changeTileset(SpriteSheetData ui){
//		currentMapping = new TileMapping(ui.getSpriteSheetLocation(), currentMapping.getTilemapping());
//	}
//
//	private void changeTextures(SpriteSheetData ui){
//		SavedMap m = currentMap.getAsset(); 
//		m.setMapData(m.getMapData().changeTexturesLocation(ui.getSpriteSheetLocation()));
//	}


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

	boolean creating;
	ListSelectionListener savedListener;
	@Override
	protected void addToList() {
		//FIXME addToList
		infoTabs.setEnabled(false);
		creating = true;
		
		savedListener = resourceList.getListSelectionListeners()[0];
		resourceList.removeListSelectionListener(savedListener);
		resourceList.setEnabled(false);
		
		infoName.setEnabled(false);
		infoTileDiagonal.setEnabled(false);
		infoTileHeight.setEnabled(false);

		infoWidth.setEnabled(true);
		infoHeight.setEnabled(true);
		infoTileset.setEnabled(true);
		infoTextures.setEnabled(true);
		
		infoEdit.setVisible(false);
		infoCreate.setVisible(true);
		infoCancel.setVisible(true);
		
		listAddButton.setEnabled(false);
		listDeleteButton.setEnabled(false);
		editor.setTabsEnabled(false);
	}

//	currentMapping = new TileMapping(ui.getSpriteSheetLocation(), currentMapping.getTilemapping());
//	m.setMapData(m.getMapData().changeTexturesLocation(ui.getSpriteSheetLocation()));
	
	private void cancel() {
		finishedCreating();
	}
	
	//	currentMapping = new TileMapping(ui.getSpriteSheetLocation(), currentMapping.getTilemapping());
	//	m.setMapData(m.getMapData().changeTexturesLocation(ui.getSpriteSheetLocation()));
		
		private void finishedCreating(){
		resourceList.addListSelectionListener(savedListener);
		savedListener = null;
		
		infoName.setEnabled(true);
		infoTileDiagonal.setEnabled(true);
		infoTileHeight.setEnabled(true);
	
		infoWidth.setEnabled(false);
		infoHeight.setEnabled(false);
		infoTileset.setEnabled(false);
		infoTextures.setEnabled(false);
		
		infoEdit.setVisible(true);
		infoCreate.setVisible(false);
		infoCancel.setVisible(false);
		
		infoTabs.setEnabled(true);
		creating = false;
		resourceList.setEnabled(true);
		listAddButton.setEnabled(true);
		listDeleteButton.setEnabled(true);
		editor.setTabsEnabled(true);
	}

	private void createMap() {
		// Map setting and Map Data
		MapSettings settings = MapSettings.defaults();
		SpriteSheetData dtextures = (SpriteSheetData) infoTextures.getSelectedItem();
		SpriteSheetData dtiles = (SpriteSheetData) infoTileset.getSelectedItem();
		MapData data = new MapData("New Map", dtiles.getAnimationPath(), dtextures.getSpriteSheetLocation());

		// External Data
		Units ememies = new Units();
		Unit u = new Unit("Unit 1", 20, 4, 10, 15);
		IWeapon w = editor.getWeapons().iterator().next();
		u.setWeapon(w);
		SpriteSheetData images = editor.getUnitImages().iterator().next();
		u.setImageData(images.getAnimationPath(), images);
		ememies.put(u);

		HashMap<UUID, Location> placement = new HashMap<UUID, Location>();
		placement.put(u.getUuid(), new Location(0, 0));

		UnitPlacement up = new UnitPlacement(ememies, placement);
		MapEvents events = new MapEvents(new DialogParts(), new DialogParts());

		MapMusic music = new MapMusic();
		Musics musics = editor.getMusic();
		music.setBackground(musics.iterator().next().getUuid());
		
		Musics sounds = editor.getSounds();
		UUID musicId = sounds.iterator().next().getUuid();
		music.setAttackSound(musicId);
		music.setDefeatUnitSound(musicId);
		music.setLoseMapSound(musicId);
		music.setLoseUnitSound(musicId);
		music.setWinMapSound(musicId);
		
		MapConditions conditions = new MapConditions();
		conditions.setWinCondition(new DefeatAllUnitsCondition());

		// Tiles
		Sprites ss = Config.loadPreference(IOUtil.replaceExtension(dtiles.getSpriteSheetLocation(), ".xml"));
		String tileName = ss.getSprites()[0].getName();

		Sprites tt = Config.loadPreference(IOUtil.replaceExtension(dtextures.getSpriteSheetLocation(), ".xml"));
		String textureName = tt.getSprites()[0].getName();

		int width = ((Number) infoWidth.getValue()).intValue();
		int height = ((Number) infoHeight.getValue()).intValue();
		SavedTile[] tiles = new SavedTile[width * height];

		for (int i = 0, k = 0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				tiles[k] = new SavedTile(
						tileName,
						1,
						1,
						i, j,
						Orientation.TO_EAST,
						textureName,
						textureName);
			}
		}

		SavedMap map = new SavedMap(
				((Number) infoWidth.getValue()).intValue(),
				((Number) infoHeight.getValue()).intValue(),
				tiles,
				settings,
				data);

		String name = "map_" + map.getUuid();
		DeferredMap newMap = new DeferredMap(map, "maps/" + name + ".xml");

		// Set the real location
		data = new MapData(name, IOUtil.replaceExtension(dtiles.getSpriteSheetLocation(), "-mapping.xml"),
				dtextures.getSpriteSheetLocation());
		map.setMapData(data.changeName("New Map " + (resourceListModel.size() + 1)));

		// Saving
		Config.savePreferences(up, data.getEnemiesLocation());
		Config.savePreferences(events, data.getEventsLocation());
		Config.savePreferences(music, data.getMusicLocation());
		Config.savePreferences(conditions, data.getConditionsLocation());
		newMap.saveAsset();

		resourceListModel.addElement(newMap);
		finishedCreating();
		resourceList.setSelectedIndex(resourceListModel.size() - 1);
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
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoWidth);
		p.add(infoWidth, new CC().alignX("leading").maxWidth("70"));
		infoWidth.setEnabled(false);
		
		p.add(new JLabel("Map Height:"), "gap unrelated");
		infoHeight = new JSpinner(new SpinnerNumberModel(15, 5, 50, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoHeight);
		p.add(infoHeight, new CC().alignX("leading").maxWidth("70").wrap());
		infoHeight.setEnabled(false);
		
		addSeparator(p,"Tiles");

		p.add(new JLabel("Tile Diagonal:"),new CC().alignX("leading"));
		infoTileDiagonal = new JSpinner(new SpinnerNumberModel(60, 40, 256, 5));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoTileDiagonal);
		p.add(infoTileDiagonal, new CC().alignX("leading").maxWidth("70"));

		infoTileDiagonal.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changeTileDiagonal(((Number)infoTileDiagonal.getValue()).intValue());
			}
		});
		
		p.add(new JLabel("Tile Height:"), "gap unrelated");
		infoTileHeight = new JSpinner(new SpinnerNumberModel(10, 10, 50, 1));
		AbstractResourcesPanel.makeJSpinnerSaveOnType(infoTileHeight);
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
		p.add(infoTileset, "span, growx");
		
		infoTextures = new JComboBox(new IWeapon[]{});
		infoTextures.setEditable(false);
		infoTextures.setEnabled(false);
		p.add(new JLabel("Textures:"), new CC().alignX("leading"));
		p.add(infoTextures, "span, growx");
		
		
		addSeparator(p,"Editing");

		p.add(infoEdit   =  new JButton(new EditAction()));
		p.add(infoCreate =  new JButton(new CreateAction()));
		p.add(infoCancel =  new JButton(new CancelAction()));
		infoCreate.setVisible(false);
		infoCreate.setVisible(false);
		infoCreate.setVisible(false);
		infoCreate.setVisible(false);
		infoCancel.setVisible(false);
		
		p.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border

		infoTabs = new JTabbedPane();
		infoTabs.addTab("Details", p);
		infoTabs.addTab("Enemies ",       enemiesPanel     = new AiUnitPanel(editor.getUnitsSprites(), false, "Enemy"));
		infoTabs.addTab("Start Dialog",   dialogStartPanel = new MapDialogPanel(this, editor));
		infoTabs.addTab("Finish Dialog",  dialogEndPanel   = new MapDialogPanel(this, editor));
		infoTabs.addTab("Music",          musicPanel       = new MapMusicPanel());
		infoTabs.addTab("Win Condition",  conditionsPanel  = new MapConditionsPanel(this));
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

	protected class CreateAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public CreateAction() {
			putValue(NAME, "Create Map");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			// putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			createMap();
		}
	}
	
	protected class CancelAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public CancelAction() {
			putValue(NAME, "Cancel");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			// putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cancel();
		}
	}
	
	@Override
	protected String resourceDisplayName(DeferredMap map, int index){
		assert map != null;
		SavedMap m = map.getAsset();
		if (m == null) return "New Map";
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
		return null;
	}


}
