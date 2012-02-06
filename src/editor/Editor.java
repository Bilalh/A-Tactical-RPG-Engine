package editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import view.map.IMapRendererParent;
import config.XMLUtil;
import config.xml.SavedMap;
import config.xml.SavedTile;
import editor.map.EditorMap;
import editor.map.EditorSpriteSheet;
import editor.map.EditorIsoTile;
import editor.spritesheet.*;
import editor.ui.FloatablePanel;
import editor.ui.TButton;
import engine.map.Tile;

/**
 * Editor for the engine
 * @author Bilal Hussain
 */
public class Editor implements ActionListener, IMapRendererParent, ISpriteProvider<MutableSprite> {
	private static final Logger log = Logger.getLogger(Editor.class);

	private JFrame frame;

	private AbstractButton drawButton, eraseButton, pourButton;
	private AbstractButton eyeButton, selectionButton, moveButton;
	private final Action zoomInAction, zoomOutAction, zoomNormalAction;
	private State state = State.DRAW;

	private JScrollPane mapScrollPane;
	private JPanel infoPanel;
	private FloatablePanel infoPanelContainer;
	private SpriteSheetPanel tilesetPanel;
	private FloatablePanel tilesetsPanelContainer;

	private EditorMap map;
	private EditorMapPanel editorMapPanel;
	private EditorSpriteSheet editorSpriteSheet;
	private Packer packer = new Packer();

	private MutableSprite selectedTileSprite;
	private ArrayList<EditorIsoTile> selection = new ArrayList<EditorIsoTile>();

	private static final String TOOL_DRAW = "Draw";
	private static final String TOOL_ERASE = "Erase";
	private static final String TOOL_FILL = "Fill";
	private static final String TOOL_EYE_DROPPER = "Eye Dropper";
	private static final String TOOL_SELECTION = "Select Area";
	private static final String TOOL_MOVE = "Move";
	
	public Editor() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new JFrame("Tacical Engine Editor");

		zoomInAction = new ZoomInAction();
		zoomOutAction = new ZoomOutAction();
		zoomNormalAction = new ZoomNormalAction();

		frame.setContentPane(createContentPane());
		frame.setJMenuBar(createMenubar());

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				onQuit();
				System.exit(0);
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				onQuit();
			}
		}));

		infoPanelContainer.restore();
		tilesetsPanelContainer.restore();

		Preferences pref = Prefs.getNode("panels/main");
		int width = pref.getInt("width", 1280);
		int height = pref.getInt("height", 800);
		
		frame.setSize(width, height);
		frame.setVisible(true);
	}

	/** @category Gui **/
	private void onQuit() {
		final int extendedState = frame.getExtendedState();
		final Preferences pref = Prefs.getNode("panels/main");
		pref.putInt("state", extendedState);
		if (extendedState == Frame.NORMAL) {
			pref.putInt("width", frame.getWidth());
			pref.putInt("height", frame.getHeight());
		}

		// Allow the floatable panels to save their position and size
		infoPanelContainer.save();
		tilesetsPanelContainer.save();
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

	/** @category Callback **/
	public void tileClicked(EditorIsoTile tile) {
		boolean repaint = false;
		switch (state) {
			case DRAW:
				if (selectedTileSprite != null){
					map.setSprite(tile.getFieldLocation(), selectedTileSprite);
					repaint = true;
				}
				break;
			case EYE:
				selectedTileSprite = tile.getSprite();
				tilesetPanel.setSelectedSprites(selectedTileSprite);
				repaint = true;
				break;
			case FILL:
				if (selectedTileSprite != null && selection.contains(tile)){
					for (EditorIsoTile t : selection) {
						t.setSprite(selectedTileSprite);
					}
				}
				repaint = true;
				break;
		}

		//FIXME rethink this 
		removeSelection(selection);
		selection.clear();
		selection.add(tile);
		
		infoHeight.setValue(tile.getHeight());
		//FIXME hack type name
		infoType.setText(tile.getSprite().getName());
		
		infoLocation.setText(String.format("(%s,%s)", tile.getX(), tile.getY()));
		
		if (repaint) editorMapPanel.repaintMap();
	}

	
	private void removeSelection(Iterable<EditorIsoTile> it){
		for (EditorIsoTile t : it) {
			t.setSelected(false);
		}
	}

	/** @category Callback **/
	public void tilesSelected(ArrayList<EditorIsoTile> tiles){
		removeSelection(selection);
		selection = tiles;
		for (EditorIsoTile t : selection) {
			t.setSelected(true);
		}
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
		
		MouseAdapter mapScroller = new MapScroller();
        editorMapPanel.addMouseMotionListener(mapScroller);
        editorMapPanel.addMouseListener(mapScroller);
		
		return main;
	}
	
	// Infopanel controls 
	private JLabel infoLocation;
	private JTextField infoType;
	private JSpinner  infoHeight = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));

	
	/** @category Gui**/
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("General"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoLocation = new JLabel("        ")), "span, growx");
		
		p.add(new JLabel("Type:"), "gap 4");
		p.add((infoType = new JTextField(15)), "span, growx");
		
		p.add(new JLabel("Height:"), "gap 4");
		infoHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//TODO deal with no tile selected.
				for (EditorIsoTile tile : selection) {
					map.setHeight(tile.getFieldLocation(), ((Number)infoHeight.getValue()).intValue());	
				}
				editorMapPanel.repaintMap();
			}
		});
		p.add(infoHeight, "alignx leading, span, wrap");
		
		return p;
	}

	/** @category Gui **/
	private JToolBar createSideBar() {
		
		ImageIcon iconMove    = Resources.getIcon("images/gimp-tool-move-22.png");
		ImageIcon iconPaint   = Resources.getIcon("images/gimp-tool-pencil-22.png");
		ImageIcon iconErase   = Resources.getIcon("images/gimp-tool-eraser-22.png");
		ImageIcon iconPour    = Resources.getIcon("images/gimp-tool-bucket-fill-22.png");
		ImageIcon iconEyed    = Resources.getIcon("images/gimp-tool-color-picker-22.png");
		ImageIcon iconSelection = Resources.getIcon("images/gimp-tool-rect-select-22.png");

		drawButton    = makeToggleButton(iconPaint, State.DRAW.name(),  TOOL_DRAW);
		eraseButton   = makeToggleButton(iconErase, State.ERASE.name(), TOOL_ERASE);
		pourButton    = makeToggleButton(iconPour,  State.FILL.name(),  TOOL_FILL);
		eyeButton    = makeToggleButton(iconEyed,  State.EYE.name(),  TOOL_EYE_DROPPER);
		moveButton    = makeToggleButton(iconMove,  State.MOVE.name(),  TOOL_MOVE);

		selectionButton = makeToggleButton(iconSelection, "SELECTION", TOOL_SELECTION);
		
		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(true);
		toolBar.add(moveButton);
		toolBar.add(drawButton);
		//TODO  erase button needed?
//		toolBar.add(eraseButton); 
		toolBar.add(pourButton);
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
			final JCheckBoxMenuItem number = new JCheckBoxMenuItem("Show numbering on tiles", editorMapPanel.hasNumbering());
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

	private void createMap() {
		map = new EditorMap("maps/nice.xml");
		editorMapPanel = new EditorMapPanel(this, map.getGuiField());

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
		
		String filename="nice";
		SavedTile[] tiles = new SavedTile[map.getFieldWidth() * map.getFieldHeight()]; 
		Tile[][] field = map.getField();
		for (int i = 0, k=0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++,k++) {
				tiles[k] = new SavedTile(field[i][j].getType(),field[i][j].getEndHeight(),i,j); 
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
		State s = State.valueOf(e.getActionCommand());
		setState(s);
	}

	public State getState(){
		return state;
	}
	
	private void setState(State s) {
		state = s;
		drawButton.setSelected(state == State.DRAW);
		eraseButton.setSelected(state == State.ERASE);
		pourButton.setSelected(state == State.FILL);
		eyeButton.setSelected(state == State.EYE);
		selectionButton.setSelected(state == State.SELECTION);
		moveButton.setSelected(state == State.MOVE);

	}

	
	class MapScroller extends MouseAdapter {
		private final Cursor normal   = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		private final Cursor movement = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		private final Point start = new Point();
	
		@Override
		public void mouseDragged(MouseEvent e) {
			if (state != State.MOVE) return;
			Point p = e.getPoint();
			Point vp = mapViewport.getViewPosition();
			vp.translate(start.x - p.x, start.y - p.y);
			editorMapPanel.scrollRectToVisible(new Rectangle(vp, mapViewport.getSize()));
			start.setLocation(p);
		}
	
		@Override
		public void mousePressed(MouseEvent e) {
			if (state != State.MOVE) return;
			((JComponent) e.getSource()).setCursor(movement);
			start.setLocation(e.getPoint());
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			if (state != State.MOVE) return;
			((JComponent) e.getSource()).setCursor(normal);
		}
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
		new Editor();
	}
}
