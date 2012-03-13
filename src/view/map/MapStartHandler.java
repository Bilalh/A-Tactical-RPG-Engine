package view.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import view.map.GuiMap.ActionsEnum;
import view.util.MapActions;

/**
 * Shows a Victory/Game Over
 * @author Bilal Hussain
 */
public class MapStartHandler extends MapActions {
	private static final Logger log = Logger.getLogger(MapStartHandler.class);

	private String message;
	private Font f = new Font("Helvetica", Font.BOLD, 30);

	private boolean shownMessage;
	
	public MapStartHandler(GuiMap map) {
		super(map);
	}

	public void setup(String message) {
		this.message = message;
	}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		assert message != null : "setup should be called first";

		g = (Graphics2D) g.create();
		g.setFont(f);
		g.setColor(Color.ORANGE);

		FontMetrics metrics = g.getFontMetrics();
		Rectangle2D rect = metrics.getStringBounds(message, g);
		
		g.drawString("Win Condition", (int) (width / 2  - rect.getWidth()/2) , (height / 2)-50);
		g.drawString(message, (int) (width / 2  - rect.getWidth()/2) , (height / 2));
		g.dispose();
		shownMessage = true;
	}

	private void finished() {
		map.getMapController().dialogFinished();
	}

	@Override
	public void keyComfirm() {
		if (shownMessage) finished();
	}

}
