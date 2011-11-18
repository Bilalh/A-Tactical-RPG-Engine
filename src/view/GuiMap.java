/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
    
    private MousePoxy mousePoxy;
    
    final private ActionsAdapter[] actions = {new Movement(), new DialogHandler()};
    enum ActionsEnum {
    	MOVEMENT, DIALOG,
    }
    
	/** @category Constructor */
	public GuiMap(MapController mapController) {
		assert actions.length == ActionsEnum.values().length;
		
		final Tile grid[][] = mapController.getGrid();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
        field = new MapTile[fieldWidth][fieldHeight];
        current = getActionHandler(ActionsEnum.MOVEMENT);
        mousePoxy = new MousePoxy();
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
        selectedTile = null;
        
        dialog = new Dialog(665, 70, "mage", SpriteManager.instance().getSprite("assets/gui/mage.png"));
        
        this.mapController = mapController;
        setSelectedTile(0, 0);
        mapController.addMapObserver(this);
        mapController.startMap();
	}
		
	// draws the map to the screen 
	public void draw(Graphics g, long timeDiff, int width, int height) {
		int x = drawX;
		int y = drawY;

		// Draw map
		for (int i = fieldHeight - 1; i >= 0; i--) {
			for (int j = 0; j < fieldWidth; j++) {
				int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
				int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
				if (x - horizontal  <= width 
						&& y -vertical  <= height 
						&& x + horizontal >= 0 
						&& y + vertical>= 0 ){
					field[j][i].draw(x, y, g);
				}else{
					//            		System.out.printf("(%d, %d) at (%d, %d) not drawn dim:(%d, %d)  \n",j,i, x,y, width, height);
				}

				Color old = g.getColor();
				g.setColor(Color.RED);
				g.drawString(String.format("(%d,%d) %d", j,i,field[j][i].getCost() ), (int) (x-(MapSettings.tileDiagonal * MapSettings.zoom)/2 +20), y +10);
				g.setColor(old);
				//        		        		
				x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
				y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
			}
			x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
			y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (fieldHeight - i));
		}


		// Draw Units
		if (units != null){
			for (int i = 0; i < units.length; i++) {
				int xPos =drawX, yPos = drawY;

				// height
				xPos -= (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * ((fieldHeight - 1 -  units[i].gridY) ));
				yPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * ((fieldHeight - 1 -  units[i].gridY)) );

				// width
				xPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom) * (units[i].gridX);
				yPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom) * (units[i].gridX);				

				units[i].draw(g,field,  xPos, yPos, timeDiff);
			}
		}

		if (aiUnits != null){
			for (int i = 0; i < aiUnits.length; i++) {
				int xPos =drawX, yPos = drawY;

				// height
				xPos -= (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * ((fieldHeight - 1 -  aiUnits[i].gridY) ));
				yPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * ((fieldHeight - 1 -  aiUnits[i].gridY)) );

				// width
				xPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom) * (aiUnits[i].gridX);
				yPos += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom) * (aiUnits[i].gridX);				

				aiUnits[i].draw(g,field,  xPos, yPos, timeDiff);
			}
		}

		if (showDialog) dialog.draw((Graphics2D) g, 5, height - dialog.getHeight() - 5);
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
		}
		mapController.setUsersUnits(selectedPostions);
		this.units = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			final IUnit u = aiUnits.get(i);
			newAiUnits[i] = new AnimatedUnit(u.getGridX(), u.getGridY(), 
					new String[]{"assets/gui/alien.gif", "assets/gui/alien2.gif", "assets/gui/alien3.gif"}, 
					u);
		}
		this.aiUnits = newAiUnits;
	}
	
	public void unitMoved(IUnit u){
		units[0].setGridX(u.getGridX());
		units[0].setGridY(u.getGridY());
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
		
		boolean b = false;
		
		Collection<Point> inRange; 
		
		@Override
		public void keyComfirm() {
			if (b){
				getSelectedTile().isInRange();
				mapController.moveUnit(units[0].unit.getUuid(), getSelectedTile().getFieldLocation());
				b = false;
				for (Point p : inRange) {
					field[p.x][p.y].setInRange(false);
				}
				inRange = null;
			}
			
			inRange =  mapController.getMovementRange(units[0].unit.getUuid());
			for (Point p : inRange) {
				field[p.x][p.y].setInRange(true);
				System.out.println(p);
			}
			b = true;
		}
		
		@Override
		public void keyCancel() {
			// TODO keyCancel method

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
						"heights in Provence, his physical and mental health plummeted. ");
				showDialog = true;
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
        
    }

    /** @category Generated */
	public MapTile getSelectedTile() {
		return selectedTile;
	}
    
    /** Setter **/
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
		return mousePoxy;
	}
	
	public MouseMotionListener getMouseMotionListener(){
		return mousePoxy;
	}
	
	private ActionsAdapter getActionHandler(ActionsEnum num){
		return actions[num.ordinal()];
	}
	
	private void setActionHandler(ActionsEnum num){
		final ActionsAdapter aa = actions[num.ordinal()];
		current = aa;
		mousePoxy.setMouseListener(aa);
		mousePoxy.setMouseMotionListener(aa);
	}
	
}
