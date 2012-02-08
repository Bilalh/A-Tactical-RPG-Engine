package view.map;

import java.awt.event.MouseEvent;

import view.map.GuiMap.ActionsEnum;
import view.util.MapActions;

class DialogHandler extends MapActions {

	public DialogHandler(GuiMap map) {
		super(map);
	}

	@Override
	public void keyComfirm() {
		nextPage();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		nextPage();
	}

	private void nextPage() {
		if (!this.map.dialog.nextPage()) {
			this.map.showDialog = false;
			map.setActionHandler(ActionsEnum.MOVEMENT);
		}
	}
	
}