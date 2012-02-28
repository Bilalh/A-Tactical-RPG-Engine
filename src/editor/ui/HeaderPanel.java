package editor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel {
	private static final long serialVersionUID = 8790419278839242794L;

	public HeaderPanel(BorderLayout borderLayout) {
		super(borderLayout);
		setBorder(BorderFactory.createEmptyBorder(1, 4, 2, 1));
	}

	@Override
	protected void paintComponent(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		Color backgroundColor = new Color(200, 200, 240);
		g.setColor(backgroundColor);
		g.fill(g.getClip());
		g.setColor(backgroundColor.darker());
		g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
	}
}