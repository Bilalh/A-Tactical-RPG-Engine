package view.util;

import java.awt.Dimension;

/**
 * Size of the buffer for map drawing.
 * @author Bilal Hussain
 */
public class BufferSize extends Dimension {
	private static final long serialVersionUID = 3298926805857003768L;
	
	public final int heightOffset;
	
	/** @category Generated */
	public BufferSize(int heightOffset, int bufferWidth, int bufferHeight) {
		this.heightOffset = heightOffset;
		this.width  = bufferWidth;
		this.height = bufferHeight;
	}
	
	
}
