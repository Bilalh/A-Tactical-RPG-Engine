package view.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import view.ui.interfaces.IDisplayable;
import view.units.AnimatedUnit;
import view.units.GuiUnit;

import common.interfaces.IMapUnit;

/**
 * Display a units status
 * 
 * @author Bilal Hussain
 */
public class UnitInfoDisplay implements IDisplayable {

	private int xOffset = 5;
	private int yOffset = 20;

	private AnimatedUnit aunit;
	private AnimatedUnit current;

	@Override
	public void draw(Graphics2D g, int drawX, int drawY) {
		
		IMapUnit unit= aunit.getUnit();
		
		Color colour = unit.isAI() ? Color.RED : Color.GREEN;
		float alpha = 0.65f;
		String extra = "";
		if (current == aunit){
			extra = " (Current)";
			alpha = 0.8f;
		}
		
		String[] arr = {
				unit.getName() + extra,
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

		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(alphaComposite);
		g.setColor(colour);
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

	public void setUnit(AnimatedUnit aunit) {
		this.aunit = aunit;
	}

	public void setCurrentUnit(AnimatedUnit current){
		this.current = current;
	}
	
}
