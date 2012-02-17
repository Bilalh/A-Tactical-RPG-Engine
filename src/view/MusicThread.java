package view;

import openal.Music;

/**
 * Plays the music
 * @author Bilal Hussain
 */
public class MusicThread extends Thread {

	private Music music;
	private boolean running = true;
	private boolean musicPlaying = false;

	// public MusicThread() {
	// try {
	// music = new Music("music/1-19 Fight It Out!.ogg", true);
	// } catch (SlickException e) {
	// e.printStackTrace();
	// }
	// }

	public synchronized void setMusic(Music newMusic) {
		assert newMusic != null;
		if (music != null) music.stop();
		music = newMusic;
		toggleMusic();
	}

	public synchronized void toggleMusic() {
		if (music == null){
			return;
		}
		if (musicPlaying) {
			music.stop();
		} else {
			music.loop();
		}
		musicPlaying = !musicPlaying;
	}

	@Override
	public void run() {
		super.run();
		long realOld = System.nanoTime();

		while (running) {
			long temp = System.nanoTime();
			if (musicPlaying) {
				Music.poll((int) ((temp - realOld) / 1000000));
			}
			realOld = temp;

			Thread.yield();
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

}