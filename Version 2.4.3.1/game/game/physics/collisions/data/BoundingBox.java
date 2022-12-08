package game.physics.collisions.data;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.physics.collisions.CollideCondition;
import game.physics.collisions.PhysicsCollisions;
import game.physics.collisions.PhysicsMovement;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;

public class BoundingBox extends CollisionData implements PhysicsCollisions.ROTATED_BOUNDING_BOX_COLLISION{
	
	protected final float xMin, xMax, yMin, yMax, zMin, zMax;
	protected final float[] mins, maxs;
	protected final Boxes collisionType;
	
	public enum Boxes implements CollideCondition{
		STATIC(PhysicsCollisions.BOUNDING_BOX),
		Y_ROTATABLE(PhysicsCollisions.Y_ROTATED_BOUNDING_BOX),
		@Deprecated
		ROTATABLE(PhysicsCollisions.ROTATED_BOUNDING_BOX);
		private PhysicsCollisions collision;
		private Boxes(PhysicsCollisions collision) {this.collision=collision;}
	}

	public BoundingBox(Boxes box, float furthestPoint, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		super(furthestPoint);
		this.xMin=xMin; this.xMax=xMax;
		this.yMin=yMin; this.yMax=yMax;
		this.zMin=zMin; this.zMax=zMax;
		this.collisionType=box;
		
		mins = new float[] {xMin, yMin, zMin};
		maxs = new float[] {xMax, yMax, zMax};
	}

	@Override
	public float terrainCollision(RenderEntity ent, TerrainHeightFinder heights, PhysicsMovement move) {
		Vector3f appliedPos = move.getAppliedMovementToPosition(ent.getPosition());
		float entHeight = appliedPos.y+yMin*ent.getScale();
		return entHeight - heights.getHeight(appliedPos);
	}

	@Override
	public float[] getMinBoundsClone() {
		return mins.clone();
	}

	@Override
	public float[] getMaxBoundsClone() {
		return maxs.clone();
	}
	
	@Override
	public PhysicsCollisions getCollisionType(CollideCondition condition) {
		if(condition==null || !(condition instanceof Boxes)) {
			return collisionType.collision;
		}
		return ((Boxes)condition).collision;
	}

	
}
