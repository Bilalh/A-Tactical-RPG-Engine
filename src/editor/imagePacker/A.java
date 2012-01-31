package editor.imagePacker;

import java.io.File;
import java.io.IOException;

import common.spritesheet.Sprite;


import config.XMLUtil;

/**
 * @author Bilal Hussain
 */
public class A {

	public static void main(String[] args) throws IOException {
		Spritee a = new Spritee(new File("test.png"));
		String s = XMLUtil.makeFormattedXml((Sprite)a);
		System.out.println( s);
	}
	
}
