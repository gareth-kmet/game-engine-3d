package game.physics.collisions;

import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_ALL_COLLISIONS;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_FULL_XZ_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_NULL_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_PART_XZ_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.MOVEMENT_ROT_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.NEGATIVE_Y_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.POSITIVE_Y_COLLISION;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.physics.collisions.data.CollisionData;

public enum PhysicsCollisions {
	NULL {
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
	},
	BOUNDING_SPHERE {
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			BOUNDING_SPHERE_COLLISION b1 = (BOUNDING_SPHERE_COLLISION)d1;
			BOUNDING_SPHERE_COLLISION b2 = (BOUNDING_SPHERE_COLLISION)d2;
			return PhysicsCollisionMethods.boundingSphere_boundingSphere(o1, r1, e1, b1, o2, r2, e2, b2);
		}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			BOUNDING_SPHERE_COLLISION b1 = (BOUNDING_SPHERE_COLLISION)d1;
			BOUNDING_BOX_COLLISION b2 = (BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.boundingSphere_boundingBox(o1, r1, e1, b1, o2, r2, e2, b2);
		}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return SEGMENTED_COLLISION.collision$BOUNDING_SPHERE(o2, r2, e2, d2, o1, r1, e1, d1);
		}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			return Y_ROTATED_BOUNDING_BOX.collision$BOUNDING_SPHERE(p2, r2, e2, d2, p1, r1, e1, d1);
		}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return ROTATED_BOUNDING_BOX.collision$BOUNDING_SPHERE(p2, r2, e2, d2, p1, r1, e1, d1);
		}
	},
	BOUNDING_BOX {
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return BOUNDING_SPHERE.collision$BOUNDING_BOX(o2,r2,e2,d2,o1,r1, e1, d1);
		}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			BOUNDING_BOX_COLLISION b1 = (BOUNDING_BOX_COLLISION)d1;
			BOUNDING_BOX_COLLISION b2 = (BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.boundingBox_boundingBox(o1, r1, e1, b1, o2, r2, e2, b2);
		}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return SEGMENTED_COLLISION.collision$BOUNDING_BOX(o2, r2, e2, d2, o1, r1, e1, d1);
		}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			return Y_ROTATED_BOUNDING_BOX.collision$BOUNDING_BOX(p2, r2, e2, d2, p1, r1, e1, d1);
		}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return ROTATED_BOUNDING_BOX.collision$BOUNDING_BOX(p2, r2, e2, d2, p1, r1, e1, d1);
		}
	},
	Y_ROTATED_BOUNDING_BOX{
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			Y_ROTATED_BOUNDING_BOX_COLLISION b1 = (Y_ROTATED_BOUNDING_BOX_COLLISION)d1;
			BOUNDING_SPHERE_COLLISION b2 = (BOUNDING_SPHERE_COLLISION)d2;
			return PhysicsCollisionMethods.yRotatedBoundingBox_boundingSphere(p1, r1, e1, b1, p2, r2, e2, b2);
		}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			Y_ROTATED_BOUNDING_BOX_COLLISION b1 = (Y_ROTATED_BOUNDING_BOX_COLLISION)d1;
			BOUNDING_BOX_COLLISION b2 = (BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.yRotatedBoundingBox_boundingBox(p1, r1, e1, b1, p2, r2, e2, b2);
		}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			Y_ROTATED_BOUNDING_BOX_COLLISION b1 = (Y_ROTATED_BOUNDING_BOX_COLLISION)d1;
			Y_ROTATED_BOUNDING_BOX_COLLISION b2 = (Y_ROTATED_BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.yRotatedBoundingBox_yRotatedBoundingBox(p1, r1, e1, b1, p2, r2, e2, b2);
		}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			return SEGMENTED_COLLISION.collision$Y_ROTATED_BOUNDING_BOX(p2, r2, e2, d2, p1, r1, e1, d1);
		}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			return ROTATED_BOUNDING_BOX.collision$Y_ROTATED_BOUNDING_BOX(p2, r2, e2, d2, p1, r1, e1, d1);
		}
	},
	@Deprecated
	ROTATED_BOUNDING_BOX{
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			ROTATED_BOUNDING_BOX_COLLISION t1 = (ROTATED_BOUNDING_BOX_COLLISION)d1;
			BOUNDING_SPHERE_COLLISION t2 = (BOUNDING_SPHERE_COLLISION)d2;
			return PhysicsCollisionMethods.rotatedBoundingBox_boundingSphere(p1, r1, e1, t1, p2, r2, e2, t2);
		}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			ROTATED_BOUNDING_BOX_COLLISION t1 = (ROTATED_BOUNDING_BOX_COLLISION)d1;
			BOUNDING_BOX_COLLISION t2 = (BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.rotatedBoundingBox_boundingBox(p1, r1, e1, t1, p2, r2, e2, t2);
		}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			return SEGMENTED_COLLISION.collision$ROTATED_BOUNDING_BOX(p2, r2, e2, d2, p1, r1, e1, d1);
		}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1,Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			ROTATED_BOUNDING_BOX_COLLISION t1 = (ROTATED_BOUNDING_BOX_COLLISION)d1;
			Y_ROTATED_BOUNDING_BOX_COLLISION t2 = (Y_ROTATED_BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.rotatedBoundingBox_yRotatedBoundingBox(p1, r1, e1, t1, p2, r2, e2, t2);
		}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			ROTATED_BOUNDING_BOX_COLLISION t1 = (ROTATED_BOUNDING_BOX_COLLISION)d1;
			ROTATED_BOUNDING_BOX_COLLISION t2 = (ROTATED_BOUNDING_BOX_COLLISION)d2;
			return PhysicsCollisionMethods.rotatedBoundingBox_rotatedBoundingBox(p1, r1, e1, t1, p2, r2, e2, t2);
		}
		
	},
	SEGMENTED_COLLISION{
		@Override
		public boolean collision$BOUNDING_SPHERE(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			for(CollisionData d : ((SEGMENTED_COLLISION_COLLISION)d1).getCollisionDatas()) {
				boolean collide = d.getCollisionType(null).collision$BOUNDING_SPHERE(o1, r1, e1, d, o2, r2, e2, d2);
				if(collide)return true;
			}
			return false;
		}
		@Override
		public boolean collision$BOUNDING_BOX(Vector3f o1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f o2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			for(CollisionData d : ((SEGMENTED_COLLISION_COLLISION)d1).getCollisionDatas()) {
				boolean collide = d.getCollisionType(null).collision$BOUNDING_BOX(o1, r1, e1, d, o2, r2, e2, d2);
				if(collide)return true;
			}
			return false;
		}
		@Override
		public boolean collision$SEGMENTED_COLLISION(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
			CollisionData[] datas1 = ((SEGMENTED_COLLISION_COLLISION)d1).getCollisionDatas();
			CollisionData[] datas2 = ((SEGMENTED_COLLISION_COLLISION)d2).getCollisionDatas();
			
			for(CollisionData b1 : datas1) {
				for(CollisionData b2:datas2) {
					boolean collide = collision(p1, r1, e1, b1, p2, r2, e2, b2);
					if(collide)return true;
				}
			}
			return false;
		}
		@Override
		public boolean collision$Y_ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			for(CollisionData d : ((SEGMENTED_COLLISION_COLLISION)d1).getCollisionDatas()) {
				boolean collide = d.getCollisionType(null).collision$Y_ROTATED_BOUNDING_BOX(p1, r1, e1, d, p2, r2, e2, d2);
				if(collide)return true;
			}
			return false;
		}
		@Override
		public boolean collision$ROTATED_BOUNDING_BOX(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2,Vector3f r2, RenderEntity e2, CollisionData d2) {
			for(CollisionData d : ((SEGMENTED_COLLISION_COLLISION)d1).getCollisionDatas()) {
				boolean collide = d.getCollisionType(null).collision$ROTATED_BOUNDING_BOX(p1, r1, e1, d, p2, r2, e2, d2);
				if(collide)return true;
			}
			return false;
		}
	},
	;
	
	public 			boolean collision$NULL						(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {return false;}
	public abstract boolean collision$BOUNDING_SPHERE			(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2);
	public abstract boolean collision$BOUNDING_BOX				(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2);
	public abstract boolean collision$SEGMENTED_COLLISION		(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2);
	public abstract boolean collision$Y_ROTATED_BOUNDING_BOX	(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2);
	@Deprecated
	public abstract boolean collision$ROTATED_BOUNDING_BOX		(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2);
	
	public interface COLLISION{
		public default PhysicsCollisions getCollisionType() {return getCollisionType(null);}
		public PhysicsCollisions getCollisionType(CollideCondition condition);
	};
	public interface NULL_COLLISION extends COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return NULL;}
	};
	public interface BOUNDING_SPHERE_COLLISION extends COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return BOUNDING_SPHERE;}
		public float getBoundingRadius();
	};
	public interface BOUNDING_BOX_COLLISION extends COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return BOUNDING_BOX;}
		public float[] getMinBoundsClone();
		public float[] getMaxBoundsClone();
	};
	public interface Y_ROTATED_BOUNDING_BOX_COLLISION extends BOUNDING_BOX_COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return Y_ROTATED_BOUNDING_BOX;}
	}
	public interface ROTATED_BOUNDING_BOX_COLLISION extends Y_ROTATED_BOUNDING_BOX_COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return ROTATED_BOUNDING_BOX;}
	}
	public interface SEGMENTED_COLLISION_COLLISION extends COLLISION{
		@Override public default PhysicsCollisions getCollisionType(CollideCondition condition) {return SEGMENTED_COLLISION;}
		public CollisionData[] getCollisionDatas();
	}
	

	//	----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static boolean collision(Vector3f p1, Vector3f r1, RenderEntity e1, CollisionData d1, Vector3f p2, Vector3f r2, RenderEntity e2, CollisionData d2) {
		PhysicsCollisions t1 = d1.getCollisionType(e1.getCollideCondition());
		PhysicsCollisions t2 = d2.getCollisionType(e2.getCollideCondition());
		return switch(t2) {
			case SEGMENTED_COLLISION 	-> t1.collision$SEGMENTED_COLLISION(p1, r1, e1, d1, p2, r2, e2, d2);
			case BOUNDING_SPHERE 		-> t1.collision$BOUNDING_SPHERE(p1, r1, e1, d1, p2, r2, e2, d2);
			case BOUNDING_BOX 			-> t1.collision$BOUNDING_BOX(p1, r1, e1, d1, p2, r2, e2, d2);
			case Y_ROTATED_BOUNDING_BOX -> t1.collision$Y_ROTATED_BOUNDING_BOX(p1, r1, e1, d1, p2, r2, e2, d2);
			case ROTATED_BOUNDING_BOX 	-> t1.collision$ROTATED_BOUNDING_BOX(p1, r1, e1, d1, p2, r2, e2, d2);
			default 					-> t1.collision$NULL(p1,r1,e1,d1,p2,r2, e2, d2);
		};
	}
	
	private static final int MAX_ROTATION = 90;
	private static final float INVERSE_MAX_ROTATION = 1f/MAX_ROTATION;

	
	public static byte movementCollision(PhysicsMovement o1, RenderEntity e1, CollisionData d1, PhysicsMovement o2, RenderEntity e2, CollisionData d2) {
		Vector3f p1a = e1.getPosition();
		Vector3f p1b = o1.getAppliedMovementToPosition(p1a);
		Vector3f p2 = o2.getAppliedMovementToPosition(e2.getPosition());
		Vector3f movement = o1.getMovement();
		Vector3f r1 = new Vector3f(e1.getRotX(),e1.getRotY(),e1.getRotZ());
		Vector3f appliedR1 = new Vector3f(e1.getRotX(),e1.getRotY()+o1.getXzMovementRotation(),e1.getRotZ());
		Vector3f appliedR2 = new Vector3f(e2.getRotX(),e2.getRotY()+o2.getXzMovementRotation(),e2.getRotZ());
		
		if(PhysicsCollisions.collision(p1b,appliedR1,e1,d1,p2,appliedR2,e2,d2)) {
			byte collision = EXIST_NULL_COLLISION;
			
			if(!PhysicsCollisions.collision(p1b, r1, e1, d1, p2, appliedR2, e2, d2)) {
				collision|=MOVEMENT_ROT_COLLISION;
				return collision;
			}
			
			Vector3f tryY = Vector3f.add(p1a, new Vector3f(0,movement.y,0), new Vector3f());
			if(PhysicsCollisions.collision(tryY, appliedR1,e1, d1, p2, appliedR2,e2, d2)) {
				if(movement.y<0) {
					collision |= POSITIVE_Y_COLLISION;
				}else if(movement.y>0) {
					collision |= NEGATIVE_Y_COLLISION;
				}else {
					collision |= POSITIVE_Y_COLLISION|NEGATIVE_Y_COLLISION;
				}
			}
			
			
			float initialDistance = o1.getXzMovementDistance();
			float initialRotation = o1.getXzMovementRotation();
			
			float turn = MAX_ROTATION * o1.collisionSpecificity;
			int i=0;
			float currentTurn = 0;
			boolean found = false;
			setMovementLoop: while(currentTurn<MAX_ROTATION) {
				float thisDistance = (1-currentTurn*INVERSE_MAX_ROTATION)*initialDistance;
				
				o1.setXzMovementDistance(thisDistance);
				o1.setXzMovementRotation(initialRotation+currentTurn);
				o1.fixMovementVector();
				Vector3f positionPositve = o1.getAppliedMovementToPosition(p1a);
				boolean collisionPositive = PhysicsCollisions.collision(positionPositve, appliedR1, e1, d1, p2, appliedR2, e2, d2);
				if(!collisionPositive) {
					found=true;
					break setMovementLoop;
				}
				
				if(i==0) {
					currentTurn=turn*++i;
					continue setMovementLoop;
				}
				
				o1.setXzMovementDistance(thisDistance);
				o1.setXzMovementRotation(initialRotation+-currentTurn);
				o1.fixMovementVector();
				Vector3f positionNegative = o1.getAppliedMovementToPosition(p1a);
				boolean collisionNegative = PhysicsCollisions.collision(positionNegative, appliedR1, e1, d1, p2, appliedR2, e2, d2);
				if(!collisionNegative) {
					found=true;
					break setMovementLoop;
				}
				currentTurn=turn*++i;
			}
			if(i!=0) {
				if(found) {
					collision|=EXIST_PART_XZ_COLLISION;
				}else {
					collision|=EXIST_FULL_XZ_COLLISION;
				}
			}
			
			o1.setXzMovementRotation(initialRotation);
			
			if(collision==EXIST_NULL_COLLISION) {
				collision=EXIST_ALL_COLLISIONS;
			}
			return collision;
		}else {
			return EXIST_NULL_COLLISION;
		}
	}
	/*
	@Deprecated
	public static byte _movementCollision(PhysicsMovement o1, Entity e1, CollisionData d1, PhysicsMovement o2, Entity e2, CollisionData d2) {
		Vector3f p1a = e1.getPosition();
		Vector3f p1b = o1.getAppliedMovementToPosition(p1a);
		Vector3f p2 = o2.getAppliedMovementToPosition(e2.getPosition());
		Vector3f movement = o1.getMovement();

		if(PhysicsCollisions.collision(p1b,e1,d1,p2,e2,d2)) {
			byte collision = EXIST_NULL_COLLISION;
			
			Vector3f tryX = Vector3f.add(p1a, new Vector3f(movement.x,0,0), new Vector3f());
			Vector3f tryY = Vector3f.add(p1a, new Vector3f(0,movement.y,0), new Vector3f());
			Vector3f tryZ = Vector3f.add(p1a, new Vector3f(0,0,movement.z), new Vector3f());
			
			if(PhysicsCollisions.collision(tryX, e1, d1, p2, e2, d2)) {
				if(movement.x<0) {
					collision |= POSITIVE_X_COLLISION;
				}else if(movement.x>0) {
					collision |= NEGATIVE_X_COLLISION;
				}else {
					collision |= POSITIVE_X_COLLISION|NEGATIVE_X_COLLISION;
				}
			}
			if(PhysicsCollisions.collision(tryY, e1, d1, p2, e2, d2)) {
				if(movement.y<0) {
					collision |= POSITIVE_Y_COLLISION;
				}else if(movement.y>0) {
					collision |= NEGATIVE_Y_COLLISION;
				}else {
					collision |= POSITIVE_Y_COLLISION|NEGATIVE_Y_COLLISION;
				}
			}
			if(PhysicsCollisions.collision(tryZ, e1, d1, p2, e2, d2)) {
				if(movement.z<0) {
					collision |= POSITIVE_Z_COLLISION;
				}else if(movement.z>0) {
					collision |= NEGATIVE_Z_COLLISION;
				}else {
					collision |= POSITIVE_Z_COLLISION|NEGATIVE_Z_COLLISION;
				}
			}
			if(collision==EXIST_NULL_COLLISION) {
				collision=EXIST_ALL_COLLISIONS;
			}
			return collision;
		}else {
			return EXIST_NULL_COLLISION;
		}
	}
	*/
	
}
