package game.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.ai.GameAStarTools;
import game.ai.astar.AStar.AStarEffect;
import game.ai.astar.AStarGrid.AStarGridAffectingObject;
import game.objects.entities.MoveableGameEntity;
import game.objects.informations.Physics.Collidable;
import game.objects.informations.Physics.Collidable.StaticCollidable;
import game.physics.collisions.PhysicsCollidableGrid;
import game.physics.collisions.PhysicsCollisions;
import game.physics.collisions.data.BoundingBox;
import game.physics.collisions.data.CollisionData;
import game.world.WorldChunk;
import rendering.entities.EntityRenderCondition;

public abstract class GameObject implements AStarGridAffectingObject{
	
	protected Vector3f position, rotation;
	protected float scale;
	private boolean loaded = false;
	
	
	private final HashMap<Integer, RenderEntity> objectEntities = new HashMap<Integer,RenderEntity>();
	private final HashSet<RenderEntity> objectCollidableEntities = new HashSet<RenderEntity>();
	private final HashSet<Point> affectingNodes = new HashSet<Point>();
	private float affectingRadius=0f;
	
	protected GameObject(Vector3f pos, Vector3f rot, float scale) {
		requireLoadErrors(this);
		setPosition(pos);
		setRotation(rot);
		setScale(scale);
	}
	
	
	
	/**
	 * 
	 * Must be called before the entity can be realistically used.
	 * The Object must be loaded before it can pass entities or other items.
	 */
	public void load() {
		if(loaded)return;
		for(RenderEntity entity : objectEntities.values()) {
			entity.loadTexturedModelType();
		}	
		setAffectingNodes(this);
		loaded = true;
	}
	
	private static void setAffectingNodes(GameObject o) {
		if(o instanceof Collidable) {
			setCollisionAffectingNodes(o);
		}else {
			Vector3f pos = WorldChunk.getChunkLocalPos(o.position);
			Point p = new Point((int)pos.x, (int)pos.z);
			o.affectingNodes.add(p);
		}
	}
	
	private static void setCollisionAffectingNodes(GameObject o) {
		for(RenderEntity e : o.objectCollidableEntities) {
			Vector3f localPos = WorldChunk.getChunkLocalPos(e.getPosition());
			CollisionData d = e.getCollisionData();
			float furthestPoint = d.getFurthestPoint()*o.scale;
			if(furthestPoint>o.affectingRadius) {
				o.affectingRadius=furthestPoint;
			}
			PhysicsCollidableGrid.forEachPossiblePositionInRadius(localPos, furthestPoint, 
				(i,j)->{
					CollisionData nodeCollisionData = new BoundingBox(BoundingBox.Boxes.STATIC, Float.POSITIVE_INFINITY, 0, 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0, 1);
					if(PhysicsCollisions.collision(localPos, e.getRotationVector(), e, d, new Vector3f(i,0,j), new Vector3f(), GameAStarTools.NODE_ENTITY, nodeCollisionData)) {
						Point p = new Point(i,j);
						if(!o.affectingNodes.contains(p)) {
							o.affectingNodes.add(p);
						}
					}
				}
			);
		}
	}
	
	private int nextAvailableId = -1;
	protected int getNextID(){
		return nextAvailableId--;
	}
	
	private final void entityAddError() {
		if(loaded) throw new IllegalAccessError();
	}
	
	protected void addEntity(boolean testForCollision, RenderEntity e) {
		addEntity(testForCollision, getNextID(), e);
	}
	/**
	 * 
	 * @param e
	 * @param id of entity - must be positive
	 */
	protected void addEntity(boolean testForCollision, int id, RenderEntity e) {
		entityAddError();
		objectEntities.put(id,e);
		if(testForCollision && e.canCollide()) {
			objectCollidableEntities.add(e);
		}
	}
	
	protected void addEntity(boolean testForCollision, RenderEntity e1, RenderEntity ...es) {
		addEntity(testForCollision, e1);
		for(RenderEntity e : es) {
			addEntity(testForCollision, e);
		}
	}
	
	protected void addEntity(boolean testForCollision, int[] ids, RenderEntity e1, RenderEntity ...es) {
		addEntity(testForCollision, ids[0], e1);
		for(int i=0; i<es.length;i++) {
			addEntity(testForCollision, ids[i+1], es[i]);
		}
	}
	
	public RenderEntity getEntity(int id) {
		return objectEntities.get(id);
	}
	
	public ArrayList<RenderEntity> getEntities(int id1, int ... ids){
		ArrayList<RenderEntity> es = getEntities(ids);
		es.add(0,getEntity(id1));
		return es;
	}
	
	public ArrayList<RenderEntity> getEntities(int[] ids){
		ArrayList<RenderEntity> es = new ArrayList<RenderEntity>();
		for(int id : ids) {
			es.add(getEntity(id));
		}
		return es;
	}
	
	
	public Vector3f getPosition() {return position;}
	public Vector3f getRotation() {return rotation;}
	public float getScale() {return scale;}
	
	public final void setPosition(Vector3f pos) {
		setPosition$(pos); 
		for(RenderEntity e : objectEntities.values())
			e.setPosition(pos);
		position=pos;
	}
	public final void setRotation(Vector3f rot) {
		setRotation$(rot); 
		for(RenderEntity e : objectEntities.values()) {
			e.setRotX(rot.x);
			e.setRotY(rot.y);
			e.setRotZ(rot.z);
		}
		rotation=rot;
	}
	public final void setScale(float scale) {
		setScale$(scale);
		for(RenderEntity e : objectEntities.values())
			e.setScale(scale);
		this.scale=scale;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public HashSet<RenderEntity> getObjectCollidableEntities() {
		return (HashSet<RenderEntity>)objectCollidableEntities.clone();
	}

	/**
	 * Returns a clone so cannot be modified
	 */
	public Collection<RenderEntity> getObjectEntities(){
		return objectEntities.values();
	}
	
	protected void setPosition$(Vector3f pos) {};
	protected void setRotation$(Vector3f rot) {};
	protected void setScale$(float scale) {};
	
	
	public final void addAllRenderCondition(EntityRenderCondition cond) {
		for(RenderEntity e : objectEntities.values()) {
			e.addRenderCondition(cond);
		}
	}
	
	public void delete() {
		for(RenderEntity e : objectEntities.values()) {
			e.delete();
		}
		objectCollidableEntities.clear();
		objectEntities.clear();
	}
	
	
	public interface GameObjectType{
		public GameObject create(Vector3f pos, Vector3f rot, float scale);
		
	}
	
	@Override
	public AStarEffect getAStarEffect() {
		if(this instanceof Collidable) {
			return AStarEffect.DEFAULT_COLLISION_EFFECT;
		}else {
			return AStarEffect.NULL_EFFECT;
		}
	}
	
	@Override
	public HashSet<Point> getAffectingNodes() {
		requireLoaded(this);
		return affectingNodes;
	}
	
	public float getAffectingRadius() {
		requireLoaded(this);
		return affectingRadius;
	}
	
	protected static final void requireLoaded(GameObject object) {
		if(!object.loaded) {
			throw new IllegalStateException("GameObject not loaded");
		}
	}
	
	private static void requireLoadErrors(GameObject e) {
		if(e instanceof MoveableGameEntity && e instanceof StaticCollidable) {
			throw new IllegalStateException("Cannot be of type MoveableGameEntity and StaticCollidable");
		}
	}

}
