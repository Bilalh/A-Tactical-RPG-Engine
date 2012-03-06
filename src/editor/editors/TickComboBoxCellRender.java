package editor.editors;

import java.awt.Component;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import editor.util.Resources;

/**
 * Shows a tick next to the selected item in a JComboBox.
 * @author Bilal Hussain
 */
public class TickComboBoxCellRender extends DefaultListCellRenderer {
	private static final long serialVersionUID = -1768674558123160963L;
	protected static Icon tick    =  Resources.getIcon("panels/icon-tick.png");
	protected static Icon nothing =  Resources.getIcon("panels/nothing.png");
	
	class SelctionChanged implements PopupMenuListener{
		
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			TickComboBoxCellRender.this.setTickIndex(comboBox.getSelectedIndex());
		}
		
		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			TickComboBoxCellRender.this.setTickIndex(-1);
		}
		
		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	private int oldIndex = -1;
	private JComboBox comboBox;

	public TickComboBoxCellRender(JComboBox comboBox) {
		this.comboBox = comboBox;
		comboBox.addPopupMenuListener(new SelctionChanged());
	}
	
	private void setTickIndex(int oldIndex) {
		this.oldIndex = oldIndex;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
		if (oldIndex == index){
			label.setIcon(tick);	
		}else{
			label.setIcon(nothing);
		}
		return label;
	}
	
}