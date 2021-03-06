package view.map;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.tools.javac.code.Attribute.Array;

import config.assets.DialogPart;
import config.assets.DialogParts;

import util.Logf;
import view.map.GuiMap.ActionsEnum;
import view.ui.GuiDialog;
import view.util.MapActions;

/**
 * Handles all operations on the dialog inculdes, drawing and input handing.
 * @author Bilal Hussain
 */
class DialogHandler extends MapActions {
	private static final Logger log = Logger.getLogger(DialogHandler.class);
	
	private GuiDialog dialog;
	private ActionsEnum nextAction = ActionsEnum.MOVEMENT;

	private ArrayDeque<DialogPart> queue = new ArrayDeque<DialogPart>();
	
	public DialogHandler(GuiMap map, GuiDialog dialog) {
		super(map);
		this.dialog = dialog;
	}

	@Override
	public synchronized void draw(Graphics2D g, int width, int height) {
		assert dialog != null;
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


	// Press keyOther2 ('s') to skip the dialog`
	@Override
	public void keyOther2() {
		map.dialogFinished(nextAction);
	}
	
	private void nextPage() {
		if (!dialog.nextPage()) {
			if (queue.isEmpty()){
				map.dialogFinished(nextAction);
			}else{
				DialogPart current =queue.pop(); 
				Logf.debug(log,"Showing dialog for %s ", map.getUnit(current.getUnitId()));
				dialog.setData(current.getText(), map.getUnitInculdingDied(current.getUnitId()));
			}
		}
	}


	public GuiDialog getDialog() {
		return dialog;
	}


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
			Logf.debug(log,"Showing dialog for %s ", map.getUnit(current.getUnitId()));

			dialog.setData(current.getText(), map.getUnitInculdingDied(current.getUnitId()));
			map.setActionHandler(ActionsEnum.DIALOG);
		}
	}
	
}