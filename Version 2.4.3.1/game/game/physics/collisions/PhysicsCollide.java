package game.physics.collisions;

import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_ALL_COLLISIONS;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_FULL_XZ_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_NULL_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_ROTATION_COLLISION;
import static game.physics.collisions.PhysicsCollisionReturnData.EXIST_Y_COLLISION;

import java.util.Collection;
import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import game.objects.entities.MoveableGameEntity;
import game.objects.informations.Physics.Collidable;
import game.objects.informations.Physics.DoesNotAffectMovementCollisionTest;
import game.objects.informations.Physics.Ignorable.IgnorableCollision;
import game.objects.informations.Physics.Ignorable.IgnorableCollisionIf;
import game.physics.collisions.data.CollisionData;
import game.world.WorldChunk;
import game.world.WorldVariables;
import game.world.environment.EnvironmentChunk;
import game.world.environment.EnvironmentManager;
import game.world.terrain.TerrainGenerator.TerrainHeightFinder;

public final class PhysicsCollide {
	
	public static void updateMouvements(Collection<PhysicsMovement> movementes, WorldVariables variables, boolean clearMovementCollectionAfterUpdate) {
		
		movementLoop: for(PhysicsMovement movement:movementes) {
			if(movement.applied) {
				continue movementLoop;
			}
			Vector3f originPos = new Vector3f(movement.object.getPosition());
			if(!movement.updatesApplied) {
				movement.applyUpdatesToMovement(variables.currentFrameDeltaTime);
			}
			
			final PhysicsCollisionReturnData returnData;
			
			if(movement.object instanceof Collidable){
				float terrainCollision = testForTerrainCollision(variables.terrainVariables.heights, movement);
				boolean isTerrainCollision=dealWithTerrainCollision(variables.terrainVariables.heights, terrainCollision, movement);
				PhysicsCollisionReturnData objectCollision=dealWithObjectCollision(variables, movement);
				returnData = new PhysicsCollisionReturnData(isTerrainCollision, objectCollision);
//				returnData = new PhysicsCollisionReturnData(isTerrainCollision, PhysicsCollisionReturnData.NON_EXISTANT_COLLISION);
			}else {
				returnData = PhysicsCollisionReturnData.NON_EXISTANT_COLLISION;
			}
			movement.applyMovementToObject(returnData);
			Vector3f endPos = new Vector3f(movement.object.getPosition());
			EnvironmentManager.updateChunkOfMovableEntity(originPos, endPos, variables.environmentVariables.storage, movement.object);
			
		}
		
		if(clearMovementCollectionAfterUpdate) {
			movementes.clear();
		}
	}
	
	public static float testForTerrainCollision(TerrainHeightFinder heights, PhysicsMovement mouvement) {
		float minDifference = 0f;
		aLoop: for(RenderEntity aEnt : mouvement.object.getObjectEntities()) {
			CollisionData aCollision = aEnt.getCollisionData();
			if(!aCollision.canCollide()){
				continue aLoop;
			}
			
			float difference = aCollision.terrainCollision(aEnt, heights, mouvement);
			if(difference<minDifference) {
				minDifference=difference;
			}
		}
		
		return minDifference;
	}
	
	
	
	public static boolean dealWithTerrainCollision(TerrainHeightFinder heights, float difference, PhysicsMovement mouvement) {
		if(difference>=0)return false;
		
		mouvement.setyMovement(mouvement.getyMovement()-difference);
		mouvement.fixMovementVector();
		return true;
	}
	
	
	private static PhysicsCollisionReturnData dealWithObjectCollision(WorldVariables vars, PhysicsMovement movement) {
		
		if(movement.object instanceof IgnorableCollision i && i.ignoreCollision()) return PhysicsCollisionReturnData.NON_EXISTANT_COLLISION;
		
		HashSet<Collidable> collisions = new HashSet<Collidable>();
		byte collisionTotalByte = EXIST_NULL_COLLISION;
		
		if(movement.object.getObjectCollidableEntities().isEmpty())return new PhysicsCollisionReturnData(collisionTotalByte, false, collisions);
		
		Vector3f pos = movement.getAppliedMovementToPosition();
		Point p = WorldChunk.getChunk(pos);
		
		HashSet<Collidable> collidables = new HashSet<Collidable>();
		float radius = movement.object.getAffectingRadius() + movement.getMovement().length();
		
		PhysicsCollidableGrid[][] grids = new PhysicsCollidableGrid[3][3];
		for(int i = -1; i<=1; i++) for(int ii = -1; ii<=1; ii++){
			EnvironmentChunk chunk = vars.environmentVariables.storage.getChunk(p.getX()+i, p.getY()+ii);
			if(chunk!=null) {
				PhysicsCollidableGrid grid = chunk.getCollidables();
				grids[i+1][ii+1]=grid;
			}
		}
		
		
		Vector3f chunkLocalPos = WorldChunk.getChunkLocalPos(movement.object.getPosition());
		
		PhysicsCollidableGrid.forEachPossiblePositionInRadius(chunkLocalPos, radius, 
			(dx,dz)->{
				for(int cx=-1; cx<=1; cx++) for(int cz=-1; cz<=1; cz++) {
					PhysicsCollidableGrid g = grids[cx+1][cz+1];
					if(g!=null) {
						Point chunkLocal = new Point(dx+WorldChunk.SIZE*cx, dz+WorldChunk.SIZE*cz);
						collidables.addAll(g.get(chunkLocal));
					}
				}
			}
		);
		
		int size = movement.object.getObjectCollidableEntities().size();
		Vector3f[] collisionPoints = new Vector3f[size];
		CollisionData[] collisionDatas = new CollisionData[size];
		RenderEntity[] collisionEntities = new RenderEntity[size];
		int index = 0;
		for(RenderEntity e : movement.object.getObjectCollidableEntities()) {
			collisionDatas[index] = e.getCollisionData();
			collisionPoints[index] = movement.getAppliedMovementToPosition(e.getPosition());
			collisionEntities[index] = e;
			index++;
		} 
		
		HashSet<DoesNotAffectMovementCollisionTest> nonAffectingCollisionTestObjects = new HashSet<DoesNotAffectMovementCollisionTest>();
		possibleCollisionObjectLoop: 
			for(Collidable c : collidables) {
				GameObject o = (GameObject)c;
				if(o==movement.object || (o instanceof IgnorableCollision i && i.ignoreCollision()) ||
						((o instanceof IgnorableCollisionIf i1 && i1.ignoreCollisionIf(movement.object)) || (movement.object instanceof IgnorableCollisionIf i2 && i2.ignoreCollisionIf(o))))
					continue possibleCollisionObjectLoop;
				else if(o instanceof DoesNotAffectMovementCollisionTest m) {
					nonAffectingCollisionTestObjects.add(m);
					continue possibleCollisionObjectLoop;
				}
				
				final PhysicsMovement oMove;
				if(o instanceof MoveableGameEntity m) {
					oMove = m.getPhysicsMovement();
				}else {
					oMove = new PhysicsMovement(null);
				}
				for(RenderEntity e : o.getObjectCollidableEntities()) {
					for(int j=0; j<index; j++) {
						Vector3f distVect = Vector3f.sub(e.getPosition(),collisionPoints[j], new Vector3f());
						float length = collisionDatas[j].getFurthestPoint()*collisionEntities[j].getScale() + e.getCollisionData().getFurthestPoint()*e.getScale();
						if(distVect.lengthSquared()<length*length) {
							byte collisionByte = collision(movement, collisionEntities[j], oMove, e);
							if(collisionByte!=EXIST_NULL_COLLISION) {
								dealWithCollision(movement, collisionByte);
								collisionTotalByte|=collisionByte;
								collisions.add(c);
							}
						}
					}
				}
			}
//		} - End of possibleCollisionObjectLoop
		
		nonMovementAffectingCollisionsLoop:
			for(DoesNotAffectMovementCollisionTest m : nonAffectingCollisionTestObjects) {
				GameObject o = (GameObject)m;
				final PhysicsMovement oMove;
				if(o instanceof MoveableGameEntity n) {
					oMove = n.getPhysicsMovement();
				}else {
					oMove = new PhysicsMovement(null);
				}
				for(RenderEntity e2 : o.getObjectCollidableEntities()) {
					for(int j=0; j<index; j++) {
						Vector3f distVect = Vector3f.sub(e2.getPosition(),collisionPoints[j], new Vector3f());
						float length = collisionDatas[j].getFurthestPoint()*collisionEntities[j].getScale() + e2.getCollisionData().getFurthestPoint()*e2.getScale();
						if(distVect.lengthSquared()<length*length) {
							Vector3f p2 = oMove.getAppliedMovementToPosition(o.getPosition());
							Vector3f r2 = o.getRotation();
							CollisionData d2 = e2.getCollisionData();
							Vector3f p1 = movement.getAppliedMovementToPosition();
							Vector3f r1 = movement.object.getRotation();
							RenderEntity e1 = collisionEntities[j];
							CollisionData d1 = e1.getCollisionData();
							if( PhysicsCollisions.collision(p1, r1, e1, d1, p2, r2, e2, d2) ) {
								m.collisionWith(movement.object);
								continue nonMovementAffectingCollisionsLoop;
							}
						}
					}
				}
			} 
//		} - End of nonMovementAffectingCollisionsLoop
		
		return new PhysicsCollisionReturnData(collisionTotalByte, false, collisions);
	}
	
	
	private static byte collision(PhysicsMovement e1Move, RenderEntity e1, PhysicsMovement e2Move, RenderEntity e2) {
		CollisionData d1 = e1.getCollisionData();
		CollisionData d2 = e2.getCollisionData();
		return PhysicsCollisions.movementCollision(e1Move, e1, d1, e2Move, e2, d2);
		
	}
	
	public static void dealWithCollision(PhysicsMovement movement, byte c) {
		if(c==EXIST_NULL_COLLISION)return;
		if(c==EXIST_ALL_COLLISIONS) {
			movement.clear(1, 1, 1);
			movement.setXzMovementRotation(0);
			return;
		}
		if((c&EXIST_ROTATION_COLLISION)>0) {
			movement.setXzMovementRotation(0);
			return;
		}
		if((c&EXIST_FULL_XZ_COLLISION)>0) {
			movement.clear(1, 0, 1);
			movement.setXzMovementRotation(0);
			if((c&EXIST_Y_COLLISION)>0) {
				movement.clear(0, 1, 0);
			}
		}
		
		
		
		
		/*
		if((c&EXIST_X_COLLISION)>0 && (c&EXIST_X_COLLISION)!=EXIST_X_COLLISION) {
//			movement.clear(1, 0, 0);
		}
		if((c&EXIST_Y_COLLISION)>0 && (c&EXIST_Y_COLLISION)!=EXIST_Y_COLLISION) {
			movement.clear(0, 1, 0);
		}
		if((c&EXIST_Z_COLLISION)>0 && (c&EXIST_Z_COLLISION)!=EXIST_Z_COLLISION) {
//			movement.clear(0, 0, 1);
		}
		*/
		
	}
	
	

}
