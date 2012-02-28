package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;

import org.apache.log4j.Logger;

import common.gui.ResourceManager;
import common.spritesheet.SpriteSheet;

import config.xml.MapSettings;
import editor.map.*;
import editor.map.others.OthersMap;
import editor.spritesheet.ISpriteProvider;
import editor.spritesheet.MutableSprite;
import editor.spritesheet.Packer;
import editor.spritesheet.SpriteSheetPanel;
import editor.ui.FloatablePanel;
import editor.ui.HeaderPanel;
import editor.util.Prefs;

/**
 * Infrastructure for an editor panel that need a map renderer. 
 * @author Bilal Hussain
 */
public abstract class AbstactMapEditorPanel extends JPanel implements IEditorMapPanelListener, ISpriteProvider<MutableSprite> {
	private static final Logger log = Logger.getLogger(AbstactMapEditorPanel.class);
	private static final long serialVersionUID = -8019374138498647481L;

	// Map 
	protected OthersMap map;
	protected EditorMapPanel editorMapPanel;
	protected EditorSpriteSheet editorSpriteSheet;
	
	// Map
	protected int mapWidth, mapHeight;
	
	// Gui elements
	protected JScrollPane mapScrollPane;
	protected SpriteSheetPanel tilesetPanel;
	protected JPanel tilesetPanelWithHeader;
	protected Packer packer = new Packer();

	// The map view port 
	protected JViewport mapViewport;
	
	// Prefences Node name
	protected String prefsName;
	
	protected SpriteSheet _sheet;
	protected EditorSpriteSheet sheet;
	protected MutableSprite currentIconImage;
	
	public AbstactMapEditorPanel(String tile, int mapWidth, int mapHeight){
		super(new BorderLayout());
		this.mapWidth   = mapWidth;
		this.mapHeight  = mapHeight;
		this.prefsName  = tile;
		
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		// FIXME change
		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");
		_sheet = ResourceManager.instance().getCurrentItemSheet();
		sheet  = new EditorSpriteSheet(_sheet);
		
		createMainPane(tile);
		
		Preferences pref = Prefs.getNode(tile+ "/panels/main");
		System.out.println(pref);
		int width = pref.getInt("width", 930);
		int height = pref.getInt("height", 680);
		
		this.setSize(width, height);
		
//		this.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent event) {
//				onQuit();
//			}
//		});
		
	}
	
	protected JPanel createMainPane(String prefName) {
		mapScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mapScrollPane.setBorder(null);
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(mapScrollPane, BorderLayout.CENTER);
		p.add(createHeader(prefName + "'s Range"),BorderLayout.NORTH);
		
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true, createInfoPanel(), p);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(1.0);
		mainSplit.setBorder(null);
		
		tilesetPanel = new SpriteSheetPanel(this);
		
		tilesetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
		tilesetPanel.setMinimumSize(new Dimension(150, 500));
	
		tilesetPanelWithHeader = new JPanel(new BorderLayout());
		tilesetPanelWithHeader.add(tilesetPanel, BorderLayout.CENTER);
		tilesetPanelWithHeader.add(createHeader("Icons"),BorderLayout.NORTH);

		
		JSplitPane paletteSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, mainSplit, tilesetPanelWithHeader);
		paletteSplit.setOneTouchExpandable(true);
		paletteSplit.setResizeWeight(1.0);
	
		JSplitPane aa = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), paletteSplit);
		aa.setOneTouchExpandable(true);
		aa.setResizeWeight(0.1);
		aa.setBorder(null);
		
		this.add(aa, BorderLayout.CENTER);
		createMap();
		
		mapScrollPane.setViewportView(editorMapPanel);
		mapViewport = mapScrollPane.getViewport();
		
		MouseAdapter mapScroller = new MapScroller(mapViewport,editorMapPanel, this);
        editorMapPanel.addMouseMotionListener(mapScroller);
        editorMapPanel.addMouseListener(mapScroller);
		return this;
	}
	
	protected void refreashSprites(){
		if (tilesetPanel.getHeight() <=0 || tilesetPanel.getWidth() <=0 ) return;
		tilesetPanel.setSpriteSheet(packer.packImagesByName(sheet.getSprites(),
				tilesetPanel.getWidth(), 
				tilesetPanel.getHeight(), 2));
	}
	
	// Creates the map
	protected void createMap() {
		map = new OthersMap(mapWidth,mapHeight, MapSettings.defaults());
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),map.getMapSettings());
	} 
	
	
	// creates a header for the panel.
	protected JPanel createHeader(String text){
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}
	
	// Subclasses make the infomation panel and left pane.
	protected abstract JPanel createInfoPanel();
	protected abstract JComponent createLeftPane();

	
//	// Save window size and panel size 
//	protected void onQuit() {
//		log.info("Quiting");
//		final int extendedState = this.getExtendedState();
//		final Preferences pref = Prefs.getNode(prefsName+ "/panels/main");
//		pref.putInt("state", extendedState);
//		if (extendedState == Frame.NORMAL) {
//			pref.putInt("width", this.getWidth());
//			pref.putInt("height", this.getHeight());
//		}
//
//		// Allow the floatable panels to save their position and size
//		infoPanelContainer.save();
//		tilesetsPanelContainer.save();
//
//		try {
//			Prefs.root().sync();
//		} catch (BackingStoreException e) {
//			e.printStackTrace();
//		}
//		log.info("Saved prefs" + Prefs.root());
//	}
	
	// Since we allways want to redraw the map
	/** @category IMapRendererParent **/
	@Override
	public boolean isMouseMoving() {
		return true;
	}

	/** @category IMapRendererParent **/
	@Override
	public int getDrawX() {
		return 0;
	}

	/** @category IMapRendererParent **/
	@Override
	public int getDrawY() {
		return 0;
	}

	// Subclass can override these methods to get events
	

	/** @category ISpriteProvider**/
	@Override
	public void select(List<MutableSprite> selection) {
		// Only Allow one icon to be selected
		if (selection.size() > 1){
			ArrayList<MutableSprite>  a = new ArrayList<MutableSprite>();
			a.add(selection.get(0));
			selection =a;
		}
		tilesetPanel.setSelectedSprites(selection);
		if (selection.size() == 0) return;
		currentIconImage = selection.get(0);
	}

	/** @category ISpriteProvider**/
	@Override
	public void delete(List<MutableSprite> selected) {
	}
	
	/** @category IEditorMapPanelListener **/
	@Override
	public void tileClicked(EditorIsoTile tile) {
	}

	/** @category IEditorMapPanelListener **/
	@Override
	public void tileEntered(EditorIsoTile tile) {
		
	}

	/** @category IEditorMapPanelListener **/
	@Override
	public void tilesSelected(ArrayList<EditorIsoTile> tiles, boolean shiftDown) {

	}
	
	/** @category IEditorMapPanelListener **/
	@Override
	public MapState getEditorState() {
		return MapState.EYE;
	}
	
}
