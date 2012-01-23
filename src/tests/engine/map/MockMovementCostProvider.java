package tests.engine.map;

import engine.map.IMovementCostProvider;

/**
 * @author Bilal Hussain
 */
public class MockMovementCostProvider implements IMovementCostProvider {

	private int[][] costs;

	@Override
	public int getMovementCost(int x, int y, int newX, int newY) {
		return   1 +  Math.abs(costs[x][y] - costs[newX][newY]);
	}

	/** @category Generated Getter */
	public int[][] getCosts() {
		return costs;
	}

	/** @category Generated Setter */
	public void setCosts(int[][] costs) {
		this.costs = costs;
	}

}
