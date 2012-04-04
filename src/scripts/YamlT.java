package scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import config.assets.DialogPart;

/**
 * @author Bilal Hussain
 */
public class YamlT {

	public static void main(String[] args) throws FileNotFoundException {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		Object o = yaml.load(new FileInputStream("text.yaml"));
		if (o instanceof ArrayList){
			ArrayList<String> strings = (ArrayList<String>) o;
			for (Object s : strings) {
//				System.out.println(s);
			}
		}
		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) o;
		for (Map<String, String> data : list) {
			Map.Entry<String, String> entry =  data.entrySet().iterator().next();
			System.out.printf("- '%s': |-\n    %s\n\n",entry.getKey(), entry.getValue() );
		}
	}
	
}
