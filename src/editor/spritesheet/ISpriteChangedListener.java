package editor.spritesheet;

import common.gui.Sprite;

/**
 * @author Bilal Hussain
 */
public interface ISpriteChangedListener {

	void notifyChanged(MutableSprite newValue);
	
}
