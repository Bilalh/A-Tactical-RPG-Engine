package editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.FileUtils;

import util.FileChooser;
import util.IOUtil;
import view.Gui;

import com.javarichclient.icon.tango.actions.DocumentNewIcon;
import com.javarichclient.icon.tango.actions.DocumentOpenIcon;
import com.javarichclient.icon.tango.actions.ListAllIcon;

import config.Config;
import controller.MainController;

import editor.editors.AbstractResourcesPanel;
import editor.util.Prefs;
import engine.skills.ISkill;
import engine.skills.RangedSkill;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * Allows the user to choose a project to edit. 
 * It also allows exporting a Mac OS X apllication as well as a jar.
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
		resourceList.setCellRenderer(new RecentFileRenderer());
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
		
		JLabel title  = new JLabel("Tactical RPG Editor");
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
			putValue(SMALL_ICON,new DocumentNewIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FileChooser c = new FileChooser(frame, "Export Project", "app");
			File dir  = c.getDir();
			if (dir == null) return;
			
			String appName =  dir.getName();
			appName =  IOUtil.removeExtension(appName);
			dir = new File(dir.getParentFile(), appName);
			try {
				FileUtils.copyDirectory(new File("bundle/Default"), dir );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			openProject(dir);
		}
	}
	
	private class OpenOther extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;
		private FileChooser chooser = new FileChooser(frame, "Choose a project file", "tacticalproject");
		
		public OpenOther() {
			putValue(NAME, "Open Other");
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON,new DocumentOpenIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File f = chooser.loadFile();
			if (f == null) {
				return;
			}
			f = f.getParentFile();
			openProject(f);
		}
	}

	void openProject(File f){
		recentFiles.remove(f);
		recentFiles.add(0, f);
		saveToPrefs();
		startEditor(f);
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

	public static class RecentFileRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5874522377321012662L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			File f = (File) value;
			label.setText("<HTML><p style=\"padding:7; font-size:18\">" + f.getName() + "</p>" + f.getAbsolutePath() + "</HTML>");
			// label.setIcon(new ImageIcon(ResourceManager.instance().getItem(w.getImageRef())));
			return label;
		}
	}

}
