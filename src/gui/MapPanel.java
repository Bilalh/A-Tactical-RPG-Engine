package gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import engine.Map;


public class MapPanel extends JPanel implements Runnable {

	//
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

	// for displaying messages
	private Font msgsFont;
	private FontMetrics metrics;

	// off-screen rendering
	private Graphics dbg;
	private Image dbImage = null;

	// holds the background image
	private BufferedImage bgImage = null;

	private GuiMap map;
	
	public MapPanel(Map grid, long period) {
		this.period = period;
		
		setDoubleBuffered(false);
		setBackground(Color.black);
		setSize(300, 300);
		setPreferredSize(new Dimension(getWidth(), getHeight()));
		setIgnoreRepaint(true);
		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events
		
		// set up message font
		msgsFont = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(msgsFont);
		
		this.map  = new GuiMap(grid);
		this.addMouseListener(map);
		this.addMouseMotionListener(map);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				dbImage = null;
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
				
//				if (inputEvent.keyPressed(shortcutKey)){					
//					if      (e.getKeyCode() == KeyEvent.VK_D))  drawGameDebug = !drawGameDebug;
//					else if (e.getKeyCode() == KeyEvent.VK_I))  drawGameStatistics = !drawGameStatistics;
//					else if (e.getKeyCode() == KeyEvent.VK_T))  Tile.negateDebug();
//				}

//				if (e.getKeyCode() == KeyEvent.VK_N)  Gui.debugConsole().nextPage();
				
				if (e.isMetaDown()){
					if (e.getKeyCode() == KeyEvent.VK_D)   Gui.toggleDebugConsole();
				}
				
				if (e.isShiftDown()){
					if      (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET)   Gui.debugConsole().pageUp();
					else if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET)  Gui.debugConsole().pageDown();
					
				}else if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
					Gui.debugConsole().scrollUp();
				}else if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET ){
					Gui.debugConsole().scrollDown();
				}                                    
				
			}
		});
		
	}

	/* The frames of the animation are drawn inside the while loop. */
	@Override
	public void run() {
		long beforeTime, afterTime, timeDiff =0, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		gameStartTime = System.nanoTime();
		beforeTime = gameStartTime;

		running = true;

		while (running) {
			gameUpdate();
			gameRender(timeDiff);
			paintScreen();

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

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
		if (dbImage == null) {
			dbImage = createImage(getWidth(), getHeight());
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}

		if (bgImage == null) {
			dbg.setColor(Color.GRAY);
			dbg.fillRect(0, 0, getWidth(), getHeight());
		} else
			dbg.drawImage(bgImage, 0, 0, this);

		// draw game elements
		map.draw(dbg, timeDiff, getWidth(), getHeight());
		
		if (Gui.showDebugConsole()) {
			Gui.debugConsole().paint((Graphics2D) dbg, 0, getHeight() - Gui.debugConsole().getHeight()  , getWidth());
		}         
		
		if (gameOver)
			gameOverMessage(dbg);
	}

	// center the game-over message in the panel
	private void gameOverMessage(Graphics g) {
		String msg = "Game Over. Your score: ";

		int x = (getWidth() - metrics.stringWidth(msg)) / 2;
		int y = (getHeight() - metrics.getHeight()) / 2;
		g.setColor(Color.red);
		g.setFont(msgsFont);
		g.drawString(msg, x, y);
	}

	// use active rendering to put the buffered image on-screen
	private void paintScreen() {
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();

			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	}



}
