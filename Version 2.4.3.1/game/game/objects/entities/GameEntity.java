package game.objects.entities;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import game.world.WorldVariables;

public abstract class GameEntity extends GameObject {

	protected GameEntity(Vector3f pos, Vector3f rot, float scale) {
		super(pos, rot, scale);
	}
	
	public abstract void update(WorldVariables worldVars);
	
	protected void updateAnimators(float deltaTime) {
		for(RenderEntity e : getObjectEntities()) {
			e.updateAnimator(deltaTime);
		}
	}
	

}
