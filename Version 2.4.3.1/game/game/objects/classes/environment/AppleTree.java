package game.objects.classes.environment;

import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.GameObject;
import game.objects.GameObjectRenderingConditions;
import game.objects.informations.Physics.Collidable.StaticCollidable;
import game.world.WorldVariables;
import objects.TexturedModels;
import rendering.entities.EntityRenderCondition;
import rendering.entities.EntityRenderCondition.LinkedEntityRenderCondition;

public final class AppleTree extends GameObject implements StaticCollidable{
	
	protected final RenderEntity bark, leaves, apples;
	protected final LeavesCondition hasLeaves;
	protected final ApplesCondition hasApples;

	public AppleTree(WorldVariables worldVars, Vector3f pos, Vector3f rot, float scale) {
		super(pos, rot, scale);
		
		bark = new RenderEntity(TexturedModels.APPLE_TREE_BARK, pos, rot.x, rot.y, rot.z, scale);
		leaves = new RenderEntity(TexturedModels.APPLE_TREE_LEAVES, pos, rot.x, rot.y, rot.z, scale);
		apples = new RenderEntity(TexturedModels.APPLE_TREE_APPLES, pos, rot.x, rot.y, rot.z, scale);
		
		final EntityRenderCondition barkCondition = new GameObjectRenderingConditions.DistanceToPlayer(200f, this, worldVars);
		
		hasLeaves = new LeavesCondition(barkCondition);
		hasApples = new ApplesCondition(barkCondition);
		
		leaves.addRenderCondition(hasLeaves);
		apples.addRenderCondition(hasApples);
		bark.addRenderCondition(barkCondition);
		
		addEntity(true, bark); addEntity(false, leaves, apples);
		
	}
	
	private final class LeavesCondition extends LinkedEntityRenderCondition{
		
		
		protected LeavesCondition(EntityRenderCondition link) {super(link);}
		
		private boolean hasLeaves = true;
		@Override
		public boolean $precondition() {return hasLeaves;}
	}
	
	private final class ApplesCondition extends LinkedEntityRenderCondition{
		
		protected ApplesCondition(EntityRenderCondition link) {super(link);}
		
		private boolean hasApples = true;
		@Override
		public boolean $precondition() {return hasApples;}
	}

	


}
