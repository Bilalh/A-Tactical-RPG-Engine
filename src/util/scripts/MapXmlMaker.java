package util.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.sun.tools.internal.ws.util.xml.XmlUtil;

import common.enums.ImageType;

import config.Config;
import config.XMLUtil;
import config.xml.*;

/**
 * Creates the xml for  map 
 * @author Bilal Hussain
 */
public class MapXmlMaker {

	static String filename = "mask-test";
	
	public static void main(String[] args) throws IOException {
		Config.loadLoggingProperties();
		
		Random r = new Random(12344);
		int width  = 20;
		int height = 20;
		SavedTile[] tiles = new SavedTile[width*height];
		
		
//		String[] types = {"m", "blue400", "grass"};
//		String[] types = {"m","m200", "m128", "m96", "m64","m32","blue400"};
//		String[] types = {"m"};
//		String[] types = {"test"};

//		String[] types  = new String[5];
//		for (int i = 0; i < types.length; i++) {
//			types[i] = "" + i;
//		}
	
		int[]diagonals = {16,32,60,64,80,100,128,192,200,256};
		String[] types  = new String[diagonals.length];
		for (int i = 0; i < types.length; i++) {
//			types[i] = "blue" + diagonals[i];
			types[i] = "mask-" + diagonals[i];
		}
		
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				String type = types[j%types.length];
				int h = r.nextInt(3)+1;
				h =1;
				tiles[k] = new SavedTile(type,h, i,j);
			}
		}

		SavedMap m = new SavedMap(width,height,tiles, filename + "-mapping.xml");

		String s1 = XMLUtil.makeXml(m);
//		System.out.println(s1);
		
		HashMap<String, TileImageData> mapping = new HashMap<String, TileImageData>();
		mapping.put("m",    new TileImageData("images/tiles/mask.png", ImageType.NON_TEXTURED));
		mapping.put("grass",new TileImageData("images/tiles/brown.png", ImageType.NON_TEXTURED));
		mapping.put("test", new TileImageData("images/tiles/test.png", ImageType.NON_TEXTURED));
		mapping.put("blue400", new TileImageData("images/tiles/blue400.png", ImageType.NON_TEXTURED));

//		
//		for (int i = 0; i<=types.length; i++) {
//			mapping.put(""+i, new TileImageData("images/tiles/" + i+"-rst.png",  ImageType.NON_TEXTURED));
//		}
		
		for (int i = 0; i < types.length; i++) {
//			mapping.put("blue" + diagonals[i], new TileImageData("images/tiles/" + "blue" + diagonals[i] +".png", ImageType.NON_TEXTURED));
			mapping.put("mask-" + diagonals[i], new TileImageData("images/tiles/" + "mask-" + diagonals[i] +".png", ImageType.NON_TEXTURED));
		}
		
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
