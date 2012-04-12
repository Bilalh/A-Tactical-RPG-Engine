package editor.map.others;

import java.awt.image.BufferedImage;

import common.interfaces.IUnit;
import common.spritesheet.SpriteSheet;

import view.units.AnimatedUnit;

/**
 * AnimatedUnit that always uses the same image 
 * @author Bilal Hussain
 */
public class OthersUnit extends AnimatedUnit  {

	protected static SpriteSheet spriteSheet; 


	public OthersUnit(int gridX, int gridY, IUnit u) {
		super(gridX, gridY, u);
	}

	@Override
	protected SpriteSheet makeSpriteSheet() {
		assert spriteSheet != null;
		return spriteSheet;
	}


	public static void setSpriteSheet(SpriteSheet spriteSheet) {
		OthersUnit.spriteSheet = spriteSheet;
	}

	
	
}
