package config.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.sun.tools.internal.ws.util.xml.XmlUtil;

import config.XMLUtil;

/**
 * @author Bilal Hussain
 */
public class XmlTest {

	public static void main(String[] args) {
		
		Random r = new Random(1234);
		int width  = 2;
		int height = 2;
		SavedTile[] tiles = new SavedTile[width*height];
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				tiles[k] = new SavedTile("grass",r.nextInt(4)+1, i,j);
			}
		}
		
		SavedMap m = new SavedMap(width,height,tiles, "mapping.xml");

		String s1 = XMLUtil.makeFormattedXml(m);
		System.out.println(s1);
		SavedMap m2 =  XMLUtil.convertXml(s1);
		System.out.println(m2);
//		
		HashMap<String, String> mapping = new HashMap<String, String>();
		mapping.put("grass", "images/tiles");
		
		TileMapping map = new TileMapping(mapping);
		String s = XMLUtil.makeFormattedXml(map);
		System.out.println(s);
		
		TileMapping map2 = XMLUtil.convertXml(s);
		System.out.println(map2);
		
	}
	
}
