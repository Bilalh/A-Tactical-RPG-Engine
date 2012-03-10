package view.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import view.units.AnimatedUnit;
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

	private AnimatedUnit aunit;

	public void draw(Graphics2D g, int drawX, int drawY) {
		
		IMapUnit unit= aunit.getUnit();
		String[] arr = {
				unit.getName(),
				String.format("Lv %s Exp %s", unit.getLevel(),     unit.getExp()),
				String.format("HP %3d/%3d",   unit.getCurrentHp(), unit.getMaxHp()),
				
				String.format("STR %s", unit.getStrength()),
				String.format("DEF %s", unit.getDefence()),
				String.format("SPD %s", unit.getSpeed()),
		};
		
		RoundRectangle2D.Float area = new RoundRectangle2D.Float(drawX, drawY,
				120,
				10 + (arr.length+1)*20,
				10, 10);

		Color old = g.getColor();
		Composite oldC = g.getComposite();

		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g.setComposite(alphaComposite);
		g.setColor(Color.green);
		g.fill(area);

		g.setComposite(oldC);
		g.setColor(old);

		int sDrawY = yOffset;
		for (String s : arr) {
			g.drawString(s, drawX + xOffset, drawY + sDrawY);
			sDrawY +=20;
		}
		
		String wpn = unit.getWeapon().getDetails();
		g.drawString(wpn, drawX + xOffset+25, drawY + sDrawY);
		g.drawImage(aunit.getWeaponSprite(), drawX + xOffset, drawY + sDrawY - 15, null);
		
	}

	/** @category Generated */
	public void setUnit(AnimatedUnit aunit) {
		this.aunit = aunit;
	}

}
