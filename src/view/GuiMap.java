/**
 * 
 */
package view;

import static view.MapTile.Orientation.UP_TO_EAST;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

import view.interfaces.IActions;
import view.notifications.ChooseUnitsNotifications;
import view.ui.Dialog;
import view.util.ActionsAdapter;
import view.util.MousePoxy;

import common.gui.SpriteManager;
import common.interfaces.IMapNotification;
import common.interfaces.INotification;
import common.interfaces.IUnit;
import controller.MapController;

import engine.Map;
import engine.Tile;
import engine.Unit;
import engine.interfaces.IModelUnit;


/**
 * @author bilalh
 */
public class GuiMap implements Observer {

    private int drawX;
    private int drawY;
    private static MapTile selectedTile;
    private MapTile[][] field;
    
    private int fieldWidth, fieldHeight;
    
    private AnimatedUnit[] units;
    private AnimatedUnit[] aiUnits;

    private MapController mapController; 
    
    private Dialog dialog;
    private boolean showDialog = true;
        
    // The Class that with handed the input 
    private ActionsAdapter current;
    
    private MousePoxy MousePoxy;
    
    // bad idea allready in unit?
    private HashMap<UUID,AnimatedUnit> unitMapping;
    private HashMap<MapTile,AnimatedUnit> tileMapping; 

    // Buffer for drawing the map.    	
    private final int bufferWidth;
	private final int bufferHeight;
    private Image mapBuffer;
	
    final int startX;
    final int startY;
    
    final private ActionsAdapter[] actions = {new Movement(), new DialogHandler()};
	private boolean showNumbering = true;
    enum ActionsEnum {
    	MOVEMENT, DIALOG,
    }
    
    
	/** @category Constructor */
	public GuiMap(MapController mapController) {
		assert actions.length == ActionsEnum.values().length;
		
		this.mapController = mapController;
		
		final Tile grid[][] = mapController.getGrid();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
		
        field = new MapTile[fieldWidth][fieldHeight];
        current = getActionHandler(ActionsEnum.MOVEMENT);
        MousePoxy = new MousePoxy();
        setActionHandler(ActionsEnum.MOVEMENT);
		
        //FIXME 
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	field[i][j] = new MapTile(MapTile.Orientation.UP_TO_EAST,
            			grid[i][j].getStartHeight(),
            			grid[i][j].getEndHeight());
            	field[i][j].setFieldLocation(new Point(i, j));
            	field[i][j].setCost(grid[i][j].getCost());
            }
        }
        
        bufferWidth  =  MapSettings.tileDiagonal*fieldWidth +5;  
		bufferHeight = (int) (MapSettings.tileDiagonal/2f*fieldHeight +23);
		
        drawX = 0;
        drawY = bufferHeight/2 - Gui.HEIGHT/2;

        startX = bufferWidth/2;
        startY = 21;
        
        unitMapping = new HashMap<UUID, AnimatedUnit>();
        tileMapping = new HashMap<MapTile, AnimatedUnit>();
        
        dialog = new Dialog(665, 70, "mage", SpriteManager.instance().getSprite("assets/gui/mage.png"));
        
        selectedTile = field[0][0];
        
        mapController.addMapObserver(this);
        mapController.startMap();
        
	}

	void makeImageBuffer(Component parent){
		mapBuffer = parent.createImage(bufferWidth,bufferHeight);
	}

	// draws the map to the screen
	private boolean drawn = false;
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		
		Graphics g = mapBuffer.getGraphics();
		//TODO rotates works!
		
		if (!drawn) {
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			selectedTile.setSelected(false);
			int x = startX;
			int y = startY;
			final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
			final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
			for (int i = fieldHeight - 1; i >= 0; i--) {
				//		for (int i = 0 ; i < fieldHeight; i++) { //  for rotate
				for (int j = 0; j < fieldWidth; j++) {
					//			for (int j = fieldWidth - 1; j >= 0; j--) { // for rotate

					// Only draw the the tiles if they in the viewport 
//					if (x - horizontal <= width
//							&& y - vertical <= height
//							&& x + horizontal >= 0
//							&& y + vertical >= 0) {

						field[j][i].draw(x, y, g, true,true);
//
//						AnimatedUnit au = tileMapping.get(field[j][i]);
//						if (au != null) {
//							au.draw(g, field, x, y, timeDiff);
//						}

//						if (showNumbering) {
							Color old = g.getColor();
							g.setColor(Color.RED);
							g.drawString(String.format("(%d,%d) %d", j, i, field[j][i].getCost()),
									(int) (x - (MapSettings.tileDiagonal * MapSettings.zoom) / 2 + 20),
									y + 10);
							g.setColor(old);
//						}

//					}

					x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
					y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
				}
				x = startX- (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
				y = startY+ (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch* MapSettings.zoom * (fieldHeight - i));
				//			x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (i+1)); // for rotate
				//			y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (i+1)); // for rotate
			}
			
//			Point p = getDrawLocation(startX, startY, selectedTile.getFieldLocation().x, selectedTile.getFieldLocation().y);
//			selectedTile.draw(p.x, p.y, g, false,false);
			drawn = true;
			selectedTile.setSelected(true);
		}

//		drawOnScreen();
		
//		for (AnimatedUnit au : units) {
//			Point p = getDrawLocation(startX, startY, au.getGridX(), au.getGridY());
//			au.draw(g, field, p.x, p.y, timeDiff);
//		}

//		Point u = (Point) units[0].getPostion().clone();  // 2, 5
//		redrawSection(g,(Point) units[0].getPostion().clone());
		
		
//		System.out.printf("(%s,%s,%s,%s) (%s.%s,%s,%s)\n\n", 
//				p.x, p.y, MapSettings.tileDiagonal, MapSettings.tileDiagonal/2, 
//				drawX+p.x, drawY+p.y, drawX+MapSettings.tileDiagonal, drawY+MapSettings.tileDiagonal/2);
//		_g.drawImage(img,p.x, p.y, MapSettings.tileDiagonal, MapSettings.tileDiagonal/2, 
//				drawX+p.x, drawY+p.y, drawX+MapSettings.tileDiagonal, drawY+MapSettings.tileDiagonal/2, null);
	
//		System.out.printf("(%s,%s,%s,%s) (%s.%s,%s,%s)\n\n", 
//				380, 230, MapSettings.tileDiagonal, MapSettings.tileDiagonal/2, 
//				p.x, p.y, p.x+MapSettings.tileDiagonal, p.y+MapSettings.tileDiagonal/2);
//		_g.drawImage(img,380, 230-15, 380+MapSettings.tileDiagonal, 230+MapSettings.tileDiagonal/2, 
//				p.x, p.y-15, p.x+MapSettings.tileDiagonal, p.y+MapSettings.tileDiagonal/2, null);
		
//		Point selected = getDrawLocation(startX, startY, 0, 0);
//		selectedTile.draw(selected.x, selected.y, g, false, false);
		

		_g.drawImage(mapBuffer,0, 0, width, height, drawX, drawY, drawX+width, drawY+height, null);
//		setSelectedTile(3, 1);
		
		float ratioX, ratioY;
		ratioX = (float) drawX/(float)bufferWidth;
		ratioY = (float)drawY/(float)bufferWidth;
		
		int px = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - selectedTile.getFieldLocation().y-1f));
		int py = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - selectedTile.getFieldLocation().y-1));
		px += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * selectedTile.getFieldLocation().x;
		py += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom)* selectedTile.getFieldLocation().x;
		
		py -= drawY;
		px -= drawX;
		
//		System.out.printf("grid (%d,%d)\n", fieldWidth, fieldHeight);
//		System.out.printf("buff (%d,%d)\n", bufferWidth, bufferHeight);
//		System.out.printf("start(%d,%d)\n", startX, startY);
//		System.out.printf("draw (%d,%d)\n", drawX, drawY);
//		System.out.printf("p    (%d,%d)\n", px, py);
//		System.out.println();
		selectedTile.draw(px, py, _g, false,false);
//		_g.drawImage(mapBuffer,0, 0, width, height, drawX, drawY, drawX+width, drawY+height, null);

		
		if (showDialog) dialog.draw((Graphics2D) _g, 5, height - dialog.getHeight() - 5);
	}
	
	void mapToWorld(Point p){
		p.translate(-drawX, -drawY);
	}
	
	Point getDrawLocation(int startX, int startY, int gridX, int gridY){
		int x = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - gridY-1f));
		int y = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - gridY-1));
		x += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * gridX;
		y += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom)* gridX;
		return new Point(x,y);
	}
	

	void drawOnScreen(){
		Graphics g = mapBuffer.getGraphics();
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
//		
//		int sY = 4, eY = 6;
//		int sX = 1, eX = 3;
		
		int sY = 0, eY =fieldHeight;
		int sX = 0, eX =fieldWidth;
		
		
		int x = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - eY));
		int y = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - eY));
		
		System.out.println();
		for (int i = eY ; i >= sY; i--) {
			for (int j = sX; j <= eX; j++) {
				if (x - horizontal <= Gui.WIDTH
						&& y - vertical <= Gui.HEIGHT
						&& x + horizontal >= 0
						&& y + vertical >= 0) {
					System.out.printf("(%d,%d)\n", j, i);
					field[j][i].draw(x, y, g, false,false);	
				}	
//				Color old = g.getColor();
//				g.setColor(Color.RED);
//				g.drawString(String.format("(%d,%d) %d", j, i, field[j][i].getCost()),
//						(int) (x - (MapSettings.tileDiagonal * MapSettings.zoom) / 2 + 20),
//						y + 10);
//				g.setColor(old);
				x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
				y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
			}
			x = startX- (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
			y = startY+ (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch* MapSettings.zoom * (fieldHeight - i));
		}
		
	}
	
	void redrawSection(Graphics g, Point u){
//		Draw down (0, -1) and right (1,0)
		Point p = getDrawLocation(startX, startY, u.x+1, u.y);
		field[u.x+1][u.y].draw(p.x, p.y, g, true, true);

		p = getDrawLocation(startX, startY, u.x, u.y-1);
		field[u.x][u.y-1].draw(p.x, p.y, g, true, true);

		u.y-=2;
//		// Zigzag draw the 4 tiles down from u
//		// wrong draw order 
		for (int i =0; i<5;++i){
//			System.out.printf("drawing (%d,%d)\n", u.x, u.y);
			Point draw = getDrawLocation(startX, startY, u.x, u.y);
			field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
			if (i % 2 == 0) u.x += 1;
			else            u.y += 1;
		}
		
//		u.x+=2;
//		Point draw = getDrawLocation(startX, startY, u.x, u.y);
//		field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
//		
//		u.y-=1;
//		u.x-=1;
//		draw = getDrawLocation(startX, startY, u.x, u.y);
//		field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
//		u.x++;
//		draw = getDrawLocation(startX, startY, u.x, u.y);
//		field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
//		
//		u.x-=2;
//		u.y-=1;
//		draw = getDrawLocation(startX, startY, u.x, u.y);
//		field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
//		u.x++;
//		draw = getDrawLocation(startX, startY, u.x, u.y);
//		field[u.x][u.y].draw(draw.x, draw.y, g, false, false);
		
	}
	
	@Override
	public void update(Observable map, Object notification) {
		Gui.console().println(notification);
		((IMapNotification) notification).process(this);
	}

	
	public void chooseUnits(ArrayList<? extends IUnit> allPlayerUnits, ArrayList<? extends IUnit> aiUnits) {
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		HashMap<UUID, Point> selectedPostions = new HashMap<UUID, Point>();
		for (int i = 0; i < newUnits.length; i++) {
			//FIXME indies
			final IUnit u = allPlayerUnits.get(i);
			Point p = new Point(2,i+5); 
			newUnits[i] = new AnimatedUnit(p.x, p.y, new String[]{"assets/gui/Archer.png"},u );
			selectedPostions.put(u.getUuid(), p);
			unitMapping.put(u.getUuid(), newUnits[i]);
			tileMapping.put(field[p.x][p.y], newUnits[i]);
		}
		mapController.setUsersUnits(selectedPostions);
		this.units = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			final IUnit u = aiUnits.get(i);
			newAiUnits[i] = new AnimatedUnit(u.getGridX(), u.getGridY(), 
					new String[]{"assets/gui/alien.gif", "assets/gui/alien2.gif", "assets/gui/alien3.gif"}, 
					u);
			tileMapping.put(field[u.getGridX()][u.getGridY()], newAiUnits[i]);
		}
		this.aiUnits = newAiUnits;
	}
	
	public void unitMoved(IUnit u){
		AnimatedUnit au =  unitMapping.get(u.getUuid());
		tileMapping.remove(field[au.gridX][au.gridY]);
		au.setGridX(u.getGridX());
		au.setGridY(u.getGridY());
		tileMapping.put(field[au.gridX][au.gridY],au);
		drawn = false;
	}
	
	public void playersTurn(){
		displayMessage("Player's Turn");
	}
	
	private void displayMessage(String text){
		dialog.setPicture(null);
		dialog.setName(null);
		dialog.setText(text);
		showDialog = true;
		setActionHandler(ActionsEnum.DIALOG);
	}
		
	private class DialogHandler extends ActionsAdapter{

		@Override
		public void keyComfirm() {
			nextPage();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			nextPage();
		}
		
		private void nextPage(){
			if (!dialog.nextPage()){
				showDialog = false;
				setActionHandler(ActionsEnum.MOVEMENT);
			}
		}
		
	}

	private class Movement extends ActionsAdapter{
		
	    private Point mouseStart, mouseEnd;
	    private int offsetX, offsetY;
		
		@Override
		public void keyUp() {
			setSelectedTile(selectedTile.getFieldLocation().x, selectedTile.getFieldLocation().y+1);
		}

		@Override
		public void keyDown() {
			setSelectedTile(selectedTile.getFieldLocation().x, selectedTile.getFieldLocation().y-1);
		}

		@Override
		public void keyLeft() {
			setSelectedTile(selectedTile.getFieldLocation().x-1, selectedTile.getFieldLocation().y);
			
		}

		@Override
		public void keyRight() {
			setSelectedTile(selectedTile.getFieldLocation().x+1, selectedTile.getFieldLocation().y);
		}
		
		@Override
		public void keyComfirm() {
			selectMoveUnit();
		}
		
		AnimatedUnit selected = null;
		Collection<Point> inRange = null;


		private void selectMoveUnit() {
			if (selected != null){
				System.out.println("selected " + selected);
				if ( !getSelectedTile().isInRange() ) return;
				mapController.moveUnit(selected.unit.getUuid(), getSelectedTile().getFieldLocation());
				for (Point p : inRange) {
					field[p.x][p.y].setInRange(false);
				}
				selected = null;
				inRange = null;
				System.out.println("move finished");
				return;
			}

			AnimatedUnit unitS = null;
			MapTile t = selectedTile;
			
			for (AnimatedUnit u : units) {
				if (u.getGridX() == t.getFieldLocation().x && u.getGridY() == t.getFieldLocation().y){
					unitS = u;
					break;
				}
			}
			
			if(unitS == null) return; 
			
			if (unitS != selected){
				if (inRange != null){
					for (Point p : inRange) {
						field[p.x][p.y].setInRange(false);
					}	
				}
				inRange = null;
				selected = unitS;
			}
			
			inRange =  mapController.getMovementRange(unitS.unit.getUuid());
			for (Point p : inRange) {
				field[p.x][p.y].setInRange(true);
			}
		}
		
		@Override
		public void keyCancel() {
			selected = null;
			if (inRange != null){
				for (Point p : inRange) {
					field[p.x][p.y].setInRange(false);
				}
				inRange = null;
			}
			
		}
		
	    @Override
		public void mousePressed(MouseEvent e) {
	        mouseStart = e.getPoint();

	        offsetX = e.getX() - drawX;
	        offsetY = e.getY() - drawY;
	    }

	    @Override
		public void mouseReleased(MouseEvent e) {
	        mouseEnd = e.getPoint();
	        int a = Math.abs((int) (mouseEnd.getX() - mouseStart.getX()));
	        int b = Math.abs((int) (mouseEnd.getY() - mouseStart.getY()));

	        if (Math.sqrt(a * a + b * b) > 3) {
//	            this.setDrawLocation(mouseEnd);
	        } else {
	            findAndSelectTile(e.getX(), e.getY());
	            selectMoveUnit();
	        }
	    }

	    @Override
		public void mouseDragged(MouseEvent e) {
	        Point current = e.getPoint();
	        setDrawLocation(e.getX() - offsetX, e.getY() - offsetY);
	    }
		
	}
	
	public void otherKeys(KeyEvent e){
		switch (e.getKeyCode()) {
			case KeyEvent.VK_T:
				setActionHandler(ActionsEnum.DIALOG);
				dialog.setPicture(SpriteManager.instance().getSprite("assets/gui/mage.png"));
				dialog.setName("Mage");
				dialog.setText(
						"Many people believe that Vincent van Gogh painted his best works " +
						"during the two-year period he spent in Provence. Here is where he " +
						"painted The Starry Night--which some consider to be his greatest " +
						"work of all. However, as his artistic brilliance reached new " +
						"heights in Provence, his ysical and mental health plummeted. ");
				showDialog = true;
				break;
			case KeyEvent.VK_0:
				showNumbering  = !showNumbering;
				break;
			case KeyEvent.VK_MINUS:
				MapSettings.zoom *= 0.8;
				if ( MapSettings.zoom <0.6) MapSettings.zoom = 0.6f;
				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				System.out.println(MapSettings.zoom);
				break;
			case KeyEvent.VK_EQUALS:
				MapSettings.zoom *= 1.2;
				if ( MapSettings.zoom >1.2) MapSettings.zoom = 1.2f;
				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				System.out.println(MapSettings.zoom);
				break;
			case KeyEvent.VK_COMMA:
				MapSettings.pitch *= 0.8;
				if ( MapSettings.pitch <0.3) MapSettings.pitch = 0.3f;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				System.out.println(MapSettings.pitch);
				break;
			case KeyEvent.VK_PERIOD:
				MapSettings.pitch *= 1.2;
				if ( MapSettings.pitch >0.8) MapSettings.pitch = 0.8f;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				System.out.println(MapSettings.pitch);
				break;
			case KeyEvent.VK_U:
				Point newP = new Point(units[0].getGridX()+1, units[0].getGridY());
				mapController.moveUnit(units[0].getUuid(), newP);
				break;
			case KeyEvent.VK_I:
				System.out.printf("draw (%d,%d)\n", drawX, drawY);
				break;
		}
		
	}
	
	/**
     * Select the file that is under the mouse click
	 */
    public Point findAndSelectTile(int x, int y) {
        double highest = 0.0;
        int xIndex = -1, yIndex = -1;
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                if (field[i][j].wasClickedOn(new Point(x, y)) && field[i][j].getHeight() > highest) {
                    highest = field[i][j].getHeight();
                    xIndex = i;
                    yIndex = j;
                }
            }
        }
        if (xIndex > -1 && yIndex > -1) {
            this.setSelectedTile(xIndex, yIndex);
            return new Point(xIndex, yIndex);
        } else {
            return null;
        }
    }
    
    // Old and newSelected Tiles   
    int oldX, oldY;
    int newX, newY;
	public void setSelectedTile(int x, int y) {
        assert !(x < 0  || y < 0  || x >= fieldWidth || y >= fieldHeight); 
        
        oldX = newX; oldY = newY;
        newX = x;    newY = y;
        Graphics g =mapBuffer.getGraphics();
        if (selectedTile != null) {
            selectedTile.setSelected(false);
            Point selected = getDrawLocation(startX, startY, oldX, oldY);
//            selectedTile.draw(selected.x, selected.y, g, false, false);
//            System.out.printf("(%d,%d) cleared\n", oldX, oldY);
        }
        
        selectedTile = field[newX][newY];
        selectedTile.setSelected(true);
        
		Point selected = getDrawLocation(startX, startY, newX, newY);
//		selectedTile.draw(selected.x, selected.y, g, false, false);
//        System.out.printf("(%d,%d) selected\n", newX, newY);
        
    }

    /** @category Generated */
	public MapTile getSelectedTile() {
		return selectedTile;
	}
    
	
    public void setDrawLocation(int x, int y) {
        drawX = x;
        drawY = y;
    }

	/** @category Generated */
	public boolean isShowDialog() {
		return showDialog;
	}

	/** @category Generated */
	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}

	public IActions getActionHandler() {
		return current;
	}
	
	public MouseListener getMouseListener(){
		return MousePoxy;
	}
	
	public MouseMotionListener getMouseMotionListener(){
		return MousePoxy;
	}
	
	private ActionsAdapter getActionHandler(ActionsEnum num){
		return actions[num.ordinal()];
	}
	
	private void setActionHandler(ActionsEnum num){
		final ActionsAdapter aa = actions[num.ordinal()];
		current = aa;
		MousePoxy.setMouseListener(aa);
		MousePoxy.setMouseMotionListener(aa);
	}
}
