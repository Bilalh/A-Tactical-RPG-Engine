package scripts;

import java.util.UUID;

/**
 * @author Bilal Hussain
 */
public class GenrateUUIDs {

	static int number = 5;
	
	public static void main(String[] args) {
		for (int i = 0; i < number; i++) {
			System.out.println(UUID.randomUUID());
		}
	}
	
}
