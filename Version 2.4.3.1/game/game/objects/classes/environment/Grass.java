package game.objects.classes.environment;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import game.objects.GameObjectRenderingConditions;
import game.world.WorldVariables;
import objects.TexturedModels;

public class Grass extends GameObject{

	public Grass(Vector3f pos, Vector3f rot, float scale, WorldVariables worldVars) {
		super(pos, rot, scale);
		RenderEntity grass = new RenderEntity(TexturedModels.GRASS, pos, rot.x, rot.y, rot.z, scale);
		grass.addRenderCondition(new GameObjectRenderingConditions.DistanceToPlayer(100f, this, worldVars));
		addEntity(false, grass);
	}

}
