/**
 * 
 */
package view;

import static view.MapTile.Orientation.UP_TO_EAST;
import static view.MapTile.TileState;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubtractDescriptor;

import com.sun.org.apache.bcel.internal.generic.NEW;

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

import engine.map.IModelUnit;
import engine.map.Map;
import engine.map.Tile;
import engine.map.Unit;

import common.ILocation;
import common.Location;

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
	private boolean showNumbering = false;
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
		
        //FIXME heights?
        for (int i = 0; i < fieldWidth; i++) { 
            for (int j = 0; j < fieldHeight; j++) {
            	field[i][j] = new MapTile(MapTile.Orientation.UP_TO_EAST,
            			grid[i][j].getStartHeight(),
            			grid[i][j].getEndHeight(), i, j);
            	field[i][j].setCost(grid[i][j].getCost());
            }
        }
        
        int heightOffset = (MapSettings.tileDiagonal);
        bufferWidth  =  MapSettings.tileDiagonal*fieldWidth +5;  
		bufferHeight = (int) (MapSettings.tileDiagonal/2f*fieldHeight +heightOffset);
		
        drawX = 0;
        drawY = bufferHeight/2 - Gui.HEIGHT/2;

        startX = bufferWidth/2;
        startY = heightOffset;
        
        unitMapping = new HashMap<UUID, AnimatedUnit>();
        tileMapping = new HashMap<MapTile, AnimatedUnit>();
        
        dialog = new Dialog(665, 70, "mage", SpriteManager.instance().getSprite("assets/gui/mage.png"));
        
        selectedTile = field[0][0];
        selectedTile.setSelected(true);
        
        mapController.addMapObserver(this);
        mapController.startMap();
        
        toMove.add(field[2][5]);
//        toMove.add(field[3][5]);
//        toMove.add(field[4][5]);
//        toMove.add(field[4][6]);
//        toMove.add(field[5][6]);
//        toMove.add(field[6][6]);
//        toMove.add(field[6][7]);
//        toMove.add(field[6][8]);

	}

	void makeImageBuffer(Component parent){
		mapBuffer = parent.createImage(bufferWidth,bufferHeight);
	}

	// draws the map to the screen
	private boolean drawn = false;
//	int frameDuration = 120 *1000000;
//	int lastFrameChange = 0;
	
	int animationDuration = 750 *1000000;
	int animationFrameChange = 0;
	
	Queue<MapTile> toMove = new ArrayDeque<MapTile>();
	
	long total = 0;
	public void draw(Graphics _g, long timeDiff, int width, int height) {
		
//		if (mouseMoving) System.out.print("!" + (drawn ? 1 : 0)+ "!");
		Graphics g = mapBuffer.getGraphics();
		//TODO rotates workss!
		if (!drawn) {
//			System.out.println("%%%drawn is " + drawn + " mouse is " + mouseMoving);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, bufferWidth, bufferHeight);
			int x = startX;
			int y = startY;
			final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
			final int vertical = (int) (MapSettings.tileDiagonal * MapSettings.pitch * MapSettings.zoom);
//			System.out.println("___drawn is " + drawn + " mouse is " + mouseMoving);
			
			boolean drawnEverything = true;
			boolean tex = false;
			for (int i = fieldHeight - 1; i >= 0; i--) {
	//		for (int i = 0 ; i < fieldHeight; i++) { //  for rotate
				tex = !tex;
				for (int j = 0; j < fieldWidth; j++) {
//				for (int j = fieldWidth - 1; j >= 0; j--) { // for rotate
					tex = !tex;
					if (mouseMoving ||    
							(x - horizontal   - drawX <= Gui.WIDTH+MapSettings.tileDiagonal*2
							&& y - vertical   - drawY <= Gui.HEIGHT+MapSettings.tileDiagonal*2
							&& x + horizontal - drawX >= -MapSettings.tileDiagonal*2
							&& y + vertical   - drawY >= -MapSettings.tileDiagonal*2)) {
						field[j][i].textured = tex;
						field[j][i].draw(x, y, g, true,true);
						
						if (showNumbering) {
							Color old = g.getColor();
							g.setColor(Color.RED);
							g.drawString(String.format("(%d,%d) %d", j, i, field[j][i].getCost()),
									(int) (x - (MapSettings.tileDiagonal * MapSettings.zoom) / 2 + 20),
									y + 10);
							g.setColor(old);
						}
						
						AnimatedUnit u =  tileMapping.get(field[j][i]);
						if (u != null){
							u.draw(g, field, x, y, animationDuration);
						}
					}else{
						drawnEverything = false;
					}
					x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
					y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
				}
				x = startX- (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
				y = startY+ (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch* MapSettings.zoom * (fieldHeight - i));
				//			x = drawX - (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (i+1)); // for rotate
				//			y = drawY + (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom * (i+1)); // for rotate
			}
			drawn = (mouseMoving && drawnEverything) || !mouseMoving;
//			System.out.println("@@@DRAWN IS "  + drawn +  " + MOUSE IS " + mouseMoving + " Everything is " + drawnEverything);
		}
		
//		Animated movement by redrawing partical steps.
//		lastFrameChange += timeDiff;
//		if (lastFrameChange > frameDuration){
//			if (toMove.size() >=2){
//				AnimatedUnit u =  tileMapping.remove(toMove.remove());
//				tileMapping.put(toMove.peek(),u);
//				drawn=false;
//			}
//			lastFrameChange=0;
//		}
		
		 
		if(!mouseMoving){
			animationFrameChange += timeDiff;
			if (animationFrameChange > animationDuration){
				total += animationFrameChange;
//				System.out.printf("NEXT FRAME %.5f ::: %.3f\n", animationFrameChange * .000001, total * .000000001);
				animationFrameChange=0;
				drawn=false;
//				if (toMove.size() >=2){
//					AnimatedUnit u =  tileMapping.remove(toMove.remove());
//					tileMapping.put(toMove.peek(),u);
//					drawn=false;
//				}
			}	
		}
		
		_g.drawImage(mapBuffer,0, 0, width, height, drawX, drawY, drawX+width, drawY+height, null);
		
//		if (inRange != null){
//			for (Point p :inRange) {
//				overlayTile(_g, field[p.x][p.y],Color.BLUE,10);
//				Color c = new Color((int) Math.random(), (int)Math.random(), (int)Math.random());
//			}	
//		}
		
//		overlayTile(_g,selectedTile,Color.ORANGE,10);
//		drawUnits(units,_g,timeDiff);
//		drawUnits(aiUnits,_g,timeDiff);

		if (showDialog) dialog.draw((Graphics2D) _g, 5, height - dialog.getHeight() - 5);
	}
	
	/**
	 * Draw only Viewable tiles
	 * @category Unused
	 */
	void drawViewable(Graphics g){
//		g.fillRect(0, 0, bufferWidth, bufferHeight);

		int x = startX;
		int y = startY;
		final int horizontal = (int) (MapSettings.tileDiagonal * MapSettings.zoom);
		final int vertical = (int) (MapSettings.tileDiagonal/2 * MapSettings.pitch * MapSettings.zoom);
		
		for (int i = fieldHeight - 1; i >= 0; i--) {
			for (int j = 0; j < fieldWidth; j++) {

				// Only draw the the tiles if they in the viewport 
				if (    x - horizontal  - drawX  <= Gui.WIDTH
						&& y - vertical  - drawY <= Gui.HEIGHT
						&& x + horizontal - drawX >= 0
						&& y + vertical   -drawY >= 0) {
					field[j][i].draw(x, y, g, true,true);
				}
				
				x += (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom);
				y += (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch * MapSettings.zoom);
			}
			x = startX- (int) (MapSettings.tileDiagonal / 2 * MapSettings.zoom * (fieldHeight - i));
			y = startY+ (int) (MapSettings.tileDiagonal / 2 * MapSettings.pitch* MapSettings.zoom * (fieldHeight - i));
		}
	}
	
	void mapToWorld(Location p){
		p.translate(-drawX, -drawY);
	}
	
	/**
	 *  Overlay units on the map.
	 *  @category unused
	 */
	void drawUnits(AnimatedUnit[] units, Graphics g, long timeDiff){
		Graphics2D g2 = (Graphics2D) g;
		for (AnimatedUnit au : units) {
			Location l = au.getPostion();
			Location p = getDrawLocation(startX, startY, au.getGridX(), au.getGridY());
			mapToWorld(p);
			
			float height  = field[l.x][l.y].getHeight();
			Rectangle r = new Rectangle(au.topLeftPoint(field, p.x, p.y));
			r.width = au.getWidth();
			r.height = au.getHeight();
		    Area resultingArea = new Area(r);

			au.draw(g2, field, p.x, p.y, timeDiff);
		    
		    MapTile m;
		    if (l.x+1 < fieldWidth && (m =  field[l.x+1][l.y]).getHeight() > height ){
		    	makePolygons(m);
			    Area a;
			    
			    a= new Area(m.left);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);

			    a= new Area(m.right);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.top);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tGrass);
			    g2.fill(a);
		    }

		    if (l.y -1 >= 0 && (m =  field[l.x][l.y-1]).getHeight() > height ){	    	
		    	makePolygons(m);
			    Area a;
			    a= new Area(m.left);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.right);
			    a.intersect(resultingArea);
			    
			    MapTile mm =  field[l.x-1][l.y-2];
			    makePolygons(mm);
			    Area aa = new Area(mm.top);
			    a.subtract(aa);
			    
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.top);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tGrass);
			    g2.fill(a);			    
			    
		    }
		    
		    if (l.x +1 < fieldWidth && l.y - 1 >= 0 &&  (m =  field[l.x+1][l.y-1]).getHeight() > height){	    	
		    	makePolygons(m);
		    	Area a;
			    a= new Area(m.left);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.right);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.top);
			    a.intersect(resultingArea);
			    
//			    MapTile mm =  field[l.x+2][l.y-1];
//			    makePolygons(mm);
//			    a.subtract(new Area(mm.left));
//			    a.subtract(new Area(mm.top));

			    g2.setPaint(m.tGrass);
			    g2.fill(a);
			    
		    }
		    
		    if (l.x -1  >=0 && l.y - 1 >= 0 &&  (m =  field[l.x-1][l.y-1]).getHeight() > height){	    	
		    	makePolygons(m);
		    	Area a;
			    a= new Area(m.left);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.right);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tWall);
			    g2.fill(a);
			    
			    a= new Area(m.top);
			    a.intersect(resultingArea);
			    g2.setPaint(m.tGrass);
			    g2.fill(a);
			    
		    }
		    
		}		
	}

	/**
	 * @category unused
	 */
	private  void overlayTile(Graphics g, MapTile t, Paint paint, int limit){
		Graphics2D g2 = (Graphics2D) g;
		
		Location l  = t.getFieldLocation();
		makePolygons(t);
	    Area resultingArea = new Area(t.top);
	    
	    MapTile m;
	    if (l.x+1 < fieldWidth && (m =  field[l.x+1][l.y]).getHeight() > t.getHeight() ){
	    	makePolygons(m);
		    resultingArea.subtract(new Area(m.top));
		    resultingArea.subtract(new Area(m.left));
	    }

	    if (l.y -1 > 0 && (m =  field[l.x][l.y-1]).getHeight() > t.getHeight() ){	    	
	    	makePolygons(m);
		    resultingArea.subtract(new Area(m.top));
		    resultingArea.subtract(new Area(m.right));
	    }
	    
	    if (l.x +1 < fieldWidth && l.y - 1 > 0 &&  (m =  field[l.x+1][l.y-1]).getHeight() > t.getHeight() 
	    		&& m.getHeight() - t.getHeight()  <=limit ){	    	
		    makePolygons(m);
		    resultingArea.subtract(new Area(m.top));
		    resultingArea.subtract(new Area(m.right));
	    }
	   
	    Paint old = g2.getPaint();
	    g2.setPaint(paint);
	    g2.draw(resultingArea);
		g2.setPaint(old);
	    
	}
	
	
	private void makePolygons(MapTile m){
	    Location p = getDrawLocation(startX, startY, m.getFieldLocation().x, m.getFieldLocation().y);
	    p.x -= drawX;
	    p.y -= drawY;
	    m.makePolygons(p.x, p.y);
	}
	
	Location getDrawLocation(int startX, int startY, int gridX, int gridY){
		int x = startX- (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom * (fieldHeight - gridY-1f));
		int y = startY+ (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch* MapSettings.zoom * (fieldHeight - gridY-1));
		x += (int) (MapSettings.tileDiagonal / 2f * MapSettings.zoom) * gridX;
		y += (int) (MapSettings.tileDiagonal / 2f * MapSettings.pitch * MapSettings.zoom)* gridX;
		return new Location(x,y);
	}

	
	@Override
	public void update(Observable map, Object notification) {
		Gui.console().println(notification);
		((IMapNotification) notification).process(this);
	}

	
	public void chooseUnits(ArrayList<? extends IUnit> allPlayerUnits, ArrayList<? extends IUnit> aiUnits) {
		
		AnimatedUnit[] newUnits = new AnimatedUnit[allPlayerUnits.size()];
		HashMap<UUID, Location> selectedPostions = new HashMap<UUID, Location>();
		for (int i = 0; i < newUnits.length; i++) {
//			//FIXME indies
				final IUnit u = allPlayerUnits.get(i);
				Location p = new Location(2,i+5); 
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
		System.out.println("Unit Moved");
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

	
	
	private Collection<Location> inRange = null;
	private boolean mouseMoving = false;
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
		private void selectMoveUnit() {
			if (selected != null){
				System.out.println("selected " + selected);
				if ( !getSelectedTile().isSelected() ) return;
				mapController.moveUnit(selected.unit.getUuid(), getSelectedTile().getFieldLocation());
				for (Location p : inRange) {
					field[p.x][p.y].setState(TileState.NONE);
				}
				selected = null;
				inRange = null;
				System.out.println("Selected unit move finished");
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
					for (Location p : inRange) {
						field[p.x][p.y].setState(TileState.NONE);
					}	
				}
				inRange = null;
				selected = unitS;
			}
			
			inRange =  mapController.getMovementRange(unitS.unit.getUuid());
			for (Location p : inRange) {
				field[p.x][p.y].setState(TileState.MOVEMENT_RANGE);
			}
		}
		
		@Override
		public void keyCancel() {
			selected = null;
			if (inRange != null){
				for (Location p : inRange) {
					field[p.x][p.y].setState(TileState.NONE);
				}
				inRange = null;
			}
			
		}
		
	    @Override
		public void mousePressed(MouseEvent e) {
	    	System.out.println("\nMousePressed");
	        mouseStart = e.getPoint();
	        offsetX = e.getX() - drawX;
	        offsetY = e.getY() - drawY;
	        System.out.printf("MousePressed MouseMoving:%s drawn:%s\n", mouseMoving,drawn);
	    }

	    @Override
		public void mouseReleased(MouseEvent e) {
	    	mouseMoving = false;
	    	System.out.println("MousrReleased");
	        mouseEnd = e.getPoint();
	        int a = Math.abs((int) (mouseEnd.getX() - mouseStart.getX()));
	        int b = Math.abs((int) (mouseEnd.getY() - mouseStart.getY()));
	        if (Math.sqrt(a * a + b * b) > 3) {
	            
	        } else {
	            findAndSelectTile(e.getX(), e.getY());
	            selectMoveUnit();
	        }
	        System.out.printf("MouseReleased MouseMoving:%s drawn:%s\n", mouseMoving,drawn);
	    }

	    @Override
		public void mouseDragged(MouseEvent e) {
	    	
	    	if (!mouseMoving){
	    		mouseMoving =true;
	    		System.out.println("mouseDragged ");
		    	drawn = false;	
	    	}
	    	
	        Point current = e.getPoint();
	        setDrawLocation(e.getX() - offsetX, e.getY() - offsetY);
//	        System.out.print((drawn ? "T" : "F") +  (mouseMoving ? ":" : "@"));
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
				if ( MapSettings.zoom <=0.6) break;
				MapSettings.zoom -= 0.2;
//				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				System.out.println(MapSettings.zoom * MapSettings.tileDiagonal);
				if ((MapSettings.zoom * MapSettings.tileDiagonal) % 2 !=0){
//					System.out.println("Odd");
				}
				
				System.out.println(MapSettings.zoom);
				drawn=false;
				break;
			case KeyEvent.VK_EQUALS:
				if ( MapSettings.zoom >1.2) break;
				MapSettings.zoom += 0.2;
//				MapSettings.zoom =  Math.round(MapSettings.zoom*10f)/10f;
				System.out.println(MapSettings.zoom * MapSettings.tileDiagonal);
				System.out.println(MapSettings.zoom);
				drawn=false;
				break;
			case KeyEvent.VK_COMMA:
				if ( MapSettings.pitch <0.6) break;
				MapSettings.pitch *= 0.8;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				System.out.println(MapSettings.pitch);
				drawn=false;
				break;
			case KeyEvent.VK_PERIOD:
				if ( MapSettings.pitch >0.8) break;
				MapSettings.pitch *= 1.2;
				MapSettings.pitch =  Math.round(MapSettings.pitch*10f)/10f;
				System.out.println(MapSettings.pitch);
				drawn=false;
				break;
			case KeyEvent.VK_U:
				Location newP = new Location(units[0].getGridX()+1, units[0].getGridY());
				mapController.moveUnit(units[0].getUuid(), newP);
				break;
			case KeyEvent.VK_I:
				System.out.printf("draw (%d,%d) selected %s\n", drawX, drawY, selectedTile);
				break;
		}
		
	}
	
	/**
     * Select the file that is under the mouse click
	 */
    public ILocation findAndSelectTile(int x, int y) {
        double highest = 0.0;
        x += drawX;
        y += drawY;
        int xIndex = -1, yIndex = -1;
        
//        System.out.printf("p:(%d,%d)\n", x, y);
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                if (field[i][j].wasClickedOn(new Point(x, y))) {
//                	System.out.printf("Clicked(%d,%d)\n", i, j);
                	
                	if (field[i][j].getHeight() > highest){
//                		System.out.println("\t highest");
                        highest = field[i][j].getHeight();
                        xIndex = i;
                        yIndex = j;	
                	}
                    
                }
            }
        }
        if (xIndex > -1 && yIndex > -1) {
            this.setSelectedTile(xIndex, yIndex);
//            System.out.printf("(%d,%d)\n\n", xIndex, yIndex);
            return new Location(xIndex, yIndex);
        } else {
//        	System.out.printf("(%d,%d)\n\n", xIndex, yIndex);
            return null;
        }
    }
    
	public void setSelectedTile(int x, int y) {
        assert !(x < 0  || y < 0  || x >= fieldWidth || y >= fieldHeight); 
        
        Graphics g =mapBuffer.getGraphics();
        if (selectedTile != null) {
            selectedTile.setSelected(false);
        }
        
        selectedTile = field[x][y];
        selectedTile.setSelected(true);
        
		ILocation selected = getDrawLocation(startX, startY, x, y);
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
