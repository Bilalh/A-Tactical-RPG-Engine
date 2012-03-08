package editor.editors;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import openal.Music;

import config.assets.MusicData;
import config.assets.Musics;
import config.xml.MapMusic;
import editor.Editor;

import net.miginfocom.swing.MigLayout;

/**
 * @author Bilal Hussain
 */
public class MapMusicPanel extends JPanel implements IRefreshable {
	private static final long serialVersionUID = -9163168786596236956L;

	protected JComboBox backgroundMusic;
	protected MapMusic  current;
	
	public MapMusicPanel(){
		createMainPane();
	}

	protected void createMainPane() {
		this.setLayout(AbstractResourcesPanel.defaultInfoPanelLayout());
		
		this.add(new JLabel("Backgrounds Music:"));
		
		backgroundMusic = new JComboBox(new MusicData[]{});
		backgroundMusic.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) backgroundMusic.getSelectedItem();
				current.setBackgroundId(m.getUuid());
			}
		});
		
		this.add(backgroundMusic, "span, growx");
		
	}

	public void setMapMusic(Musics all, MapMusic data){
		current = data;
		ItemListener lsl  = backgroundMusic.getItemListeners()[0];
		backgroundMusic.removeItemListener(lsl);
		backgroundMusic.setSelectedItem(all.get(data.getBackgroundId()));
		backgroundMusic.addItemListener(lsl);
	}
	
	@Override
	public void panelSelected(Editor editor) {
		Musics assets = editor.getMusic();
		
		ItemListener lsl  = backgroundMusic.getItemListeners()[0];
		backgroundMusic.removeItemListener(lsl);
		backgroundMusic.removeAllItems();
		
		for (MusicData m : assets) {
			backgroundMusic.addItem(m);
		}
		
		backgroundMusic.addItemListener(lsl);
		backgroundMusic.setSelectedIndex(0);
	}
	
}
