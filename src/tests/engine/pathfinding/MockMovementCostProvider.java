package tests.engine.pathfinding;

import engine.pathfinding.IMovementCostProvider;

/**
 * @author Bilal Hussain
 */
public class MockMovementCostProvider implements IMovementCostProvider {

	private int[][] costs;

	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return   1 +  Math.abs(costs[x][y] - costs[newX][newY]);
	}


	public int[][] getCosts() {
		return costs;
	}


	public void setCosts(int[][] costs) {
		this.costs = costs;
	}

}
