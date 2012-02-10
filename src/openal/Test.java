package openal;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import common.gui.ResourceManager;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import openal.*;

/**
 * @author bilalh
 */
public class Test {

	/** The sound to be played */
	private Sound sound;
	/** The sound to be played */
	private Sound charlie;
	/** The sound to be played */
	private Sound burp;
	/** The music to be played */
	private Music music;
	/** first music that can be played */
	private Music musica;
	/** second music that can be played */
	private Music musicb;
	/** The sound to be played */
	private Audio engine;
	/** The Volume of the playing music */
	private int volume = 10;

	/** The IDs of the sources used for each engine noise */
	private int[] engines = new int[3];

	public Test() throws SlickException, FileNotFoundException, JavaLayerException {
		SoundStore.get().setMaxSources(32);

		sound = new Sound("restart.ogg");
		charlie = new Sound("cbrown01.wav");
		try {
			engine = AudioLoader.getAudio("WAV",
					ResourceManager.instance().getResourceAsStream("engine.wav"));
		} catch (IOException e) {
			throw new SlickException("Failed to load engine", e);
		}
		music = musica = new Music("theme.ogg", true);
		musicb = new Music("kirby.ogg", true);
		burp = new Sound("burp.aif");

		// FileInputStream fis = new FileInputStream("08 The Legend of Zelda - Treasure Catch Fanfare.mp3");
		// BufferedInputStream bis = new BufferedInputStream(fis);
		// player = new Player(bis);
	}

    private Player player; 


	public void playPause(Music m){
		if (m.playing()) m.pause();
		else m.play();
	}
    
	public static void main(String[] args) throws SlickException, InterruptedException, FileNotFoundException, JavaLayerException {
		final Test t = new Test();
		t.music.loop();
		long old = System.nanoTime();
		while (true) {
//			t.player.play(10);
			// t.sound.playAt(-1, 0, 0);
			long now = System.nanoTime() - old;
			Music.poll((int) (now/1000000));
			Thread.sleep(200);
				if (now % 650 <= 10){
					t.sound.play();	
					// Play different Pice of music
//					t.music.stop();
//					t.playPause(t.music);
//					t.music = t.musicb;
//					t.music.loop();
				}
				
		}
	}
}
