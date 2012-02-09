package notifications;

import java.util.*;

import view.map.GuiMap;
import common.LocationInfo;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;


/**
 * @author Bilal Hussain
 */
public class UserMovedNotification  implements IMapNotification {
	
	final private IMapUnit u;
	final private Collection<LocationInfo> path;

	/** @category Generated Constructor */
	public UserMovedNotification(IMapUnit u, Collection<LocationInfo> path) {
		this.u = u;
		this.path = path;
	}

	@Override
	public void process(GuiMap map) {
		map.unitMoved(u,path);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format("UserMovedNotification [u=%s, path=%s]", u, path != null ? toString(path, maxLen) : null);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}


	
	
}
