package game.physics.collisions.loaders;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import game.physics.collisions.data.CollisionData;
import rendering.loaders.Vertex;

class BoundingSphere implements CollisionDataLoader{
	
	protected float furthestDistance;
	protected CollisionData currentData;
	
	protected BoundingSphere() {}
	
	@Override
	public void initiate() {
		furthestDistance = Float.NEGATIVE_INFINITY;
	}
	@Override
	public void loadVertex(Vertex v) {
		float length = v.getLength();
		if(length>furthestDistance) {
			furthestDistance=length;
		}
	}
	protected void loadVector(Vector3f v) {
		float length = v.length();
		if(length>furthestDistance) {
			furthestDistance=length;
		}
	}
	@Override
	public void finish(List<Vertex> vertices) {
		currentData = new game.physics.collisions.data.BoundingSphere(furthestDistance);
	}
	@Override
	public CollisionData getLoadedData() {
		return currentData;
	}
	

}
