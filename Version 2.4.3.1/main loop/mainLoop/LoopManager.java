package mainLoop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

import entities.Camera;
import entities.Light;
import entities.RenderEntity;
import game.GameManager;
import game.world.terrain.Terrain;
import objects.TexturedModels;
import rendering.RenderConditions;
import rendering.guis.GuiTexture;
import rendering.water.WaterTile;

public abstract class LoopManager implements Runnable{
	
	GivenMainLoopRequiredVariables lastCreatedVariables = null;
	protected final MainLoopRequiredVariables mainLoopVariables = new MainLoopRequiredVariables();
	boolean stop=false;
	
	private long lastFrameUpdate;
	protected float frameTimeSeconds;

	/**
	 * Creates the section
	 * Must also create the variables for the MainLoopRequiredVariables
	 * 
	 * @return if creation worked
	 */
	public abstract boolean create();
	
	void step() {
		long thisFrameUpdate = System.currentTimeMillis();
		frameTimeSeconds = (thisFrameUpdate-lastFrameUpdate)*0.001f;
		lastFrameUpdate=thisFrameUpdate;
		loopStep();
		lastCreatedVariables = GivenMainLoopRequiredVariables.create(mainLoopVariables);
	}
	
	/**
	 * Called each step of the loop
	 * 
	 */
	protected abstract void loopStep();
	
	/**
	 * Creates the section
	 * 
	 * @return boolean that is added with display.closed to run while loop
	 */
	public abstract boolean loopBoolean();
	
	public abstract void cleanUp();
	
	
	@Override
	public void run() {
		create();
		lastFrameUpdate = System.currentTimeMillis();
		while(loopBoolean() && !stop) {
			step();
		}
		cleanUp();
	}
	
	/**
	 * 
	 * Visible to the Loop
	 *
	 */
	public final class MainLoopRequiredVariables{
		public RenderConditions[] renderConditions;
		public Camera camera;
		public Light sun;
		
		public final Collection<RenderEntity> outlinedEntities = new HashSet<RenderEntity>();
		public final RenderEntityStorage renderEntityStorage = new RenderEntityStorage();
		public final Collection<Terrain> terrains = new HashSet<Terrain>(); 
		public final Collection<WaterTile> waterTiles = new HashSet<WaterTile>();
		public final List<Light> lights = new ArrayList<Light>();
		public final Collection<GuiTexture> guis = new HashSet<GuiTexture>();
	}
	/**
	 * 
	 * Visible to the Renderer
	 *
	 */
	public record GivenMainLoopRequiredVariables(
			RenderConditions[] renderConditions,
			Camera camera,
			Light sun,
			Collection<Terrain> terrains,
			Collection<WaterTile> waterTiles,
			Collection<GuiTexture> guis,
			Collection<RenderEntity> outlinedEntities,
			List<Light> lights,
			EnumMap<TexturedModels, HashSet<RenderEntity>> entities
	) {
		@SuppressWarnings("unchecked")
		private static GivenMainLoopRequiredVariables create(MainLoopRequiredVariables main) {
			RenderConditions[] renderConditions = main.renderConditions.clone();
			Camera camera = main.camera;
			Light sun=main.sun;
			Collection<Terrain> terrains = new HashSet<Terrain>(main.terrains);
			Collection<WaterTile> waterTiles = new HashSet<WaterTile>(main.waterTiles);
			Collection<GuiTexture> guis = new HashSet<GuiTexture>(main.guis);
			Collection<RenderEntity> outlinedEntities = new HashSet<RenderEntity>(main.outlinedEntities);
			List<Light> lights = new ArrayList<Light>(main.lights);
			EnumMap<TexturedModels, HashSet<RenderEntity>> entities = new EnumMap<TexturedModels, HashSet<RenderEntity>>(TexturedModels.class);
			main.renderEntityStorage.allEntities.forEach((key,set)->entities.put(key, (HashSet<RenderEntity>)set.clone()));
			return new GivenMainLoopRequiredVariables(renderConditions, camera, sun, terrains, waterTiles, guis, outlinedEntities, lights, entities);
		}
	}
	
	
	enum Managers{
		GAME {
			@Override
			LoopManager getManagerNewInstant() {
				return new GameManager();
			}
		},
		
		;
		
		boolean canUseNext = true;
		
		abstract LoopManager getManagerNewInstant();
		
		public boolean setNextManager() {
			return setNextManager(this);
		}
		
		public static boolean setNextManager(Managers nextManager) {
			
			if(nextManager.canUseNext) {
				MainLoop.nextManager = nextManager;
			}
			
			return nextManager.canUseNext;
			
		}
	}

}
