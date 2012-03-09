package editor;

import static org.jvnet.inflector.Noun.pluralOf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.FileChooser;
import view.Gui;

import com.javarichclient.icon.tango.actions.ListAllIcon;

import config.Config;
import controller.MainController;

import editor.editors.AbstractResourcesPanel;
import editor.util.Prefs;
import engine.skills.RangedSkill;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Bilal Hussain
 */
public class ProjectChooser {

	JFrame frame;
	JList recent;

	protected JList resourceList;
	protected Vector<File> recentFiles;
	
	public ProjectChooser() {
		Preferences pref = Prefs.getNode("recent");
		int num = pref.getInt("numberOfFiles", 0);
		
		recentFiles = new Vector<File>();
		for (int i = 0; i < num; i++) {
			String path = pref.get("recentFile"+i, null);
			if (path == null) continue;
			File f = new File(path);
			if (!f.exists()) continue;
			recentFiles.add(f);
		}
		
		frame = new JFrame("Project chooser");
		frame.setContentPane(createMainPane());
		frame.setVisible(true);
		frame.setMinimumSize(new Dimension(600, 375));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Container createMainPane() {
		JComponent p = createInfoPanel();
		JSplitPane mainSplit;
		mainSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, true, p, createLeftPane());
		mainSplit.setResizeWeight(0.05);

		return mainSplit;
	}

	private Component createLeftPane() {
		resourceList = new JList(recentFiles);
		resourceList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == true) return;
				startEditor((File) resourceList.getSelectedValue());
			}
		});	
		
		JScrollPane slist = new JScrollPane(resourceList);
		slist.setMinimumSize(new Dimension(100, 200));
		slist.setColumnHeaderView(AbstractResourcesPanel.createHeader("Recent Projects"));
		return slist;
	}

	private JComponent createInfoPanel() {
		JPanel p = new JPanel(new MigLayout());
		
		JLabel title  = new JLabel("Tacical RPG Editor");
		title.setFont(new Font("Serif", Font.BOLD, 18));
		
		p.add(title, "span, center, wrap");
		p.add(new JButton(new NewProject()), "id b1, pushx");
		p.add(new JButton(new OpenOther()), "span, center");
		return p;
	}

	public void saveToPrefs(){
		Preferences pref = Prefs.getNode("recent");
		pref.putInt("numberOfFiles", recentFiles.size());

		System.out.println(recentFiles);
		for (int i = 0; i < recentFiles.size(); i++) {
			pref.put("recentFile"+i, recentFiles.get(i).getAbsolutePath());
		}
		
		try {
			pref.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	

	public void startEditor(final File selected) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Editor editor = new Editor(selected);
			}
		});
		frame.dispose();
	}
	
	private class NewProject extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public NewProject() {
			putValue(NAME, "New Project");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
//			putValue(SMALL_ICON,new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	private class OpenOther extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
		private FileChooser chooser = new FileChooser(frame, "Choose a project file", "tacticalproject");
		
		public OpenOther() {
			putValue(NAME, "Open Other");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
//			putValue(SMALL_ICON,new ListAllIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = chooser.loadFile();
			if (f == null) {
				return;
			}
			f = f.getParentFile();
			recentFiles.remove(f);
			recentFiles.add(0, f);
			saveToPrefs();
			startEditor(f);
		}
	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ProjectChooser projectChooser = new ProjectChooser();
			}
		});
	}

}
