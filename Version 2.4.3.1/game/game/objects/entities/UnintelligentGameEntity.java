package game.objects.entities;

import org.lwjgl.util.vector.Vector3f;

import game.world.WorldVariables;

public non-sealed abstract class UnintelligentGameEntity extends MoveableGameEntity {

	protected UnintelligentGameEntity(Vector3f pos, Vector3f rot, float scale, float physicsMovementRatio) {
		super(pos, rot, scale, physicsMovementRatio);
		setPhysicsMovement();
	}
	
	protected abstract void setPhysicsMovement();
	
	@Override
	public final void update(WorldVariables worldVars) {
		physicsMovement.reset();
		addMovementToFrame(worldVars);
		updateAnimators(worldVars.currentFrameDeltaTime);
	}
	

}
