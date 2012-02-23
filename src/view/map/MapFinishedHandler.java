package view.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import view.util.MapActions;

/**
 *  Shows a Victory/Game Over
 * @author Bilal Hussain
 */
public class MapFinishedHandler extends MapActions {
	private static final Logger log = Logger.getLogger(MapFinishedHandler.class);

	private boolean won;
	private String message;
	private Font f = new Font("Helvetica", Font.BOLD, 30);

	private boolean shownMessage;
	
	public MapFinishedHandler(GuiMap map) {
		super(map);
	}

	public void setup(boolean won) {
		this.won = won;
		message = won ? "Victory" : "Game Over";
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		assert message != null : "setup should be called first";

		g = (Graphics2D) g.create();
		g.setFont(f);
		g.setColor(Color.ORANGE);

		FontMetrics metrics = g.getFontMetrics();
		Rectangle2D rect = metrics.getStringBounds(message, g);
		
		g.drawString(message, (int) (width / 2  - rect.getWidth()/2) , (height / 2));
		g.dispose();
		shownMessage = true;
	}

	private void finished() {
		log.info("Finished Map");
		if (won) map.getMapController().mapWon();
		else     map.getMapController().mapLost();
	}

	@Override
	public void keyComfirm() {
		if (shownMessage) finished();
	}

}
