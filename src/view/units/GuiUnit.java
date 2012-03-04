package view.units;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import org.apache.log4j.Logger;

import util.Logf;
import view.map.IsoTile;

import common.Location;
import common.enums.Direction;
import common.gui.ResourceManager;
import common.interfaces.ILocation;
import common.interfaces.IMapUnit;
import common.interfaces.IUnit;
import common.spritesheet.SpriteSheet;

import config.Config;
import engine.unit.SpriteSheetData;

/**
 * @author bilalh
 */
public class GuiUnit {
	private static final Logger log = Logger.getLogger(GuiUnit.class);
	
	protected IMapUnit unit;
	protected int gridX;
	protected int gridY;
	protected Direction direction;
	
	protected SpriteSheet spriteSheet;
	protected SpriteSheetData images;
	
	protected BufferedImage sprite;
	protected BufferedImage weaponSprite;
	
	protected Rectangle2D bounds;
		
	// For drawing the damage when attacked.
	protected static Font numbers = new Font("Helvetica", Font.BOLD,  15);
	protected int damage = 0;
	boolean showDamage   = false;
	
	public GuiUnit(int gridX,int gridY, IUnit u) {
		this.gridX = gridX;
		this.gridY = gridY;
		assert u != null;
		
		images = u.getImageData(); 
		assert images != null;
		Logf.info(log,"Using sheet %s", images.getSpriteSheetLocation());
		spriteSheet = makeSpriteSheet();
		
		weaponSprite = makeWeaponImage(u.getWeapon().getImageRef());
		assert weaponSprite != null;	
		
		setDirection(Direction.EAST);
		assert direction != null : "Direction Should not be null";
	} 
	
	protected SpriteSheet makeSpriteSheet(){
		return Config.loadSpriteSheet(images.getSpriteSheetLocation());
	}
	
	protected BufferedImage makeWeaponImage(String ref){
		 return ResourceManager.instance().getItem(ref);
	}
	
	public void draw(Graphics g, final IsoTile[][] tiles, int drawX, int drawY) {
		final Point centrePoint =  tiles[gridX][gridY].calculateCentrePoint(drawX,drawY);
		int xPos =centrePoint.x - sprite.getWidth()/2;
		int yPos =(int) (centrePoint.y -  sprite.getHeight()/1.3);
		g.drawImage(sprite,xPos,yPos,null);
		
		if (showDamage) drawDamage(g, xPos, yPos);
		
		//FIXME check?
//		bounds  = new Rectangle2D.Float(xPos,yPos+getHeight(),getWidth(),getHeight());
		bounds  = new Rectangle2D.Float(xPos,yPos,getWidth(),getHeight());
	}

	protected void drawDamage(Graphics g, int xPos, int yPos){
		Font old   = g.getFont();
		Color oldC = g.getColor();
		g.setFont(numbers);
		g.setColor(Color.RED);
		if (damage <100) xPos+=3;
		g.drawString(""+damage, xPos+1, yPos-5);
		
		g.setColor(oldC);
		g.setFont(old);
	}
	
	public void setDamage(int value){
		this.damage = value;
		this.showDamage = true;
	}
	
	public void removeDamage(){
		this.showDamage = false;
	}
	
	public boolean isIntersecting(IsoTile t, int x, int y){
		assert t !=null;
		return bounds.contains(x,y);
	}
	
	/** @category unused**/
	public Point topLeftPoint(IsoTile[][] tiles, int x, int y){
		final Point result =  tiles[gridX][gridY].calculateCentrePoint(x,y);
		result.translate(-sprite.getWidth()/2, (int) (-sprite.getHeight()/1.5));
		return result;
	}
	
	
	public Collection<Location> getAttackRange(int width, int height){
		return getUnit().getWeapon().getAttackRange(getLocation(), width, height);
	}
	
	public boolean onSameTeam(AnimatedUnit u){
		return u != null && u.getUnit().isAI() == getUnit().isAI(); 
	}
	
	public Location getLocation(){
		return new Location(gridX,gridY);
	}

	public void setLocation(ILocation l){
		assert l != null;
		gridX = l.getX();
		gridY = l.getY();
	}

	/** @category Generated */
	public int getGridX() {
		return gridX;
	}

	/** @category Generated */
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	/** @category Generated */
	public int getGridY() {
		return gridY;
	}

	/** @category Generated */
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
	
	/** @category Generated */
	public int getWidth() {
		return sprite.getWidth();
	}

	/** @category Generated */
	public int getHeight() {
		return sprite.getHeight();
	}

	/** @category Generated */
	public IMapUnit getUnit() {
		return unit;
	}

	/** @category Generated */
	public void setMapUnit(IMapUnit unit) {
		this.unit = unit;
	}

	/** @category Generated */
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		sprite = spriteSheet.getSpriteImage(direction.reference()+"0");
	}

	/** @category Generated */
	public BufferedImage getWeaponSprite() {
		return weaponSprite;
	}
	
}
