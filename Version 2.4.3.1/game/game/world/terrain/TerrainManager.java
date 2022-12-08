package game.world.terrain;

import java.util.Random;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.world.WorldChunk;
import game.world.WorldLoopManager;
import game.world.WorldVariables;

public class TerrainManager implements WorldLoopManager {
	
	private TerrainGenerator generator;
	private final WorldVariables worldVariables;
	
	public TerrainManager(WorldVariables variables) {
		worldVariables = variables;
	}

	@Override
	public void create(Random random) {
		
		
		generator = new TerrainGenerator(worldVariables);
		
		worldVariables.terrainVariables.heights = generator.new TerrainHeightFinder();
		
		worldVariables.playerVariables.currentChunk = getPlayerChunk();
		worldVariables.playerVariables.lastChunk = worldVariables.playerVariables.currentChunk;
		
		
		updateNewChunk();
		update();
	}
	
	public void updatePlayerChunk() {
		worldVariables.playerVariables.lastChunk = worldVariables.playerVariables.currentChunk;
		worldVariables.playerVariables.currentChunk = getPlayerChunk();
	}
	
	public void updateNewChunk() {
		generator.generate();
		generator.updateWaters();
	}

	@Override
	public void update() {
		generator.updateGeneration();
	}
	
	private Point getPlayerChunk() {
		Vector3f pos = worldVariables.playerVariables.player.getPosition();
		return WorldChunk.getChunk(pos);
	}

	@Override
	public void destroy() {
		
	}

}
