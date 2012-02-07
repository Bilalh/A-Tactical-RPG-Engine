package view.map;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;

import common.Location;


import util.Logf;
import view.AnimatedUnit;

/**
 * Draws a isomertricMap
 * @author Bilal Hussain
 */
public class IsomertricMapRenderer implements IMapRenderer {

	private static final Logger log = Logger.getLogger(IsomertricMapRenderer.class);
	
	private IMapRendererParent parent;
	private IsoTile[][] field;

	private final int fieldWidth, fieldHeight;
	private final int startX, startY;

	private boolean drawn = false;
	private boolean showNumbering = false;
	int animationDuration = 750 * 1000000;

	
	
	/** @category Generated Constructor */
	public IsomertricMapRenderer(IsoTile[][] field, IMapRendererParent parent) {
		this.parent = parent;
		this.field = field;

		this.fieldWidth = field.length;
		this.fieldHeight = field[0].length;
		makeSize();
		startX = size.width/2 +(fieldHeight - fieldWidth)*MapSettings.tileDiagonal/4;
		startY = size.heightOffset;
	}

	
	private BufferSize size;
	private void makeSize(){
        int max = Math.max(fieldWidth, fieldHeight);
		int heightOffset  = (MapSettings.tileDiagonal)*3;
//		bufferWidth   = MapSettings.tileDiagonal * max + 5;
//		bufferHeight  = (int) (MapSettings.tileDiagonal / 2f *max + heightOffset);
		
		int w = fieldWidth + (fieldHeight-fieldWidth)/2;
		int bufferWidth   = MapSettings.tileDiagonal * w+ 5;
		int bufferHeight  = (int) (MapSettings.tileDiagonal / 2f *w+ heightOffset);
		size = new BufferSize(heightOffset, bufferWidth, bufferHeight);
	}
	
	
	Font numbers = new Font("Helvetica", Font.PLAIN,  10);
	@Override
	public boolean draw(Graphics g, int width, int height) {

		final int drawX = parent.getDrawX();
		final int drawY = parent.getDrawY();

		// TODO rotates workss!
		int x = startX;
		int y = startY;
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
		// System.out.println("___drawn is " + drawn + " mouse is " + mouseMoving);

		boolean drawnEverything = true;
		Font oldFont = null;
		if (showNumbering){
			oldFont =  g.getFont();
			g.setFont(numbers);	
		}
		
		for (int i = fieldHeight - 1; i >= 0; i--) {
			// for (int i = 0 ; i < fieldHeight; i++) { // for rotate
			for (int j = 0; j < fieldWidth; j++) {
				// for (int j = fieldWidth - 1; j >= 0; j--) { // for rotate
				if (parent.isMouseMoving() ||
						(x - horizontal - drawX <= width + MapSettings.tileDiagonal * 3
								&& y - vertical - drawY <= height + MapSettings.tileDiagonal * 3
								&& x + horizontal - drawX >= -MapSettings.tileDiagonal * 3
								&& y + vertical - drawY >= -MapSettings.tileDiagonal * 3)) {
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
				} else {
					drawnEverything = false;
				}
				x += Math.round(MapSettings.tileDiagonal / 2 * MapSettings.zoom);
				y += Math.round(MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
			}
			Math.round((MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i)));
//			x = startX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
//			y = startY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (fieldHeight - i));
			x = startX - Math.round(MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
			y = startY + Math.round(MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (fieldHeight - i));
			
			// x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (i+1)); // for rotate
			// y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (i+1)); // for rotate
		}
		
		g.setFont(oldFont);
		return  (parent.isMouseMoving() && drawnEverything) || !parent.isMouseMoving();
		// System.out.println("@@@DRAWN IS " + drawn + " + MOUSE IS " + mouseMoving + " Everything is " + drawnEverything);
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

}
