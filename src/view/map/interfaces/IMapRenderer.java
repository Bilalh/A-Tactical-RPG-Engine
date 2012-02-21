package view.map.interfaces;

import java.awt.Graphics;

import view.util.BufferSize;

/**
 * Renders a map.
 * @author Bilal Hussain
 */
public interface IMapRenderer {

	/**
	 * Draws the map on the specific graphics object.
	 * @param   width  The width of viewport.
	 * @param   height The height of the viewport.
	 * @return  True if completely drawn. 
	 */
	boolean draw(Graphics g, int width, int height);

	/**
	 * Return the size of in pixels of the map.
	 */
	BufferSize getMapDimensions();

}