package editor.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import static org.jvnet.inflector.Noun.pluralOf;

import util.IOUtil;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;
import common.interfaces.Identifiable;

import config.Config;
import config.xml.SavedMap;
import editor.Editor;
import editor.editors.AbstractSpriteSheetOrganiser.ImageListRenderer;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.assets.AbstractAssets;
import engine.assets.Maps;
import engine.unit.UnitImages;

/**
 * Infrastructure for an panel that manages resources
 * @author Bilal Hussain
 */
public abstract class AbstractResourcesPanel<R extends Identifiable, A extends AbstractAssets<R>> extends JPanel implements IRefreshable {

	private static final Logger log = Logger.getLogger(AbstactMapEditorPanel.class);
	private static final long serialVersionUID = 7115578732106671007L;
	protected Editor editor;
	protected JList resourceList;
	protected DefaultListModel resourceListModel;

	public AbstractResourcesPanel(Editor editor){
		this(editor, new BorderLayout());
	}
	public AbstractResourcesPanel(Editor editor, LayoutManager layout) {
		super(layout);
		this.editor = editor;
		createMainPane();
	}


	protected abstract A createAssetsInstance(); 
	
	public A getResouces() {
		A ws = createAssetsInstance();
		for (int i = 0; i < resourceListModel.size(); i++) {
			ws.put((R) resourceListModel.get(i));
		}
		return ws;
	}

	public synchronized void setResources(A assets) {
		ListSelectionListener lsl =  resourceList.getListSelectionListeners()[0];
		resourceList.removeListSelectionListener(lsl);
		resourceListModel.clear();
		for (R u : assets.values()) {
			resourceListModel.addElement(u);
		}
		resourceList.addListSelectionListener(lsl);
		resourceList.setSelectedIndex(0);
	}

	protected abstract void setCurrentResource(R resource);
	protected abstract R defaultResource();
	protected abstract String resourceName();
	protected abstract String resourceDisplayName(R resource);
	
	protected void createMainPane() {
		JPanel p = createInfoPanel();
		JSplitPane mainSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, true, createLeftPane(), p);
		mainSplit.setOneTouchExpandable(true);
		mainSplit.setResizeWeight(0.05);
		mainSplit.setBorder(null);
		this.add(mainSplit, BorderLayout.CENTER);
	}

	protected JComponent createLeftPane() {
		R defaultt = defaultResource();
		
		resourceListModel = new DefaultListModel();
		resourceList = new JList(resourceListModel);
		resourceList.setCellRenderer(new AbstractListRenderer());
		resourceListModel.addElement(defaultt);
		
		resourceList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				R resource =  (R) resourceList.getSelectedValue();
				if (resource == null) return;
				setCurrentResource(resource);
			}
		});
		resourceList.setSelectedIndex(0);
	
		resourceList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (resourceListModel.size() <= 1) return;
	
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					deleteFromList(resourceList.getSelectedIndex());
				}
			}
		});
	
		JScrollPane slist = new JScrollPane(resourceList);
		slist.setColumnHeaderView(createHeader("All " + pluralOf(resourceName())));
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		
		return p;
	}

	
	protected LayoutManager defaultInfoPanelLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 3).grow(100, 1, 3).align("right", 3).gap("15", 1,3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}
	
	protected abstract JPanel createInfoPanel();

	protected void addSeparator(JPanel p, String title){
		JLabel pTitle = new JLabel(title);
		pTitle.setForeground(Color.BLUE.brighter());
		
		p.add(pTitle, new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));		
	}
	
	protected JPanel createHeader(String text) {
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>" + text + "<BR></HTML>"), BorderLayout.CENTER);
		return header;
	}

	protected class DeleteAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
	
		public DeleteAction() {
			putValue(NAME, "Delete the selected " + resourceName());
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListRemoveIcon(16, 16));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			// Must have at lest one map
			if (resourceListModel.size() <= 1) {
				return;
			}
			deleteFromList(resourceList.getSelectedIndex());
		}
	}

	protected void deleteFromList(int index) {
		assert resourceListModel.size() >= 2;
		assert index != -1;
		int nextIndex = index == 0 ? resourceListModel.size() - 1 : index - 1;
		resourceList.setSelectedIndex(nextIndex);
		resourceListModel.remove(index);
	}

	protected class AddAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
	
		public AddAction() {
			putValue(NAME, "Add a new " + resourceName());
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new ListAllIcon(16, 16));
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			addToList();
		}
	}

	protected abstract void addToList();
	
	protected class AbstractListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 5874522377321012662L;
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
            R r= (R) value;
            label.setText(resourceDisplayName(r));
            return label;
        }
    }
	
}