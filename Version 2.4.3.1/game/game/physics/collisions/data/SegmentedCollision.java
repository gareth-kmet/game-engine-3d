package game.physics.collisions.data;

import entities.RenderEntity;
import game.physics.collisions.PhysicsMovement;
import game.physics.collisions.PhysicsCollisions.SEGMENTED_COLLISION_COLLISION;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;

public class SegmentedCollision extends CollisionData implements SEGMENTED_COLLISION_COLLISION{
	
	public final CollisionData[] datas;
	
	public SegmentedCollision(CollisionData[] datas) {
		super(getFurthestDistance(datas));
		this.datas=datas;
	}

	@Override
	public float terrainCollision(RenderEntity ent, TerrainHeightFinder heights, PhysicsMovement move) {
		float minValue = Float.POSITIVE_INFINITY;
		for(CollisionData d : datas) {
			float value = d.terrainCollision(ent, heights, move);
			if(value<minValue) {
				minValue = value;
			}
		}
		return minValue;
	}
	
	private static float getFurthestDistance(CollisionData[] datas) {
		float furthest = Float.NEGATIVE_INFINITY;
		for(CollisionData d:datas) {
			float dist = d.getFurthestPoint();
			if(dist>furthest) {
				furthest=dist;
			}
		}
		
		return furthest;
	}

	@Override
	public CollisionData[] getCollisionDatas() {
		return datas;
	}

}
