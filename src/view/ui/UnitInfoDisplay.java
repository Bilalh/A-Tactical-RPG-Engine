package view.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import view.GuiUnit;

import common.interfaces.IMapUnit;

/**
 * Display a units status
 * @author Bilal Hussain
 */
public class UnitInfoDisplay {

	int xOffset = 5;
	int yOffset = 20;
	
	public void draw(Graphics2D g2, int x, int y, IMapUnit u){
		RoundRectangle2D.Float area =  new RoundRectangle2D.Float(x, y,
				120,
				90,
				10, 10);
		
		Color old =  g2.getColor();
		Composite oldC = g2.getComposite();
		
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
		g2.setComposite(alphaComposite);
		g2.setColor(Color.green);
		g2.fill(area);
		
		g2.setComposite(oldC);
		g2.setColor(old);
		
		// Draw the unts info 
		g2.drawString(u.getName(), x+xOffset, y+yOffset);
		String hp = String.format("HP %3d/%3d", u.getCurrentHp(), u.getMaxHp());
		g2.drawString(hp, x+xOffset, y+yOffset+20);
		
	}
	
}
