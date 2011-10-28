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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import view.interfaces.IActions;
import view.notifications.ChooseUnitsNotifications;
import view.ui.Dialog;

import common.gui.SpriteManager;
import common.interfaces.IMapNotification;
import common.interfaces.INotification;

import engine.Map;
import engine.Tile;
import engine.Unit;


/**
 * @author bilalh
 */
public class GuiMap implements MouseListener, MouseMotionListener, Observer, IActions {

    private int drawX;
    private int drawY;
    private static MapTile selectedTile;
    private MapTile[][] field;
    private Point mouseStart, mouseEnd;
    private int offsetX, offsetY;
    
    private int fieldWidth, fieldHeight;
    
    private AnimatedUnit[] units;
    private AnimatedUnit[] aiUnits;

    private Map map; // Model
    
    private Dialog dialog;
    private boolean showDialog = true;
      
	/** @category Constructor */
	public GuiMap(Map map) {
		
		final Tile grid[][] = map.getField();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
        field = new MapTile[fieldWidth][fieldHeight];
		
        //FIXME 
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	field[i][j] = new MapTile(MapTile.Orientation.UP_TO_EAST,
            			grid[i][j].getStartHeight(),
            			grid[i][j].getEndHeight());
            	field[i][j].setFieldLocation(new Point(i, j));
            }
        }
        
        drawX = (int) MapSettings.drawStart.getX();
        drawY = (int) MapSettings.drawStart.getY();
        selectedTile = null;
        
        dialog = new Dialog(665, 70, "mage", SpriteManager.instance().getSprite("assets/gui/mage.png"));
//        text = new BText(665, 100);
        
        this.map = map;
        setSelectedTile(0, 0);
        map.addObserver(this);
        map.start();
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

				//System.out.printf("cell (%d, %d)\n", j,i);
				Color old = g.getColor();
				g.setColor(Color.RED);
				g.drawString(String.format("(%d,%d)", j,i), (int) (x-(MapSettings.tileDiagonal * MapSettings.zoom)/2 +20), y +10);
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

	
	public void chooseUnits(ArrayList<Unit> allPlayerUnits, ArrayList<Unit> aiUnits) {
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		ArrayList<Unit> unitsList = new ArrayList<Unit>();
		for (int i = 0; i < newUnits.length; i++) {
			//FIXME indies
			allPlayerUnits.get(i).setGridX(0);
			allPlayerUnits.get(i).setGridY(i);
			newUnits[i] = new AnimatedUnit(0, i, new String[]{"assets/gui/Archer.png"});
			unitsList.add(allPlayerUnits.get(i));
		}
		map.setUsersUnits(unitsList);
		this.units = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			//FIXME indies
			aiUnits.get(i).setGridX(fieldWidth-1);
			aiUnits.get(i).setGridY(i);
			newAiUnits[i] = new AnimatedUnit(fieldWidth-1, i, 
					new String[]{"assets/gui/alien.gif", "assets/gui/alien2.gif", "assets/gui/alien3.gif"});
		}
		this.aiUnits = newAiUnits;
	}
	
	public void unitMoved(Unit u){
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
	}
	
	@Override
	public void keyComfirm() {
		map.moveUnit(null, 2, 3);
//		if (!dialog.nextPage()){
//			showDialog = false;
//		}
	}

	@Override
	public void keyCancel() {
		// TODO keyCancel method
		
	}

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
	
	public void otherKeys(KeyEvent e){
		switch (e.getKeyCode()) {
			case KeyEvent.VK_T:
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
	
/* * Drawing and selecting * */
	
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
//            this.setDrawLocation(mouseEnd);
        } else {
            this.findAndSelectTile(e.getX(), e.getY());
        }
    }

    @Override
	public void mouseDragged(MouseEvent e) {
        Point current = e.getPoint();
        this.setDrawLocation(e.getX() - offsetX, e.getY() - offsetY);
    }

    /** @category Generated */ @Override
	public void mouseMoved(MouseEvent e) {}
    /** @category Generated */ @Override
	public void mouseClicked(MouseEvent e) {}
    /** @category Generated */ @Override
	public void mouseEntered(MouseEvent e) {}
    /** @category Generated */ @Override
	public void mouseExited(MouseEvent e) {}

   
	public void setSelectedTile(int x, int y) {
        if (x < 0  || y < 0  || x >= fieldWidth || y >= fieldHeight ) {
        	throw new IllegalArgumentException("Bad index " +x +  "," +y);
//            selectedTile = null;
//            return;
        }
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }
        selectedTile = field[x][y];
        selectedTile.setSelected(true);
        
    }

    /** @category Generated Getter */
	public MapTile getSelectedTile() {
		return selectedTile;
	}
    
    /** Setter **/
    public void setDrawLocation(int x, int y) {
        drawX = x;
        drawY = y;
    }


	/** @category Generated Getter */
	public MapTile[][] getField() {
		return field;
	}


	/** @category Generated Getter */
	public boolean isShowDialog() {
		return showDialog;
	}


	/** @category Generated Setter */
	public void setShowDialog(boolean showDialog) {
		this.showDialog = showDialog;
	}
	
}
