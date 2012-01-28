package tests.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	public static void main(String[] args) throws IOException {
		Config.loadLoggingProperties();
		
		Random r = new Random(12344);
		int width  = 10;
		int height = 10;
		SavedTile[] tiles = new SavedTile[width*height];
		
		
//		String[] types = {"brown", "blue", "grass"};
		String[] types = {"m", "m128", "m96", "m64","m60","m32"};
		
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				String type = types[j%types.length];
				int h = r.nextInt(3)+1;
				h =1;
				tiles[k] = new SavedTile(type,h, i,j);
			}
		}

		String filename = "map2";
		SavedMap m = new SavedMap(width,height,tiles, filename + "-mapping.xml");

		String s1 = XMLUtil.makeFormattedXml(m);
		System.out.println(s1);
		
		HashMap<String, TileImageData> mapping = new HashMap<String, TileImageData>();
		mapping.put("grass", new TileImageData("images/tiles/brown.png", ImageType.NON_TEXTURED));
		mapping.put("blue",  new TileImageData("images/tiles/blue.png",  ImageType.NON_TEXTURED));
		mapping.put("m",   new TileImageData("images/tiles/mask.png", ImageType.NON_TEXTURED));
		mapping.put("m128",new TileImageData("images/tiles/m128.png", ImageType.NON_TEXTURED));
		mapping.put("m96", new TileImageData("images/tiles/m96.png", ImageType.NON_TEXTURED));
		mapping.put("m64", new TileImageData("images/tiles/m64.png", ImageType.NON_TEXTURED));
		mapping.put("m60", new TileImageData("images/tiles/m60.png", ImageType.NON_TEXTURED));
		mapping.put("m32", new TileImageData("images/tiles/m64.png", ImageType.NON_TEXTURED));
		
		TileMapping map = new TileMapping(mapping);
		String s2 = XMLUtil.makeFormattedXml(map);
		System.out.println(s2);
		
		FileWriter fw = new FileWriter(new File("./Resources/maps/" + filename + ".xml"));
		fw.write(s1);
		fw.close();
		
		fw = new FileWriter(new File("./Resources/maps/" + filename + "-mapping.xml"));
		fw.write(s2);
		fw.close();
		
	}
	
}
