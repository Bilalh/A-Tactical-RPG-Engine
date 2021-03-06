package view;

import java.io.IOException;
import java.util.UUID;

import config.assets.AssetStore;

import util.openal.Music;
import util.openal.Sound;

/**
 * Plays music
 * 
 * @author Bilal Hussain
 */
public class MusicThread extends Thread {

	private Music music;
	private boolean running = true;
	private boolean musicPlaying = false;

	/**
	 *  Replaces the current music, then plays it 
	 */
	public synchronized void replaceMusic(Music newMusic) {
		assert newMusic != null;
		if (music != null) music.stop();
		music = newMusic;
//		music.setVolume(0.5f);
		music.loop();
	}

	/**
	 * Toggle the playing state.
	 */
	public synchronized void toggleMusic() {
		if (music == null) {
			return;
		}
		if (musicPlaying) {
			music.pause();
		} else {
//			music.setVolume(0.5f);
			music.resume();
		}
		musicPlaying = !musicPlaying;
	}

	/**
	 * Pause the music.
	 */
	public synchronized void pause() {
		music.pause();
		musicPlaying = false;
	}

	public synchronized void removeMusic(){
		if (music == null) return;
		music.stop();
		music = null;
	}
	
	@Override
	public void run() {
		super.run();
		long realOld = System.nanoTime();
		while (running) {
			long temp = System.nanoTime();
			if (musicPlaying) {
				// System.out.println((int) ((temp - realOld) / 1000000));
				Music.poll((int) ((temp - realOld) / 1000000));
			}
			realOld = temp;

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void kill() {
		running = false;
	}

	public void playSound(String ref) {
		if (!musicPlaying) return;
		try {
			new Sound(ref).play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playSound(UUID uuid) {
		playSound(AssetStore.instance().getSound(uuid).getLocation());
	}

	public Sound getSound(UUID uuid){
		try {
			return new Sound(AssetStore.instance().getSound(uuid).getLocation());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void playSound(Sound s) {
		if (!musicPlaying) return;
		s.play();
	}
	
	// for editor
	public void forcePlaySound(String ref) {
		try {
			new Sound(ref).play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}