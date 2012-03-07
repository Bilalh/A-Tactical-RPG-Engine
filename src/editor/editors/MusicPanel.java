package editor.editors;

import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import util.IOUtil;

import common.assets.MusicData;
import common.assets.Musics;

import config.Config;
import editor.Editor;

/**
 * @author Bilal Hussain
 */
public class MusicPanel extends AbstractResourcesPanel<MusicData, Musics> {
	private static final long serialVersionUID = -8134784428673033659L;

	private MusicData current;
	private JFileChooser chooser;

	private JTextField infoTitle;
	private JTextField infoTrack;
	private JTextField infoAlbum;
	private JTextField infoArtist;
	
	private HashMap<UUID, TrackInfo> cachedInfo = new HashMap<UUID, MusicPanel.TrackInfo>();
	
	public MusicPanel(Editor editor) {
		super(editor);
		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Ogg Audio (*.ogg)", "ogg"));
		chooser.setMultiSelectionEnabled(true);
		cachedInfo = new HashMap<UUID, TrackInfo>();
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method
	}

	@Override
	protected void setCurrentResource(MusicData resource) {
		current = resource;
		
		TrackInfo ti = cachedInfo.get(current.getUuid());
		if (ti != null){
			infoArtist.setText(ti.artist);
			infoAlbum.setText(ti.album);
			infoTitle.setText(ti.title);
			infoTrack.setText(ti.track);
			return;
		}
				
		try {
			File file =Config.getResourceFile(current.getLocation());
			if (!file.exists()) return;
			
			TrackInfo info = new TrackInfo();
			AudioFile f = AudioFileIO.read(Config.getResourceFile(current.getLocation()));
			Tag tag = f.getTag();
			infoAlbum.setText(  (info.album  = tag.getFirst(FieldKey.ALBUM)));
			infoArtist.setText( (info.artist = tag.getFirst(FieldKey.ARTIST)));
			infoTitle.setText(  (info.title  = tag.getFirst(FieldKey.TITLE)));
			
			String track      = tag.getFirst(FieldKey.TRACK);
			String trackTotal = tag.getFirst(FieldKey.TRACK_TOTAL);
			if (!trackTotal.equals("")){
				if (!track.equals("")){
					track +=  " of " + trackTotal;
				}else{
					track = "- of " + trackTotal;
				}
			}
			info.track = track;
			infoTrack.setText(track);
			cachedInfo.put(current.getUuid(), info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void addToList() {
		// FIXME addToList method

		int rst = chooser.showOpenDialog(this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File[] selected = chooser.getSelectedFiles();
			for (File file : selected) {
				try {
					String path = "music/" +file.getName();
					IOUtil.copyFile(file, Config.getResourceFile(path));
					MusicData md = new MusicData(path);
					resourceListModel.addElement(md);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this,  "Unable to add: " + file.getName());
				}
			}
		}
		
		int index = resourceListModel.size()-1;
		resourceList.setSelectedIndex(index);
	}


	private LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 4).grow(100, 1, 3).align("right", 3).gap("15", 1, 3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}
	
	@Override
	protected JComponent createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		p.add(new JLabel("Title:"), new CC().alignX("leading"));
		p.add((infoTitle = new JTextField(15)), "span, growx");
		infoTitle.setEditable(false);
		
		p.add(new JLabel("Artist:"), new CC().alignX("leading"));
		p.add((infoArtist = new JTextField(15)), "span, growx");
		infoArtist.setEditable(false);
		
		p.add(new JLabel("Album:"), new CC().alignX("leading"));
		p.add((infoAlbum = new JTextField(15)), "span, growx");
		infoAlbum.setEditable(false);
		
		p.add(new JLabel("Track#:"), new CC().alignX("leading"));
		p.add((infoTrack = new JTextField(15)), new CC().alignX("left").maxWidth("70"));
		infoTrack.setEditable(false);
		
		return p;
	}


	@Override
	protected MusicData defaultResource() {
		return new MusicData();
	}

	@Override
	protected Musics createAssetInstance() {
		return new Musics();
	}

	@Override
	protected String resourceDisplayName(MusicData resource, int index) {
		if (resource.getLocation() == null) return "none";
		
		//TODO change title
//		TrackInfo info =  cachedInfo.get(resource.getUuid());
//		if (info != null){
//			return info.title;
//		}else{
		return  IOUtil.removeExtension(Config.getResourceFile(resource.getLocation()).getName());
	}

	@Override
	protected String resourceName() {
		return "Music";
	}

	/**
	 * For storing cached info.
	 * @author Bilal Hussain
	 */
	class TrackInfo {
		String title;
		String album;
		String artist;
		String track;
	}
	
}
