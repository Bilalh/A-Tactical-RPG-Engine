package view.ui;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import java.util.ArrayList;
import java.util.Arrays;

import common.interfaces.IMapUnit;

/**
 * @author Bilal Hussain
 */
public class Menu {

	int xOffset = 25;
	int yOffset = 20;
	
	RoundRectangle2D.Float area = new RoundRectangle2D.Float();
	ArrayList<MenuItem> commands = new ArrayList<MenuItem>();

	MenuItem selected = null;
	
	public Menu(){
		commands.addAll(Arrays.asList(new MenuItem[]{
				new MenuItem("Attack"), new MenuItem("Wait"), new MenuItem("item")}
		));
		selected=commands.get(0);
	}
	

	public void draw(Graphics2D g2, int x, int y){
		g2 = (Graphics2D) g2.create();
		
		
		area =  new RoundRectangle2D.Float(x, y,
				70,
				25*commands.size(),
				10, 10);
		
		Composite oldC = g2.getComposite();
		
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
		Color oldCo = g2.getColor();
		g2.setComposite(alphaComposite);
		g2.setColor(Color.green);
		g2.fill(area);
		
		g2.setComposite(oldC);
		g2.setColor(oldCo);

		g2.translate(x+xOffset, y+yOffset);

        g2.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		for (MenuItem m : commands) {
			m.draw(g2, m == selected);
			g2.translate(0, 20);
		}
		
	}

	
	public MenuItem clicked(Point p){
		if (area.contains(p)){
			System.out.println(p);
			System.out.println(area.getBounds());
			float index = p.y-area.y;
			index /= 25;
			selected = commands.get((int) index);
			return selected;
		}
		return null;
	}
	
	public void addCommand(MenuItem m){
		commands.add(m);
	}

	public void clear(){
		commands.clear();
	}
	
	/** @category Generated */
	public void setCommands(ArrayList<MenuItem> commands) {
		this.commands = commands;
	}
}
