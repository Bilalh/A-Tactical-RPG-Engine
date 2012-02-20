package view.map;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import view.map.GuiMap.ActionsEnum;
import view.ui.Dialog;
import view.util.MapActions;

class DialogHandler extends MapActions {

	private Dialog dialog;
	private ActionsEnum nextAction = ActionsEnum.MOVEMENT;

	public DialogHandler(GuiMap map, Dialog dialog) {
		super(map);
		this.dialog = dialog;
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		dialog.draw(g, 5, height - dialog.getHeight() - 5);
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
		if (!dialog.nextPage()) {
			map.setActionHandler(nextAction);
		}
	}

	/** @category Generated */
	public Dialog getDialog() {
		return dialog;
	}

	/** @category Generated */
	public void setNextAction(ActionsEnum nextAction) {
		this.nextAction = nextAction;
	}
}