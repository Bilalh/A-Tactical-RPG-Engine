/**
 * 
 */
package gui;

import java.awt.Graphics;

import common.gui.Sprite;
import common.gui.SpriteManager;

/**
 * A units that look anumated e.g. has sprites with slight changes to make it look like it moving.
 * @author bilalh
 */
public class AnimatedUnit extends GuiUnit {

	/** The animation frames */
	private Sprite[] frames = new Sprite[4];
	/** The time since the last frame change took place */
	private long lastFrameChange;
	/** The current frame of animation being displayed */
	private int frameNumber;
	
	/** The frame duration in milliseconds, i.e. how long any given frame of animation lasts in nanoseconds */
	// 50 millisecond frames
	private long frameDuration = 100 *1000000; 
	
	public AnimatedUnit(String ref, int gridX, int gridY) {
		super(ref,gridX,gridY);
		
		// setup the animatin frames
		frames[0] = sprite;
		frames[1] = SpriteManager.instance().getSprite("assets/gui/alien2.gif");
		frames[2] = sprite;
		frames[3] = SpriteManager.instance().getSprite("assets/gui/alien3.gif");		
	}

	public void draw(Graphics g, final MapTile[][] tiles, int x, int y, long timeDiff){
		lastFrameChange += timeDiff;
		
		// if we need to change the frame, update the frame number and flip over the sprite in use
		if (lastFrameChange > frameDuration) {
			// reset our frame change time counter
			lastFrameChange = 0;
			
			// update the frame
			frameNumber++;
			if (frameNumber >= frames.length) {
				frameNumber = 0;
			}
			
			sprite = frames[frameNumber];
		}
		
		draw(g, tiles,x, y);
	}

	@Override
	public String toString() {
		return String.format("AnimatedUnit [gridX=%s, gridY=%s, sprite=%s]", gridX, gridY, sprite);
	}
	
	
	
}
