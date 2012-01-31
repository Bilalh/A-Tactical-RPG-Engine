package editor.imagePacker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Bilal Hussain
 */
public class SpriteSheetEditor extends JFrame {

	private static final long serialVersionUID = 8150077954325861946L;
	private static final Integer[] sizes = new Integer[] { 128, 256, 512, 1024, 2048, 4096, 8192 };

	private JTextField sheetName;
	private JTextField selectedType;

	private SheetPanel2 sheetPanel;
	private int sWidth = sizes[0], sHeight = sizes[0];

	private JFileChooser chooser = new JFileChooser(".");
	private JFileChooser saveChooser = new JFileChooser(".");

	private JSpinner border = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
	private DefaultListModel sprites = new DefaultListModel();
	private JList list = new JList(sprites);

	Pack pack = new Pack();

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
		JMenuItem save = new JMenuItem("Save");
		file.add(save);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		setJMenuBar(bar);

		saveChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				return (f.getName().endsWith(".png"));
			}

			@Override
			public String getDescription() {
				return "PNG Images (*.png)";
			}

		});
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				return (f.getName().endsWith(".png") ||
						f.getName().endsWith(".jpg") ||
				f.getName().endsWith(".gif"));
			}

			@Override
			public String getDescription() {
				return "Images (*.jpg, *.png, *.gif)";
			}

		});

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
				sheetPanel.setTextureSize(sWidth, sHeight);
				regenerate();
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
				regenerate();
			}
		});
		p.add(border, "alignx leading, span, wrap");

		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int resp = chooser.showOpenDialog(SpriteSheetEditor.this);
				if (resp == JFileChooser.APPROVE_OPTION) {
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
				regenerate();
			}
		});

		p.add(add, new CC().spanX(5).split(5).wrap().tag("other"));

		p.add(new JLabel("Selected"), "split, span, gaptop 4");
		p.add(new JSeparator(), "growx, wrap, gaptop 4");

		p.add(new JLabel("Type:"), "gap 4");
		p.add((selectedType = new JTextField(10)), "span, growx");

		JButton del = new JButton("Delete");
		del.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] selected = list.getSelectedValues();
				for (int i = 0; i < selected.length; i++) {
					sprites.removeElement(selected[i]);
				}
				regenerate();
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

		sheetPanel = new SheetPanel2(this);
		JScrollPane scroll = new JScrollPane(sheetPanel);
		this.add(scroll, BorderLayout.CENTER);

		sheetPanel.setTextureSize(sWidth, sHeight);
		regenerate();
	}

	protected void regenerate() {
		try {
			ArrayList list = new ArrayList();
			for (int i = 0; i < sprites.size(); i++) {
				list.add(sprites.elementAt(i));
			}

			int b = ((Integer) border.getValue());
			Sheet sheet = pack.packImages(list, sWidth, sHeight, b, null);
			sheetPanel.setImage(sheet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Component createButton(String string, ActionListener al) {
		JButton btn = new JButton(string);
		btn.addActionListener(al);
		return btn;
	}

	private JPanel createPanel(String string) {
		JPanel a = new JPanel();
		a.add(new JLabel(string));
		return a;
	}

	public static void main(String[] args) {
		new SpriteSheetEditor().setVisible(true);
	}

	public void select(ArrayList selection) {
		list.clearSelection();
		int[] selected = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			selected[i] = sprites.indexOf(selection.get(i));
		}
		list.setSelectedIndices(selected);
		sheetPanel.setSelection(selection);
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
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			Spritee sprite = (Spritee) value;
			label.setText(sprite.getName());

			return label;
		}
	}

	private void save() {
		int resp = saveChooser.showSaveDialog(this);
		if (resp == JFileChooser.APPROVE_OPTION) {
			File out = saveChooser.getSelectedFile();

			ArrayList list = new ArrayList();
			for (int i = 0; i < sprites.size(); i++) {
				list.add(sprites.elementAt(i));
			}

			try {
				int b = ((Integer) border.getValue()).intValue();
				pack.packImages(list, sWidth, sHeight, b, out);
			} catch (IOException e) {
				// shouldn't happen
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to write output");
			}
		}
	}

}
