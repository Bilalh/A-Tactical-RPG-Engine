package common;

import java.util.List;

import editor.spritesheet.ISpriteChangedListener;

/**
 * @author Bilal Hussain
 */
public class ListenerUtil {

	public static <E> void notifyListeners(List<? extends IListener<E>> listeners, E e){
		for (IListener<E> l : listeners) {
			l.notifyChanged(e);
		}
	}

	
}
