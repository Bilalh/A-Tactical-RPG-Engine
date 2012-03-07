package notifications.map;

import view.map.GuiMap;
import common.interfaces.IMapNotification;
import config.assets.DialogPart;
import config.assets.DialogParts;

/**
 * @author Bilal Hussain
 */
public class DialogNotification implements IMapNotification {

	public DialogParts dialog;

	public DialogNotification(DialogParts dialog) {
		this.dialog = dialog;
	}

	@Override
	public void process(GuiMap map) {
		map.displayDialog(dialog);
	}

	@Override
	public String readableInfo() {
		return null;
	}
	
	
	
}
