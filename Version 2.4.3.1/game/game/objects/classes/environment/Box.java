package game.objects.classes.environment;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import entities.Player;
import game.objects.entities.UnintelligentGameEntity;
import game.physics.collisions.PhysicsCollisionReturnData;
import game.physics.collisions.data.BoundingBox;
import objects.TexturedModels;

public final class Box extends UnintelligentGameEntity{
	
	protected final RenderEntity bark;

	public Box(Vector3f pos, Vector3f rot, float scale) {
		super(pos, rot, scale, 1f);
		
		bark = new RenderEntity(TexturedModels.BOX, pos, rot.x, rot.y, rot.z, scale);
		bark.setCollideCondition(BoundingBox.Boxes.Y_ROTATABLE);
		addEntity(true, bark);
	}

	@Override
	protected void setPhysicsMovement() {
		physicsMovement.setyAcceleration(Player.GRAVITY);
		
	}

	@Override
	public void physicsMovementApplied(PhysicsCollisionReturnData collisionReturnData) {}
	


}
