package game.world.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import org.lwjgl.util.Point;

import entities.RenderEntity;
import game.ai.astar.AStar.AStarEffect;
import game.ai.astar.AStarGrid;
import game.ai.astar.AStarGrid.AStarGridAffectingObject.NodeValue;
import game.ai.astar.Node;
import game.objects.GameObject;
import game.objects.entities.GameEntity;
import game.objects.entities.MoveableGameEntity;
import game.objects.informations.Death;
import game.objects.informations.Death.CanDie.DiesOnDespawn;
import game.objects.informations.Death.Drops.DropsOnDeath;
import game.objects.informations.Death.Drops.DropsOnDespawn;
import game.objects.informations.Death.Drops.DropsOnKill;
import game.objects.informations.Physics.Collidable;
import game.physics.collisions.PhysicsCollidableGrid;
import game.world.WorldChunk;
import game.world.WorldVariables;

public final class EnvironmentChunk{
	
	public static final int 
		RENDER_SMALL=2,
		RENDER_MEDIUM=4,
		RENDER_LARGE=9,
		RENDER_ALL=16;//Max chunk render distance for all entities
	
	
	/**
	 * The four primary storage sets for GameObjects
	 */
	private final HashSet<GameObject> other,large,medium,small;
	/**
	 * All collidables should be located within one of the four primary storage sets for GameObjects
	 */
	private final PhysicsCollidableGrid collisionGrid;
	/**
	 * All gameEntities should be located within one of the four primary storage sets for GameObjects added to the list either through the creation of the chunk or the spawn method.
	 */
	private final HashSet<GameEntity> gameEntities;
	
	private final ArrayList<NodeValue> outofboundsAStarEffects = new ArrayList<NodeValue>();
	private final AStarGrid aStarGrid;
	private boolean rendering;
	boolean loaded = false;
	
	final int x, z;
	
	private float currentDistance = 0;
	private final EnvironmentStorage storage;
	private final WorldVariables worldVars;
	
	EnvironmentChunk(WorldVariables worldVars, int x, int z, HashSet<GameObject> other, HashSet<GameObject> large, HashSet<GameObject> medium,HashSet<GameObject> small) {
		this.worldVars=worldVars;
		this.storage=worldVars.environmentVariables.storage;
		this.x=x; this.z=z;
		this.medium=medium;
		this.small=small;
		this.large = large;
		this.other=other;
		gameEntities = new HashSet<GameEntity>();
		collisionGrid = new PhysicsCollidableGrid();
		aStarGrid=WorldChunk.newAStarGrid(x,z);
	}
	
	EnvironmentChunk(WorldVariables worldVars, int x, int z, HashSet<GameObject> large, HashSet<GameObject> medium,HashSet<GameObject> small) {
		this(worldVars,x,z,new HashSet<GameObject>(),large, medium, small);
	}
	
	private final Consumer<GameObject> sortConsumer = new Consumer<GameObject>() {
		@Override
		public void accept(GameObject t) {
			if(t instanceof GameEntity g) {
				gameEntities.add(g);
			}
			if(t instanceof Collidable c) {
				collisionGrid.put(c);
			}
		}
	},
			desortConsumer = new Consumer<GameObject>() {

		@Override
		public void accept(GameObject t) {
			if(t instanceof GameEntity g) {
				gameEntities.remove(g);
			}
			if(t instanceof Collidable c) {
				collisionGrid.remove(c);
			}
			
		}
		
	};
	
	public AStarGrid getAStarGrid() {
		return aStarGrid;
	}
	private void sortObjects() {
		small.forEach(sortConsumer);
		medium.forEach(sortConsumer);
		large.forEach(sortConsumer);
		other.forEach(sortConsumer);
	}
	
	@SuppressWarnings("unchecked")
	public void update() {
		if(currentDistance<=EnvironmentManager.UPDATE_ENTITY_DISTANCE)
			for(GameEntity e : (Collection<GameEntity>)gameEntities.clone()) {
				e.update(worldVars);
			}
	}
	
	
	void loadModelsToEntities() {
		if(!loaded) {
			loadAllEntitiesFromList(other);
			loadAllEntitiesFromList(large);
			loadAllEntitiesFromList(medium);
			loadAllEntitiesFromList(small);
			
			loaded=true;

			sortObjects();
		}
		
	}
	
	private void loadAllEntitiesFromList(HashSet<? extends GameObject> list) {
		for(GameObject object : list) {
			loadEntity(object);
		}

	}
	
	private void loadEntity(GameObject o) {
		o.load();
		loadAStarEffectsForEntity(o);
	}
	
	private void loadAStarEffectsForEntity(GameObject o) {
		if(o instanceof MoveableGameEntity || o.getAStarEffect().equals(AStarEffect.NULL_EFFECT))return;
		
		HashSet<Point> nodeValues = o.getAffectingNodes();
		for(Point p : nodeValues) {
			NodeValue v = new NodeValue(p, o.getAStarEffect());
			if(0<=v.x()&&v.x()<WorldChunk.SIZE  &&  0<=v.z()&&v.z()<WorldChunk.SIZE) {
//				Inside chunk
				Node node = aStarGrid.nodes()[v.x()][v.z()];
				node.addNodeEffect(v.astarEffect());
			}else {
//				Outside
				outofboundsAStarEffects.add(v);
			}
		}
		
	}
	
	private void deloadAstarEffectsForEntity(GameObject o) {
		if(o instanceof MoveableGameEntity || o.getAStarEffect().equals(AStarEffect.NULL_EFFECT))return;
		
		HashSet<Point> nodeValues = o.getAffectingNodes();
		for(Point n : nodeValues) {
			NodeValue v = new NodeValue(n, o.getAStarEffect());
			if(0<=v.x()&&v.x()<WorldChunk.SIZE  &&  0<=v.z()&&v.z()<WorldChunk.SIZE) {
//				Inside chunk
				Node node = aStarGrid.nodes()[v.x()][v.z()];
				node.removeNodeEffect(v.astarEffect());
			}else {
//				Outside
				outofboundsAStarEffects.remove(v);
			}
		}
	}
	
	
	void remove() {
		if(currentDistance<=RENDER_ALL) {
			renderAllEntitiesFromList(other, false);
			if(currentDistance<=RENDER_LARGE) {
				renderAllEntitiesFromList(large, false);
				if(currentDistance<=RENDER_MEDIUM) {
					renderAllEntitiesFromList(medium, false);
					if(currentDistance<=RENDER_SMALL) {
						renderAllEntitiesFromList(small, false);
					}
				}
			}
		}
		rendering = false;
	}
	
	void add(float dist) {
		setCurrentDistance(dist);
		add();
	}
	
	void setNewChunkDistance(float dist) {
		
		if(!rendering) {
			add(dist);
			return;
		}
		
		if(dist==currentDistance)return;
		
		if(currentDistance>RENDER_SMALL) {
			if(dist<=RENDER_SMALL)renderAllEntitiesFromList(small, true);
			if(currentDistance>RENDER_MEDIUM) {
				if(dist<=RENDER_MEDIUM)renderAllEntitiesFromList(medium, true);
				if(currentDistance>RENDER_LARGE) {
					if(dist<=RENDER_LARGE)renderAllEntitiesFromList(large, true);
					if(currentDistance>RENDER_ALL) {
						if(dist<=RENDER_ALL)renderAllEntitiesFromList(other,true);
					}
				}
			}
		}
		
		if(currentDistance<=RENDER_ALL) {
			if(dist>RENDER_ALL)renderAllEntitiesFromList(other, false);
			if(currentDistance<=RENDER_LARGE) {
				if(dist>RENDER_LARGE)renderAllEntitiesFromList(large, false);
				if(currentDistance<=RENDER_MEDIUM) {
					if(dist>RENDER_MEDIUM)renderAllEntitiesFromList(medium, false);
					if(currentDistance<=RENDER_SMALL) {
						if(dist>RENDER_SMALL)renderAllEntitiesFromList(small, false);
					}
				}
			}
		}
		
		setCurrentDistance(dist);
	}
	
	void add() {
		if(currentDistance<=RENDER_ALL) {
			renderAllEntitiesFromList(other, true);
			if(currentDistance<=RENDER_LARGE) {
				renderAllEntitiesFromList(large, true);
				if(currentDistance<=RENDER_MEDIUM) {
					renderAllEntitiesFromList(medium, true);
					if(currentDistance<=RENDER_SMALL) {
						renderAllEntitiesFromList(small, true);
					}
				}
			}
		}
		
		
		rendering = true;
	}
	
	void setCurrentDistance(float dist) {
		this.currentDistance=dist;
	}
	
	
	private void renderAllEntitiesFromList(HashSet<? extends GameObject> list, boolean add) {
		for(GameObject object : list) {
			renderGameObject(object, add);
		}
	}
	
	private void renderGameObject(GameObject o, boolean add) {
		for(RenderEntity e : o.getObjectEntities()) {
			if(add) {
				worldVars.mainLoopVariables.renderEntityStorage.addEntity(e);
			}else {
				worldVars.mainLoopVariables.renderEntityStorage.removeEntity(e);
			}
		}
	}
	
	public HashSet<GameObject> get(int type) {
		switch(type) {
			case RENDER_SMALL:
				return small;
			case RENDER_MEDIUM:
				return medium;
			case RENDER_LARGE:
				return large;
			default:
				return other;
		}
	}
	
	public Collection<NodeValue> getOutofboundsValues(){
		return outofboundsAStarEffects;
	}
	
	public HashSet<GameEntity> getEntities(){
		return gameEntities;
	}
	
	public void spawnGameObject(GameObject e) {spawnGameObject(e,true);}
	public void spawnGameObject(GameObject e, boolean creation) {
		e.load();
		other.add(e);
		sortConsumer.accept(e);
		renderGameObject(e, currentDistance<=RENDER_ALL);
		loadAStarEffectsForEntity(e);
	}
	
	public void despawnGameObject(GameObject e) {despawnGameObject(e, true);}
	public void despawnGameObject(GameObject e, boolean destruction) {
		if(destruction) {destroyEntity(e);}
		renderGameObject(e, false);
		boolean removed = other.remove(e);
		if(!removed) {
			large.remove(e);
			medium.remove(e);
			small.remove(e);
		}
		desortConsumer.accept(e);
		deloadAstarEffectsForEntity(e);
	}
	
	private void destroyEntity(GameObject e) {
		e.delete();
		if(e instanceof DiesOnDespawn d) {
			d.die();
		}
		if(e instanceof Death.Drops d &&
				((d instanceof DropsOnDespawn) || 
				(d instanceof DropsOnKill k && k.isKilled()) || 
				(d instanceof DropsOnDeath j && j.isDead()))) {
			EnvironmentChunk.spawnObject(storage, d.drops());
		}
		
	}
	
	public static void transferGameObject(EnvironmentChunk from, EnvironmentChunk to, GameObject e) {
		if(from!=null)from.looseGameEntity(e);
		if(to!=null)to.recieveGameEntity(e);
		else from.despawnGameObject(e, true);
	}
	
	public static void spawnObject(EnvironmentStorage storage, GameObject e) {
		EnvironmentChunk c = storage.getChunk(WorldChunk.getChunk(e.getPosition()));
		if(c!=null) {
			c.spawnGameObject(e, true);
		}
	}
	
	public static void spawnObject(EnvironmentStorage storage, GameObject...es) {
		for(GameObject e:es) {
			spawnObject(storage, e);
		}
	}
	public static void spawnObject(EnvironmentStorage storage, Collection<? extends GameObject> es) {
		for(GameObject e:es) {
			spawnObject(storage, e);
		}
	}
	
	public static void despawnObject(EnvironmentStorage storage, GameObject e) {
		EnvironmentChunk c = storage.getChunk(WorldChunk.getChunk(e.getPosition()));
		if(c!=null) {
			c.despawnGameObject(e, true);
		}
	}
	
	public static void despawnObject(EnvironmentStorage storage, GameObject...es) {
		for(GameObject e:es) {
			despawnObject(storage, e);
		}
	}
	public static void despawnObject(EnvironmentStorage storage, Collection<? extends GameObject> es) {
		for(GameObject e:es) {
			despawnObject(storage, e);
		}
	}
	
	public void looseGameEntity(GameObject e) {
		despawnGameObject(e, false);
	}
	
	public void recieveGameEntity(GameObject e) {
		spawnGameObject(e, false);
	}
	
	
	
	public boolean transferGameEntity(EnvironmentChunk c, GameEntity... es) {
		boolean removed = true;
		for(GameEntity e : es) {
			removed |= transferGameEntity(c, e);
		}
		return removed;
	}
	
	public PhysicsCollidableGrid getCollidables(){
		return collisionGrid;
	}
	
	
	void delete() {
		if(rendering) {
			remove();
		}
		
		for(GameObject other : other) {
			other.delete();
		}
		for(GameObject large : large) {
			large.delete();
		}
		for(GameObject medium : medium) {
			medium.delete();
		}
		for(GameObject small : small) {
			small.delete();
		}
		for(GameEntity entity : gameEntities) {
			entity.delete();
		}
		
		loaded = false;
	}

}
