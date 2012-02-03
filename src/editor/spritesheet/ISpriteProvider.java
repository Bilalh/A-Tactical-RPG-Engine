package editor.spritesheet;

import common.spritesheet.SpriteInfo;

/**
 * @author Bilal Hussain
 */
public interface ISpriteProvider<E extends SpriteInfo> {

	/**
	 * Tells the SpriteProvider, that the specifed sprite have been selected
	 */
	void select(java.util.List<E> selection);

	/**
	 * Get the sprite at the specifed point if there one
	 */
	E getSpriteAt(int x, int y);

	/**
	 * Deletes the specifed Sprite(s)
	 */
	void delete(java.util.List<E> selected);

}