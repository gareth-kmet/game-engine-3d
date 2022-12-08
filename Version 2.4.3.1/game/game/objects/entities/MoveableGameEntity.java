package game.objects.entities;

import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.physics.collisions.PhysicsCollisionReturnData;
import game.physics.collisions.PhysicsMovement;
import game.world.WorldVariables;

public sealed abstract class MoveableGameEntity extends GameEntity permits IntelligentGameEntity, UnintelligentGameEntity{
	
	protected final PhysicsMovement physicsMovement;
	private HashSet<Point> currentlyAffectingPoints = new HashSet<Point>(0); // almost all moving entities will have collision detection so I would rather just keep the methods and variables here

	protected MoveableGameEntity(Vector3f pos, Vector3f rot, float scale, float physicsMovementRatio) {
		super(pos, rot, scale);
		physicsMovement=new PhysicsMovement(this,physicsMovementRatio);
	}
	
	public abstract void physicsMovementApplied(PhysicsCollisionReturnData collisionReturnData);
	
	public final PhysicsMovement getPhysicsMovement() {
		return this.physicsMovement;
	}

	protected void addMovementToFrame(WorldVariables worldVars) {
		worldVars.physicsVariables.addPhysicsMovementToThisFrame(physicsMovement);
	}
	
	public final HashSet<Point> getCurrentPoints() {
		return currentlyAffectingPoints;
	}
	
	public final HashSet<Point> setCurrentPoints(HashSet<Point> ps) {
		return currentlyAffectingPoints=ps;
	}
	
	
	
}
