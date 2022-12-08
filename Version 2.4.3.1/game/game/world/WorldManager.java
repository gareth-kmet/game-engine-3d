package game.world;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import entities.Player;
import game.GameLoopManager;
import game.ai.GameAITools;
import game.objects.classes.animals.AfraidBullAnimal;
import game.physics.collisions.PhysicsCollide;
import game.physics.collisions.data.BoundingBox;
import game.world.environment.EnvironmentManager;
import game.world.environment.styles.EnvironmentStyles;
import game.world.terrain.TerrainManager;
import mainLoop.LoopManager.MainLoopRequiredVariables;
import objects.TexturedModels;
import rendering.RenderConditions;

public final class WorldManager implements GameLoopManager{
	
	private final Random random = new Random();
	
	private int seed;
	
	private WorldLoaderThread thread;
	private GameAITools gameAITools;
	
	private TerrainManager terrainManager;
	private EnvironmentManager environmentManager;
	
	
	private WorldVariables worldVariables;
	
	private AfraidBullAnimal testObject;
	private Player player;
	
	private HashSet<AfraidBullAnimal> animals = new HashSet<AfraidBullAnimal>();

	@Override
	public void create(MainLoopRequiredVariables mainLoopRequiredVariables) {
		
		
//		seed = random.nextInt(1000000);
		seed=0x25c0c;// test seed
		System.out.println(Integer.toHexString(seed));
		
		thread = new WorldLoaderThread();
		thread.start();
		gameAITools = new GameAITools();
		gameAITools.init();
		
		player = new Player(TexturedModels.BULL, new Vector3f(40,20,0),new Vector3f(0,0,0),0.2515f);
		player.playerEntity.setCollideCondition(BoundingBox.Boxes.Y_ROTATABLE);
		mainLoopRequiredVariables.camera = new Camera(player);
	
		
		worldVariables = new WorldVariables(seed, thread, gameAITools, mainLoopRequiredVariables);
		worldVariables.playerVariables.player=player;
		
		terrainManager = new TerrainManager(worldVariables);
		environmentManager = new EnvironmentManager(worldVariables);
		
		terrainManager.create(random);
		worldVariables.worldThreadMethods.waitForNextCompletion();
		environmentManager.create(random);
		worldVariables.worldThreadMethods.waitForNextCompletion();
		worldVariables.environmentVariables.storage.getChunk(worldVariables.playerVariables.currentChunk).recieveGameEntity(player);
		
		Light sun = new Light(new Vector3f(1000,10000,1000), new Vector3f(1.3f,1.3f,1.3f));
		mainLoopRequiredVariables.sun=sun;
		mainLoopRequiredVariables.lights.add(sun);
		updateSunPos();
		
		
		EnvironmentStyles.environmentInit();
//		GameObject boulderA = EnvironmentGameObjects.BOULDER.create(new Vector3f(10,0,100), new Vector3f(), 1);
//		GameObject boulderB = EnvironmentGameObjects.BOULDER.create(new Vector3f(200,0,20), new Vector3f(), 1);
//		EnvironmentChunk.spawnObject(loader, worldVariables.environmentVariables.storage, boulderA, boulderB);
		
		
		
		
	}
	
	private float sunAngle = (float) (Math.PI/2f);
	
	private void updateSunPos() {
		Vector3f pos = worldVariables.mainLoopVariables.sun.getPosition();
		pos.x = (float) (Math.cos(sunAngle)*10000);
		pos.y = (float) (Math.sin(sunAngle)*10000);
	}
	
	boolean octavesBool = false;
	
	@Override
	public void update(float frameDeltaTime) {
		worldVariables.currentFrameDeltaTime=frameDeltaTime;
		
		Iterator<AfraidBullAnimal> it = animals.iterator();
		while(it.hasNext()) {
			AfraidBullAnimal an = it.next();
			if(an.isDead()) {
				it.remove();
			}
		}
		
		for(int i=animals.size(); i<=5; i++) {
			animals.add(AfraidBullAnimal.spawn(player.getPosition(), worldVariables.environmentVariables.storage));
		}
		
//		sunAngle -= (float)(Math.PI/10f) * DisplayManager.getFrameTimeSeconds();
//		updateSunPos();
		
		terrainManager.updatePlayerChunk();
		
		if(playerInNewChunk()) {
			terrainManager.updateNewChunk();
			environmentManager.updateNewChunk();
		}
		
		terrainManager.update();
		environmentManager.update();
		
		PhysicsCollide.updateMouvements(worldVariables.physicsVariables.thisFramePhysicsMovements, worldVariables, true);
	}
	
	private boolean playerInNewChunk() {
		return !worldVariables.playerVariables.currentChunk.equals(worldVariables.playerVariables.lastChunk);
	}

	@Override
	public void destroy() {
		thread.stop=true;
		try {
			thread.interrupt();
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gameAITools.finish();
		terrainManager.destroy();
		
	}
	
	public RenderConditions[] getStyles() {
		return worldVariables.renderConditions;
	}
	
	
	

}
