package common;

import java.util.List;

import editor.spritesheet.ISpriteChangedListener;

/**
 * Convenient methods for IListeners
 * @author Bilal Hussain
 */
public class ListenerUtil {

	/**
	 * Send the message e to specifed listeners 
	 */
	public static <E> void notifyListeners(List<? extends IListener<E>> listeners, E e){
		for (IListener<E> l : listeners) {
			l.notifyChanged(e);
		}
	}

}
