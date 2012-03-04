package editor.map;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import view.map.interfaces.IMapRendererParent;

import common.enums.Orientation;

import config.XMLUtil;
import config.xml.SavedMap;
import config.xml.SavedTile;
import editor.map.*;
import editor.spritesheet.*;
import editor.ui.FloatablePanel;
import editor.ui.TButton;
import editor.util.Prefs;
import editor.util.Resources;
import engine.map.Tile;

/**
 * Map Editor for the engine
 * @author Bilal Hussain
 */
public class MapEditor implements ActionListener, ISpriteProvider<MutableSprite>, IEditorMapPanelListener {
	private static final Logger log = Logger.getLogger(MapEditor.class);

	private JFrame frame;

	private AbstractButton drawButton, drawInfoButton,  eraseButton, fillButton;
	private AbstractButton eyeButton,  selectionButton, moveButton;
	private final Action zoomInAction, zoomOutAction,   zoomNormalAction;
	private MapState state = MapState.DRAW;

	private JScrollPane mapScrollPane;
	private JPanel infoPanel;
	private FloatablePanel infoPanelContainer;
	private SpriteSheetPanel tilesetPanel;
	private FloatablePanel tilesetsPanelContainer;

	private JLabel statusLabel;

	private EditorMap map;
	private EditorMapPanel editorMapPanel;
	private EditorSpriteSheet editorSpriteSheet;
	private Packer packer = new Packer();

	private MutableSprite selectedTileSprite;
	private HashSet<EditorIsoTile> selection = new HashSet<EditorIsoTile>();

	// Highlight the tile the mouse is over.
	private boolean showOnHover = false;
	
	// For draw
	private EditorIsoTile selectedTile;

	
	public MapEditor() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new JFrame("Map Editor");

		zoomInAction = new ZoomInAction();
		zoomOutAction = new ZoomOutAction();
		zoomNormalAction = new ZoomNormalAction();

		frame.setContentPane(createContentPane());
		frame.setJMenuBar(createMenubar());

//		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent event) {
//				onQuit();
//				System.exit(0);
//			}
//		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				onQuit();
			}
		}));

		infoPanelContainer.restore();
		tilesetsPanelContainer.restore();

		Preferences pref = Prefs.getNode("map/panels/main");
		System.out.println(pref);
		int width = pref.getInt("width", 1280);
		int height = pref.getInt("height", 800);
		
		frame.setSize(width, height);
		frame.setVisible(true);
	}

	/** @category Gui **/
	private void onQuit() {
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
		tilesetsPanelContainer.save();
		
		try {
			Prefs.root().sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		log.info("Saved prefs" + Prefs.root());
	}

	/** @category ISpriteProvider**/
	@Override
	public void select(List<MutableSprite> selection) {
		tilesetPanel.setSelectedSprites(selection);
		if(selection != null && !selection.isEmpty()) {
			selectedTileSprite = selection.get(0);
		}else{
			selectedTileSprite = null;
		}
	}

	/** @category ISpriteProvider**/
	@Override
	public void delete(List<MutableSprite> selected) {
		// Do nothing
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
	
	/** @category Callback **/
	@Override
	public void tileClicked(EditorIsoTile tile) {
		boolean repaint = false;
		selectedTile = tile;
		switch (state) {
			case DRAW:
				if (selectedTileSprite != null){
					map.setTileSprite(tile.getLocation(), selectedTileSprite);
					repaint = true;
				}
				break;
			case DRAW_INFO:
				if (selectedTileSprite != null){
					map.setTileSprite(tile.getLocation(), selectedTileSprite);
					repaint = true;
				}
				
				int  height = ((Number) infoHeight.getValue()).intValue();
				if (height >=0){
					map.setHeight(tile.getLocation(), height);
					repaint = true;
				}
				
				Orientation o = (Orientation) infoOrientation.getSelectedItem();
				if (o != tile.getOrientation()){
					map.setOrientation(tile.getLocation(),o);
					repaint = true;
				}
				
			case EYE:
				selectedTileSprite = tile.getSprite();
				tilesetPanel.setSelectedSprites(selectedTileSprite);
				break;
			case FILL:
				if (selectedTileSprite != null && selection.contains(tile)){
					for (EditorIsoTile t : selection) {
						map.setTileSprite(t.getLocation(), selectedTileSprite);
					}
				}
				repaint = true;
				break;
		}

		//FIXME rethink this 
		if (state==MapState.SELECTION){
			removeSelection(selection);
			selection.clear();
			selection.add(tile);	
		}
		
		if (state == MapState.DRAW_INFO || state == MapState.DRAW || state == MapState.EYE ){
			infoHeight.setValue(tile.getHeight());
			//FIXME hack type name
			infoType.setText(tile.getSprite().getName());
			infoOrientation.setSelectedItem(tile.getOrientation());
			infoLocation.setText(String.format("(%s,%s)", tile.getX(), tile.getY()));	
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

	// The map view port 
	private JViewport mapViewport;
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
	
		tilesetPanel = new SpriteSheetPanel(this);
		tilesetsPanelContainer = new FloatablePanel(frame, tilesetPanel, "Tiles", "tilesets");
		
		JSplitPane paletteSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true, mainSplit, tilesetsPanelContainer);
		paletteSplit.setOneTouchExpandable(true);
		paletteSplit.setResizeWeight(1.0);
	
		JPanel main = new JPanel(new BorderLayout());
		main.add(paletteSplit, BorderLayout.CENTER);
		main.add(createSideBar(), BorderLayout.WEST);
		createMap();

		mapScrollPane.setViewportView(editorMapPanel);
		mapViewport = mapScrollPane.getViewport();
		
		MouseAdapter mapScroller = new MapScroller(mapViewport,editorMapPanel, this);
        editorMapPanel.addMouseMotionListener(mapScroller);
        editorMapPanel.addMouseListener(mapScroller);
		
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        main.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusPanel.add(statusLabel);
		return main;
	}
	
	// Infopanel controls 
	private JLabel     infoLocation;
	private JTextField infoType;
	private JComboBox  infoOrientation = new JComboBox(Orientation.values());
	private JSpinner   infoHeight      = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));

	
	/** @category Gui**/
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("General"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoLocation = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Type:"), "gap 4");
		p.add((infoType = new JTextField(15)), "span, growx");
		
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
					map.setHeight(selectedTile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
		        }else{
					for (EditorIsoTile tile : selection) {
						map.setHeight(tile.getLocation(), ((Number)infoHeight.getValue()).intValue());	
					}	
		        }
				editorMapPanel.repaintMap();
			}
		});
		p.add(infoHeight, "alignx leading, span, wrap");
		
		return p;
	}

	/** @category Gui **/
	private JToolBar createSideBar() {
		
		ImageIcon iconMove      = Resources.getIcon("images/gimp-tool-move-22.png");
		ImageIcon iconDraw     = Resources.getIcon("images/gimp-tool-pencil-22.png");
		ImageIcon iconErase     = Resources.getIcon("images/gimp-tool-eraser-22.png");
		ImageIcon iconFill      = Resources.getIcon("images/gimp-tool-bucket-fill-22.png");
		ImageIcon iconEyed      = Resources.getIcon("images/gimp-tool-color-picker-22.png");
		ImageIcon iconSelection = Resources.getIcon("images/gimp-tool-rect-select-22.png");

		drawButton     = makeToggleButton(iconDraw,  MapState.DRAW.name(),       MapState.DRAW.description);
		drawInfoButton = makeToggleButton(iconDraw,  MapState.DRAW_INFO.name(),  MapState.DRAW_INFO.description);
		
		eraseButton    = makeToggleButton(iconErase, MapState.ERASE.name(), MapState.ERASE.description);
		fillButton     = makeToggleButton(iconFill,  MapState.FILL.name(),  MapState.FILL.description);
		eyeButton      = makeToggleButton(iconEyed,  MapState.EYE.name(),   MapState.ERASE.description);
		moveButton     = makeToggleButton(iconMove,  MapState.MOVE.name(),  MapState.MOVE.description);

		selectionButton = makeToggleButton(iconSelection, MapState.SELECTION.name(), MapState.SELECTION.description);
		
		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(true);
		toolBar.add(moveButton);
		toolBar.add(drawButton);
		toolBar.add(drawInfoButton);
		//TODO  erase button needed?
//		toolBar.add(eraseButton); 
		toolBar.add(fillButton);
		toolBar.add(eyeButton);
		toolBar.add(selectionButton);
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
			
			JMenu edit = new JMenu("Edit");
			bar.add(edit);
			
			JMenu view = new JMenu("View");
			final JCheckBoxMenuItem number = new JCheckBoxMenuItem("Show Numbering on Tiles", editorMapPanel.hasNumbering());
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
			
			bar.add(view);
			JMenu editors = new JMenu("Editors");
			
			JMenuItem spriteSheetEditorItem = new JMenuItem("Sprite Sheet Editor");
	//		spriteSheetEditor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,mask));
			spriteSheetEditorItem.addActionListener(new ActionListener() {
				SpriteSheetEditor spriteSheetEditor;
				@Override
				public void actionPerformed(ActionEvent e) {
					if (spriteSheetEditor == null) {
						spriteSheetEditor = new SpriteSheetEditor(WindowConstants.DO_NOTHING_ON_CLOSE);
						spriteSheetEditor.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(WindowEvent e) {
								spriteSheetEditor.setVisible(false);
							}
						});
					}
					spriteSheetEditor.setVisible(true);
				}
			});
			
			editors.add(spriteSheetEditorItem);
			bar.add(editors);
			
			return bar;
		}

	String filename = "fft2";
	private void createMap() {
		map = new EditorMap("maps/"+filename+".xml");
		editorMapPanel = new EditorMapPanel(this, map.getGuiField(),map.getMapSettings());

		// Relayout the sprites to fill the whole panel.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				refreashSprites();
			}
		});
		tilesetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
		
		
	} 

	private void refreashSprites(){
		if (tilesetPanel.getHeight() <=0 || tilesetPanel.getWidth() <=0 ) return;
		tilesetPanel.setSpriteSheet(packer.packImages(map.getSpriteSheet().getSprites(),
				tilesetPanel.getWidth(), tilesetPanel.getHeight(), 2));
	}

	private void save(){
		SavedTile[] tiles = new SavedTile[map.getFieldWidth() * map.getFieldHeight()]; 
		Tile[][] field = map.getField();
		for (int i = 0, k=0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++,k++) {
				tiles[k] = new SavedTile(field[i][j].getType(),field[i][j].getEndHeight(),i,j,field[i][j].getOrientation()); 
			}
		}
		SavedMap m = new SavedMap(map.getFieldWidth(), map.getFieldHeight(),tiles , map.getMapSettings(), map.getData());
	
		String s1 = XMLUtil.makeFormattedXml(m);
		String s2 = XMLUtil.makeFormattedXml(map.getTileMapping());
	
		FileWriter fw;
		try {
			fw = new FileWriter(new File("./Resources/maps/" + filename + ".xml"));
			fw.write(s1);
			fw.close();
			
			fw = new FileWriter(new File("./Resources/maps/" + filename + "-mapping.xml"));
			fw.write(s2);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Saved as " + filename);
	}

	/** @category Gui **/
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
		eraseButton.setSelected(state == MapState.ERASE);
		fillButton.setSelected(state == MapState.FILL);
		eyeButton.setSelected(state == MapState.EYE);
		selectionButton.setSelected(state == MapState.SELECTION);
		moveButton.setSelected(state == MapState.MOVE);
	}

	
	private class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public ZoomInAction() {
			putValue(SHORT_DESCRIPTION, "action.zoom.in.tooltip");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, Resources.getIcon("images/gnome-zoom-in.png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private class ZoomOutAction extends AbstractAction {
		private static final long serialVersionUID = 1630640363711233878L;

		public ZoomOutAction() {
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control MINUS"));
			putValue(SHORT_DESCRIPTION, "action.zoom.out.tooltip");
			putValue(LARGE_ICON_KEY, Resources.getIcon("images/gnome-zoom-out.png"));
			putValue(SMALL_ICON, Resources.getIcon("images/gnome-zoom-out.png"));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {

		}
	}

	private class ZoomNormalAction extends AbstractAction {
		private static final long serialVersionUID = -4301272641043394395L;

		public ZoomNormalAction() {
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 0"));
			putValue(SHORT_DESCRIPTION, "action.zoom.normal.tooltip");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		config.Config.loadLoggingProperties();
		new MapEditor();
	}
}
