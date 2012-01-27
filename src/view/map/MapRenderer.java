package view.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.log4j.Logger;


import util.Logf;
import view.AnimatedUnit;

/**
 * @author Bilal Hussain
 */
public class MapRenderer {

	private static final Logger log = Logger.getLogger(MapRenderer.class);
	
	IMapRendererParent parent;
	MapTile[][] field;

	private final int fieldWidth, fieldHeight;
	private final int startX, startY;

	private boolean drawn = false;
	private boolean showNumbering = false;
	int animationDuration = 750 * 1000000;

	/** @category Generated Constructor */
	public MapRenderer(MapTile[][] field, IMapRendererParent parent, int startX, int startY) {
		this.startX = startX;
		this.startY = startY;
		this.parent = parent;
		this.field = field;

		this.fieldWidth = field.length;
		this.fieldHeight = field[0].length;
	}

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
		boolean tex = false;
		for (int i = fieldHeight - 1; i >= 0; i--) {
			// for (int i = 0 ; i < fieldHeight; i++) { // for rotate
			tex = !tex;
			for (int j = 0; j < fieldWidth; j++) {
				// for (int j = fieldWidth - 1; j >= 0; j--) { // for rotate
				tex = !tex;
				if (parent.isMouseMoving() ||
						(x - horizontal - drawX <= width + MapSettings.tileDiagonal * 2
								&& y - vertical - drawY <= height + MapSettings.tileDiagonal * 2
								&& x + horizontal - drawX >= -MapSettings.tileDiagonal * 2
								&& y + vertical - drawY >= -MapSettings.tileDiagonal * 2)) {
					field[j][i].textured = tex;
					field[j][i].draw(x, y, g, true, true);

					if (showNumbering) {
						Color old = g.getColor();
						g.setColor(Color.RED);
						g.drawString(String.format("(%d,%d) %d", j, i, field[j][i].getCost()),
								(int) (x - (MapSettings.tileDiagonal * MapSettings.zoom) / 2 + 20),
								y + 10);
						g.setColor(old);
					}

					AnimatedUnit u = parent.getUnit(field[j][i]);
					if (u != null) {
//						Logf.debug(log,"(%s,%s) unit:%s",j,i, u);
						u.draw(g, field, x, y, animationDuration);
					}
				} else {
					drawnEverything = false;
				}
				x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
				y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
			}
			x = startX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
			y = startY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (fieldHeight - i));
			// x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (i+1)); // for rotate
			// y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (i+1)); // for rotate
		}
		return  (parent.isMouseMoving() && drawnEverything) || !parent.isMouseMoving();
		// System.out.println("@@@DRAWN IS " + drawn + " + MOUSE IS " + mouseMoving + " Everything is " + drawnEverything);

	}

}
