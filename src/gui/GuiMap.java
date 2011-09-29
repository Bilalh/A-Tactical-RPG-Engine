/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import engine.Map;
import engine.Tile;
import engine.Unit;
import engine.notifications.ChooseUnitsNotifications;


/**
 * @author bilalh
 */
public class GuiMap implements MouseListener, MouseMotionListener, Observer {

    private int drawX;
    private int drawY;
    private static MapTile selectedTile;
    private MapTile[][] field;
    private Point mouseStart, mouseEnd;
    private int offsetX, offsetY;
    
    private int fieldWidth, fieldHeight;
    
    private AnimatedUnit[] units;
    private AnimatedUnit[] aiUnits;

    private Map map;
    
	/** @category old Constructor */
	public GuiMap(int width, int height) {
		this.fieldWidth = width;
		this.fieldHeight = height;
		
        field = new MapTile[width][height];
        
        for (int i = 0; i < width; i++) { 
            for (int j = 0; j < height; j++) {
            	field[i][j] = new MapTile(MapTile.Orientation.NORMAL, 1.0f, 1.0f);
            	field[i][j].setFieldLocation(new Point(i, j));
            }
        }
        
        drawX = (int) MapSettings.drawStart.getX();
        drawY = (int) MapSettings.drawStart.getY();
        selectedTile = null;
        
        units = new AnimatedUnit[2];
        for (int i = 0; i < units.length; i++) { 
        	units[i] = new AnimatedUnit("", 0,i);
        }
        
	}

    
	/** @category Constructor */
	public GuiMap(Map map) {
		
		final Tile grid[][] = map.getField();
		this.fieldWidth = grid.length;
		this.fieldHeight = grid[0].length;
        field = new MapTile[fieldWidth][fieldHeight];
		
        //FIXME 
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	field[i][j] = new MapTile(MapTile.Orientation.NORMAL, 1.0f, 1.0f);
            	field[i][j].setFieldLocation(new Point(i, j));
            }
        }
        
        drawX = (int) MapSettings.drawStart.getX();
        drawY = (int) MapSettings.drawStart.getY();
        selectedTile = null;
        
        this.map = map;
        map.addObserver(this);
        map.start();
	}
	
	
	@Override
	public void update(Observable map, Object notification) {
		
		if (notification instanceof ChooseUnitsNotifications){
			chooseUnits(((ChooseUnitsNotifications) notification).getUnits(), 
					((ChooseUnitsNotifications) notification).getAiUnits());
		}
		
	}

	
	private void chooseUnits(ArrayList<Unit> allUnits, ArrayList<Unit> aiUnits) {
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allUnits.size()];
		ArrayList<Unit> unitsList = new ArrayList<Unit>();
		for (int i = 0; i < newUnits.length; i++) {
			//FIXME indies
			allUnits.get(i).setGridX(0);
			allUnits.get(i).setGridY(i);
			newUnits[i] = new AnimatedUnit("assets/gui/alien.gif", 0, i);
			unitsList.add(allUnits.get(i));
		}
		map.setUsersUnits(unitsList);
		this.units = newUnits;
		
		AnimatedUnit[] newAiUnits = new AnimatedUnit[aiUnits.size()];
		for (int i = 0; i < newAiUnits.length; i++) {
			//FIXME indies
			aiUnits.get(i).setGridX(fieldWidth-1);
			aiUnits.get(i).setGridY(i);
			newAiUnits[i] = new AnimatedUnit("assets/gui/alien.gif", fieldWidth-1, i);
		}
		this.aiUnits = newAiUnits;
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
//        e.getComponent().repaint();
    }

    @Override
	public void mouseDragged(MouseEvent e) {
        Point current = e.getPoint();
        this.setDrawLocation(e.getX() - offsetX, e.getY() - offsetY);
//        e.getComponent().repaint();
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
        if (x == -1 && y == -1) {
            selectedTile = null;
            return;
        }
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }
        selectedTile = field[x][y];
        selectedTile.setSelected(true);
    }

    /** @category Generated Getter */
	public static MapTile getSelectedTile() {
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

}
