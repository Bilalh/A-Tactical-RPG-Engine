package editor.map.others;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import common.enums.Orientation;
import common.gui.ResourceManager;
import common.spritesheet.SpriteSheet;

import config.Config;

import view.map.interfaces.IMapRendererParent;
import editor.map.*;
import editor.spritesheet.ISpriteProvider;
import editor.spritesheet.MutableSprite;
import editor.spritesheet.Packer;
import editor.spritesheet.SpriteSheetPanel;
import editor.ui.FloatablePanel;
import editor.util.Prefs;

/**
 * Infrastructure for an editor that need a map renderer. 
 * @author Bilal Hussain
 */
public abstract class AbstactMapEditor extends JFrame implements IEditorMapPanelListener, ISpriteProvider<MutableSprite> {
	private static final Logger log = Logger.getLogger(AbstactMapEditor.class);
	private static final long serialVersionUID = -8019374138498647481L;

	// Map 
	protected OthersMap map;
	protected EditorMapPanel editorMapPanel;
	protected EditorSpriteSheet editorSpriteSheet;
	
	// Map
	protected int mapWidth, mapHeight;
	
	// Gui elements
	protected JScrollPane mapScrollPane;
	protected JPanel infoPanel;
	protected FloatablePanel infoPanelContainer;
	protected SpriteSheetPanel tilesetPanel;
	protected FloatablePanel tilesetsPanelContainer;
	protected Packer packer = new Packer();

	
	// The map view port 
	protected JViewport mapViewport;
	
	// Prefences Node name
	protected String prefsName;
	protected SpriteSheet _sheet;
	protected EditorSpriteSheet sheet;
	
	public AbstactMapEditor(String tile, String prefsName, int mapWidth, int mapHeight){
		super(tile);
		this.mapWidth   = mapWidth;
		this.mapHeight  = mapHeight;
		this.prefsName  = prefsName;
		
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		// FIXME change
		ResourceManager.instance().loadItemSheetFromResources("images/items/items.png");
		_sheet = ResourceManager.instance().getCurrentItemSheet();
		sheet  = new EditorSpriteSheet(_sheet);
		
		this.setContentPane(createContentPane(prefsName));
		
		Preferences pref = Prefs.getNode(prefsName+ "/panels/main");
		System.out.println(pref);
		int width = pref.getInt("width", 900);
		int height = pref.getInt("height", 550);
		
		this.setSize(width, height);
		infoPanelContainer.restore();
		tilesetsPanelContainer.restore();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				onQuit();
			}
		});
		
	}
	
	// Create Gui.
	private JPanel createContentPane(String prefName) {
		mapScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mapScrollPane.setBorder(null);
	
		infoPanel = createInfoPanel();
		infoPanelContainer = new FloatablePanel(this, infoPanel, "Info", prefName);
		
		
		
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, mapScrollPane, infoPanelContainer);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(1.0);
		mainSplit.setBorder(null);
		
		tilesetPanel = new SpriteSheetPanel(this);
		tilesetsPanelContainer = new FloatablePanel(this, tilesetPanel, "Icons", prefName+"-tilesets");
		
		tilesetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
		
		JSplitPane paletteSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true, mainSplit, tilesetsPanelContainer);
		paletteSplit.setOneTouchExpandable(true);
		paletteSplit.setResizeWeight(1.0);
	
		
		JPanel main = new JPanel(new BorderLayout());
		main.add(paletteSplit, BorderLayout.CENTER);
		createMap();
		
		mapScrollPane.setViewportView(editorMapPanel);
		mapViewport = mapScrollPane.getViewport();
		
		MouseAdapter mapScroller = new MapScroller(mapViewport,editorMapPanel, this);
        editorMapPanel.addMouseMotionListener(mapScroller);
        editorMapPanel.addMouseListener(mapScroller);
		return main;
	}
	
	protected void refreashSprites(){
		if (tilesetPanel.getHeight() <=0 || tilesetPanel.getWidth() <=0 ) return;
		tilesetPanel.setSpriteSheet(packer.packImagesByName(sheet.getSprites(),
				tilesetPanel.getWidth(), 
				tilesetPanel.getHeight(), 2));
	}
	
	// Subclasses make the infomation panel.
	protected abstract JPanel createInfoPanel();
	
	// Save window size and panel size 
	protected void onQuit() {
		log.info("Quiting");
		final int extendedState = this.getExtendedState();
		final Preferences pref = Prefs.getNode(prefsName+ "/panels/main");
		pref.putInt("state", extendedState);
		if (extendedState == Frame.NORMAL) {
			pref.putInt("width", this.getWidth());
			pref.putInt("height", this.getHeight());
		}

		// Allow the floatable panels to save their position and size
		infoPanelContainer.save();
		tilesetsPanelContainer.save();

		try {
			Prefs.root().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		log.info("Saved prefs" + Prefs.root());
	}
	
	// Creates the map
	private void createMap() {
		map = new OthersMap(mapWidth,mapHeight);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField());
	} 
	
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

	// Subclass can override these  methods to get events
	

	/** @category ISpriteProvider**/
	@Override
	public void select(List<MutableSprite> selection) {
		if (selection.size() > 1){
			ArrayList<MutableSprite>  a = new ArrayList<MutableSprite>();
			a.add(selection.get(0));
			selection =a;
		}
		tilesetPanel.setSelectedSprites(selection);
	}

	/** @category ISpriteProvider**/
	@Override
	public void delete(List<MutableSprite> selected) {
		// FIXME delete method
		
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
