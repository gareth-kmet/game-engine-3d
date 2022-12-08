package game.objects.classes.environment;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import game.objects.GameObjectRenderingConditions;
import game.world.WorldVariables;
import objects.TexturedModels;

public class Fern extends GameObject{

	public Fern(Vector3f pos, Vector3f rot, float scale, int index, WorldVariables worldVars) {
		super(pos, rot, scale);
		RenderEntity fern = new RenderEntity(TexturedModels.FERN, index, pos, rot.x, rot.y, rot.z, scale);
		fern.addRenderCondition(new GameObjectRenderingConditions.DistanceRenderOnlyToPlayer(50f,this,worldVars));
		addEntity(false, fern);
	}

}
