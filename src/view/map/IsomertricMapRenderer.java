package view.map;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import common.Location;
import common.enums.Direction;
import common.interfaces.ILocation;


import util.Logf;
import view.units.AnimatedUnit;
import view.util.BufferSize;

/**
 * Draws a isomertricMap
 * @author Bilal Hussain
 */
public class IsomertricMapRenderer implements IMapRenderer {
	private static final Logger log = Logger.getLogger(IsomertricMapRenderer.class);
	
	private IMapRendererParent parent;
	private IsoTile[][] field;

	// For drawing	 
	private final int fieldWidth, fieldHeight;
	private final int startX, startY;
	private boolean drawn = false;
	
	// For numbering the tiles (for debuging).
	private boolean showNumbering = false;
	Font numbers = new Font("Helvetica", Font.PLAIN,  10);
	
	int animationDuration = 750 * 1000000;

	// expression used for all x/y cond calculations 
	float xCalc, yCalc;

	int horizontal;
	int vertical;
	
	private Rotation rotation = Rotation.WEST;
	public IsomertricMapRenderer(IsoTile[][] field, IMapRendererParent parent) {
		this.parent = parent;
		this.field = field;

		this.fieldWidth = field.length;
		this.fieldHeight = field[0].length;
		calculateSize();
		startX = size.width/2 +(fieldHeight - fieldWidth)*MapSettings.tileDiagonal/4;
		startY = size.heightOffset;
		
		invaildate();
	}

	public void invaildate(){

		 horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		 vertical   = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		
		 xCalc =  horizontal/2;
		 yCalc =  vertical /2;
	}
	
	private BufferSize size;
	private void calculateSize(){
        int max = Math.max(fieldWidth, fieldHeight);
		int heightOffset  = (MapSettings.tileDiagonal)*3;

		int w = fieldWidth + (fieldHeight-fieldWidth)/2;
		int bufferWidth   = MapSettings.tileDiagonal * w+ 5;
		int bufferHeight  = (int) (MapSettings.tileDiagonal / 2f *w+ heightOffset);
		size = new BufferSize(heightOffset, bufferWidth, bufferHeight);
	}

	@Override
	public boolean draw(Graphics g, int width, int height) {
	
		Font oldFont = null;
		if (showNumbering){
			oldFont =  g.getFont();
			g.setFont(numbers);	
		}
		
		boolean drawnEverything =true;
		switch (rotation){
			case EAST:
				drawnEverything = drawEast(g, width, height);
				break;
			case NORTH:
				drawnEverything = drawNorth(g, width, height);
				break;
			case SOUTH:
				drawnEverything = drawSouth(g, width, height);
				break;
			case WEST:
				drawnEverything = drawWest(g, width, height);
				break;
		}
		
		g.setFont(oldFont);
		return  (parent.isMouseMoving() && drawnEverything) || !parent.isMouseMoving();
	}

	// Methods to rotate the map 
	
	private boolean drawWest(Graphics g, int width, int height) {

		int x = startX, y = startY;
		final int drawX = parent.getDrawX(),  drawY = parent.getDrawY();
		boolean drawnEverything = true;

		for (int i = fieldHeight - 1; i >= 0; i--) {
			for (int j = 0; j < fieldWidth; j++) {
				drawnEverything = drawSub(g, width, height, drawX, drawY, x, y, i, j);
				x += Math.round(xCalc);
				y += Math.round(yCalc);
			}
			x = startX - (int) (xCalc * (fieldHeight - i));
			y = startY + (int) (yCalc * (fieldHeight - i));
		}

		return drawnEverything;
	}
	
	private boolean drawSouth(Graphics g, int width, int height) {

		int x = startX, y = startY;
		final int drawX = parent.getDrawX(),  drawY = parent.getDrawY();
		boolean drawnEverything = true;

		for (int i = fieldHeight - 1; i >= 0; i--) {
			for (int j = fieldWidth - 1; j >= 0; j--){
				drawnEverything = drawSub(g, width, height, drawX, drawY, x, y, i, j);
				x += Math.round(xCalc);
				y += Math.round(yCalc);
			}
			x = startX - (int) (xCalc * (fieldHeight - i));
			y = startY + (int) (yCalc * (fieldHeight - i));
		}

		return drawnEverything;
	}
	
	private boolean drawEast(Graphics g, int width, int height) {

		int x = startX, y = startY;
		final int drawX = parent.getDrawX(),  drawY = parent.getDrawY();
		boolean drawnEverything = true;

		for (int i = 0 ; i < fieldHeight; i++) {
			for (int j = fieldWidth - 1; j >= 0; j--){
				drawnEverything = drawSub(g, width, height, drawX, drawY, x, y, i, j);
				x += Math.round(xCalc);
				y += Math.round(yCalc);
			}
			x = startX - (int) (xCalc * (1 + i));
			y = startY + (int) (yCalc * (1 + i));
		}

		return drawnEverything;
	}
	
	private boolean drawNorth(Graphics g, int width, int height) {

		int x = startX, y = startY;
		final int drawX = parent.getDrawX(),  drawY = parent.getDrawY();
		boolean drawnEverything = true;

		for (int i = 0 ; i < fieldHeight; i++) {
			for (int j = 0; j < fieldWidth; j++) {
				drawnEverything = drawSub(g, width, height, drawX, drawY, x, y, i, j);
				x += Math.round(xCalc);
				y += Math.round(yCalc);
			}
			x = startX - (int) (xCalc * (1 + i));
			y = startY + (int) (yCalc * (1 + i));
		}

		return drawnEverything;
	}
	

	// Actual drawing 
	private boolean drawSub(Graphics g, int width, int height, final int drawX, final int drawY, int x, int y, int i, int j) {
		if (parent.isMouseMoving() ||
				(x - horizontal - drawX  <= width +   MapSettings.tileDiagonal * 3
						&& y - vertical   - drawY <=  height + MapSettings.tileDiagonal * 3
						&& x + horizontal - drawX >= -MapSettings.tileDiagonal * 3
						&& y + vertical   - drawY >= -MapSettings.tileDiagonal * 3)) {
			field[j][i].draw(x, y, g, true, true);

			if (showNumbering) {
				Color old = g.getColor();
				g.setColor(Color.RED);
				Point pp =field[j][i].calculateCentrePoint(x,y);
				g.drawString(String.format("%d,%d", j,i),pp.x - MapSettings.tileDiagonal/4 ,pp.y);
				
				g.setColor(old);
			}

			AnimatedUnit u = field[j][i].getUnit();
			if (u != null) {
//						Logf.debug(log,"(%s,%s) unit:%s",j,i, u);
				u.draw(g, field, x, y, animationDuration);
			}
			return true;
		}else{
			return false;
		}
		
	}


	public void rotateMap(){
		rotation = rotation.next();
	}

	public Direction traslateDirection(Direction d){
		assert d != null : "Direction should not null";
		return rotation.transtateDirection(d);
	}
	
	public void toggleNumbering(){
		showNumbering = !showNumbering;
	}
	
	/** @category Generated */
	public boolean hasNumbering() {
		return showNumbering;
	}


	/** @category Generated */
	@Override
	public BufferSize getMapDimensions() {
		return size;
	}

	/** @category Generated */
	public Rotation getRotation() {
		return rotation;
	}
	
	/** @category Generated */
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

}
