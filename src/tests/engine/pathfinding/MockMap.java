package tests.engine.pathfinding;

import engine.map.Tile;

public class MockMap extends MapStub {
	
	public Tile[][] tiles;
	
	public void setTiles(int[][] costs){
		tiles = new Tile[costs.length][costs[0].length];
		for (int i = 0; i < costs.length; i++) {
			for (int j = 0; j < costs.length; j++) {
				tiles[i][j] = new Tile(costs[i][j],costs[i][j]);
			}
		}
	}
	
	@Override
	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}

	@Override
	public int getFieldHeight() {
		return tiles[0].length;
	}

	@Override
	public int getFieldWidth() {
		return tiles.length;
	}
	
}