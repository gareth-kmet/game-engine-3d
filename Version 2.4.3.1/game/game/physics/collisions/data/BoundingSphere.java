package game.physics.collisions.data;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.physics.collisions.PhysicsCollisions;
import game.physics.collisions.PhysicsMovement;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;

public class BoundingSphere extends CollisionData implements PhysicsCollisions.BOUNDING_SPHERE_COLLISION{
	
	public BoundingSphere(float furthestPoint) {
		super(furthestPoint);
	}

	@Override
	public float terrainCollision(RenderEntity ent, TerrainHeightFinder heights, PhysicsMovement move) {
		Vector3f appliedPosition = move.getAppliedMovementToPosition(ent.getPosition());
		float difference = appliedPosition.y-(getFurthestPoint()*ent.getScale())-heights.getHeight(appliedPosition);
		return difference;
	}


	@Override
	public float getBoundingRadius() {
		return furthestPoint;
	}

	

}
