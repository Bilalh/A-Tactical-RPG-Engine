package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import util.IOUtil;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import common.assets.SpriteSheetsData;
import common.spritesheet.SpriteSheet;

import config.Config;

import editor.Editor;
import editor.map.EditorSpriteSheet;
import editor.spritesheet.*;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.unit.SpriteSheetData;

/**
 * Infrastructure for an editor panel that manages spritesheets. 
 * @author Bilal Hussain
 */
public abstract class AbstractSpriteSheetOrganiser extends JPanel  implements IRefreshable, ISpriteProvider<MutableSprite>, ISpriteEditorListener {
	private static final Logger log = Logger.getLogger(AbstractSpriteSheetOrganiser.class);
	private static final long serialVersionUID = 4482937708670082834L;
	
	protected Editor editor;
	protected JList imagesList;
	protected DefaultListModel imagesListModel;
	protected SpriteSheetPanel spriteSheetPanel;
	
	protected java.util.Map<UUID, EditorSpriteSheet> spriteSheets = Collections.synchronizedMap(new HashMap<UUID, EditorSpriteSheet>());
	protected Packer packer = new Packer();
	
	protected SpriteSheetData currentImages;
	protected EditorSpriteSheet currentSheet;
	protected String justCreated = null;
	
	protected String spriteSheetHelpString = "Must have the images (north0, south0, east0 and west0) in the sheet.";
	protected boolean showAnimations       = true;
	protected boolean validationForUnits   = false;
	protected boolean makeTileMapping      = false;
	
	public AbstractSpriteSheetOrganiser(Editor editor){
		this(editor, new BorderLayout());
	}
	public AbstractSpriteSheetOrganiser(Editor editor, LayoutManager layout) {
		super(layout);
		this.editor = editor;
		createMainPane();
	}

	
	public SpriteSheetsData getSpriteSheetData() {
		SpriteSheetsData ws = new SpriteSheetsData();
		
		System.out.println(Arrays.toString(imagesListModel.toArray()));
		for (int i = 0; i < imagesListModel.size(); i++) {
			ws.put((SpriteSheetData) imagesListModel.get(i));
		}
		System.out.println(ws);
		return ws;
	}

	public void setSpriteSheetData(SpriteSheetsData images) {
		ListSelectionListener lsl =  imagesList.getListSelectionListeners()[0];
		imagesList.removeListSelectionListener(lsl);
		spriteSheets.clear();
		imagesListModel.clear();
		for (SpriteSheetData ui : images.values()) {
			SpriteSheet _sheet = Config.loadSpriteSheet(ui.getSpriteSheetLocation());
			spriteSheets.put(ui.getUuid(), new EditorSpriteSheet(_sheet));
			imagesListModel.addElement(ui);
		}
		imagesList.addListSelectionListener(lsl);
		imagesList.setSelectedIndex(0);
	}

	public void setCurrentSpriteSheetData(SpriteSheetData ui) {
		log.info("selecting" + ui.getSpriteSheetLocation());
		currentImages = ui;
		EditorSpriteSheet sheet = spriteSheets.get(ui.getUuid());
		if (sheet == null){
			SpriteSheet _sheet = Config.loadSpriteSheet(ui.getSpriteSheetLocation());
			currentSheet  = new EditorSpriteSheet(_sheet);
			assert currentSheet != null;
			spriteSheets.put(ui.getUuid(), currentSheet);	
		}else{
			currentSheet = sheet;
		}
		refreashSprites();
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method  needed?
	}

	@Override
	public void spriteEditingFinished() {
		if (justCreated != null){
			SpriteSheetData ui =  Config.loadPreference(justCreated.replaceAll("\\.png$", "-animations.xml"));
			SpriteSheet _sheet = Config.loadSpriteSheet(ui.getSpriteSheetLocation());
			spriteSheets.put(ui.getUuid(), new EditorSpriteSheet(_sheet));
			imagesListModel.addElement(ui);
		}else{
			spriteSheets.remove(currentImages.getUuid());
			setCurrentSpriteSheetData(currentImages);	
		}
		editor.setVisible(true);
	}

	protected void createMainPane() {
		JPanel p = createInfoPanel();
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), p);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(0.05);
		mainSplit.setBorder(null);
		this.add(mainSplit, BorderLayout.CENTER);
	}

	protected abstract SpriteSheetData defaultImages();

	protected JComponent createLeftPane() {
		SpriteSheetData uu = defaultImages();
		setCurrentSpriteSheetData(uu);
		
		imagesListModel = new DefaultListModel();
		
		imagesList = new JList(imagesListModel);
		imagesList.setCellRenderer(new ImageListRenderer());
		imagesListModel.addElement(uu);
		imagesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SpriteSheetData ui =  (SpriteSheetData) imagesList.getSelectedValue();
				if (ui == null) return;
				setCurrentSpriteSheetData(ui);
			}
		});
		imagesList.setSelectedIndex(0);
	
		imagesList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (imagesListModel.size() <= 1) return;
	
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					deleteFromList(imagesList.getSelectedIndex());
				}
			}
		});
	
		JScrollPane slist = new JScrollPane(imagesList);
		slist.setColumnHeaderView(createHeader("All " + infoPanelTitle()));
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		
		return p;
	}

	protected JPanel createInfoPanel() {
		spriteSheetPanel = new SpriteSheetPanel(this);
		
		spriteSheetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
		spriteSheetPanel.setMinimumSize(new Dimension(512, 512));
	
		JPanel tilesetPanelWithHeader = new JPanel(new BorderLayout());
		tilesetPanelWithHeader.add(spriteSheetPanel,BorderLayout.CENTER);
		tilesetPanelWithHeader.add(createHeader(infoPanelTitle()),BorderLayout.NORTH);
		tilesetPanelWithHeader.add(new JButton(new EditAction()), BorderLayout.SOUTH);		
		tilesetPanelWithHeader.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border
		return tilesetPanelWithHeader;
	}

	protected abstract String infoPanelTitle();

	protected LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}
	
	protected JPanel createHeader(String text) {
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}

	protected class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
	
		public DeleteAction() {
			putValue(NAME, "Delete the selected Spritesheet");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListRemoveIcon(16, 16));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			// Must have at lest one SpriteSheet
			if (imagesListModel.size() <= 1) {
				return;
			}
			deleteFromList(imagesList.getSelectedIndex());
		}
	}

	protected void deleteFromList(int index) {
		assert imagesListModel.size() >=2;
		assert index != -1;
		int nextIndex = index == 0 ? imagesListModel.size()-1 : index - 1;
		imagesList.setSelectedIndex(nextIndex);
		SpriteSheetData s=  (SpriteSheetData) imagesListModel.remove(index);
		spriteSheets.remove(s);
	}

	protected class AddAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
	
		public AddAction() {
			putValue(NAME, "Add a new Spritesheet");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			justCreated =(String) JOptionPane.showInputDialog(AbstractSpriteSheetOrganiser.this,
					"Name for new Spritesheet", 
					"Adding new Spritesheet", 
					JOptionPane.INFORMATION_MESSAGE, 
					null, null, 
					"Spritesheet" +  (imagesListModel.size() + 1));
			if (justCreated != null){
				if (justCreated.lastIndexOf(".png") == -1){
					justCreated += ".png";
				}
				justCreated = spriteSheetBasePath() + justCreated;
				editSpriteSheet(justCreated);
			}
	
		}
	}

	protected abstract String spriteSheetBasePath();

	protected class EditAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public EditAction() {
			putValue(NAME, "Edit");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			// putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			editSpriteSheet(currentImages.getSpriteSheetLocation());
		}
	}

	protected void editSpriteSheet(String path) {
		editor.setVisible(false);
		new SpriteSheetEditor(WindowConstants.DO_NOTHING_ON_CLOSE,
				new File ("").getAbsolutePath()+ "/"+ editor.getProjectPath() + "/Resources/"+ path,
				AbstractSpriteSheetOrganiser.this,
				spriteSheetHelpString,
				showAnimations,
				validationForUnits,
				makeTileMapping).setVisible(true);
	}
	
	public void refreashSprites() {
		if (spriteSheetPanel.getHeight() <=0 || spriteSheetPanel.getWidth() <=0 ) return;
		spriteSheetPanel.setSpriteSheet(packer.packImagesByName(currentSheet.getSprites(),
				spriteSheetPanel.getWidth(), 
				spriteSheetPanel.getHeight(), 2));
	}

	/** @category ISpriteProvider**/
	@Override
	public void select(List<MutableSprite> selection) {
		// FIXME select method needed?
	}

	/** @category ISpriteProvider**/
	@Override
	public void delete(List<MutableSprite> selected) {
	}

	/** @category Generated */
	public java.util.Map<UUID, EditorSpriteSheet> getSpriteSheets() {
		return spriteSheets;
	}

	/** @category Generated */
	public void setSpriteSheets(java.util.Map<UUID, EditorSpriteSheet> spriteSheets) {
		this.spriteSheets = spriteSheets;
	}

	public static List<SpriteSheetData> sortedSprites(Collection<SpriteSheetData> data){
		ArrayList<SpriteSheetData> ll = new ArrayList(data);
		Collections.sort(ll,new Comparator<SpriteSheetData>() {
			@Override
			public int compare(SpriteSheetData o1, SpriteSheetData o2) {
				return o1.getSpriteSheetLocation().compareTo(o2.getSpriteSheetLocation());
			}
			
		});
		return ll;
	}
	
	static class ImageListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 5874522377321012662L;
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
            
            SpriteSheetData w= (SpriteSheetData) value;
            label.setText(IOUtil.removeExtension(new File(w.getSpriteSheetLocation()).getName()));
            return label;
        }
    }
 
	
}