package notifications.map;

import view.map.GuiMap;
import common.assets.DialogPart;
import common.assets.DialogParts;
import common.interfaces.IMapNotification;

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
