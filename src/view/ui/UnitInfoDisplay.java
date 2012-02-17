package view.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import view.units.GuiUnit;

import common.interfaces.IMapUnit;

/**
 * Display a units status
 * 
 * @author Bilal Hussain
 */
public class UnitInfoDisplay {

	private int xOffset = 5;
	private int yOffset = 20;

	private IMapUnit unit;

	public void draw(Graphics2D g, int drawX, int drawY) {
		RoundRectangle2D.Float area = new RoundRectangle2D.Float(drawX, drawY,
				120,
				90,
				10, 10);

		Color old = g.getColor();
		Composite oldC = g.getComposite();

		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g.setComposite(alphaComposite);
		g.setColor(Color.green);
		g.fill(area);

		g.setComposite(oldC);
		g.setColor(old);

		// Draw the unts info
		g.drawString(unit.getName(), drawX + xOffset, drawY + yOffset);
		String hp = String.format("HP %3d/%3d", unit.getCurrentHp(), unit.getMaxHp());
		g.drawString(hp, drawX + xOffset, drawY + yOffset + 20);

	}

	/** @category Generated */
	public void setUnit(IMapUnit unit) {
		this.unit = unit;
	}

}
