package game.physics.collisions.loaders;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import game.physics.collisions.data.BoundingBox.Boxes;
import rendering.loaders.Vertex;

class BoundingBox extends BoundingSphere{
	
	float xMin, xMax, yMin, yMax, zMin, zMax;
	private final Boxes rotatable;
	
	
	protected BoundingBox(Boxes box) {
		super();
		this.rotatable=box;
	}
	
	@Override
	public void initiate() {
		super.initiate();
		xMin = Float.POSITIVE_INFINITY; xMax = Float.NEGATIVE_INFINITY; 
		yMin = Float.POSITIVE_INFINITY; yMax = Float.NEGATIVE_INFINITY; 
		zMin = Float.POSITIVE_INFINITY; zMax = Float.NEGATIVE_INFINITY;
	}
	@Override
	public void loadVertex(Vertex v) {
		super.loadVertex(v);
		
		
		Vector3f pos = v.getPosition();
		if(pos.x<xMin)xMin=pos.x; if(pos.x>xMax)xMax=pos.x;
		if(pos.y<yMin)yMin=pos.y; if(pos.y>yMax)yMax=pos.y;
		if(pos.z<zMin)zMin=pos.z; if(pos.z>zMax)zMax=pos.z;
	}
	
	@Override
	public void finish(List<Vertex> vertices) {
		currentData= new game.physics.collisions.data.BoundingBox(rotatable, furthestDistance, xMin, xMax, yMin, yMax, zMin, zMax);
	}
	
	void overrideFinish(List<Vertex> vertices, Float x1, Float x2, Float y1, Float y2, Float z1, Float z2) {
		xMin=x1==null?xMin:x1;
		xMax=x2==null?xMax:x2;
		yMin=y1==null?yMin:y1;
		yMax=y2==null?yMax:y2;
		zMin=z1==null?zMin:z1;
		zMax=z2==null?zMax:z2;
		
		loadVector(new Vector3f(xMin, yMin, zMin));
		loadVector(new Vector3f(xMin, yMin, zMax));
		loadVector(new Vector3f(xMin, yMax, zMin));
		loadVector(new Vector3f(xMin, yMax, zMax));
		loadVector(new Vector3f(xMax, yMin, zMin));
		loadVector(new Vector3f(xMax, yMin, zMax));
		loadVector(new Vector3f(xMax, yMax, zMin));
		loadVector(new Vector3f(xMax, yMax, zMax));
		
		finish(vertices);
		
	}
}
