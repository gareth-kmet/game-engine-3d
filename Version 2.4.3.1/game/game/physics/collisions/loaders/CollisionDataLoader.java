package game.physics.collisions.loaders;

import java.util.List;

import game.physics.collisions.data.CollisionData;
import game.physics.collisions.data.BoundingBox.Boxes;
import rendering.loaders.Vertex;

public interface CollisionDataLoader{
	
	public void initiate();
	public void loadVertex(Vertex v);
	public void finish(List<Vertex> vertices);
	public CollisionData getLoadedData();
	public default boolean canCollide() {return true;};
	
	public enum Loaders {
		
		NULL_COLLISIONS{
			private final CollisionDataLoader NULL_COLLISIONS = new CollisionDataLoader(){
				@Override public void initiate() {}
				@Override public void loadVertex(Vertex v) {}
				@Override public void finish(List<Vertex> vertices) {}
				@Override public CollisionData getLoadedData() {return CollisionData.NULL_COLLISION;}
				@Override public boolean canCollide() {return false;}
			};
			@Override public boolean canCollide() {return false;}
			@Override public CollisionDataLoader New(float...fs) {return NULL_COLLISIONS;}
		},
		FURTHEST_POINT_SPHERE 									{@Override public CollisionDataLoader New(float...conditions) {return new BoundingSphere();}},
		BOUNDING_BOX 											{@Override public CollisionDataLoader New(float...conditions) {return new BoundingBox(Boxes.STATIC);}},
		Y_ROTATABLE_BOUNDING_BOX 								{@Override public CollisionDataLoader New(float...conditions) {return new BoundingBox(Boxes.Y_ROTATABLE);}},
		@Deprecated
		ROTATABLE_BOUNDING_BOX 									{@Override public CollisionDataLoader New(float...conditions) {return new BoundingBox(Boxes.ROTATABLE);}},
		SQUARE_XZ_BOUNDING_BOX_MIN								{@Override public CollisionDataLoader New(float...conditions) {return new SquareXZPlaneBoundingBox(Boxes.STATIC, SquareXZPlaneBoundingBox.TYPE.MIN);}},
		SQUARE_XZ_BOUNDING_BOX_MAX								{@Override public CollisionDataLoader New(float...conditions) {return new SquareXZPlaneBoundingBox(Boxes.STATIC, SquareXZPlaneBoundingBox.TYPE.MAX);}},
		SQUARE_XZ_BOUNDING_BOX_AVG								{@Override public CollisionDataLoader New(float...conditions) {return new SquareXZPlaneBoundingBox(Boxes.STATIC, SquareXZPlaneBoundingBox.TYPE.AVERAGE);}},
		VERTICAL_SEGMENT_FULL_HEIGHT_BOUNDING_BOX				{@Override public CollisionDataLoader New(float...conditions) {return new VerticalSegmentFullHeightBoundingBox(Boxes.STATIC, conditions[0]);}},
		Y_ROTATABLE_VERTICAL_SEGMENT_FULL_HEIGHT_BOUNDING_BOX	{@Override public CollisionDataLoader New(float...conditions) {return new VerticalSegmentFullHeightBoundingBox(Boxes.Y_ROTATABLE, conditions[0]);}},
		@Deprecated 
		ROTATABLE_VERTICAL_SEGMENT_FULL_HEIGHT_BOUNDING_BOX		{@Override public CollisionDataLoader New(float...conditions) {return new VerticalSegmentFullHeightBoundingBox(Boxes.ROTATABLE, conditions[0]);}},
		;
		
		public abstract CollisionDataLoader New(float...conditions);
		public boolean canCollide() {
			return true;
		}
	}
}
