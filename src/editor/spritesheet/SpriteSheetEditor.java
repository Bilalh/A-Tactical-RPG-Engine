package editor.spritesheet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import common.spritesheet.Sprite;
import common.spritesheet.SpriteSheet;

import config.Config;
import config.XMLUtil;

import util.IOUtil;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Bilal Hussain
 */
public class SpriteSheetEditor extends JFrame {

	private static final long serialVersionUID = 8150077954325861946L;
	private static final Integer[] sizes = new Integer[] { 128, 256, 512, 1024, 2048, 4096, 8192 };

	private JTextField sheetName;
	private JTextField selectedName;

	private SpriteSheetPanel sheetPanel;
	private int sWidth = sizes[0], sHeight = sizes[0];

	private JFileChooser chooser = new JFileChooser(".");
	private JFileChooser saveChooser = new JFileChooser(".");

	private JSpinner border = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
	private DefaultListModel sprites = new DefaultListModel();
	private JList list = new JList(sprites);

	Packer pack = new Packer();

	public SpriteSheetEditor() {
		init();
	}

	public void init() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		setSize(800, 400);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		bar.add(file);
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,mask));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		JMenuItem open = new JMenuItem("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,mask));
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		
		file.add(open);		
		file.add(save);
		setJMenuBar(bar);

		saveChooser.setFileFilter(IOUtil.makeFileFilter(true, ".png", "Portable Network Graphics (*.png)"));
		
		chooser.setFileFilter(IOUtil.makeRegexFileFilter(
				true, ".*\\.(png|jpe?g|gif)$", "Images (*.jpg, *.png, *.gif)"));

		JTabbedPane tab = new JTabbedPane();
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("General"), "split, span, gaptop 4");
		p.add(new JSeparator(), "growx, wrap, gaptop 4");

		p.add(new JLabel("Sheet Name:"), "gap 4");
		p.add((sheetName = new JTextField(10)), "span, growx");

		ActionListener aSizes = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox b = ((JComboBox) e.getSource());
				int i = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
				if (b.getActionCommand().equals("width")) {
					sWidth = i;
				} else {
					sHeight = i;
				}
				sheetPanel.setSheetSize(sWidth, sHeight);
				renew();
			}
		};

		p.add(new JLabel("Width:"), "gap 4");
		JComboBox widths = new JComboBox(sizes);
		widths.setActionCommand("width");
		widths.addActionListener(aSizes);
		p.add(widths, "alignx leading, span, wrap");

		p.add(new JLabel("Height:"), "gap 4");
		JComboBox heights = new JComboBox(sizes);
		heights.setActionCommand("heights");
		heights.addActionListener(aSizes);
		p.add(heights, "alignx leading, span, wrap");

		p.add(new JLabel("Border:"), "gap 4");
		border.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				renew();
			}
		});
		p.add(border, "alignx leading, span, wrap");

		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.setMultiSelectionEnabled(true);
				int rst = chooser.showOpenDialog(SpriteSheetEditor.this);
				if (rst == JFileChooser.APPROVE_OPTION) {
					File[] selected = chooser.getSelectedFiles();
					for (int i = 0; i < selected.length; i++) {
						try {
							sprites.addElement(new Spritee(selected[i]));
						} catch (IOException x) {
							x.printStackTrace();
							JOptionPane.showMessageDialog(SpriteSheetEditor.this, "Unable to load: " + selected[i].getName());
						}
					}
				}
				renew();
			}
		});

		p.add(add, new CC().spanX(5).split(5).wrap().tag("other"));

		p.add(new JLabel("Selected"), "split, span, gaptop 4");
		p.add(new JSeparator(), "growx, wrap, gaptop 4");

		p.add(new JLabel("Type:"), "gap 4");
		p.add((selectedName = new JTextField(10)), "span, growx");

		JButton del = new JButton("Delete");
		del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] selected = list.getSelectedValues();
				for (int i = 0; i < selected.length; i++) {
					sprites.removeElement(selected[i]);
				}
				renew();
			}
		});

		p.add(new JButton("Change"), new CC().spanX(5).split(5).tag("other"));
		p.add(del, new CC().tag("other"));
		tab.add("Data", p);

		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setBounds(540, 5, 200, 350);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object[] values = list.getSelectedValues();
				ArrayList sprites = new ArrayList();
				for (int i = 0; i < values.length; i++) {
					sprites.add(values[i]);
				}

				list.removeListSelectionListener(this);
				select(sprites);
				list.addListSelectionListener(this);
			}
		});
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setCellRenderer(new FileListRenderer());
		tab.add("Listing", listScroll);

		this.add(tab, BorderLayout.EAST);
		this.add(createPanel(""), BorderLayout.WEST);
		this.add(createPanel(""), BorderLayout.SOUTH);
		this.add(createPanel(""), BorderLayout.NORTH);

		sheetPanel = new SpriteSheetPanel(this);
		JScrollPane scroll = new JScrollPane(sheetPanel);
		this.add(scroll, BorderLayout.CENTER);

		sheetPanel.setSheetSize(sWidth, sHeight);
		renew();
	}

	protected void renew() {
		try {
			ArrayList<Spritee> list = new ArrayList<Spritee>();
			for (int i = 0; i < sprites.size(); i++) {
				list.add((Spritee) sprites.elementAt(i));
			}

			int b = ((Integer) border.getValue());
			Sheet sheet = pack.packImages(list, sWidth, sHeight, b, null);
			sheetPanel.setImage(sheet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private JPanel createPanel(String string) {
		JPanel a = new JPanel();
		a.add(new JLabel(string));
		return a;
	}

	public void select(ArrayList selection) {
		list.clearSelection();
		int[] selected = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			selected[i] = sprites.indexOf(selection.get(i));
		}
		list.setSelectedIndices(selected);
		sheetPanel.setSelectedSprites(selection);
	}

	public Spritee getSpriteAt(int x, int y) {
		for (int i = 0; i < sprites.size(); i++) {
			if (((Spritee) sprites.get(i)).contains(x, y)) {
				return ((Spritee) sprites.get(i));
			}
		}
		return null;
	}

	private class FileListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 5874522377321012662L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			Spritee sprite = (Spritee) value;
			label.setText(sprite.getName());
			return label;
		}
	}

	// Saves a Sprite sheet 	
	private int save() {
		int rst = saveChooser.showSaveDialog(this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File out = saveChooser.getSelectedFile();

			ArrayList<Spritee> list = new ArrayList<Spritee>();
			for (int i = 0; i < sprites.size(); i++) {
				list.add((Spritee) sprites.elementAt(i));
			}

			try {
				int b = ((Integer) border.getValue());
				pack.packImages(list, sWidth, sHeight, b, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rst;
	}

	// loads a Sprite sheet 	
	private void load() {
		if (sprites.size() >0){
			int rst =JOptionPane.showConfirmDialog(SpriteSheetEditor.this, "Save current sheet?");
			if      (rst == JOptionPane.CANCEL_OPTION) return;
			else if (rst == JOptionPane.YES_OPTION){
				if (save() == JFileChooser.CANCEL_OPTION) return;
			}
		}
		
		chooser.setMultiSelectionEnabled(false);
		int rst = chooser.showOpenDialog(SpriteSheetEditor.this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File in = chooser.getSelectedFile();
			File xml = new File(in.getParentFile(), in.getName().replaceAll("\\.png", "\\.xml"));
			try {
				BufferedImage b = ImageIO.read(in);
				SpriteSheet ss = new SpriteSheet(b, new FileInputStream(xml));
				sprites.clear();
				for (Entry<String, BufferedImage> e : ss.getSpritesMap().entrySet()) {
					sprites.addElement(new Spritee(e.getKey(), e.getValue()));
				}
				renew();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "xml file " + xml.getName() + " not found",
						"File Not found", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new SpriteSheetEditor().setVisible(true);
	}

	
}
