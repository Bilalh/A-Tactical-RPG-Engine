package editor.spritesheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.LMUL;

import common.spritesheet.SpriteSheet;

import config.Config;
import config.XMLUtil;
import editor.spritesheet.ReorderableJList.ReorderableListCellRenderer;
import editor.ui.AlphanumComparator;
import engine.unit.UnitAnimation;
import engine.unit.UnitImages;

/**
 * A Editor for spritesheets that can create, modify or split them. It can also create animations. 
 * @author Bilal Hussain
 */
public class SpriteSheetEditor extends JFrame implements ISpriteProvider<MutableSprite> {
	private static final Logger log = Logger.getLogger(SpriteSheetEditor.class);
	
	private static final long serialVersionUID = 8150077954325861946L;
	private static final Integer[] sizes = new Integer[] { 128, 256, 512, 1024, 2048, 4096, 8192 };

	// Sprite sheets data
	private JTextField sheetName;
	private JTextField selectedName;
	private SpriteSheetPanel sheetPanel;
	private int sWidth = sizes[0], sHeight = sizes[0];

	// For IO
	private final String STARTING_PATH = "Resources/images";
	private JFileChooser chooser = new JFileChooser(STARTING_PATH);
	private JFileChooser saveChooser = new JFileChooser(STARTING_PATH);
	private JFileChooser dirChooser = new JFileChooser(STARTING_PATH);
	
	private JSpinner border = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));

	// Lists
	private DefaultListModel sprites = new DefaultListModel();
	private ReorderableJList list;
	private DefaultListModel lmanimations = new DefaultListModel();
	private JList lanimations = new JList(lmanimations);

	// Stores the data.	
	private Packer packer = new Packer();
	private UnitImages animations = new UnitImages();
	
	public SpriteSheetEditor(int frameClosingValue) {
		super("Sprite Sheet Editor");
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		
		setSize(810, 580);
		initGui();
		setJMenuBar(createMenubar());
		this.setDefaultCloseOperation(frameClosingValue);
	}

	private void initGui() {
		this.setLayout(new BorderLayout());
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		saveChooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		chooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "png", "jpg", "jpeg", "gif"));

		JTabbedPane tab = new JTabbedPane();
		tab.add("Data",       createDataPanel());
		tab.add("Listing",    createFileListPanel());
		tab.add("Animations", createAnimationPanel());
		
		this.add(tab, BorderLayout.EAST);
		this.add(createPanel(""), BorderLayout.WEST);
		this.add(createPanel(""), BorderLayout.SOUTH);
		this.add(createPanel(""), BorderLayout.NORTH);

		sheetPanel = new SpriteSheetPanel(this);
		sheetPanel.setSheetSize(sWidth, sHeight);
		this.add(new JScrollPane(sheetPanel), BorderLayout.CENTER);
		
		renew();
	}

	private JPanel createDataPanel(){
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		p.add(new JLabel("General"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

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

		// p.add(new JLabel("Name:"), "gap 4");
		// p.add((selectedName = new JTextField(10)), "span, growx");

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

		// p.add(new JButton("Change"), new CC().spanX(5).split(5).tag("other"));
		// p.add(del, new CC().tag("other"));
		p.add(del, new CC().spanX(5).split(5).tag("other"));
		return p;
	}
	
	private JScrollPane createFileListPanel(){

		list = new ReorderableJList(sprites, new IDragFinishedListener() {
			@Override
			public void dragDropEnd(DragSourceDropEvent dsde) {
				renew();
			}
		});

		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setBounds(540, 5, 200, 350);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object[] values = list.getSelectedValues();
				ArrayList<MutableSprite> sprites = new ArrayList<MutableSprite>();
				for (int i = 0; i < values.length; i++) {
					sprites.add((MutableSprite) values[i]);
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
				if (sprites.isEmpty() || list.getSelectedIndex() < 0) return;

				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					for (Object o : list.getSelectedValues()) {
						sprites.removeElement(o);
					}

					renew();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_F2) {
					if (list.getSelectedIndices().length != 1) return;
					MutableSprite selected = ((MutableSprite) list.getSelectedValue());

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
		return listScroll;
	}

	private JScrollPane createAnimationPanel(){
		JScrollPane animationScroll = new JScrollPane(lanimations);
		animationScroll.setBounds(540, 5, 200, 350);
		
		lanimations.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		lanimations.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (lmanimations.isEmpty() || lanimations.getSelectedIndex() < 0) return;

				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
					for (Object o : lanimations.getSelectedValues()) {
						lmanimations.removeElement(o);
						animations.remove(((UnitAnimation)o).getName());
					}
					lanimations.repaint();
				}
			}
		});
		return animationScroll;
	}
	
	private JMenuBar createMenubar() {
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
		
		JMenuItem neww = new JMenuItem("New");
		neww.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,mask));
		neww.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});

		JMenuItem addSheet = new JMenuItem("Add Sheet");
		addSheet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,mask));
		addSheet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSheet(false);
			}
		});

		file.add(neww);		
		file.add(open);		
		file.add(save);
		file.addSeparator();
		file.add(split);
		file.add(addSheet);
		
		JMenu edit = new JMenu("Edit");
		bar.add(edit);
		
		JMenuItem selectedAll = new JMenuItem("Select All");
		selectedAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,mask));
		selectedAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sprites.isEmpty()) return;
				Object[] values = sprites.toArray();
				ArrayList<MutableSprite> sprites = new ArrayList<MutableSprite>();
				for (int i = 0; i < values.length; i++) {
					sprites.add((MutableSprite) values[i]);
				}
				select(sprites);
			}
		});
		
		JMenuItem sort = new JMenuItem("Sort by Name");
		sort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] values =sprites.toArray();
				ArrayList<MutableSprite> alist = new ArrayList<MutableSprite>();
				for (int i = 0; i < values.length; i++) {
					alist.add((MutableSprite) values[i]);
				}
				Collections.sort(alist, new Comparator<MutableSprite>() {
					@Override
					public int compare(MutableSprite o1, MutableSprite o2) {
						return AlphanumComparator.compareStrings(o1.getName(), o2.getName());
					}
				});
				
				
				sprites.clear();
				for (MutableSprite mutableSprite : alist) {
					sprites.addElement(mutableSprite);
				}
				renew();
			}
		});

		JMenuItem removeExt = new JMenuItem("Remove Extensions from Names");
		removeExt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] values =sprites.toArray();
				for (Object o : values) {
					MutableSprite s = (MutableSprite) o;
					final String str = s.getName();
					final int index  = str.lastIndexOf('.');
					if(index ==-1) continue;
					s.setName(str.substring(0, index));
				}
				list.repaint();
			}
		});
		
		
		JMenuItem animation = new JMenuItem("Make Animation from Selected");
		animation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeAnimation();
			}
		});
		
		edit.add(selectedAll);
		edit.add(sort);
		edit.add(removeExt);
		edit.addSeparator();
		edit.add(animation);
		
		return bar;
	}

	private JPanel createPanel(String string) {
		JPanel a = new JPanel();
		a.add(new JLabel(string));
		return a;
	}

	/**
	 * Redraws the sprite sheet
	 */
	private void renew() {
		ArrayList<MutableSprite> list = new ArrayList<MutableSprite>();
		for (int i = 0; i < sprites.size(); i++) {
			list.add((MutableSprite) sprites.elementAt(i));
		}

		int b = ((Integer) border.getValue());
		Sheet sheet = packer.packImages(list, sWidth, sHeight, b);
		sheetPanel.setSpriteSheet(sheet);
	}


	/**
	 * Selected the specifed sprites
	 */
	@Override
	public void select(java.util.List<MutableSprite> selection) {
		list.clearSelection();
		int[] selected = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			selected[i] = sprites.indexOf(selection.get(i));
		}
		list.setSelectedIndices(selected);
		sheetPanel.setSelectedSprites(selection);
	}


	/**
	 * Deletes the specifed Sprite(s)
	 */
	@Override
	public void delete(java.util.List<MutableSprite> selected) {
		for (MutableSprite s : selected) {
			sprites.removeElement(s);
		}
		renew();
	}

	/**
	 * Saves a Sprite sheet
	 */
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
					String aname = out.getName().replaceAll("\\.png$", "") + "-animations.xml"; 
					
					String shortaName = out.getAbsolutePath().replaceFirst(".*Resources/", "");
					animations.setSpriteSheetLocation(shortaName);
					
					PrintStream pout = new PrintStream(new FileOutputStream(new File(out.getParentFile(),aname)));
					pout.print(XMLUtil.makeFormattedXml(animations));					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rst;
	}


	private void load() {
		if (!sprites.isEmpty() && !askToSave() ) return;
		loadSheet(true);
	}

	/**
	 * loads a Sprite sheet
	 */
	private void loadSheet(boolean reset){
		int rst = chooser.showOpenDialog(SpriteSheetEditor.this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File in = chooser.getSelectedFile();
			File xml = new File(in.getParentFile(), in.getName().replaceAll("\\.png$", "\\.xml"));
			try {
				BufferedImage b = ImageIO.read(in);
				SpriteSheet ss = new SpriteSheet(b, new FileInputStream(xml));
				if (reset){
					sprites.clear();
					sheetName.setText(in.getName());
				}
				for (Entry<String, BufferedImage> e : ss.getSpritesMap().entrySet()) {
					sprites.addElement(new MutableSprite(e.getKey(), e.getValue()));
				}
				
				File a = new File(in.getParentFile(), in.getName().replaceAll("\\.png$", "") + "-animations.xml");
				if (a.exists()){
					UnitImages temp = XMLUtil.convertXml(new FileInputStream(a));
					if (temp != null){
						animations = temp;
						for (Entry<String, UnitAnimation> e: animations.entrySet()) {
							lmanimations.addElement(e.getValue());
							lanimations.repaint();
						}
					}else{
						animations = new UnitImages();
					}
				}
				
				renew();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "xml file " + xml.getName() + " not found",
						"File Not found", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Spilts a sprite sheet into multiple images
	 */
	private void spilt() {
		if (sprites.isEmpty()) return;
		
		int rst = dirChooser.showSaveDialog(SpriteSheetEditor.this);
		if (rst != JFileChooser.APPROVE_OPTION) return;
		File dir = dirChooser.getSelectedFile();
		if (!dir.isDirectory()) dir = dir.getParentFile();
		log.info("Saving to dir: " +dir);
		for (int i = 0; i < sprites.size(); i++) {
			MutableSprite e = (MutableSprite) sprites.get(i);
			try {
				String name = e.getName();
				if (!name.endsWith(".png")) name += ".png";
			 	File f = new File(dir,name);
			 	f.createNewFile();
				ImageIO.write(e.getImage(), "PNG", f);
				log.info("Saved: " +f.getName());
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this,"Image writing error", "Writing error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
	}

	/**
	 * Removes the sprite sheets
	 */
	private void clear() {
		if (sprites.isEmpty() || !askToSave() ) return;
		sprites.clear();
		animations = new UnitImages();
		lmanimations.clear();
		renew();
	}

	private boolean askToSave(){
		int rst =JOptionPane.showConfirmDialog(SpriteSheetEditor.this, "Save current sheet?");
		if      (rst == JOptionPane.CANCEL_OPTION) return false;
		else if (rst == JOptionPane.YES_OPTION){
			if (rst == JOptionPane.CANCEL_OPTION) return false;
		}
		return true;
	}

	/**
	 * Changes the id of the sprite
	 */
	private void rename(MutableSprite selected) {
		String s;// = JOptionPane.showInputDialog("New name for " + selected.getName(),  selected.getName());
		s =(String) JOptionPane.showInputDialog(SpriteSheetEditor.this, "New name for " + selected.getName(), 
				"Renaming " + selected.getName() , JOptionPane.INFORMATION_MESSAGE, 
				null, null, selected.getName());
		if (s!=null) selected.setName(s);
	}

	/**
	 * Makes a animation from the selected images
	 */
	private void makeAnimation() {
		if (list.getSelectedIndex() == -1) return;
		String base;
		base =(String) JOptionPane.showInputDialog(SpriteSheetEditor.this, "Base name for animation", 
				"Animation" , JOptionPane.INFORMATION_MESSAGE, null, null, ((MutableSprite) list.getSelectedValue()).getName());
		if (base!=null){
			int i=0;
			for (Object o : list.getSelectedValues()) {
				MutableSprite ms = (MutableSprite) o;
				ms.setName(base + i++);
			}
			UnitAnimation data =  new UnitAnimation(base, list.getSelectedValues().length);
			animations.put(base, data);
			lmanimations.addElement(data);
			list.repaint();
			lanimations.repaint();
		}
	}
	
	/**
	 * Displays the name of the sprites.
	 * @author Bilal Hussain
	 */
	private class FileListRenderer extends  ReorderableListCellRenderer {
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
		new SpriteSheetEditor(JFrame.EXIT_ON_CLOSE).setVisible(true);
	}


	
}
	