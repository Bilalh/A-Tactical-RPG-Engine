package engine.pathfinding;

/**
 * @author Bilal Hussain
 */
public interface IMovementCostProvider {

	/**
	 * Return the cost from (x,y) to (newX,newY) or Integer.MAX_VALUE if not reachable.  
	 * The cost (x,y) to (x,y) is always zero.
	 * @returns the cost or  Integer.MAX_VALUE
	 */
	int getMovementCost(int x, int y,  int newX, int newY);
	
}
