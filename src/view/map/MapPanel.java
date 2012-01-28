package view.map;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

import com.sun.tools.example.debug.gui.GUI;

import view.Gui;
import view.interfaces.IActions;

import controller.MapController;

import engine.map.Map;


public class MapPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private static final int NO_DELAYS_PER_YIELD = 16;

	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered
	private static int MAX_FRAME_SKIPS = 5;


	private long gameStartTime;

	private Thread animator; // the thread that performs the animation
	private volatile boolean running = false; // used to stop the animation thread
	private volatile boolean isPaused = false;

	private long period; // period between drawing in _nanosecs_


	// used at game termination
	private volatile boolean gameOver = false;


	// off-screen rendering
	private Graphics dbg;
	private Image dbImage = null;

	private GuiMap map;

	public MapPanel(MapController mapController, long period) {
		this.period = period;

		setDoubleBuffered(false);
		setBackground(Color.black);
		setSize(300, 300);
		setPreferredSize(new Dimension(getWidth(), getHeight()));
		setIgnoreRepaint(true);
		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events

		this.map  = new GuiMap(mapController, this);
		this.addMouseListener(map.getMouseListener());
		this.addMouseMotionListener(map.getMouseMotionListener());
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
//				dbImage = null;
//				System.out.println("resized");
			}

		});

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.isMetaDown()){
					if (e.getKeyCode() == KeyEvent.VK_D)   Gui.toggleDebugConsole();
					return;
				}else if (e.isShiftDown()){
					if      (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET)   Gui.console().pageUp();
					else if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET)  Gui.console().pageDown();
					return;
				}

				final IActions handler = map.getActionHandler();

				switch (e.getKeyCode()) {					
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
					case KeyEvent.VK_X:
						handler.keyComfirm();
						break;
					case KeyEvent.VK_Z:
						handler.keyCancel();
						break;
					default:
						map.otherKeys(e);
						break;
				}

			}
		});		
	}

	@Override
	public void run() {
		long beforeTime, afterTime, timeDiff =0, sleepTime=0;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		beforeTime = gameStartTime;

		dbImage = createImage(Gui.WIDTH, Gui.HEIGHT);
		map.makeImageBuffer(this);
		
		running = true;

		
		long realOld = System.nanoTime();
		while (running) {
			gameUpdate();
			long temp = System.nanoTime();
//			gameRender(timeDiff);
			gameRender(temp - realOld);
			realOld = temp;
			paintScreen();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
//			System.out.println(sleepTime + "   " + "   " + "   " + beforeTime + "   " +  afterTime + "   "+ timeDiff);
			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			/*
			 * If frame animation is taking too long, update the game state without rendering it, to
			 * get the updates/sec nearer to the required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
//				System.out.println("Skipping " + skips);
			}
		}
		System.exit(0); // so window disappears
	}

	// wait for the JPanel to be added to the JFrame before starting
	@Override
	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}


	// initialise and start the thread
	private void startGame(){
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	// called by the JFrame's window listener methods

	// called when the JFrame is activated / deiconified
	public void resumeGame(){
		isPaused = false;
	}

	// called when the JFrame is deactivated / iconified
	public void pauseGame(){
		isPaused = true;
	}

	// called when the JFrame is closing
	public void stopGame() {
		running = false;
	}

	private void gameUpdate() {
		if (!isPaused && !gameOver) {
		}
	}


	private void gameRender(long timeDiff) {
//		dbImage = createImage(getWidth(), getHeight());
		if (dbImage == null) {
			System.out.println("dbImage is null");
			System.exit(1);
		}
		dbg = dbImage.getGraphics();
		
		
		dbg.setColor(Color.GRAY);
		dbg.fillRect(0, 0, getWidth(), getHeight());		
		// draw game elements
		map.draw(dbImage.getGraphics(), timeDiff, getWidth(), getHeight());


		if (Gui.showDebugConsole()) {
			Gui.console().paint((Graphics2D) dbg, 0, getHeight() - Gui.console().getHeight()  , getWidth());
		}         

		dbg.dispose();
	}

	// use active rendering to put the buffered image on-screen
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null)){

				g.drawImage(dbImage, 0,0, null);
			}
				
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();

			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	}


}

