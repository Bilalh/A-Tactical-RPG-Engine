package editor.spritesheet;

import java.awt.dnd.DragSourceDropEvent;

/**
 * 
 * @author Bilal Hussain
 */
public interface IDragFinishedListener{
	void dragDropEnd(DragSourceDropEvent dsde, int oldIndex, int newIndex);
}