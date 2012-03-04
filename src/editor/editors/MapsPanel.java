package editor.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.ListRemoveIcon;

import editor.Editor;
import editor.editors.AbstractSpriteSheetOrganiser.AddAction;
import editor.editors.AbstractSpriteSheetOrganiser.DeleteAction;
import editor.editors.AbstractSpriteSheetOrganiser.ImageListRenderer;
import editor.ui.HeaderPanel;
import editor.ui.TButton;
import engine.map.Map;
import engine.unit.UnitImages;

/**
 * @author Bilal Hussain
 */
public class MapsPanel extends JPanel implements IRefreshable {
	private static final Logger log = Logger.getLogger(MapsPanel.class);
	private static final long serialVersionUID = 7115578732106671007L;

	protected Editor editor;
	protected JList mapsList;
	protected DefaultListModel mapsListModel;

	public MapsPanel(Editor editor) {
		super(new BorderLayout());
		this.editor = editor;
		createMainPane();
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method

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

	protected JComponent createLeftPane() {
//		UnitImages uu = defaultImages();
//		setCurrentUnitImages(uu);
		
		mapsListModel = new DefaultListModel();
		
		mapsList = new JList(mapsListModel);
		mapsList.setCellRenderer(new ImageListRenderer());
//		mapsListModel.addElement(uu);
		mapsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Map ui =  (Map) mapsList.getSelectedValue();
				if (ui == null) return;
//				setCurrentUnitImages(ui);
			}
		});
		mapsList.setSelectedIndex(0);
	
		mapsList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (mapsListModel.size() <= 1) return;
	
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					deleteFromList(mapsList.getSelectedIndex());
				}
			}
		});
	
		JScrollPane slist = new JScrollPane(mapsList);
		slist.setColumnHeaderView(createHeader("All " + "Maps"));
		
		JPanel p  = new JPanel(new BorderLayout());
		p.add(slist, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel();
		buttons.add(new TButton(new DeleteAction()));
		buttons.add(new TButton(new AddAction()));
		p.add(buttons, BorderLayout.SOUTH);
		
		return p;
	}

	protected JPanel createInfoPanel() {
		return new JPanel();
	}

	protected JPanel createHeader(String text) {
		JPanel header = new HeaderPanel(new BorderLayout());
		header.add(new JLabel("<HTML>" + text + "<BR></HTML>"), BorderLayout.CENTER);
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
			// Must have at lest one map
			if (mapsListModel.size() <= 1) {
				return;
			}
			deleteFromList(mapsList.getSelectedIndex());
		}
	}

	protected void deleteFromList(int index) {
		assert mapsListModel.size() >= 2;
		assert index != -1;
		int nextIndex = index == 0 ? mapsListModel.size() - 1 : index - 1;
		mapsList.setSelectedIndex(nextIndex);
		mapsListModel.remove(index);
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

		}
	}

}
