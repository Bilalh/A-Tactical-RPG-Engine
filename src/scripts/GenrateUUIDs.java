package scripts;

import java.util.UUID;

/**
 * Genrate a number of UUIDs
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
