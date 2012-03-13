/**
 * 
 */
package view.units;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import view.map.IsoTile;

import common.enums.Direction;
import common.interfaces.IUnit;

import engine.unit.UnitAnimation;

/**
 * A units that look anumated e.g. has sprites with slight changes to make it look like it moving.
 * @author bilalh
 */
public class AnimatedUnit extends GuiUnit {
	private static final Logger log = Logger.getLogger(AnimatedUnit.class);
	
	/** The animation frames */
	private BufferedImage[] frames;
	/** The time since the last frame change took place */
	private long lastFrameChange;
	/** The current frame of animation being displayed */
	private int frameNumber;
	
	/** The frame duration in milliseconds, i.e. how long any given frame of animation lasts in nanoseconds */
	// 100 millisecond frames
	private long frameDuration = 100 *1000000; 
	
	/** @category Generated */
	public AnimatedUnit(int gridX, int gridY, IUnit u) {
		super(gridX, gridY,u);
	}

	public void draw(Graphics g, final IsoTile[][] tiles, int x, int y, long timeDiff){
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
		
		super.draw(g, tiles,x, y);
	}

	@Override
	public synchronized void setDirection(Direction direction) {
		assert images !=null;
		this.direction = direction;
		if(!images.containsKey(direction.reference())){
			super.setDirection(direction);
			frames    = new BufferedImage[1];
			frames[0] = sprite;
			return;
		}
		UnitAnimation id = images.get(direction.reference());
		frames = new BufferedImage[id.getNumberOfFrames()];
		for (int i = 0; i < frames.length; i++) {
			frames[i] =spriteSheet.getSpriteImage(direction.reference()+i);
		}
		sprite = frames[0];
	}

	public synchronized void inverseDirection() {
		this.setDirection(direction.inverse());
	}
	
	
	@Override
	public String toString() {
		return String.format("AnimatedUnit [frames=%s name=%s L=%s Dir=%s ModelL=%s]", frames.length,  unit == null ? "" : unit.getName(), getLocation(),direction, unit == null ? "" : unit.getLocation());
	}

	
	
}
