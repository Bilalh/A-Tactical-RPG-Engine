package editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.log4j.Logger;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import common.gui.ResourceManager;
import common.interfaces.IWeapon;
import common.spritesheet.SpriteSheet;
import config.Config;

import editor.Editor;
import editor.editors.UnitsPanel.*;
import editor.map.EditorSpriteSheet;
import editor.spritesheet.*;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.assets.Units;
import engine.assets.UnitsImages;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public class UnitsImagesPanel extends JPanel implements IRefreshable, ISpriteProvider<MutableSprite>, ISpriteEditorListener {
	private static final Logger log = Logger.getLogger(UnitsImagesPanel.class);
	private static final long serialVersionUID = -6821378708781154897L;

	private JList imagesList;
	private DefaultListModel imagesListModel;
	
	private java.util.Map<UUID, EditorSpriteSheet> spriteSheets = Collections.synchronizedMap(new HashMap<UUID, EditorSpriteSheet>());
	
	private SpriteSheetPanel tilesetPanel;
	private JPanel tilesetPanelWithHeader;
	private Packer packer = new Packer();
	
	private UnitImages currentImages;
	private EditorSpriteSheet currentSheet;
	
	private SpriteSheetEditor spriteSheetEditor;
	private Editor editor;
	
	public UnitsImagesPanel(Editor editor){
		super(new BorderLayout());
		this.editor = editor;
		createMainPane();
	}
	
	public UnitsImages getUnitsImages(){
		UnitsImages ws = new UnitsImages();
		for (int i = 0; i < imagesListModel.size(); i++) {
			ws.put((UnitImages) imagesListModel.get(i));
		}
		return ws;
	}

	public void setUnitsImages(UnitsImages images){
		ListSelectionListener lsl =  imagesList.getListSelectionListeners()[0];
		imagesList.removeListSelectionListener(lsl);
		spriteSheets.clear();
		imagesListModel.clear();
		for (UnitImages ui : images.values()) {
			EditorSpriteSheet sheet = spriteSheets.get(ui.getUuid());
			SpriteSheet _sheet = Config.loadSpriteSheet(ui.getSpriteSheetLocation());
			spriteSheets.put(ui.getUuid(), new EditorSpriteSheet(_sheet));
			imagesListModel.addElement(ui);
		}
		imagesList.addListSelectionListener(lsl);
		imagesList.setSelectedIndex(0);
	}
	
	public void setCurrentUnitImages(UnitImages ui){
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
		// FIXME panelSelected method
	}
	
	@Override
	public void spriteEditingFinished() {
		spriteSheets.remove(currentImages.getUuid());
		setCurrentUnitImages(currentImages);
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
	
	protected JComponent createLeftPane(){
		UnitImages uu = Config.loadPreference("images/characters/defaultImages-animations.xml");
		setCurrentUnitImages(uu);
		
		imagesListModel = new DefaultListModel();
		
		imagesList = new JList(imagesListModel);
		imagesList.setCellRenderer(new ImageListRenderer());
		imagesListModel.addElement(uu);
		imagesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				UnitImages ui =  (UnitImages) imagesList.getSelectedValue();
				if (ui == null) return;
				setCurrentUnitImages(ui);
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
		slist.setColumnHeaderView(createHeader("All Images"));
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		
		return p;
	}
	
	private void deleteFromList(int index){
		assert imagesListModel.size() >=2;
		assert index != -1;
		int nextIndex = index == 0 ? imagesListModel.size()-1 : index - 1;
		System.out.printf("(%d,%d)\n", index, nextIndex );
		imagesList.setSelectedIndex(nextIndex);
		UnitImages s=  (UnitImages) imagesListModel.remove(index);
		spriteSheets.remove(s);
	}
	
	private class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public DeleteAction() {
			putValue(NAME, "Delete the selected Unit");
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

	private class AddAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public AddAction() {
			putValue(NAME, "Add a new Unit");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			assert false : "not done yet";
			IMutableUnit w = new Unit();
			int index = imagesListModel.size();
			w.setName("New Unit " + (index + 1));
			imagesListModel.addElement(w);
			imagesList.setSelectedIndex(index);
		}
	}

	protected LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}

	protected JPanel createInfoPanel() {
		tilesetPanel = new SpriteSheetPanel(this);
		
		tilesetPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreashSprites();
			}
		});
		tilesetPanel.setMinimumSize(new Dimension(512, 512));
	
		tilesetPanelWithHeader = new JPanel(new BorderLayout());
		tilesetPanelWithHeader.add(tilesetPanel,BorderLayout.CENTER);
		tilesetPanelWithHeader.add(createHeader("Sprites"),BorderLayout.NORTH);
		tilesetPanelWithHeader.add(new JButton(new EditAction()), BorderLayout.SOUTH);

		tilesetPanelWithHeader.setBorder(BorderFactory.createEtchedBorder()); //TODO fix border
		return tilesetPanelWithHeader;
	}
	
	private class EditAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public EditAction() {
			putValue(NAME, "Edit");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
//			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			spriteSheetEditor = new SpriteSheetEditor(WindowConstants.DISPOSE_ON_CLOSE,
					new File ("").getAbsolutePath()+ "/"+ editor.getProjectPath() + "/Resources/"+ currentImages.getSpriteSheetLocation(),
					UnitsImagesPanel.this);
			spriteSheetEditor.setVisible(true);
			//FIXME finish
		}
	}
	
	public void refreashSprites(){
		if (tilesetPanel.getHeight() <=0 || tilesetPanel.getWidth() <=0 ) return;
		tilesetPanel.setSpriteSheet(packer.packImagesByName(currentSheet.getSprites(),
				tilesetPanel.getWidth(), 
				tilesetPanel.getHeight(), 2));
	}
	
	
	/** @category ISpriteProvider**/
	@Override
	public void select(List<MutableSprite> selection) {
		// FIXME select method
	}

	/** @category ISpriteProvider**/
	@Override
	public void delete(List<MutableSprite> selected) {
	}
	
	// creates a header for the panel.
	protected JPanel createHeader(String text){
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>"+text+"<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}

	static class ImageListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			UnitImages w= (UnitImages) value;
			label.setText(new File(w.getSpriteSheetLocation()).getName());
//			label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}

	/** @category Generated */
	public java.util.Map<UUID, EditorSpriteSheet> getSpriteSheets() {
		return spriteSheets;
	}

	/** @category Generated */
	public void setSpriteSheets(java.util.Map<UUID, EditorSpriteSheet> spriteSheets) {
		this.spriteSheets = spriteSheets;
	}

	
}
