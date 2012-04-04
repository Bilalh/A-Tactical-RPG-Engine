package view.map;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import view.Gui;
import view.map.interfaces.IActions;

import controller.MapController;

import engine.map.Map;

/**
 * Handles the game loop of the map
 * @author Bilal Hussain
 */
public class MapPanel extends JPanel implements Runnable {
	private static final Logger log = Logger.getLogger(MapPanel.class);
	private static final long serialVersionUID = 525072238231645623L;
	
	private static final int NO_DELAYS_PER_YIELD = 16;
	// Number of frame that can be skipped.
	private static final int MAX_FRAME_SKIPS = 5;
	// Differnces in nanoseconds between each draw.
	private long period; // period between drawing in _nanosecs_

	// For the thread
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean isPaused = false;
	private volatile boolean gameOver = false;

	// off-screen rendering
	private Graphics bg;
	private Image buffer = null;

	private MapController mapController;
	private GuiMap map;
	int width, height;
	
	public MapPanel(final MapController mapController, long period, int width, int height) {
		this.period = period;
		this.width  = width;
		this.height = height;
		
		setDoubleBuffered(false);
		setBackground(Color.black);
		setSize(width, height);
		setPreferredSize(new Dimension(getWidth(), getHeight()));
		setIgnoreRepaint(true);
		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events

		this.mapController = mapController;
		
		this.map = new GuiMap(mapController, this);
		this.addMouseListener(map.getMouseListener());
		this.addMouseMotionListener(map.getMouseMotionListener());

		this.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {

				int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
				
				if ((mask == Event.META_MASK && e.isMetaDown())  || (mask == Event.CTRL_MASK && e.isControlDown())) {
					if (e.getKeyCode() == KeyEvent.VK_D) Gui.toggleConsole();
					if (e.getKeyCode() == KeyEvent.VK_S) mapController.save();

					return;
				} else if (e.isShiftDown()) {
					if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET)
						Gui.console().pageUp();
					else if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) Gui.console().pageDown();
					return;
				}

				final IActions handler = map.getActionHandler();
				log.trace("using "+ handler + " for " + e.getKeyCode() + " " + e.getKeyChar());
				
				switch (e.getKeyCode()) {
					case KeyEvent.VK_L:
						Gui.toggleConsole();
						break;
					case KeyEvent.VK_OPEN_BRACKET:
						Gui.console().scrollUp();
						break;
					case KeyEvent.VK_CLOSE_BRACKET:
						Gui.console().scrollDown();
						break;
					case KeyEvent.VK_UP:
						handler.keyUp();
						break;
					case KeyEvent.VK_DOWN:
						handler.keyDown();
						break;
					case KeyEvent.VK_LEFT:
						handler.keyLeft();
						break;
					case KeyEvent.VK_RIGHT:
						handler.keyRight();
						break;
					case KeyEvent.VK_ENTER:
					case KeyEvent.VK_X:
						handler.keyComfirm();
						break;
					case KeyEvent.VK_Z:
						handler.keyCancel();
						break;
					case KeyEvent.VK_A:
						handler.keyOther1();
						break;
					case KeyEvent.VK_S:
						handler.keyOther2();
						break;
					case KeyEvent.VK_C:
						handler.keyOther3();
					case KeyEvent.VK_SLASH:
					case KeyEvent.VK_H:
						Gui.showKeyMapping();
					default:
						map.otherKeys(e);
						break;
				}

			}
		});

	}

	@Override
	public void run() {
		long before, after, timeDiff = 0, sleepTime = 0;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		before = System.nanoTime();
		buffer = createImage(width, height);
		map.makeImageBuffer();

		running = true;

		long realOld = System.nanoTime();
		while (running) {
			long temp = System.nanoTime();
			render(temp - realOld);
			realOld = temp;
			draw();

			after = System.nanoTime();
			timeDiff = after - before;
			sleepTime = (period - timeDiff) - overSleepTime;
			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // ns -> ms
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				overSleepTime = (System.nanoTime() - after) - sleepTime;

				// The frame took longer then the period
			} else {
				excess -= sleepTime;
				overSleepTime = 0L;

				// Allow other threads to run.
				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();
					noDelays = 0;
				}

			}

			before = System.nanoTime();

			// Frame skip if this is taken to long
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				skips++;
			}
		}
	}

	@Override
	public void addNotify() {
		super.addNotify(); 
		initialize(); // start the thread
	}

	private void initialize() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public void finished() {
		running = false;
	}

	private void render(long timeDiff) {
		assert buffer != null : "image buffer null";
		bg = buffer.getGraphics();

		// Clear the panel
		bg.setColor(Color.BLUE.darker());
		bg.fillRect(0, 0, getWidth(), getHeight());
		// Draw the elements of the panel
		map.draw(buffer.getGraphics(), timeDiff, getWidth(), getHeight());

		// Draw the Console if needed
		
		if (Gui.isShowConsole()) {
			Gui.console().setWidth(getWidth());
			Gui.console().draw((Graphics2D) bg, 0, getHeight() - Gui.console().getHeight());
		}

		bg.dispose();
	}

	// Uses active rendering
	private void draw() {
		Graphics g = this.getGraphics();
		if (g == null) return;
		assert buffer != null : "image    buffer null";

		g.drawImage(buffer, 0, 0, null);

		// Fix for some linux systems.
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

}
