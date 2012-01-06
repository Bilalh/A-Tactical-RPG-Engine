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
    
    
    final private ActionsAdapter[] actions = {new Movement(), new DialogHandler()};
	private boolean showNumbering = true;
    enum ActionsEnum {
    	MOVEMENT, DIALOG,
    }
    
    private Image img;
    
	/** @category Constructor */
	public GuiMap(MapController mapController) {
		assert actions.length == ActionsEnum.values().length;
		
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
        
        drawX = (int) MapSettings.drawStart.getX();
        drawY = (int) MapSettings.drawStart.getY();
        unitMapping = new HashMap<UUID, AnimatedUnit>();
        tileMapping = new HashMap<MapTile, AnimatedUnit>();
        selectedTile = null;
        
        dialog = new Dialog(665, 70, "mage", SpriteManager.instance().getSprite("assets/gui/mage.png"));
        
        this.mapController = mapController;
        setSelectedTile(0, 0);
        mapController.addMapObserver(this);
        mapController.startMap();
	}

	private boolean drawn = false;
	// draws the map to the screen
	
	int imgWidth = MapSettings.tileDiagonal*20+5,  imgHeight = MapSettings.tileDiagonal/2*20 +23;
	
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		
		Graphics g = img.getGraphics();
		//TODO rotates works!
		
		int startX = imgWidth/2;
		int startY = 21;
		
		if (!drawn) {
			g.fillRect(0, 0, imgWidth, imgHeight);
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

						boolean drawLeft = true, drawRight = true;
						//					Casues flickering 
						//					if (i > 0 && field[j][i-1].getHeight() >= field[j][i].getHeight()){
						////						System.out.printf("(%d,%d) not drawn left side not drawn\n", j,i);
						//						drawLeft = false;
						//					}
						//
						//					if (j+1 < fieldWidth && field[j+1][i].getStartHeight() >= field[j][i].getEndHeight()){
						////						System.out.printf("(%d,%d) not drawn right side not drawn\n", j,i);
						//						drawRight = false;
						//					}

						//					if (tilesInvaild){
						//						field[j][i].invaildate();
						//					}
						field[j][i].draw(x, y, g, drawLeft, drawRight);

						AnimatedUnit au = tileMapping.get(field[j][i]);
						if (au != null) {
							au.draw(g, field, x, y, timeDiff);
						}

						if (showNumbering) {
							Color old = g.getColor();
							g.setColor(Color.RED);
							g.drawString(
									String.format("(%d,%d) %d", j, i, field[j][i].getCost()),
									(int) (x - (MapSettings.tileDiagonal * MapSettings.zoom) / 2 + 20),
									y + 10);
							g.setColor(old);
						}

//					}

					x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
					y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
				}
				x = startX- (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
				y = startY+ (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch* MapSettings.zoom * (fieldHeight - i));
				//			x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (i+1)); // for rotate
				//			y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (i+1)); // for rotate
			}
			drawn = true;
		}


//		setSelectedTile(5, 5);
		Point drawLocation = getDrawLocation(startX, startY, 5, 5);
//		field[5][5].draw(drawLocation.x, drawLocation.y, g, true,true);
		
		_g.drawImage(img,0, 0, width, height, drawX, drawY, drawX+width, drawY+height, null);
		Color oldColor = _g.getColor();
		_g.setColor(MapTile.TileState.SELECTED.colour);
		Polygon p = field[5][5].getpoly();
		p.translate(drawX, drawY);
//		System.out.println(Arrays.toString(p.xpoints));
//		System.out.println(Arrays.toString(p.ypoints));
		_g.fillPolygon(p);
		_g.setColor(oldColor);
		
//		for (AnimatedUnit au : units) {
//			Point drawLocation = getDrawLocation(startX, startY, au.getGridX(), au.getGridY());
//			au.draw(g, field, drawLocation.x, drawLocation.y, timeDiff);
//		}

//		Point drawLocation = getDrawLocation(startX, startY, 3, 5);
//		field[3][5].draw(drawLocation.x, drawLocation.y, g, true,true);
		
//		System.out.printf("(%s,%s,%s,%s) (%s.%s,%s,%s)\n", 
//				0, 0, width, height, drawX, drawY, drawX+width, drawY+height);
//		_g.drawImage(img,0, 0, width, height, drawX, drawY, drawX+width, drawY+height, null);

//		Point p = getDrawLocation(startX, startY, 2, 5);
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
		
		
		if (showDialog) dialog.draw((Graphics2D) _g, 5, height - dialog.getHeight() - 5);
		tilesInvaild = false;
	}

	Point getDrawLocation(int startX, int startY, int gridX, int gridY){
		int x = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - gridY-1f));
		int y = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - gridY-1));
		x += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * gridX;
		y += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom)* gridX;
		return new Point(x,y);
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
			MapTile t = getSelectedTile();
			
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
		}
		
	}
	
	/**
     * Select the file that is under the mouse click
     * @param x X position of the mouse click
     * @param y Y position of the mouse click
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
    
	public void setSelectedTile(int x, int y) {
        if (x < 0  || y < 0  || x >= fieldWidth || y >= fieldHeight ) {
        	throw new IllegalArgumentException("Bad index " +x +  "," +y);
        }
        
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }
        
        selectedTile = field[x][y];
        selectedTile.setSelected(true);
        
        System.out.printf("(%d,%d) selected\n", y, y);
        
    }

    /** @category Generated */
	public MapTile getSelectedTile() {
		return selectedTile;
	}
    
	boolean tilesInvaild = false; 
    /** Setter **/
    public void setDrawLocation(int x, int y) {
        drawX = x;
        drawY = y;
        tilesInvaild  = true;
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

	/** @category Generated Setter */
	public void setImg(Image img) {
		this.img = img;
	}
	
}
