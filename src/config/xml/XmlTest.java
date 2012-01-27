package config.xml;

import java.util.Random;

import config.XMLUtil;

/**
 * @author Bilal Hussain
 */
public class XmlTest {

	public static void main(String[] args) {
		
		Random r = new Random(1234);
		SavedTile[][] tiles = new SavedTile[2][2];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				tiles[i][j] = new SavedTile("grass",r.nextInt(4)+1, i,j);
			}
		}
		
		SavedMap m = new SavedMap(tiles.length, tiles[0].length, tiles);

		String s = XMLUtil.makeFormattedXml(m);
		System.out.println(s);
		
	}
	
}
