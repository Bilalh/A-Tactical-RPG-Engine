package editor.map;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.jvnet.inflector.Noun;

import view.map.GuiMap;
import view.map.interfaces.IMapRendererParent;
import view.units.AnimatedUnit;

import common.Location;
import common.enums.ImageType;
import common.enums.Orientation;
import common.gui.ResourceManager;
import common.interfaces.IMapUnit;
import common.interfaces.IWeapon;

import config.Config;
import config.XMLUtil;
import config.assets.AssetStore;
import config.xml.MapData;
import config.xml.SavedMap;
import config.xml.SavedTile;
import editor.editors.AbstractResourcesPanel;
import editor.editors.UnitsPanel;
import editor.editors.UnitsPanel.UnitListRenderer;
import editor.editors.UnitsPanel.WeaponDropDownListRenderer;
import editor.map.*;
import editor.spritesheet.*;
import editor.ui.FloatablePanel;
import editor.ui.TButton;
import editor.util.Prefs;
import editor.util.EditorResources;
import engine.map.MapUnit;
import engine.map.Tile;
import engine.map.interfaces.IMutableMapUnit;
import engine.unit.IMutableUnit;
import engine.unit.SpriteSheetData;
import engine.unit.Unit;

/**
 * Map Editor for the engine
 * @author Bilal Hussain
 */
public class MapEditor implements ActionListener, IEditorMapPanelListener {
	private static final Logger log = Logger.getLogger(MapEditor.class);

	private JFrame frame;

	private AbstractButton eyeButton,    selectionButton, moveButton,  placeButton;
	private AbstractButton drawButton,   drawInfoButton,  fillButton;
	private final Action   zoomInAction, zoomOutAction;

	private AbstractButton leftWallButton,  rightWallButton, startButton;
	
	private MapState state = MapState.DRAW;

	private JPanel           infoPanel;
	private FloatablePanel   infoPanelContainer;
	
	private JTabbedPane      spritesTabs;
	private SpriteSheetPanel tilesetPanel;
	private SpriteSheetPanel texturesPanel;
	private FloatablePanel   spritesPanelContainer;
	private JPanel           emenemiesPanel;
	
	private JLabel statusLabel;

	private EditorMap map;
	private EditorMapPanel editorMapPanel;
	private JScrollPane    mapScrollPane;

	
	private Packer packer = new Packer();

	private MutableSprite selectedTileSprite;
	private MutableSprite selectedTextureSprite;

	private HashSet<EditorIsoTile> selection = new HashSet<EditorIsoTile>();

	// Highlight the tile the mouse is over.
	private boolean showOnHover = false;
	
	
	// Infopanel controls 
	private JLabel     infoLocation;
	private JTextField infoType;
	private JComboBox  infoOrientation   = new JComboBox(Orientation.values());
	private JSpinner   infoHeight        = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
	private JSpinner   infoStartHeight   = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
	private JLabel     infoStartHeightl; 

	private JLabel     infoStartLocation; 
	
	// For draw
	private EditorIsoTile selectedTile;

	// for editor
	private String savePath;	
	private IMapEditorListener listener;
	
	private HashMap<UUID, AnimatedUnit> toMapUnit;
	
	public MapEditor(int frameClosingValue, String savePath, IMapEditorListener listener) {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		
		this.savePath = savePath;
		this.listener = listener;
		
		zoomInAction = new ZoomIn();
		zoomOutAction = new ZoomOut();

		frame = new JFrame("Map Editor");
		frame.setContentPane(createContentPane());
		frame.setJMenuBar(createMenubar());
		frame.setDefaultCloseOperation(frameClosingValue);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (onQuit()){
					MapEditor.this.listener.mapEditingFinished();
					MapEditor.this.frame.dispose();
				}
			}
		});
		
		infoPanelContainer.restore();
		spritesPanelContainer.restore();

		Preferences pref = Prefs.getNode("map/panels/main");
		System.out.println(pref);
		int width = pref.getInt("width", 1280);
		int height = pref.getInt("height", 800);
		frame.setMinimumSize(new Dimension(850, 700));
		
		frame.setSize(width, height);
		frame.setVisible(true);
	}

	 /** @category Gui **/
	private boolean onQuit() {
		log.info("Quiting");
		final int extendedState = frame.getExtendedState();
		final Preferences pref = Prefs.getNode("map/panels/main");
		pref.putInt("state", extendedState);
		if (extendedState == Frame.NORMAL) {
			pref.putInt("width", frame.getWidth());
			pref.putInt("height", frame.getHeight());
		}

		// Allow the floatable panels to save their position and size
		infoPanelContainer.save();
		spritesPanelContainer.save();
		
		try {
			Prefs.root().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		log.info("Saved prefs" + Prefs.root());
		
		save();
		
		return true;
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

	
	// Old tile
	private EditorIsoTile old;
	@Override
	public void tileEntered(EditorIsoTile tile) {
		if (old != null) {
			old.setSelected(false);
		}
		if (showOnHover && state != MapState.SELECTION && state != MapState.MOVE){
			tile.setSelected(true);
			editorMapPanel.repaintMap();
			old = tile;
		}
		statusLabel.setText(tile.toFormatedString());
	}
	
	/**
	 * Handles the tile click based on the selected button 
	 * @category Callback 
	 */
	@Override
	public void tileClicked(EditorIsoTile tile) {
		boolean repaint = false;
		selectedTile = tile;
		switch (state) {
			case DRAW:
				if (tile.getType() == ImageType.TEXTURED){
					if (selectedTextureSprite != null){
						map.setTileTexture(tile.getLocation(), selectedTextureSprite);
						repaint = true;
					}					
				}else if (tile.getType() == ImageType.NON_TEXTURED){
					if (selectedTileSprite != null){
						map.setTileSprite(tile.getLocation(), selectedTileSprite);
						repaint = true;
					}
				}

				break;
				
			case LEFT_WALL:
				if (selectedTextureSprite != null){
					map.setLeftWallSprite(tile.getLocation(), selectedTextureSprite);
					repaint = true;
				}
				break;

			case RIGHT_WALL:
				if (selectedTextureSprite != null){
					map.setRightWallSprite(tile.getLocation(), selectedTextureSprite);
					repaint = true;
				}
				break;
				
			case DRAW_INFO:
				int height = ((Number) infoHeight.getValue()).intValue();
				if (height >=0){
					map.setHeight(tile.getLocation(), height);
					repaint = true;
				}
				
				if (tile.getType() == ImageType.TEXTURED){
					if (selectedTextureSprite != null){
						map.setTileTexture(tile.getLocation(), selectedTextureSprite);
						repaint = true;
					}
					int startHeight = ((Number) infoStartHeight.getValue()).intValue();
					if (startHeight >=0){
						map.setStartingHeight(tile.getLocation(), startHeight);
						repaint = true;
					}
					
				}else if (tile.getType() == ImageType.NON_TEXTURED){
					if (selectedTileSprite != null){
						map.setTileSprite(tile.getLocation(), selectedTileSprite);
						repaint = true;
					}
					
				}

				
				Orientation o = (Orientation) infoOrientation.getSelectedItem();
				if (o != tile.getOrientation()){
					map.setOrientation(tile.getLocation(),o);
					repaint = true;
				}
				
			case EYE:
				if (selectedTile.getType() == ImageType.TEXTURED){
					selectedTextureSprite = tile.getSprite();
					texturesPanel.setSelectedSprites(selectedTextureSprite);
				}else{
					selectedTileSprite = tile.getSprite();
					tilesetPanel.setSelectedSprites(selectedTileSprite);	
				}
				break;
				
			case FILL:
				if (selectedTileSprite != null && selection.contains(tile)){
					for (EditorIsoTile t : selection) {
						if (t.getType() == ImageType.TEXTURED) continue;
						map.setTileSprite(t.getLocation(), selectedTileSprite);
					}
				}
				repaint = true;
				break;
				
			case PLACE:{
				assert currentEnemy != null;
				AnimatedUnit a  = toMapUnit.get(currentEnemy.getUuid()); 
				assert a != null;
				if (tile.getOrientation() == Orientation.EMPTY || tile.getUnit() != null){
					break;
				}
				
				if (!a.getLocation().equals(new Location(-1,-1))){
					map.getGuiTile(a.getLocation()).removeUnit();
				}
				a.setLocation(tile.getLocation());
				tile.setUnit(a);
				map.addMapping(currentEnemy.getUuid(), tile.getLocation());
				repaint=true;
				setCurrentEnemy(currentEnemy);
				
				break;
			}
			case START:{
				if (tile.getOrientation() == Orientation.EMPTY || tile.getUnit() != null){
					break;
				}
				map.setStartLocation(tile.getLocation());
				infoStartLocation.setText(String.format("  (%s, %s)", tile.getX(), tile.getY()));
			}
				
		}

		
		if (state==MapState.SELECTION){
			removeSelection(selection);
			selection.clear();
			selection.add(tile);	
		}
		
		if (state == MapState.DRAW_INFO || state == MapState.DRAW){
			infoHeight.setValue(tile.getEndHeight());
			infoType.setText(tile.getSprite().getName());
			infoOrientation.setSelectedItem(tile.getOrientation());
			infoLocation.setText(String.format("(%s,%s)", tile.getX(), tile.getY()));	
		}
		
		if (state == MapState.EYE){
			ChangeListener cl =  infoHeight.getChangeListeners()[0];
			infoHeight.removeChangeListener(cl);
			infoHeight.setValue(tile.getEndHeight());
			infoHeight.addChangeListener(cl);
			
			ItemListener il = infoOrientation.getItemListeners()[0];
			infoOrientation.setSelectedItem(tile.getOrientation());
			infoOrientation.addItemListener(il);
			
			infoLocation.setText(String.format("(%s,%s)", tile.getX(), tile.getY()));	

		}

		if (selectedTile.getType() == ImageType.TEXTURED){
			infoStartHeight.setVisible(true);
			infoStartHeightl.setVisible(true);
			infoStartHeight.setValue(selectedTile.getStartHeight());
		}else{
			infoStartHeightl.setVisible(false);
			infoStartHeight.setVisible(false);
		}
		
		if (repaint) editorMapPanel.repaintMap();
	}

	
	private void removeSelection(Iterable<EditorIsoTile> it){
		for (EditorIsoTile t : it) {
			t.setSelected(false);
		}
	}

	 /** @category Callback **/
	@Override
	public void tilesSelected(ArrayList<EditorIsoTile> tiles, boolean shiftDown){
		
		System.out.println();
		System.out.println(selection);
		System.out.println(tiles);
		if (!shiftDown){
			System.out.println("cleered");
			removeSelection(selection);
			selection.clear();
		}
		selection.addAll(tiles);
		System.out.println(selection);
		for (EditorIsoTile t : selection) {
			t.setSelected(true);
		}
		mapScrollPane.repaint();
		editorMapPanel.repaintMap();
	}

	/** @category Gui **/
	private JPanel createContentPane() {
		mapScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mapScrollPane.setBorder(null);
	
		infoPanel = createInfoPanel();
		infoPanelContainer = new FloatablePanel(frame, infoPanel, "Info", "info");
	
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, mapScrollPane, infoPanelContainer);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(1.0);
		mainSplit.setBorder(null);
	
		spritesTabs = new JTabbedPane();

		tilesetPanel = new SpriteSheetPanel(new ISpriteProvider<MutableSprite>() {
			
			@Override
			public void select(List<MutableSprite> selection) {
				if(selection != null && !selection.isEmpty()) {
					selectedTileSprite = selection.get(0);
					selection = new ArrayList<MutableSprite>();
					selection.add(selectedTileSprite);
					tilesetPanel.setSelectedSprites(selection);
				}else{
					selectedTileSprite = null;
				}			
			}
			
			@Override
			public void delete(List<MutableSprite> selected) {
				// Do nothing
			}
		});
		spritesTabs.addTab("Tiles", tilesetPanel);

		texturesPanel= new SpriteSheetPanel(new ISpriteProvider<MutableSprite>() {
			
			@Override
			public void select(List<MutableSprite> selection) {
				if(selection != null && !selection.isEmpty()) {
					selectedTextureSprite = selection.get(0);
					selection = new ArrayList<MutableSprite>();
					selection.add(selectedTextureSprite);
					texturesPanel.setSelectedSprites(selection);
				}else{
					selectedTextureSprite = null;
				}
			}
			
			@Override
			public void delete(List<MutableSprite> selected) {
				// Do nothing
			}
		});
		spritesTabs.addTab("Textures", texturesPanel);
		spritesPanelContainer = new FloatablePanel(frame, spritesTabs, "Sprites", "tilesets");
		
		JSplitPane paletteSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true, mainSplit, spritesPanelContainer);
		paletteSplit.setOneTouchExpandable(true);
		paletteSplit.setResizeWeight(1.0);
	
		JPanel main = new JPanel(new BorderLayout());
		main.add(paletteSplit, BorderLayout.CENTER);
		main.add(createSideBar(), BorderLayout.WEST);
		createMap();
		
		mapScrollPane.setViewportView(editorMapPanel);
		JViewport mapViewport = mapScrollPane.getViewport();
		
		MouseAdapter mapScroller = new MapScroller(mapViewport,editorMapPanel, this);
        editorMapPanel.addMouseMotionListener(mapScroller);
        editorMapPanel.addMouseListener(mapScroller);
		
        
        toMapUnit = new HashMap<UUID, AnimatedUnit>();
		spritesTabs.addTab("Enemies",   emenemiesPanel = createUnitsPanel());
		for (IMutableUnit u : map.getEnemies().getUnits()) {
			unitsListModel.addElement(u);
			AnimatedUnit m = new AnimatedUnit(-1,-1, u);
			toMapUnit.put(u.getUuid(), m);
		}

		for (Entry<UUID, Location> e : map.getEnemies().getUnitPlacement().entrySet()) {
			AnimatedUnit a = toMapUnit.get(e.getKey());
			if (a == null) continue;
			a.setLocation(e.getValue());
			map.getGuiTile(e.getValue()).setUnit(a);
		}
		
		unitsList.setSelectedIndex(0);

		
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        main.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
		
        Location l = map.getConditions().getDefaultStartLocation();
		infoStartLocation.setText(String.format("  (%s, %s)", l.getX(), l.getY()));
        
        return main;
	}
	
	
	// Controls to display units info
	private JList unitsList;
	private DefaultListModel unitsListModel;
	private IMutableUnit currentEnemy;
	
	private JTextField unitName;
	private JComboBox  unitWeapon;
	
	private JSpinner   unitStrength;
	private JSpinner   unitDefence;
	
	private JSpinner   unitSpeed;
	private JSpinner   unitMove;

	private JSpinner   unitHp;
//	private JSpinner   infoMp;
	private JLabel     unitLocation;
	
	private JPanel createUnitsPanel(){
		JPanel p =  new JPanel(new MigLayout("wrap 10"));
		p.add(new JLabel("Name:"));
		p.add((unitName = new JTextField(15)), "span, growx");
		unitName.setEnabled(false);
		
		unitWeapon = new JComboBox(new IWeapon[]{});
		unitWeapon.setRenderer(new WeaponDropDownListRenderer());
		unitWeapon.setEditable(false);
		unitWeapon.setEnabled(false);
		p.add(new JLabel("Weapon:"));
		p.add(unitWeapon, "span, growx");
		
		for (IWeapon w : AssetStore.instance().getWeapons().values()) {
			unitWeapon.addItem(w);
		}
		
		p.add(new JLabel("Location:"));
		p.add((unitLocation =new JLabel("Location")), new CC().alignX("leading").maxWidth("70").wrap());
		
		p.add(new JLabel("Strength:"));
		unitStrength = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		p.add(unitStrength, new CC().alignX("leading").maxWidth("70"));
		unitStrength.setEnabled(false);
		
		p.add(new JLabel("Defence:"), "gap unrelated");
		unitDefence = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		p.add(unitDefence, new CC().alignX("leading").maxWidth("70"));
		unitDefence.setEnabled(false);
		
		p.add(new JLabel("Speed:"), "gap unrelated");
		unitSpeed = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		p.add(unitSpeed, new CC().alignX("leading").maxWidth("70"));
		unitSpeed.setEnabled(false);
		
		p.add(new JLabel("Move:"), "gap unrelated");
		unitMove = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		p.add(unitMove, new CC().alignX("leading").maxWidth("70"));
		unitMove.setEnabled(false);
		
		p.add(new JLabel("Hp:"), "gap unrelated");
		unitHp = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		p.add(unitHp, new CC().alignX("leading").maxWidth("70"));
		unitHp.setEnabled(false);

		unitsListModel = new DefaultListModel();
		
		unitsList = new JList(unitsListModel);
		unitsList.setCellRenderer(new UnitListRenderer());
		unitsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				IMutableUnit u =  (IMutableUnit) unitsList.getSelectedValue();
				setCurrentEnemy(u);
			}
		});
		
		JScrollPane slist = new JScrollPane(unitsList);
		JPanel header = AbstractResourcesPanel.createHeader("All Enemies");
		slist.setColumnHeaderView(header);

		p.add(slist, new CC().dockWest().gapAfter("10px!").spanX(2));
		
		return p;
	}
	
	private void setCurrentEnemy(IMutableUnit u){
		currentEnemy  = u;
		unitName.setText(u.getName());
		unitWeapon.setSelectedItem(u.getWeapon());
		unitStrength.setValue(u.getStrength());
		unitDefence.setValue(u.getDefence());
		unitSpeed.setValue(u.getSpeed());
		unitMove.setValue(u.getMove());
		unitHp.setValue(u.getMaxHp());
		AnimatedUnit a = toMapUnit.get(u.getUuid());
		unitLocation.setText(String.format("  (%s, %s)", a.getGridX(), a.getGridY()));
	}
	
	public static class UnitListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			IMutableUnit w= (IMutableUnit) value;
			label.setText(w.getName() + "            ");
			return label;
		}
	}
	
	/** @category Gui**/
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("Tile"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoLocation = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Type:"), "gap 4");
		p.add((infoType = new JTextField(15)), "span, growx");
		infoType.setEditable(false);
		
		infoOrientation.setEditable(false);
		p.add(new JLabel("Orientation:"), "gap 4");
		infoOrientation.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() != ItemEvent.SELECTED) return;
		        if (state == MapState.DRAW || state == MapState.DRAW_INFO || state == MapState.EYE){
					map.setOrientation(selectedTile.getLocation(), (Orientation) infoOrientation.getSelectedItem());	
		        }else{
					for (EditorIsoTile tile : selection) {
						map.setOrientation(tile.getLocation(), (Orientation) infoOrientation.getSelectedItem());	
					}	
		        }
				editorMapPanel.repaintMap();
			}
		});
		p.add(infoOrientation, "span, growx");
		
		
		p.add(new JLabel("Height:"), "gap 4");
		infoHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//TODO deal with no tile selected.
				if (state == MapState.DRAW || state == MapState.DRAW_INFO || state == MapState.EYE){
					int height = ((Number)infoHeight.getValue()).intValue();
					if (selectedTile.getType() == ImageType.TEXTURED){
						map.setEndHeight(selectedTile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
					}else{
						map.setHeight(selectedTile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
					}
		        }else{
					for (EditorIsoTile tile : selection) {
						int height = ((Number)infoHeight.getValue()).intValue();
						if (selectedTile.getType() == ImageType.TEXTURED){
							map.setEndHeight(tile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
						}else{
							map.setHeight(tile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
						}
					}	
		        }
				editorMapPanel.repaintMap();
			}
		});
		p.add(infoHeight, "alignx leading, span, wrap");
		
		p.add(infoStartHeightl= new JLabel("Start Height:"), "gap 4");
		infoStartHeightl.setVisible(false);
		infoStartHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (selectedTile.getType() != ImageType.TEXTURED) {
					return;
				}
				editorMapPanel.repaintMap();
				if (state == MapState.DRAW || state == MapState.DRAW_INFO || state == MapState.EYE) {
					int height = ((Number) infoHeight.getValue()).intValue();
					map.setStartingHeight(selectedTile.getLocation(), ((Number) infoStartHeight.getValue()).intValue());
				} else {
					for (EditorIsoTile tile : selection) {
						int height = ((Number) infoHeight.getValue()).intValue();
						map.setStartingHeight(tile.getLocation(), ((Number) infoStartHeight.getValue()).intValue());
					}
				}

			}
		});
		p.add(infoStartHeight, "alignx leading, span, wrap");
		infoStartHeight.setVisible(false);
		
		p.add(new JLabel("Map"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));
		p.add(new JLabel("Start Location:"));
		p.add((infoStartLocation =new JLabel("(0,0)")), new CC().alignX("leading").maxWidth("70").wrap());
		
		return p;
	}

	/** @category Gui **/
	private JToolBar createSideBar() {
		
		ImageIcon iconMove      = EditorResources.getIcon("images/gimp-tool-move-22.png");
		ImageIcon iconDraw      = EditorResources.getIcon("images/gimp-tool-pencil-22.png");
		ImageIcon iconInfo      = EditorResources.getIcon("images/draw-brush.png");
		ImageIcon iconFill      = EditorResources.getIcon("images/gimp-tool-bucket-fill-22.png");
		ImageIcon iconEyed      = EditorResources.getIcon("images/gimp-tool-color-picker-22.png");
		ImageIcon iconSelection = EditorResources.getIcon("images/gimp-tool-rect-select-22.png");
		ImageIcon iconPlaceUnit = EditorResources.getIcon("images/tool-smudge.png");
		
		ImageIcon iconLeft      = EditorResources.getIcon("images/leftWall.png");
		ImageIcon iconRight     = EditorResources.getIcon("images/rightWall.png");
		ImageIcon iconStart     = EditorResources.getIcon("images/tango-start.png");

		
		drawButton     = makeToggleButton(iconDraw,  MapState.DRAW);
		drawInfoButton = makeToggleButton(iconInfo,  MapState.DRAW_INFO);
		
		fillButton     = makeToggleButton(iconFill,  MapState.FILL);
		eyeButton      = makeToggleButton(iconEyed,  MapState.EYE);
		moveButton     = makeToggleButton(iconMove,  MapState.MOVE);
		
		placeButton     = makeToggleButton(iconPlaceUnit,  MapState.PLACE);
		selectionButton = makeToggleButton(iconSelection,  MapState.SELECTION);
		
		leftWallButton  = makeToggleButton(iconLeft,   MapState.LEFT_WALL);
		rightWallButton = makeToggleButton(iconRight,  MapState.RIGHT_WALL);
		startButton     = makeToggleButton(iconStart,  MapState.START);

		
		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(true);
		toolBar.add(moveButton);
		toolBar.add(drawButton);
		toolBar.add(drawInfoButton);
		toolBar.add(fillButton);
		toolBar.add(eyeButton);
		toolBar.add(selectionButton);
		
		toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolBar.add(leftWallButton);
		toolBar.add(rightWallButton);
		
		toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolBar.add(placeButton);
		toolBar.add(startButton);

		
		toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolBar.add(new TButton(zoomInAction));
		toolBar.add(new TButton(zoomOutAction));
		toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
		toolBar.add(Box.createGlue());
		
		setState(state);
		
		return toolBar;
	}

		/** @category Gui**/
		private JMenuBar createMenubar() {
			int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
			JMenuBar bar = new JMenuBar();
			JMenu file = new JMenu("File");
			
			JMenuItem save = new JMenuItem("Save");
			save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,mask));
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			
			file.add(save);
			bar.add(file);
			
			JMenu view = new JMenu("View");
			final JCheckBoxMenuItem number = new JCheckBoxMenuItem(
					"Show Numbering on Tiles", 
					editorMapPanel.hasNumbering());
			
			number.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,mask));
			number.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editorMapPanel.toggleNumbering();
					number.setSelected(editorMapPanel.hasNumbering());
					editorMapPanel.repaintMap();
				}
			});
			view.add(number);
			
			final JCheckBoxMenuItem hover = new JCheckBoxMenuItem("Highlight Tile on Hover", false);
			hover.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,mask));
			hover.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showOnHover=!showOnHover;
					hover.setSelected(showOnHover);
				}
			});
			view.add(hover);
			
			
			JMenuItem rotate = new JMenuItem("Rotate Map");
			rotate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,mask));
			rotate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editorMapPanel.rotate(toMapUnit.values());
				}
			});
			view.add(rotate);
			
			view.add(new JSeparator());
			
			JMenuItem pitchUp = new JMenuItem("Increases Pitch");
			pitchUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (map.getMapSettings().pitch >= 0.7) return;
					map.getMapSettings().pitch += 0.1;
					editorMapPanel.invalidateMap();
				}
			});			
			view.add(pitchUp);

			JMenuItem pitchDown = new JMenuItem("Decreases Pitch");
			pitchDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (map.getMapSettings().pitch <= 0.5) return;
					map.getMapSettings().pitch -= 0.1;
					editorMapPanel.invalidateMap();
				}
			});			
			view.add(pitchDown);
			

			JMenuItem reset = new JMenuItem("Reset Pitch/Zoom");
			reset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					map.getMapSettings().pitch = 0.5f;
					map.getMapSettings().zoom  = 1f;
					editorMapPanel.invalidateMap();
				}
			});			
			view.add(reset);
			
			JMenuItem outline = new JMenuItem("Toggle Outlines");
			outline.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editorMapPanel.toggleOutlines();
				}
			});			
			view.add(outline);
			
			bar.add(view);
			return bar;
		}

	private void createMap() {
		map = new EditorMap(savePath);
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),map.getMapSettings());
		
		frame.setTitle("Map Editor - " + map.getData().getName() );
		// Relayout the sprites to fill the whole panel.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				refreashTilesets();
				refreashTextures();
			}
		});
		
		tilesetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashTilesets();
			}
		});

		texturesPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashTextures();
			}
		});
		
		
	} 

	private void refreashTilesets(){
		if (tilesetPanel.getHeight() <=0 || tilesetPanel.getWidth() <=0 ) return;
		tilesetPanel.setSpriteSheet(packer.packImagesByName(
				map.getTileset().getSprites(),
				tilesetPanel.getWidth(), 
				tilesetPanel.getHeight(), 
				2));
	}

	private void refreashTextures(){
		if (texturesPanel.getHeight() <=0 || texturesPanel.getWidth() <=0 ) return;
		texturesPanel.setSpriteSheet(packer.packImagesByName(
				map.getTextures().getSprites(),
				texturesPanel.getWidth(), 
				texturesPanel.getHeight(), 
				2));
	}
	
	private void save(){
		editorMapPanel.noromalise(toMapUnit.values());
		
		SavedTile[] tiles = new SavedTile[map.getFieldWidth() * map.getFieldHeight()]; 
		Tile[][] field = map.getField();
		
		for (int i = 0, k = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++, k++) {
				tiles[k] = new SavedTile(
						field[i][j].getType(),
						field[i][j].getStartingHeight(),
						field[i][j].getEndHeight(),	
						i, j, 
						field[i][j].getOrientation(),
						field[i][j].getLeftWallName(),
						field[i][j].getRightWallName(), false);
			}
		}

		map.getMapSettings().pitch = 0.5f;
		map.getMapSettings().zoom  = 1f;
		SavedMap m = new SavedMap(
				map.getFieldWidth(), map.getFieldHeight(), tiles,
				map.getMapSettings(), map.getData());
		
		Config.savePreferences(m, savePath);
		Config.savePreferences(map.getTileMapping(), m.getMapData().getTileMappingLocation());
		Config.savePreferences(map.getEnemies(),     m.getMapData().getEnemiesLocation());
		Config.savePreferences(map.getConditions(),  m.getMapData().getConditionsLocation());
		editorMapPanel.invalidateMap();
		log.info("Saved as " + savePath);
	}

	/** @category Gui **/
	private AbstractButton makeToggleButton(Icon icon, MapState state) {
		return makeToggleButton(icon, state.name(),state.description);
	}
	private AbstractButton makeToggleButton(Icon icon, String command, String tooltip) {
		JToggleButton btn = new JToggleButton("", icon);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setActionCommand(command);
		btn.addActionListener(this);
		if (tooltip != null) {
			btn.setToolTipText(tooltip);
		}
		return btn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MapState s = MapState.valueOf(e.getActionCommand());
		setState(s);
	}

	@Override
	public MapState getEditorState(){
		return state;
	}
	
	private void setState(MapState s) {
		state = s;
		drawButton.setSelected(state == MapState.DRAW);
		drawInfoButton.setSelected(state == MapState.DRAW_INFO);
		fillButton.setSelected(state == MapState.FILL);
		eyeButton.setSelected(state == MapState.EYE);
		selectionButton.setSelected(state == MapState.SELECTION);
		moveButton.setSelected(state == MapState.MOVE);
		placeButton.setSelected(state == MapState.PLACE);
		leftWallButton.setSelected(state== MapState.LEFT_WALL);
		rightWallButton.setSelected(state== MapState.RIGHT_WALL);
		startButton.setSelected(state== MapState.START);

	}

	
	private class ZoomIn extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public ZoomIn() {
			putValue(SHORT_DESCRIPTION, "Zoom In");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, EditorResources.getIcon("images/gnome-zoom-in.png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (map.getMapSettings().zoom >= 1.0) return;
			map.getMapSettings().zoom += 0.2;
			editorMapPanel.invalidateMap();
		}
	}

	private class ZoomOut extends AbstractAction {
		private static final long serialVersionUID = -7391473073942670422L;

		public ZoomOut() {
			putValue(SHORT_DESCRIPTION, "Zoom out");
			putValue(LARGE_ICON_KEY, EditorResources.getIcon("images/gnome-zoom-out.png"));
			putValue(SMALL_ICON, EditorResources.getIcon("images/gnome-zoom-out.png"));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (map.getMapSettings().zoom <= 0.8) return;
			map.getMapSettings().zoom -= 0.2;
			editorMapPanel.invalidateMap();
		}
	}
}
