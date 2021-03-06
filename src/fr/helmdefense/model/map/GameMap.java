package fr.helmdefense.model.map;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.helmdefense.model.entities.utils.coords.Location;

public class GameMap {
	private int[][] tiles;
	private List<Location> spawns;
	private Location target;
	private Graph graph;
	
	public static final int WIDTH = 16;
	public static final int HEIGHT = 11;
	public static final int TILE_SIZE = 64;
	
	public GameMap(int[][] tiles, List<Location> spawns, Location target) {
		this.tiles = tiles;
		this.spawns = spawns;
		this.target = target;
		this.graph = new Graph(this, this.target);
		this.graph.bfs();
	}
	
	public int getTile(int x, int y) {
		if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
			throw new OutOfMapException();
		return this.tiles[y][x];
	}
	
	public Location getSpawn() {
		return this.spawns.get(new Random().nextInt(this.spawns.size()));
	}
	
	public Location getTarget() {
		return this.target;
	}
	
	public Graph getGraph() {
		return this.graph;
	}

	@Override
	public String toString() {
		return "GameMap [tiles=" + Arrays.deepToString(tiles) + ", spawns=" + spawns + ", target=" + target + "]";
	}
}