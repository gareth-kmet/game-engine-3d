package game.world;

import java.util.HashSet;
import java.util.Random;

import org.lwjgl.util.Point;

import entities.Player;
import game.ai.GameAITools;
import game.physics.collisions.PhysicsMovement;
import game.world.environment.EnvironmentStorage;
import game.world.environment.styles.EnvironmentStyles;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;
import game.world.terrain.heights.FalloutHeightStyles;
import game.world.terrain.heights.LandHeightStyles;
import game.world.terrain.heights.WaterHeightStyles;
import mainLoop.LoopManager.MainLoopRequiredVariables;
import rendering.RenderConditions;

public class WorldVariables {
	
	public final RenderConditions[] renderConditions;
	public final int seed;
	public final Random random;
	public float currentFrameDeltaTime = 0f;
	
	public final Styles styles;
	public final class Styles{
		
		public final LandHeightStyles landHeight;
		public final WaterHeightStyles waterHeight;
		public final FalloutHeightStyles falloutHeight;
		public final EnvironmentStyles environment;
		
		private Styles() {
			
			landHeight = (LandHeightStyles)WorldRandomStyles.getStyle(renderConditions, WorldRandomStyles.LAND_HEIGHT_STYLE);
			waterHeight = (WaterHeightStyles)WorldRandomStyles.getStyle(renderConditions, WorldRandomStyles.WATER_HEIGHT_STYLE); 
			falloutHeight = (FalloutHeightStyles)WorldRandomStyles.getStyle(renderConditions, WorldRandomStyles.FALLOUT_STYLE);
			environment = (EnvironmentStyles)WorldRandomStyles.getStyle(renderConditions, WorldRandomStyles.EVIRONMENT_STYLE);
			
		}
	}
	
	public final TerrainVariables terrainVariables;
	public final class TerrainVariables{
		
		public final int 
			CHUNK_LOAD = 4, 
			CHUNK_LOAD_SQR = CHUNK_LOAD * CHUNK_LOAD;
		
		public TerrainHeightFinder heights;
		
		private TerrainVariables() {}
	}
	
	public final EnvironmentVariables environmentVariables;
	public final class EnvironmentVariables{
		
		public EnvironmentStorage storage = null;
		
		private EnvironmentVariables() {}
	}
	
	public final PlayerVariables playerVariables;
	public final class PlayerVariables{
		public Player player;
		public Point currentChunk;
		public Point lastChunk;
		
		private PlayerVariables() {};
	}
	
	public final WorldThreadMethods worldThreadMethods;
	public final class WorldThreadMethods{
		
		private final WorldLoaderThread worldLoaderThread;
		
		private WorldThreadMethods(WorldLoaderThread worldLoaderThread) {
			this.worldLoaderThread = worldLoaderThread;
		}
		
		public void addRequestToThread(WorldLoaderThread.Request r) {
			r.addToThread(worldLoaderThread);
		}
		
		public void waitForNextCompletion() {
			worldLoaderThread.hasCompleted=false;
			while (!worldLoaderThread.hasCompleted) {
				Thread.yield();
				continue;
			}
		}
	}
	
	public final PhysicsVariables physicsVariables;
	public final class PhysicsVariables{
		
		final HashSet<PhysicsMovement> thisFramePhysicsMovements = new HashSet<PhysicsMovement>();
		
		public void addPhysicsMovementToThisFrame(PhysicsMovement p) {
			thisFramePhysicsMovements.add(p);
		}
		
		private PhysicsVariables() {
		}
	}
	
	public final AIVariables aiVariables;
	public final class AIVariables{
		
		public final GameAITools gameAITools;
		public final Random aiRandom;
		
		private AIVariables(GameAITools gameAITools) {
			this.gameAITools=gameAITools;
			aiRandom = new Random();
		}
	}
	
	public final MainLoopRequiredVariables mainLoopVariables;
	
	WorldVariables(int s, WorldLoaderThread worldLoaderThread, GameAITools gameAITools, MainLoopRequiredVariables mainLoopRequiredVariables){
		seed = s;
		this.random = new Random();
		this.random.setSeed(seed);
		renderConditions = WorldRandomStyles.getRandomStyles(random);
		
		worldThreadMethods = new WorldThreadMethods(worldLoaderThread);
		aiVariables = this.new AIVariables(gameAITools);
		styles = this.new Styles();
		terrainVariables = this.new TerrainVariables();
		environmentVariables = this.new EnvironmentVariables();
		playerVariables = this.new PlayerVariables();
		physicsVariables = this.new PhysicsVariables();
		this.mainLoopVariables=mainLoopRequiredVariables;
		
	}

}
