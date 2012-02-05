package editor.spritesheet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import common.ListenerUtil;
import common.spritesheet.SpriteInfo;

import config.IPreference;

import static common.ListenerUtil.*;


/**
 * A sprite who image can be changed
 * @author Bilal Hussain
 */
@XStreamAlias("sprite")
public class MutableSprite extends SpriteInfo implements Comparable<MutableSprite> {
	
	@XStreamOmitField
	private BufferedImage image;

	@XStreamOmitField
	private ArrayList<ISpriteChangedListener> listeners = new ArrayList<ISpriteChangedListener>();
	
	public MutableSprite(File file) throws IOException {
		this(file.getName(), ImageIO.read(file));
	}
	
	public MutableSprite(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	public boolean contains(int xp, int yp) {
		return !(xp < x || yp < y || xp >= x+width || yp >= y+height);
	}


	@Override
	public int compareTo(MutableSprite b) {
		return this.getHeight() - b.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setPositionInSheet(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/** @category Generated */
	public void setName(String name) {
		this.name = name;
		notifyListeners(listeners, this);
	}
	
	public void addSpriteChangedListener(ISpriteChangedListener listener){
		listeners.add(listener);
	}
}