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
public class Menu implements IDisplayable {

	private int xOffset = 25;
	private int yOffset = 20;
	
	private int selected = 0;
	
	private RoundRectangle2D.Float area  = new RoundRectangle2D.Float();
	private ArrayList<MenuItem> commands = new ArrayList<MenuItem>();
	
	public Menu(){
		commands.addAll(Arrays.asList(new MenuItem[]{
				new MenuItem("Attack"), new MenuItem("Wait"), new MenuItem("item"), new MenuItem("Back")}
		));
	}

	@Override
	public void draw(Graphics2D g, int drawX, int drawY){
		g = (Graphics2D) g.create();

		area =  new RoundRectangle2D.Float(drawX, drawY,
				70,
				25*commands.size(),
				10, 10);
		
		Composite oldC = g.getComposite();
		
		AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
		Color oldCo = g.getColor();
		g.setComposite(alphaComposite);
		g.setColor(Color.green);
		g.fill(area);
		
		g.setComposite(oldC);
		g.setColor(oldCo);

		g.translate(drawX+xOffset, drawY+yOffset);

        g.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		for (MenuItem m : commands) {
			m.draw(g, m == commands.get(selected));
			g.translate(0, 20);
		}
		
	}

	public void scrollToPrevious(){
		selected = (selected-1 + commands.size()) % commands.size();
	}
	
	public void scrollToNext(){
		selected = (selected+1) % commands.size();
	}
	
	public MenuItem clicked(Point p){
		if (area.contains(p)){
			System.out.println(p);
			System.out.println(area.getBounds());
			float index = p.y-area.y;
			selected = (int) (index/25) ;
			return commands.get(selected);
		}
		return null;
	}
	
	public void addCommand(MenuItem m){
		commands.add(m);
	}

	public void reset(){
		selected=0;
	}
	
	public void clear(){
		commands.clear();
	}
	

	public MenuItem getSelected() {
		return commands.get(selected);
	}
	
	/** @category Generated */
	public void setCommands(ArrayList<MenuItem> commands) {
		this.commands = commands;
	}

}
