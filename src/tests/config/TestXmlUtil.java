package tests.config;

import java.util.Random;

import org.junit.Test;

import common.enums.Orientation;

import config.XMLUtil;
import config.xml.SavedMap;
import config.xml.SavedTile;

import engine.pathfinding.*;
import static org.junit.Test.*;
import static org.junit.Assert.*;

/**
 * @author Bilal Hussain
 */
public class TestXmlUtil {

	@Test
	public void voidTestParsingSaving(){
		Random r = new Random(1234);
		int width  = 2;
		int height = 2;
		SavedTile[] tiles = new SavedTile[width*height];
		for (int i = 0, k =0; i < width; i++) {
			for (int j = 0; j < height; j++, k++) {
				tiles[k] = new SavedTile("grass",r.nextInt(4)+1, i,j,Orientation.UP_TO_EAST);
			}
		}
		fail();
//		
//		SavedMap m = new SavedMap(width,height,tiles, "basic-mapping.xml");
//		
//		String s1 = XMLUtil.makeFormattedXml(m);
//		SavedMap m2 =  XMLUtil.convertXml(s1);
//		
//		
//		assertArrayEquals("Tiles", m.getTiles(), m2.getTiles());
//		assertEquals("height", m.getFieldHeight(), m2.getFieldHeight());
//		assertEquals("width", m.getFieldWidth(), m2.getFieldWidth());
//		assertEquals("", m.getTileMappinglocation(), m.getTileMappinglocation());
	}
	
}
