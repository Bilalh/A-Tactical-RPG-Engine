package editor.spritesheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
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

import common.enums.ImageType;
import common.spritesheet.SpriteSheet;

import config.Config;
import config.XMLUtil;
import config.xml.ITileMapping;
import config.xml.TileImageData;
import config.xml.TileMapping;
import editor.spritesheet.ReorderableJList.ReorderableListCellRenderer;
import editor.util.AlphanumComparator;
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

	private JComboBox widths, heights;
	private int sWidth = sizes[0], sHeight = sizes[0];

	// For IO
	private String STARTING_PATH;
	private JFileChooser chooser;
	private JFileChooser saveChooser;
	private JFileChooser dirChooser;
	
	private JSpinner border = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));

	// Lists
	private DefaultListModel sprites = new DefaultListModel();
	private ReorderableJList list;
	private DefaultListModel lmanimations = new DefaultListModel();
	private JList lanimations = new JList(lmanimations);

	// Stores the data.	
	private Packer packer = new Packer();
	private UnitImages animations = new UnitImages();
	
	// For editor
	private String savePath;
	private ISpriteEditorListener listener; 
	
	private boolean showAnimations;
	private String helpString;
	private boolean validationForUnits;
	private boolean makeTileMapping;
	
	public SpriteSheetEditor(int frameClosingValue) {
		this(frameClosingValue, null,null,"",true,false,false);
	}

	public SpriteSheetEditor(int frameClosingValue, String savePath, ISpriteEditorListener listener, 
			String helpString, boolean showAnimations,
			boolean validationForUnits, boolean makeTileMapping) {
		super("Sprite Sheet Editor");
		this.savePath           = savePath;
		this.listener           = listener;
		this.helpString         = helpString;
		this.showAnimations     = showAnimations;
		this.validationForUnits = validationForUnits;
		this.makeTileMapping    = makeTileMapping;
		
		STARTING_PATH = Config.getResourceDirectory() +  "/images";
		chooser = new JFileChooser(STARTING_PATH);
		saveChooser = new JFileChooser(STARTING_PATH);
		dirChooser = new JFileChooser(STARTING_PATH);
		
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		
		setSize(810, 580);
		initGui();
		setJMenuBar(createMenubar());
		this.setDefaultCloseOperation(frameClosingValue);

		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (finished()){
					SpriteSheetEditor.this.dispose();
				}
			}
		});
		
		if (savePath != null){
			File f = new File(savePath);
			if (f.exists()) loadSheetFromFile(f, true);
		}
	}
	
	private boolean finished(){
		if (savePath != null){
			if (save() == JFileChooser.ERROR_OPTION) return false;
			assert listener != null;
			listener.spriteEditingFinished();
			return true;
		}
		return false;
	}
	
	private void initGui() {
		this.setLayout(new BorderLayout());
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		saveChooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		chooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "png", "jpg", "jpeg", "gif"));

		JTabbedPane tab = new JTabbedPane();
		tab.add("Data",       createDataPanel());
		tab.add("Listing",    createFileListPanel());
		
		JScrollPane ani = createAnimationPanel();
		if (showAnimations){
			tab.add("Animations", ani);			
		}
		
		this.add(tab, BorderLayout.EAST);
		this.add(createPanel(""), BorderLayout.WEST);
		this.add(createPanel(helpString), BorderLayout.SOUTH);
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

		sheetName = new JTextField(10);
		sheetName.setText("Untitled Sheet");
		JLabel name =new JLabel("Sheet Name:"); 
		p.add(name, "gap 4");
		p.add(sheetName, "span, growx");
		if (savePath != null){
			name.setVisible(false);
			sheetName.setVisible(false);
		}
		
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
		widths = new JComboBox(sizes);
		widths.setActionCommand("width");
		widths.addActionListener(aSizes);
		p.add(widths, "alignx leading, span, wrap");

		p.add(new JLabel("Height:"), "gap 4");
		heights = new JComboBox(sizes);
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

		if (savePath == null){
			file.add(neww);		
			file.add(open);	
		}		
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
		
		JMenuItem tileMapping = new JMenuItem("Make Tile Mapping from Sheet");
		tileMapping.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveTileMapping();
			}
		});		
		
		edit.add(selectedAll);
		edit.add(sort);
		edit.add(removeExt);
		edit.addSeparator();
		if (showAnimations)   edit.add(animation);
		if (savePath == null) edit.add(tileMapping);	

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
		if (savePath != null){
			if (validationForUnits) {

				HashSet<String> needed = new HashSet<String>(Arrays.asList(new String[] { "north0", "south0", "east0", "west0" }));
				HashSet<String> have   = new HashSet<String>();

				for (int i = 0; i < sprites.size(); i++) {
					MutableSprite s = (MutableSprite) sprites.get(i);
					if (needed.contains(s.getName())) {
						have.add(s.getName());
					}
				}

				if (needed.size() != have.size()) {
					JOptionPane.showMessageDialog(this, helpString, "Invaild", JOptionPane.ERROR_MESSAGE);
					return JFileChooser.ERROR_OPTION;
				}
			}

			saveToFile(new File(savePath));
			return JFileChooser.APPROVE_OPTION;
		}
		
		String name = sheetName.getText();
		if (!name.endsWith(".png")) name += ".png";
		saveChooser.setSelectedFile(new File(name));
		
		int rst = saveChooser.showSaveDialog(this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File out = saveChooser.getSelectedFile();
			saveToFile(out);
		}
		return rst;
	}

	private void saveToFile(File out) {
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

			PrintStream pout = new PrintStream(new FileOutputStream(new File(out.getParentFile(), aname)));
			pout.print(XMLUtil.makeFormattedXml(animations));

			if (makeTileMapping) {
				String tname = out.getName().replaceAll("\\.png$", "") + "-mapping.xml";
				saveTileMappingToFile(new File(out.getParent(), tname));
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void saveTileMapping(){
		saveChooser.setSelectedFile(new File("mapping.xml"));
		int rst = saveChooser.showSaveDialog(SpriteSheetEditor.this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			saveTileMappingToFile(saveChooser.getSelectedFile());
		}
	}

	private void saveTileMappingToFile(File out) {
		HashMap<String, TileImageData> mapping = new HashMap<String, TileImageData>();
		for (int i = 0; i < sprites.size(); i++) {
			MutableSprite m = (MutableSprite) sprites.elementAt(i);
			mapping.put(m.getName(), new TileImageData(m.getName(), ImageType.NON_TEXTURED));	
		}
		String name = sheetName.getText();
		if (!name.endsWith(".png")) name += ".png";
		ITileMapping map = new TileMapping("images/tilesets/"+name, mapping);
		String s2 = XMLUtil.makeFormattedXml(map);
		FileWriter fw;
		try {
			fw = new FileWriter(out);
			fw.write(s2);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			loadSheetFromFile(in, reset);
		}
	}

	private void loadSheetFromFile(File in, boolean reset) {
		File xml = new File(in.getParentFile(), in.getName().replaceAll("\\.png$", "\\.xml"));
		File ani = new File(in.getParentFile(), in.getName().replaceAll("\\.png$", "") + "-animations.xml");
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
			
			if (ani.exists()){
				UnitImages temp = XMLUtil.convertXml(new FileInputStream(ani));
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
			
			widths.setSelectedItem(ss.getWidth());
			heights.setSelectedItem(ss.getHeight());
			
			assert ((Number) widths.getSelectedItem()).intValue()  == ss.getWidth();
			assert ((Number) heights.getSelectedItem()).intValue() == ss.getHeight();
			// For safety
			renew();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "xml file " + xml.getAbsolutePath() + " not found",
					"File Not found", JOptionPane.ERROR_MESSAGE);
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
			if  (rst == JOptionPane.CANCEL_OPTION) return false;
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
	