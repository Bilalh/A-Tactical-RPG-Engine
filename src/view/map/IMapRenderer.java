package view.map;

import java.awt.Graphics;

/**
 * @author Bilal Hussain
 */
public interface IMapRenderer {

	boolean draw(Graphics g, int width, int height);

	public abstract BufferSize getMapDimensions();

}