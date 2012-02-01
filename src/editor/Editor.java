package editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


/**
 * @author Bilal Hussain
 */
public class Editor implements ActionListener {

	private JFrame frame;

	private AbstractButton paintButton, eraseButton, pourButton;
	private AbstractButton eyedButton, marqueeButton, moveButton;

	private final Action zoomInAction, zoomOutAction, zoomNormalAction;

	private static final String TOOL_PAINT = "paint";
	private static final String TOOL_ERASE = "erase";
	private static final String TOOL_FILL = "fill";
	private static final String TOOL_EYE_DROPPER = "eyedropper";
	private static final String TOOL_SELECT = "select";
	private static final String TOOL_MOVE_LAYER = "movelayer";

	public Editor() {
		frame = new JFrame();

        zoomInAction     = new ZoomInAction();
        zoomOutAction    = new ZoomOutAction();
        zoomNormalAction = new ZoomNormalAction();
        
		frame.setContentPane(createContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setSize(1280, 800);
		frame.setSize(1440, 900);
		frame.setVisible(true);
	}

	private JPanel createContentPane() {
		JPanel main = new JPanel(new BorderLayout());
		main.add(createToolBar(), BorderLayout.WEST);

		return main;
	}

	private JToolBar createToolBar() {
		ImageIcon iconMove = Resources.getIcon("gimp-tool-move-22.png");
		ImageIcon iconPaint = Resources.getIcon("gimp-tool-pencil-22.png");
		ImageIcon iconErase = Resources.getIcon("gimp-tool-eraser-22.png");
		ImageIcon iconPour = Resources.getIcon("gimp-tool-bucket-fill-22.png");
		ImageIcon iconEyed = Resources.getIcon("gimp-tool-color-picker-22.png");
		ImageIcon iconMarquee = Resources.getIcon("gimp-tool-rect-select-22.png");

		paintButton = createToggleButton(iconPaint, "paint", TOOL_PAINT);
		eraseButton = createToggleButton(iconErase, "erase", TOOL_ERASE);
		pourButton = createToggleButton(iconPour, "pour", TOOL_FILL);
		eyedButton = createToggleButton(iconEyed, "eyed", TOOL_EYE_DROPPER);
		marqueeButton = createToggleButton(iconMarquee, "marquee", TOOL_SELECT);
		moveButton = createToggleButton(iconMove, "move", TOOL_MOVE_LAYER);

		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		toolBar.setFloatable(true);
		toolBar.add(moveButton);
		toolBar.add(paintButton);
		toolBar.add(eraseButton);
		toolBar.add(pourButton);
		toolBar.add(eyedButton);
		toolBar.add(marqueeButton);
		toolBar.add(Box.createRigidArea(new Dimension(0, 5)));
		toolBar.add(new TButton(new ZoomInAction()));
		toolBar.add(new TButton(zoomOutAction));
		toolBar.add(Box.createRigidArea(new Dimension(5, 5)));
		toolBar.add(Box.createGlue());

		return toolBar;
	}

	private AbstractButton createToggleButton(Icon icon, String command, String tooltip) {
		JToggleButton btn = new JToggleButton("", icon);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setActionCommand(command);
		btn.addActionListener(this);
		if (tooltip != null) {
			btn.setToolTipText(tooltip);
		}
		return btn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO actionPerformed method
	}

	private class ZoomInAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public ZoomInAction() {
			putValue(SHORT_DESCRIPTION, "action.zoom.in.tooltip");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
            putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-in.png"));
        }
        public void actionPerformed(ActionEvent e) {
        }
	}
	
	private class ZoomOutAction extends AbstractAction {
		private static final long serialVersionUID = 1630640363711233878L;

		public ZoomOutAction() {
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control MINUS"));
			putValue(SHORT_DESCRIPTION, "action.zoom.out.tooltip");
			putValue(LARGE_ICON_KEY, Resources.getIcon("gnome-zoom-out.png"));
			putValue(SMALL_ICON, Resources.getIcon("gnome-zoom-out.png"));
		}
		@Override
		public void actionPerformed(ActionEvent evt) {
			
		}
	}

	private class ZoomNormalAction extends AbstractAction {
		private static final long serialVersionUID = -4301272641043394395L;

		public ZoomNormalAction() {
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control 0"));
			putValue(SHORT_DESCRIPTION,"action.zoom.normal.tooltip");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new Editor();
	}

}
