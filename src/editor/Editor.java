package editor;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.*;

import org.apache.log4j.Logger;

import common.spritesheet.SpriteInfo;

import util.Logf;
import view.map.GuiTile;
import view.map.IMapRendererParent;
import editor.spritesheet.*;
import editor.ui.FloatablePanel;
import editor.ui.TButton;

/**
 * @author Bilal Hussain
 */
public class Editor implements ActionListener, IMapRendererParent, ISpriteProvider<MutableSprite> {
	private static final Logger log = Logger.getLogger(Editor.class);

	private JFrame frame;

	private AbstractButton paintButton, eraseButton, pourButton;
	private AbstractButton eyedButton, marqueeButton, moveButton;
	private final Action zoomInAction, zoomOutAction, zoomNormalAction;
	private State state;

	private JScrollPane mapScrollPane;
	private JPanel infoPanel;
	private FloatablePanel infoPanelContainer;
	private SpriteSheetPanel tilesetPanel;
	private FloatablePanel tilesetsPanelContainer;

	private EditorMap map;
	private EditorMapPanel editorMapPanel;
	private EditorSpriteSheet editorSpriteSheet;
	
	private static final String TOOL_PAINT = "paint";
	private static final String TOOL_ERASE = "erase";
	private static final String TOOL_FILL = "fill";
	private static final String TOOL_EYE_DROPPER = "eyedropper";
	private static final String TOOL_SELECT = "select";
	private static final String TOOL_MOVE_LAYER = "movelayer";

	static enum State {
		POINT,
		PAINT,
		ERASE,
		POUR,
		EYED,
		MARQUEE,
		MOVE;
	}

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

	private Packer packer = new Packer();

	private void createMap() {
		map = new EditorMap("maps/map5.xml");
		editorMapPanel = new EditorMapPanel(this, map.getGuiField());

		// Relayout the sprites to fill the whole panel.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				refreashSprites();
			}
		});
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
	}

	private void refreashSprites(){
		tilesetPanel.setSpriteSheet(packer.packImages(map.getSpriteSheet().getSprites(),
				tilesetPanel.getWidth(), tilesetPanel.getHeight(), 2));
	}
	
	/** @category Gui **/
	private JPanel createContentPane() {
		mapScrollPane = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		mapScrollPane.setBorder(null);
	
		infoPanel = new JPanel();
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
	
		return main;
	}

	/** @category Gui **/
	private JToolBar createSideBar() {
		
		ImageIcon iconMove    = Resources.getIcon("gimp-tool-move-22.png");
		ImageIcon iconPaint   = Resources.getIcon("gimp-tool-pencil-22.png");
		ImageIcon iconErase   = Resources.getIcon("gimp-tool-eraser-22.png");
		ImageIcon iconPour    = Resources.getIcon("gimp-tool-bucket-fill-22.png");
		ImageIcon iconEyed    = Resources.getIcon("gimp-tool-color-picker-22.png");
		ImageIcon iconMarquee = Resources.getIcon("gimp-tool-rect-select-22.png");

		paintButton   = createToggleButton(iconPaint, "PAINT", TOOL_PAINT);
		eraseButton   = createToggleButton(iconErase, "ERASE", TOOL_ERASE);
		pourButton    = createToggleButton(iconPour, "POUR", TOOL_FILL);
		eyedButton    = createToggleButton(iconEyed, "EYED", TOOL_EYE_DROPPER);
		marqueeButton = createToggleButton(iconMarquee, "MARQUEE", TOOL_SELECT);
		moveButton    = createToggleButton(iconMove, "MOVE", TOOL_MOVE_LAYER);

		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(true);
		toolBar.add(moveButton);
		toolBar.add(paintButton);
		toolBar.add(eraseButton);
		toolBar.add(pourButton);
		toolBar.add(eyedButton);
		toolBar.add(marqueeButton);
		toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolBar.add(new TButton(zoomInAction));
		toolBar.add(new TButton(zoomOutAction));
		toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
		toolBar.add(Box.createGlue());

		return toolBar;
	}

	/** @category Gui**/
	private JMenuBar createMenubar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		bar.add(file);
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		JMenu edit = new JMenu("Edit");
		bar.add(edit);
		
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
	
	/** @category Gui **/
	private AbstractButton createToggleButton(Icon icon, String command, String tooltip) {
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

	private void setState(State s) {
		state = s;
		paintButton.setSelected(state == State.PAINT);
		eraseButton.setSelected(state == State.ERASE);
		pourButton.setSelected(state == State.POUR);
		eyedButton.setSelected(state == State.EYED);
		marqueeButton.setSelected(state == State.MARQUEE);
		moveButton.setSelected(state == State.MOVE);

	}

	/** @category Callback **/
	public void tileClicked(GuiTile tile) {
		tile.setSelected(true);
		editorMapPanel.repaintMap();
	}

	private class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public ZoomInAction() {
			putValue(SHORT_DESCRIPTION, "action.zoom.in.tooltip");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-in.png"));
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
			putValue(LARGE_ICON_KEY, Resources.getIcon("gnome-zoom-out.png"));
			putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-out.png"));
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
