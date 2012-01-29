import java.util.ArrayList;

import common.interfaces.IUnit;
import engine.Unit;


/**
 * @author bilalh
 */
public class u {

	public static void main(String[] args) {
		ArrayList<Unit> u = new ArrayList<Unit>();
		u.add(new Unit("d", 2, 2, 3));
		
		System.out.println(u);
		
		ArrayList<IUnit> uu = new ArrayList<IUnit>(u);
		System.out.println(uu);
	}
	
}
