package game.physics.collisions.data;

import entities.RenderEntity;
import game.physics.collisions.PhysicsCollisions;
import game.physics.collisions.PhysicsMovement;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;

public abstract class CollisionData implements PhysicsCollisions.COLLISION{
	
	
	public static final NULL_COLLISIONS NULL_COLLISION = new NULL_COLLISIONS();
	public static final class NULL_COLLISIONS extends CollisionData implements PhysicsCollisions.NULL_COLLISION {
		protected NULL_COLLISIONS() {super(Float.NaN);}
		@Override public float terrainCollision(RenderEntity ent, TerrainHeightFinder heights, PhysicsMovement move) {return 0;}
		@Override public boolean canCollide() {return false;};
	};
	
	protected final float furthestPoint;
	
	protected CollisionData(float furthestPoint) {
		this.furthestPoint = furthestPoint;
	}
	
	public float getFurthestPoint() {
		return furthestPoint;
	}
	
	
	public boolean canCollide() {return true;}
	
	public abstract float terrainCollision(RenderEntity ent, TerrainHeightFinder heights, PhysicsMovement move);
	
	
	

}
