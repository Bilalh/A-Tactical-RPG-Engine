package editor.editors;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
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


import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import com.javarichclient.icon.tango.actions.ListAllIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStartIcon;
import com.javarichclient.icon.tango.actions.MediaPlaybackStopIcon;

import util.IOUtil;
import util.openal.Music;
import util.openal.SlickException;
import view.MusicThread;


import config.Config;
import config.assets.MusicData;
import config.assets.Musics;
import editor.Editor;
import editor.ui.TButton;

/**
 * Handles music
 * @author Bilal Hussain
 */
public class MusicPanel extends AbstractResourcesPanel<MusicData, Musics> {
	private static final Logger log = Logger.getLogger(MusicPanel.class);
	private static final long serialVersionUID = -8134784428673033659L;

	protected MusicData current;
	protected JFileChooser chooser;

	protected JTextField infoTitle;
	protected JTextField infoTrack;
	protected JTextField infoAlbum;
	protected JTextField infoArtist;
	
	protected HashMap<UUID, TrackInfo> cachedInfo = new HashMap<UUID, MusicPanel.TrackInfo>();
	
	protected static MusicThread music = new MusicThread();
	static{
		music.start();
	}
	
	public MusicPanel(Editor editor) {
		super(editor);
		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Ogg Audio (*.ogg)", "ogg"));
		chooser.setMultiSelectionEnabled(true);
		cachedInfo = new HashMap<UUID, TrackInfo>();
	}

	@Override
	public void panelSelected(Editor editor) {
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
			infoAlbum.setText(  info.album  = tag.getFirst(FieldKey.ALBUM));
			infoArtist.setText( info.artist = tag.getFirst(FieldKey.ARTIST));
			infoTitle.setText(  info.title  = tag.getFirst(FieldKey.TITLE));
			
			String track      = tag.getFirst(FieldKey.TRACK);
			
			
			String trackTotal = "";
			try {
				trackTotal = tag.getFirst(FieldKey.TRACK_TOTAL);
			} catch (java.lang.UnsupportedOperationException e) {
				log.debug(f.getFile().getName() + " " + e.getMessage());
				trackTotal = "";
			}
			
			f.getFile().getName();
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

		int rst = chooser.showOpenDialog(this);
		if (rst == JFileChooser.APPROVE_OPTION) {
			File[] selected = chooser.getSelectedFiles();
			for (File file : selected) {
				try {
					String path = storePath() +file.getName();
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

	protected String storePath(){
		return "music/";
	}

	private LayoutManager createLayout() {
		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(1, 4).grow(100, 1, 3).align("right", 3).gap("15", 1, 3);
		AC rowC = new AC().align("top", 10).gap("15!", 10).grow(100, 10);
		return new MigLayout(layC, colC, rowC);
	}
	
	JButton infoPlay, infoStop;
	
	@Override
	protected JComponent createInfoPanel() {
		JPanel p = new JPanel(createLayout());
		
		p.add(new JLabel("Title:"), new CC().alignX("leading"));
		p.add(infoTitle = new JTextField(15), "span, growx");
		infoTitle.setEditable(false);
		
		p.add(new JLabel("Artist:"), new CC().alignX("leading"));
		p.add(infoArtist = new JTextField(15), "span, growx");
		infoArtist.setEditable(false);
		
		p.add(new JLabel("Album:"), new CC().alignX("leading"));
		p.add(infoAlbum = new JTextField(15), "span, growx");
		infoAlbum.setEditable(false);
		
		p.add(new JLabel("Track#:"), new CC().alignX("leading"));
		p.add(infoTrack = new JTextField(15), new CC().alignX("left").maxWidth("70").wrap());
		infoTrack.setEditable(false);
	
		JPanel buttonPanel = new JPanel(new MigLayout("", "[center, grow]"));
		buttonPanel.add(infoPlay = new JButton(new PlayAction()));
		buttonPanel.add(infoStop = new JButton(new StopAction()));
		p.add(buttonPanel, "span");
		
		return p;
	}

	private String old = "";
	protected class PlayAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public PlayAction() {
			putValue(NAME, "Play");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new MediaPlaybackStartIcon(16, 16));
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			if (current == null || old.equals(current.getLocation()) ) return;
			System.out.println("Playing " + current.getLocation());
			if (play()) old = current.getLocation();
		}
	}	

	protected boolean play(){
		try {
			music.replaceMusic(new Music(current.getLocation(), true));
		} catch (IOException e) {
			// FIXME catch block in play
			e.printStackTrace();
		}
		music.pause();
		music.toggleMusic();	
		return true;
	}
	
	protected class StopAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public StopAction() {
			putValue(NAME, "Stop");
			// putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
			putValue(SMALL_ICON, new MediaPlaybackStopIcon(16, 16));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			music.removeMusic();
			old ="";
		}
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
	protected class TrackInfo {
		String title;
		String album;
		String artist;
		String track;
	}
	
}
