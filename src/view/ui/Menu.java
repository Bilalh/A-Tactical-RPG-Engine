package view.ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A menu has a number of items that can be chosen either using the keyboard or the mouse.
 * 
 * @author Bilal Hussain
 */
public class Menu implements IMenu {

	private int xOffset = 25;
	private int yOffset = 20;

	private int selected = 0;

	private RoundRectangle2D.Float area = new RoundRectangle2D.Float();
	private List<MenuItem> commands;

	@Override
	public void draw(Graphics2D g, int drawX, int drawY) {
		g = (Graphics2D) g.create();

		area = new RoundRectangle2D.Float(drawX, drawY,
				70,
				25 * commands.size(),
				10, 10);

		Composite oldC = g.getComposite();

		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		Color oldCo = g.getColor();
		g.setComposite(alphaComposite);
		g.setColor(Color.green);
		g.fill(area);

		g.setComposite(oldC);
		g.setColor(oldCo);

		g.translate(drawX + xOffset, drawY + yOffset);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (MenuItem m : commands) {
			m.draw(g, m == commands.get(selected));
			g.translate(0, 20);
		}

	}

	@Override
	public void scrollToPrevious() {
		selected = (selected - 1 + commands.size()) % commands.size();
	}

	@Override
	public void scrollToNext() {
		selected = (selected + 1) % commands.size();
	}

	@Override
	public MenuItem getClickedItem(Point p) {
		if (area.contains(p)) {
			float index = p.y - area.y;
			selected = (int) (index / 25);
			return commands.get(selected);
		}
		return null;
	}

	@Override
	public void addCommand(MenuItem m) {
		commands.add(m);
	}

	@Override
	public void setCommands(List<MenuItem> commands) {
		this.commands = commands;
		reset();
	}

	@Override
	public void reset() {
		selected = 0;
	}

	@Override
	public void clear() {
		commands.clear();
	}

	@Override
	public MenuItem getSelected() {
		return commands.get(selected);
	}

}
