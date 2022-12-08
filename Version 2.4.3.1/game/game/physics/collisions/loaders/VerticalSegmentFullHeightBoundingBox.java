package game.physics.collisions.loaders;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import game.physics.collisions.data.CollisionData;
import game.physics.collisions.data.BoundingBox.Boxes;
import rendering.loaders.Vertex;

class VerticalSegmentFullHeightBoundingBox implements CollisionDataLoader{
	
	protected float minHeight, maxHeight;
	protected CollisionData currentData;
	protected final float percent;
	protected final Boxes rotatable;
	
	protected VerticalSegmentFullHeightBoundingBox(Boxes rotatable, float percent) {
		super();
		this.percent = percent;
		this.rotatable=rotatable;
	}
	
	@Override
	public void initiate() {
		minHeight = Float.POSITIVE_INFINITY;
		maxHeight = Float.NEGATIVE_INFINITY;
	}

	@Override
	public void loadVertex(Vertex v) {
		Vector3f pos = v.getPosition();
		if(pos.y<minHeight)minHeight=pos.y;
		if(pos.y>maxHeight)maxHeight=pos.y;
	}

	@Override
	public void finish(List<Vertex> vertices) {
		float p = Math.abs(percent);
		float pHeight = (maxHeight - minHeight)*p;
		BoundingBox bbox = new BoundingBox(rotatable);
		bbox.initiate();
		if(percent<0) {
			float minLookUpHeight = maxHeight - pHeight;
			for(Vertex vertex : vertices) {
				if(vertex.getPosition().y>=minLookUpHeight) {
					bbox.loadVertex(vertex);
				}
			}
			bbox.overrideFinish(vertices, null, null, minHeight, maxHeight, null, null);
			currentData = bbox.currentData;
		}else if(percent>0) {
			float maxLookUpHeight = minHeight + pHeight;
			for(Vertex vertex : vertices) {
				if(vertex.getPosition().y<=maxLookUpHeight) {
					bbox.loadVertex(vertex);
				}
			}
			bbox.overrideFinish(vertices, null, null, minHeight, maxHeight, null, null);
			currentData = bbox.currentData;
		}else {
			currentData = CollisionData.NULL_COLLISION;
		}
		
	}

	@Override
	public CollisionData getLoadedData() {
		return currentData;
	}

}
