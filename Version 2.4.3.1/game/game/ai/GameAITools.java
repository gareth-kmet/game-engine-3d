package game.ai;

import org.lwjgl.util.vector.Vector3f;

import game.ai.GameAStarTools.AStarRequest;
import game.ai.GameAStarTools.AStarRequest.GRID_COMBINATION_TYPE;
import game.world.environment.EnvironmentStorage;

public class GameAITools {
	
	private final AILoader aiLoader;
	private boolean aiLoaderRunning = false;
	
	public GameAITools() {
		this.aiLoader= new AILoader();
	}
	
	public void init() {
		if(aiLoaderRunning)return;
		aiLoader.start();
		aiLoaderRunning=true;
	}
	
	public void finish() {
		if(!aiLoaderRunning)return;
		aiLoader.destroy();
		aiLoaderRunning=false;
	}
	
	public AStarRequest createAStarRequest(EnvironmentStorage chunks, Vector3f startPos, Vector3f targetPos, GRID_COMBINATION_TYPE gridType) {
		return new AStarRequest(chunks, startPos, targetPos, gridType, aiLoader);
	}
	
	

}
