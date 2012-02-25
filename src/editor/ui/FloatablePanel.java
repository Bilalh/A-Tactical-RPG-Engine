/*
 *
 *  GNU public licence 2 + 
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 *
 *  Changes: added a float button, also made it more customisable 
 *
 */

package editor.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import editor.util.Prefs;
import editor.util.Resources;

/**
 * A floatable panel. The panel has a titlebar which displays the panel title plus a small button which can be used to turn the panel into a
 * frame. When the frame is closed the panel is restored.
 * 
 */
public class FloatablePanel extends JPanel
{
	private static final long serialVersionUID = 3365282132942748132L;
	private final JLabel titleLabel;
	private JDialog frame;
	private final JComponent child;
	private final Frame parent;
	private final Preferences prefs;
	private int dividerSize;

	private class FloatAction extends AbstractAction {
		private static final long serialVersionUID = 4069963919157697524L;

		public FloatAction() {
			putValue(SHORT_DESCRIPTION, "action.zoom.in.float");
			putValue(SMALL_ICON, Resources.getIcon("images/gnome-zoom-in.png"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setFloating(true);
		}
	}

	/**
	 * Constructs a floatable panel with the given title. When the panel is floated, it is placed in a {@link JDialog} with
	 * <code>parent</code> as its parent.
	 * 
	 * @param parent the parent of the child
	 * @param child the child component
	 * @param title the title of this panel
	 * @param preferencesId the unique identifier for this panel
	 */
	public FloatablePanel(Frame parent, JComponent child, String title, String preferencesId) {
		this.child = child;
		this.parent = parent;
		titleLabel = new JLabel(title);
		prefs = Prefs.getNode("map/dock/" + preferencesId);

		final JButton floatButton = new TButton(new FloatAction());

		JPanel topPanel = new HeaderPanel(new BorderLayout());
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(floatButton, BorderLayout.EAST);

		// Start in non-floating state
		// todo: Immediately restore floating state when found in preferences
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(child, BorderLayout.CENTER);
		dividerSize = 0;
		frame = null;
	}

	private void setFloating(boolean floating) {
		final JSplitPane splitPane = (JSplitPane) getParent();

		if (frame != null && !floating) {
			// Store the floating frame position and size
			prefs.putInt("width", frame.getWidth());
			prefs.putInt("height", frame.getHeight());
			prefs.putInt("x", frame.getX());
			prefs.putInt("y", frame.getY());

			// De-float the child
			frame.getContentPane().remove(child);
			frame.dispose();
			frame = null;
			child.setBorder(null);
			add(child, BorderLayout.CENTER);
			setVisible(true);

			// Restore the old divider location
			final int dividerLocation = prefs.getInt("dividerLocation", 0);
			splitPane.setDividerSize(dividerSize);
			splitPane.setDividerLocation(dividerLocation);
		}
		else if (frame == null && floating)
		{
			// Remember the current divider size
			dividerSize = splitPane.getDividerSize();

			// Store the divider location in the preferences
			prefs.putInt("dividerLocation", splitPane.getDividerLocation());

			// Hide this panel and remove our child panel
			setVisible(false);
			remove(child);
			splitPane.setDividerSize(0);

			child.setBorder(new EmptyBorder(5, 5, 5, 5));

			// Float the child
			frame = new JDialog(parent, titleLabel.getText());
			frame.getContentPane().add(child);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					setFloating(false);
				}
			});

			// Restore the floating frame position and size
			final int lastFrameWidth = prefs.getInt("width", 0);
			final int lastFrameHeight = prefs.getInt("height", 0);
			final int lastFrameX = prefs.getInt("x", 0);
			final int lastFrameY = prefs.getInt("y", 0);

			if (lastFrameWidth > 0) {
				frame.setSize(lastFrameWidth, lastFrameHeight);
				frame.setLocation(lastFrameX, lastFrameY);
			}
			else
			{
				frame.pack();
				frame.setLocationRelativeTo(parent);
			}
			frame.setVisible(true);
		}
	}

	/**
	 * Restore the state from the preferences.
	 */
	public void restore() {
		final int dividerLocation = prefs.getInt("dividerLocation", 0);
		final boolean floating = prefs.getBoolean("floating", false);

		if (floating) {
			setFloating(true);
		} else if (dividerLocation > 0) {
			final JSplitPane splitPane = (JSplitPane) getParent();
			splitPane.setDividerLocation(dividerLocation);
		}
	}

	/**
	 * Save the state to the preferences.
	 */
	public void save() {
		prefs.putBoolean("floating", frame != null);

		if (frame != null) {
			prefs.putInt("width", frame.getWidth());
			prefs.putInt("height", frame.getHeight());
			prefs.putInt("x", frame.getX());
			prefs.putInt("y", frame.getY());
		} else {
			final JSplitPane splitPane = (JSplitPane) getParent();
			prefs.putInt("dividerLocation", splitPane.getDividerLocation());
		}
	}

	/**
	 * Sets a new title for this panel.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		titleLabel.setText(title);

		if (frame != null) {
			frame.setTitle(title);
		}
	}

	/**
	 * The panel that holds the title label and float button.
	 */
	private class HeaderPanel extends JPanel {
		private static final long serialVersionUID = 8790419278839242794L;

		public HeaderPanel(BorderLayout borderLayout) {
			super(borderLayout);
			setBorder(BorderFactory.createEmptyBorder(1, 4, 2, 1));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Color backgroundColor = new Color(200, 200, 240);
			g.setColor(backgroundColor);
			((Graphics2D) g).fill(g.getClip());
			g.setColor(backgroundColor.darker());
			g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
		}
	}
}
