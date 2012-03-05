package scripts;

import org.jvnet.inflector.Noun;

/**
 * @author Bilal Hussain
 */
public class Nouns {

	public static void main(String[] args) {

		String[] arr = {
			"enemy",
			"Enemy",
		};
		
		for (String s : arr) {
			System.out.printf("%s -> %s\n", s, Noun.pluralOf(s));
		}
		
	}
	
}
