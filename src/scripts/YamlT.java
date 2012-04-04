package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;

/**
 * @author Bilal Hussain
 */
public class YamlT {

	public static void main(String[] args) throws FileNotFoundException {
		Yaml yaml = new Yaml();
		Object o = yaml.load(new FileInputStream("text.yaml"));
		if (o instanceof ArrayList){
			ArrayList<String> strings = (ArrayList<String>) o;
			for (Object s : strings) {
				System.out.println(s);
			}
		}
		
	}
	
}
