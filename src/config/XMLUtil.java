
package config;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import util.Logf;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import common.Location;
import common.interfaces.IWeapon;
import common.spritesheet.SpriteInfo;
import common.spritesheet.Sprites;

import config.assets.*;
import config.xml.*;
import engine.skills.AbstractUnitObject;
import engine.skills.ISkill;
import engine.skills.RangedSkill;
import engine.unit.AiUnit;
import engine.unit.Unit;
import engine.unit.UnitAnimation;
import engine.unit.SpriteSheetData;

/**
 * Xml utilities to create and parse xml
 * 
 * @author Bilal Hussain
 */
public abstract class XMLUtil {
	//Based on code I wrote for the JH project.
	private static final Logger log = Logger.getLogger(XMLUtil.class);

	public static XStream xs;

	static {
		xs = new XStream(new DomDriver());
		xs.processAnnotations(getClassesForAnnotations());
		 xs.addDefaultImplementation(TileMapping.class,ITileMapping.class);
		 xs.setMode(XStream.NO_REFERENCES);
	}

	/**
	 * Makes a xml string from am IPreference
	 * 
	 * @param message - an instance of IPreference
	 * @return A string containing the xml
	 */
	public static String makeXml(IPreference message) {
		Logf.debug(log, "Saving %s", message);
		StringWriter sw = new StringWriter();
		xs.marshal(message, new CompactWriter(sw));
		return sw.toString();
	}

	/**
	 * Makes a pretty printed xml string from an IPreference
	 * 
	 * @param message - an instance of IPreference
	 * @return A string containing the xml
	 */
	public static String makeFormattedXml(Object message) {
		Logf.debug(log, "Saving %s", message);
		return xs.toXML(message);
	}

	/**
	 * Converts the xml in the string to an object
	 * 
	 * @param <E> - The type
	 * @param is  - The InputStream
	 * @return A IPreference
	 */
	public static <E> E convertXml(InputStream is) {
		E fromXML = (E) xs.fromXML(is);
		Logf.info(log, "Loaded %s", fromXML);
		return fromXML;
	}

	/**
	 * Converts the xml in the string to an object
	 * 
	 * @param <E> - The type
	 * @param s   - The String
	 * @return A IPreference
	 */
	public static <E> E convertXml(String s) {
		E fromXML = (E) xs.fromXML(s);
		Logf.info(log, "loaded %s", fromXML);
		return fromXML;
	}

	/**
	 * Adds a Xstream Annotations to processed, should be done at setup.
	 * 
	 * @param <E> - The type
	 */
	public static <E> void addAnnotations(Class<E>[] classes) {
		xs.processAnnotations(classes);
	}

	/**
	 * Get all the classes the have Xstream Annotations
	 * 
	 * @return A array of classes
	 */
	public static Class<?>[] getClassesForAnnotations() {

		return new Class[] {
				// Prefs
				SavedTile.class,
				SavedMap.class,
				MapData.class,
				MapSettings.class,
				TileMapping.class,
				TileImageData.class,
				UnitAnimation.class,
				SpriteSheetData.class,
				Sprites.class,
				
				DialogParts.class,
				DialogPart.class,

				MapMusic.class,
				MapConditions.class,
				
				EditorProject.class,
				
				//Model 
				Unit.class,
				AiUnit.class,
				AbstractUnitObject.class,
				
				//common 
				Location.class,
				SpriteInfo.class,

				// Model assets
				Weapons.class,
				Skills.class,
				Units.class,
				SpriteSheetsData.class,
				Textures.class,
				Maps.class,
				UnitPlacement.class,
				MapEvents.class,
				MusicData.class,
				Musics.class,
				
				DeferredAsset.class,
				DeferredMap.class,
				
				MapOrdering.class,
				OrderedMap.class,
				ISkill.class,
				IWeapon.class
		};
	}

}
