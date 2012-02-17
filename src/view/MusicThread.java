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

	public synchronized void setMusic(Music newMusic) {
		assert newMusic != null;
		if (music != null) music.stop();
		music = newMusic;
		music.loop();
	}

	public synchronized void toggleMusic() {
		if (music == null){
			return;
		}
		if (musicPlaying) {
			music.pause();
		} else {
			music.resume();
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
//				System.out.println((int) ((temp - realOld) / 1000000));
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

}