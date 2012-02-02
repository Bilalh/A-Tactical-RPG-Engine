package editor.spritesheet;

import java.awt.*;
import java.awt.event.*;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import common.spritesheet.SpriteInfo;
import common.spritesheet.SpriteSheet;

import config.Config;
import config.XMLUtil;

import util.IOUtil;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * A Editor for spritesheets that can create, modify or split them. 
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
	private JFileChooser dirChooser = new JFileChooser(".");
	
	private JSpinner border = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
	private DefaultListModel sprites = new DefaultListModel();
	private JList list = new JList(sprites);

	private Packer packer = new Packer();

	public SpriteSheetEditor() {
		init();
	}

	public void init() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		setSize(810, 580);
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

		JMenuItem split = new JMenuItem("Spilt");
		split.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,mask));
		split.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spilt();
			}
		});
		
		file.add(open);		
		file.add(save);
		file.addSeparator();
		file.add(split);
		setJMenuBar(bar);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		saveChooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		chooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "png","jpg","jpeg","gif"));
		
		JTabbedPane tab = new JTabbedPane();
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("General"), "split, span, gaptop 4");
		p.add(new JSeparator(), "growx, wrap, gaptop 4");

		p.add(new JLabel("Sheet Name:"), "gap 4");
		p.add((sheetName = new JTextField(10)), "span, growx");
		sheetName.setText("Untitled Sheet");
		
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
							sprites.addElement(new MutableSprite(selected[i]));
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

//		p.add(new JLabel("Name:"), "gap 4");
//		p.add((selectedName = new JTextField(10)), "span, growx");

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

//		p.add(new JButton("Change"), new CC().spanX(5).split(5).tag("other"));
//		p.add(del, new CC().tag("other"));
		p.add(del, new CC().spanX(5).split(5).tag("other"));

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
		
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (sprites.isEmpty()) return;
				
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					sprites.remove(list.getSelectedIndex());
					renew();
				}else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_F2 ){
					if (list.getSelectedIndices().length != 1) return;
					MutableSprite selected =  ((MutableSprite) list.getSelectedValue());
					
					rename(selected); 
				}
			}
		});
		
		 list.addMouseListener(new MouseAdapter() {
		    @Override
			public void mouseClicked(MouseEvent e) {
		         if (!sprites.isEmpty() && e.getClickCount() == 2) {
		             int index = list.locationToIndex(e.getPoint());
		             rename((MutableSprite) sprites.getElementAt(index));
		          }
		     }
		 });
		
		
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

	// Redraws the sprite sheet
	private void renew() {
		try {
			ArrayList<MutableSprite> list = new ArrayList<MutableSprite>();
			for (int i = 0; i < sprites.size(); i++) {
				list.add((MutableSprite) sprites.elementAt(i));
			}

			int b = ((Integer) border.getValue());
			Sheet sheet = packer.packImages(list, sWidth, sHeight, b, null);
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

	public MutableSprite getSpriteAt(int x, int y) {
		for (int i = 0; i < sprites.size(); i++) {
			if (((MutableSprite) sprites.get(i)).contains(x, y)) {
				return ((MutableSprite) sprites.get(i));
			}
		}
		return null;
	}

	// Saves a Sprite sheet 	
	private int save() {
		String name = sheetName.getText();
		if (!name.endsWith(".png")) name += ".png";
		saveChooser.setSelectedFile(new File(name));
		
		int rst = saveChooser.showSaveDialog(this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File out = saveChooser.getSelectedFile();
			if (!out.getName().endsWith(".png")){
				out = new File(out.getParent(), out.getName()+".png");
			}
			
			ArrayList<MutableSprite> list = new ArrayList<MutableSprite>();
			for (int i = 0; i < sprites.size(); i++) {
				list.add((MutableSprite) sprites.elementAt(i));
			}

			try {
				int b = ((Integer) border.getValue());
				packer.packImages(list, sWidth, sHeight, b, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rst;
	}

	// loads a Sprite sheet 	
	private void load() {
		if (!sprites.isEmpty()){
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
					sprites.addElement(new MutableSprite(e.getKey(), e.getValue()));
				}
				sheetName.setText(in.getName());
				renew();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "xml file " + xml.getName() + " not found",
						"File Not found", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void spilt() {
		if (sprites.isEmpty()) return;
		
		int rst = dirChooser.showSaveDialog(SpriteSheetEditor.this);
		if (rst != JFileChooser.APPROVE_OPTION) return;
		File dir = dirChooser.getSelectedFile();
		for (int i = 0; i < sprites.size(); i++) {
			MutableSprite e = (MutableSprite) sprites.get(i);
			try {
			 	File f = new File(dir,e.getName());
			 	f.createNewFile();
				ImageIO.write(e.getImage(), "PNG", f);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this,"Image writing error", "Writing error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
	}


	public void delete(ArrayList<MutableSprite> selected) {
		for (MutableSprite s : selected) {
			sprites.removeElement(s);
		}
		renew();
	}
	
	private void rename(MutableSprite selected) {
		String s;// = JOptionPane.showInputDialog("New name for " + selected.getName(),  selected.getName());
		s =(String) JOptionPane.showInputDialog(SpriteSheetEditor.this, "New name for " + selected.getName(), 
				"Renaming " + selected.getName() , JOptionPane.INFORMATION_MESSAGE, 
				null, null, selected.getName());
		if (s!=null) selected.setName(s);
	}

	private class FileListRenderer extends DefaultListCellRenderer {
	
		private static final long serialVersionUID = 5874522377321012662L;
	
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
			MutableSprite sprite = (MutableSprite) value;
			label.setText(sprite.getName());
			return label;
		}
	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new SpriteSheetEditor().setVisible(true);
	}

	
}