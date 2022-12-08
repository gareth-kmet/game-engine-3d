package game.objects.informations;

import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.objects.GameObject;

public sealed interface Physics extends Information {
	
	public sealed interface Collidable extends Physics{
		
		public non-sealed interface StaticCollidable extends Collidable{
			public HashSet<Point> getAffectingNodes();
		}
		public non-sealed interface MovingCollidable extends Collidable{
			public float getAffectingRadius();
			public Vector3f getPosition();
			public HashSet<Point> setCurrentPoints(HashSet<Point> ps);
			public HashSet<Point> getCurrentPoints();
		}
	}
	
	/**
	 * Represents an object will not be tested for collision with another object (o) until after all other possible collisions have been checked. When checking, o's movement is not affected and collisionWith(o) will be called on this object.
	 *
	 */
	public non-sealed interface DoesNotAffectMovementCollisionTest extends Physics{
		public void collisionWith(GameObject o);
	}
	
	public sealed interface Ignorable extends Physics{
		/**
		 * Ignores collision detection if ignoreCollision() returns true;
		 */
		public non-sealed interface IgnorableCollision extends Ignorable{
			public boolean ignoreCollision();
		}
		/**
		 * Ignores collision detection with o if ignoreCollisionIf(o) returns true;
		 */
		public non-sealed interface IgnorableCollisionIf extends Ignorable{
			public boolean ignoreCollisionIf(GameObject o);
		}
	}
	

}
