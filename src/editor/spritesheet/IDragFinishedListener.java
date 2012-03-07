package editor.spritesheet;

import java.awt.dnd.DragSourceDropEvent;

/**
 *  Tells the Listener that the object has moved from oldIndex to newIndex in the list
 * @author Bilal Hussain
 */
public interface IDragFinishedListener{
	void dragDropEnd(DragSourceDropEvent dsde, int oldIndex, int newIndex);
}