/**
 * 
 */
package view;

import java.awt.Graphics;
import java.util.UUID;

import view.map.IsoTile;

import common.gui.Sprite;
import common.gui.ResourceManager;
import common.interfaces.IMapUnit;

/**
 * A units that look anumated e.g. has sprites with slight changes to make it look like it moving.
 * @author bilalh
 */
public class AnimatedUnit extends GuiUnit {

	/** The animation frames */
	private Sprite[] frames;
	/** The time since the last frame change took place */
	private long lastFrameChange;
	/** The current frame of animation being displayed */
	private int frameNumber;
	
	/** The frame duration in milliseconds, i.e. how long any given frame of animation lasts in nanoseconds */
	// 100 millisecond frames
	private long frameDuration = 100 *1000000; 
	
	/** @category Generated */
	public AnimatedUnit(int gridX, int gridY) {
		super(gridX, gridY);
	}

	public AnimatedUnit(int gridX, int gridY, String[] refs) {
		super(gridX,gridY);
//		frames = new Sprite[refs.length]; 
//		frames[0] = sprite;
//		for (int i = 1; i < refs.length; i++) {
//			frames[i] = ResourceManager.instance().getSpriteFromClassPath(refs[i]);
//		}
	}
	

	public void draw(Graphics g, final IsoTile[][] tiles, int x, int y, long timeDiff){
//		lastFrameChange += timeDiff;
//		
//		// if we need to change the frame, update the frame number and flip over the sprite in use
//		if (lastFrameChange > frameDuration) {
//			// reset our frame change time counter
//			lastFrameChange = 0;
//			
//			// update the frame
//			frameNumber++;
//			if (frameNumber >= frames.length) {
//				frameNumber = 0;
//			}
//			
//			sprite = frames[frameNumber];
//		}
//		
		draw(g, tiles,x, y);
	}
	
}
