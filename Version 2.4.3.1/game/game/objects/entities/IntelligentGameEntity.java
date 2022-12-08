package game.objects.entities;

import org.lwjgl.util.vector.Vector3f;

import game.world.WorldVariables;

public non-sealed abstract class IntelligentGameEntity extends MoveableGameEntity {
	

	protected IntelligentGameEntity(Vector3f pos, Vector3f rot, float scale, float physicsMovementRatio) {
		super(pos, rot, scale, physicsMovementRatio);
	}
	
	protected abstract void decide(WorldVariables vars);
	
	@Override
	public final void update(WorldVariables worldVars) {
		physicsMovement.reset();
		decide(worldVars);
		addMovementToFrame(worldVars);
		updateAnimators(worldVars.currentFrameDeltaTime);
	}
	
	

}