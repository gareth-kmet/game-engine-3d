package game.objects.classes.environment;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import objects.TexturedModels;

public final class Boulder extends GameObject{
	
	protected final RenderEntity bark;

	public Boulder(Vector3f pos, Vector3f rot, float scale) {
		super(pos, rot, scale);
		
		bark = new RenderEntity(TexturedModels.BOULDER, pos, rot.x, rot.y, rot.z, scale);
		
		addEntity(true,bark);
	}
	


}
