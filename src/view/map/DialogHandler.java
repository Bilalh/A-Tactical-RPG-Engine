package view.map;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.List;

import com.sun.tools.javac.code.Attribute.Array;

import common.assets.DialogPart;
import common.assets.DialogParts;

import view.map.GuiMap.ActionsEnum;
import view.ui.GuiDialog;
import view.util.MapActions;

class DialogHandler extends MapActions {

	private GuiDialog dialog;
	private ActionsEnum nextAction = ActionsEnum.MOVEMENT;

	private ArrayDeque<DialogPart> queue = new ArrayDeque<DialogPart>();
	
	public DialogHandler(GuiMap map, GuiDialog dialog) {
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
			if (queue.isEmpty()){
				map.dialogFinished(nextAction);
			}else{
				DialogPart current =queue.pop(); 
				dialog.setData(current.getText(), map.getUnit(current.getUnitId()));
			}
		}
	}

	/** @category Generated */
	public GuiDialog getDialog() {
		return dialog;
	}

	/** @category Generated */
	public void setNextAction(ActionsEnum nextAction) {
		this.nextAction = nextAction;
	}

	public void setDialog(List<DialogPart> sortedValues) {
		queue.clear();
		queue.addAll(sortedValues);
		 DialogPart current = queue.pollFirst();
		if (current == null){
			map.dialogFinished(nextAction);
		}else{
			dialog.setData(current.getText(), map.getUnit(current.getUnitId()));
			map.setActionHandler(ActionsEnum.DIALOG);
		}
	}
	
}