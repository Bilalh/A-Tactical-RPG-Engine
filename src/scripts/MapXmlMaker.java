package scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.sun.tools.internal.ws.util.xml.XmlUtil;

import common.enums.ImageType;
import common.enums.Orientation;

import config.Config;
import config.XMLUtil;
import config.xml.*;

/**
 * Creates the xml for a  map 
 * @author Bilal Hussain
 */
public class MapXmlMaker {

	static String filename = "fft";
	static String sheet = "images/tilesets/fft.png";
	
	public static void main(String[] args) throws IOException {
		Config.loadLoggingProperties();
		
		Random r = new Random(12344);
		int width  = 13;
		int height = 15;
		SavedTile[] tiles = new SavedTile[width*height];
		
//		String[] types = {"brown","darkblue","gray","ground","ice","lime","marking","metal","tile","white","wood","bush"};
		String[] types = new String[width*height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				types[i*height + j] = i+"-"+j;
			}
		}
		
		
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				String type = "" +types[(j+i)%types.length];
				int h = r.nextInt(3)+1;
				h =1;
				type="0-7";
				tiles[k] = new SavedTile(type,h,h, i,j,Orientation.TO_EAST,null,null, false);
			}
		}

		MapSettings settings = MapSettings.defaults();
		MapData     data     = new MapData("maps/" +filename + "-mapping.xml",null, filename,null,null,null,null,true);
		SavedMap m = new SavedMap(width,height,tiles, settings,data);

//		String s1 = XMLUtil.makeXml(m);
		String s1 = XMLUtil.makeFormattedXml(m);
//		System.out.println(s1);
		
		HashMap<String, TileImageData> mapping = new HashMap<String, TileImageData>();
		
		for (String t : types) {
			mapping.put(t, new TileImageData(t, ImageType.NON_TEXTURED));	
		}
			
		ITileMapping map = new TileMapping(sheet, mapping);
		String s2 = XMLUtil.makeFormattedXml(map);
		System.out.println(s2);
//		
		FileWriter fw = new FileWriter(new File("./Resources/maps/" + filename + ".xml"));
		fw.write(s1);
		fw.close();
		
		fw = new FileWriter(new File("./Resources/maps/" + filename + "-mapping.xml"));
		fw.write(s2);
		fw.close();
		
	}
	
}
