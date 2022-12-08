package game.physics.collisions;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.objects.entities.MoveableGameEntity;
import toolbox.Maths;

public final class PhysicsMovement {
	
	boolean updatesApplied = false;
	boolean applied = false;
	
	final MoveableGameEntity object;
	
	private float xzMovementDistance, xzMovementRotation, xzSpeedDistance, xzSpeedRotation, xzAccelerationDistance, xzAccelerationRotation;
	private float yMovement, ySpeed, yAcceleration;
	private Vector3f movement = new Vector3f();
	
	final float collisionSpecificity;
	private PhysicsCollisionReturnData lastCollisionReturnData = PhysicsCollisionReturnData.NON_EXISTANT_COLLISION;
	
	public PhysicsMovement(MoveableGameEntity object,float specificity) {
		this.object=object;
		this.collisionSpecificity=specificity;
	}
	
	public PhysicsMovement(MoveableGameEntity object) {
		this(object,1f);
	}
	
	void applyUpdatesToMovement(float time) {
		xzSpeedDistance += xzAccelerationDistance * time;
		xzMovementDistance += xzSpeedDistance * time;
		xzSpeedRotation += xzAccelerationRotation * time;
		xzMovementRotation += xzSpeedRotation * time;
		ySpeed += yAcceleration * time;
		yMovement += ySpeed * time;
		
		fixMovementVector();
		
		updatesApplied=true;
	}
	
	void applyMovementToObject(PhysicsCollisionReturnData collisionReturnData) {
		object.setPosition(getAppliedMovementToPosition());
		object.setRotation(Vector3f.add(object.getRotation(), new Vector3f(0,xzMovementRotation,0), null));
		applied=true;
		lastCollisionReturnData = collisionReturnData;
		object.physicsMovementApplied(collisionReturnData);
	}
	/**
	 * Resets applied and updatesApplied boolean which means that the movements will be reaplied next frame.
	 * Also resets the movements to 0 as any movement would have been applied.
	 * Resets the movement vector aswell.
	 * Does not reset the accelerations or speeds.
	 */
	public void reset() {
		applied=false;
		updatesApplied=false;
		movement.set(0, 0, 0);
		xzMovementDistance=0; xzMovementRotation=0; yMovement=0;
		lastCollisionReturnData=PhysicsCollisionReturnData.NON_EXISTANT_COLLISION;
		
	}
	
	Vector3f fixMovementVector() {
		Vector2f xzmove = Maths.getRotatedMovementFromDegrees(xzMovementDistance, object.getRotation().y+xzMovementRotation);
		movement.x=xzmove.x;
		movement.z=xzmove.y;
		movement.y=yMovement;
		return movement;
	}
	
	public Vector3f getAppliedMovementToPosition() {
		return getAppliedMovementToPosition(object.getPosition());
	}
	
	public Vector3f getAppliedMovementToPosition(Vector3f position) {
		return Vector3f.add(position, movement, null);
	}
	
	
	Vector3f getMovement() {
		return movement;
	}

	public float getXzMovementDistance() {
		return xzMovementDistance;
	}

	public void setXzMovementDistance(float xzMovementDistance) {
		this.xzMovementDistance = xzMovementDistance;
	}

	public float getXzMovementRotation() {
		return xzMovementRotation;
	}

	public void setXzMovementRotation(float xzMovementRotation) {
		this.xzMovementRotation = xzMovementRotation;
	}

	public float getXzSpeedDistance() {
		return xzSpeedDistance;
	}

	public void setXzSpeedDistance(float xzSpeedDistance) {
		this.xzSpeedDistance = xzSpeedDistance;
	}

	public float getXzSpeedRotation() {
		return xzSpeedRotation;
	}

	public void setXzSpeedRotation(float xzSpeedRotation) {
		this.xzSpeedRotation = xzSpeedRotation;
	}

	public float getXzAccelerationDistance() {
		return xzAccelerationDistance;
	}

	public void setXzAccelerationDistance(float xzAccelerationDistance) {
		this.xzAccelerationDistance = xzAccelerationDistance;
	}

	public float getXzAccelerationRotation() {
		return xzAccelerationRotation;
	}

	public void setXzAccelerationRotation(float xzAccelerationRotation) {
		this.xzAccelerationRotation = xzAccelerationRotation;
	}

	public float getyMovement() {
		return yMovement;
	}

	public void setyMovement(float yMovement) {
		this.yMovement = yMovement;
	}

	public float getySpeed() {
		return ySpeed;
	}

	public void setySpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public float getyAcceleration() {
		return yAcceleration;
	}

	public void setyAcceleration(float yAcceleration) {
		this.yAcceleration = yAcceleration;
	}
	
	
	
	public boolean isUpdatesApplied() {
		return updatesApplied;
	}

	public boolean isApplied() {
		return applied;
	}

	public PhysicsCollisionReturnData getLastCollisionReturnData() {
		return lastCollisionReturnData;
	}

	public void clear(int x, int y, int z) {
		movement.x=x==0?movement.x:0;
		movement.y=y==0?movement.y:0;
		movement.z=z==0?movement.z:0;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("Movement: ");
		string.append(movement.toString());
		string.append(" Rotation: ");
		string.append(xzMovementRotation);
		string.append('\n');
		string.append(xzMovementDistance);
		string.append('|');
		string.append(xzSpeedDistance);
		string.append('|');
		string.append(xzAccelerationDistance);
		string.append('|');
		string.append(xzMovementRotation);
		string.append('|');
		string.append(xzSpeedRotation);
		string.append('|');
		string.append(xzAccelerationRotation);
		string.append('|');
		string.append(yMovement);
		string.append('|');
		string.append(ySpeed);
		string.append('|');
		string.append(yAcceleration);
		return string.toString();
		
	}
	
	

}
