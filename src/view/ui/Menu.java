package view.ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import view.ui.interfaces.IMenu;
import view.ui.interfaces.IMenuItem;

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
	private List<? extends IMenuItem> commands;

	private int width = 80;
	
	
	
	@Override
	public void draw(Graphics2D g, int drawX, int drawY) {
		g = (Graphics2D) g.create();

		area = new RoundRectangle2D.Float(drawX, drawY,
				width,
				23 * commands.size(),
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
		for (IMenuItem m : commands) {
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
	public IMenuItem getClickedItem(Point p) {
		if (area.contains(p)) {
			float index = p.y - area.y;
			selected = (int) (index / 25);
			return commands.get(selected);
		}
		return null;
	}

	@Override
	public void setCommands(List<? extends IMenuItem> commands) {
		this.commands = commands;
		reset();
	}

//	@Override
//	public void addCommand(MenuItem m) {
//		commands.add(m);
//	}
	
	@Override
	public void clearCommands() {
		commands.clear();
	}

	@Override
	public void reset() {
		selected = 0;
	}


	@Override
	public IMenuItem getSelected() {
		return commands.get(selected);
	}

	@Override
	public int getSelectedIndex() {
		return selected;
	}

	/** @category Generated */
	@Override
	public int getWidth() {
		return width;
	}
	
	/** @category Generated */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

}
