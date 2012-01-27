package tests.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.sun.tools.internal.ws.util.xml.XmlUtil;
import common.ImageType;

import config.Config;
import config.XMLUtil;
import config.xml.*;

/**
 * @author Bilal Hussain
 */
public class XmlMaker {

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		
		Random r = new Random(12344);
		int width  = 5;
		int height = 5;
		SavedTile[] tiles = new SavedTile[width*height];
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				tiles[k] = new SavedTile("grass",r.nextInt(3)+1, i,j);
			}
		}
		
		SavedMap m = new SavedMap(width,height,tiles, "basic-mapping.xml");

		String s1 = XMLUtil.makeFormattedXml(m);
		System.out.println(s1);
		
		HashMap<String, TileImageData> mapping = new HashMap<String, TileImageData>();
		mapping.put("grass", new TileImageData("images/tiles/brawn.jpg", ImageType.NON_TEXTURED));
		
		TileMapping map = new TileMapping(mapping);
		String s = XMLUtil.makeFormattedXml(map);
		System.out.println(s);
		
	}
	
}
