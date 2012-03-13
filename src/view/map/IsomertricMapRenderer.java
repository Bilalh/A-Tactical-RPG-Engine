package view.map;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import common.Location;
import common.enums.Direction;
import common.interfaces.ILocation;
import config.xml.MapSettings;


import util.Logf;
import view.map.interfaces.IMapRenderer;
import view.map.interfaces.IMapRendererParent;
import view.units.AnimatedUnit;
import view.util.BufferSize;

/**
 * Renders a isomertric map 
 * @author Bilal Hussain
 */
public class IsomertricMapRenderer implements IMapRenderer {
	private static final Logger log = Logger.getLogger(IsomertricMapRenderer.class);
	
	IMapRendererParent parent;

	// Map data
	IsoTile[][] field;
	private MapSettings mapSettings;
	Rotation rotation = Rotation.WEST;
	BufferSize size;
	
	// For drawing	 
	final int fieldWidth, fieldHeight;
	final int startX, startY;
	boolean drawn = false;

	
	// For numbering the tiles (for debuging).
	boolean showNumbering = false;
	Font numbers = new Font("Helvetica", Font.PLAIN,  10);
	int animationDuration = 750 * 1000000;
	
	// Cached values for calcuations 
	float xCalc, yCalc;
	int horizontal;
	int vertical;

	public IsomertricMapRenderer(IsoTile[][] field, IMapRendererParent parent, float multiplier, MapSettings mapSettings) {
		this.parent = parent;
		this.field  = field;

		this.fieldWidth  = field.length;
		this.fieldHeight = field[0].length;
		this.setMapSettings(mapSettings);
		calculateSize();
		
		startX = (int) ((size.width / 2 + (fieldHeight - fieldWidth) * mapSettings.tileDiagonal / 4) * multiplier);
		startY = ((size.heightOffset));
		
		invaildate();
	}
	
	private void calculateSize(){
        int max = Math.max(fieldWidth, fieldHeight);
		int heightOffset  = (getMapSettings().tileDiagonal)*2;

		int w = fieldWidth + (fieldHeight-fieldWidth)/2;
		int bufferWidth    = getMapSettings().tileDiagonal * w+ 5;
		int bufferHeight  = (int) (getMapSettings().tileDiagonal / 2f *w+ heightOffset);
		size = new BufferSize(heightOffset, bufferWidth, bufferHeight);
	}

	@Override
	public void invaildate(){
		 horizontal = (int) (getMapSettings().tileDiagonal * getMapSettings().zoom);
		 vertical   = (int) (getMapSettings().tileDiagonal * getMapSettings().pitch * getMapSettings().zoom);
		
		 xCalc = horizontal/2;
		 yCalc = vertical /2;
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

	// Methods to draw a rotated the map
	
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
		if (parent.isMouseMoving() || (x - horizontal - drawX  <= width +   getMapSettings().tileDiagonal * 3
						&& y - vertical   - drawY <=  height + getMapSettings().tileDiagonal * 3
						&& x + horizontal - drawX >= -getMapSettings().tileDiagonal * 3
						&& y + vertical   - drawY >= -getMapSettings().tileDiagonal * 3)) {
			
			field[j][i].draw(x, y, g);

			if (showNumbering) {
				Color old = g.getColor();
				g.setColor(Color.RED);
				Point pp =field[j][i].calculateCentrePoint(x,y);
				g.drawString(String.format("%d,%d", j,i),pp.x - getMapSettings().tileDiagonal/4 ,pp.y);
				g.setColor(old);
			}

			AnimatedUnit u = field[j][i].getUnit();
			if (u != null) {
				//Logf.debug(log,"(%s,%s) unit:%s",j,i, u);
				u.draw(g, field, x, y, animationDuration);
			}
			return true;
		}else{
			return false;
		}
		
	}

	public void rotateMap(){
		rotation = rotation.next();
		System.err.println(rotation);
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
		return new BufferSize(size.heightOffset, size.width, size.height);
	}

	/** @category Generated */
	public Rotation getRotation() {
		return rotation;
	}
	
	/** @category Generated */
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	/** @category Generated */
	public int getStartX() {
		return startX;
	}

	/** @category Generated */
	public int getStartY() {
		return startY;
	}

	MapSettings getMapSettings() {
		return mapSettings;
	}

	void setMapSettings(MapSettings mapSettings) {
		this.mapSettings = mapSettings;
	}

}
