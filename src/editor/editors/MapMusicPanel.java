package editor.editors;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import util.openal.Music;

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
	
	protected JComboBox attackSound;
	protected JComboBox defeatUnitSound;
	protected JComboBox loseUnitSound;
	protected JComboBox winMapSound;
	protected JComboBox loseMapSound;
	
	protected JComboBox[] soundsArray;
	
	protected MapMusic  mapMusic;
	
	public MapMusicPanel(){
		createMainPane();
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
		
		Musics sounds = editor.getSounds();
		
		ItemListener[] arr = new ItemListener[soundsArray.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = soundsArray[i].getItemListeners()[0];
			soundsArray[i].removeItemListener(arr[i]);
			soundsArray[i].removeAllItems();
		}
		
		for (MusicData sound : sounds) {
			for (JComboBox cb : soundsArray) {
				cb.addItem(sound);
			}
		}
		
		for (int i = 0; i < arr.length; i++) {
			soundsArray[i].addItemListener(arr[i]);
			soundsArray[i].setSelectedIndex(0);
		}
	}

	public void setMapMusic(Musics music, Musics sounds, MapMusic data){
		mapMusic = data;
		ItemListener lsl  = backgroundMusic.getItemListeners()[0];
		backgroundMusic.removeItemListener(lsl);
		backgroundMusic.setSelectedItem(music.get(data.getBackgroundId()));
		backgroundMusic.addItemListener(lsl);

		ItemListener[] arr = new ItemListener[soundsArray.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = soundsArray[i].getItemListeners()[0];
			soundsArray[i].removeItemListener(arr[i]);
		}

		attackSound.setSelectedItem(sounds.get(data.getAttackSound()));
		defeatUnitSound.setSelectedItem(sounds.get(data.getDefeatUnitSound()));
		loseUnitSound.setSelectedItem(sounds.get(data.getLoseUnitSound()));
		winMapSound.setSelectedItem(sounds.get(data.getWinMapSound()));
		loseMapSound.setSelectedItem(sounds.get(data.getLoseMapSound()));

		
		for (int i = 0; i < arr.length; i++) {
			soundsArray[i].addItemListener(arr[i]);
		}
		
		
	}
	
	protected void createMainPane() {
		this.setLayout(AbstractResourcesPanel.defaultInfoPanelLayout());
		
		this.add(new JLabel("Backgrounds Music:"));
		backgroundMusic = new JComboBox(new MusicData[]{});
		backgroundMusic.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) backgroundMusic.getSelectedItem();
				mapMusic.setBackground(m.getUuid());
			}
		});
		this.add(backgroundMusic, "span, growx");
		
		this.add(new JLabel("Attack Sound:"));
		attackSound = new JComboBox(new MusicData[]{});
		attackSound.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) attackSound.getSelectedItem();
				mapMusic.setAttackSound(m.getUuid());
			}
		});
		this.add(attackSound, "span, growx");
		
		this.add(new JLabel("Defeat Enemy Unit Sound"));
		defeatUnitSound = new JComboBox(new MusicData[]{});
		defeatUnitSound.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) defeatUnitSound.getSelectedItem();
				mapMusic.setDefeatUnitSound(m.getUuid());
			}
		});
		this.add(defeatUnitSound, "span, growx");
		
		this.add(new JLabel("Lose Unit Sound"));
		loseUnitSound = new JComboBox(new MusicData[]{});
		loseUnitSound.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) loseUnitSound.getSelectedItem();
				mapMusic.setLoseUnitSound(m.getUuid());
			}
		});
		this.add(loseUnitSound, "span, growx");
		
		this.add(new JLabel("Win Map Sound"));
		winMapSound = new JComboBox(new MusicData[]{});
		winMapSound.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) winMapSound.getSelectedItem();
				mapMusic.setWinMapSound(m.getUuid());
			}
		});
		this.add(winMapSound, "span, growx");
		
		
		this.add(new JLabel("Lose Map Sound"));
		loseMapSound = new JComboBox(new MusicData[]{});
		loseMapSound.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				MusicData m =  (MusicData) loseMapSound.getSelectedItem();
				mapMusic.setLoseMapSound(m.getUuid());
			}
		});
		this.add(loseMapSound, "span, growx");
	
		JComboBox[] soundsArray = {
				attackSound,
				defeatUnitSound,
				loseUnitSound,
				winMapSound,
				loseMapSound
		};
		this.soundsArray = soundsArray;
	}
	
}
