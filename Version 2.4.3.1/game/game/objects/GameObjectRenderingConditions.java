package game.objects;

import org.lwjgl.util.vector.Vector3f;

import game.world.WorldVariables;
import rendering.entities.EntityRenderCondition;
import rendering.entities.EntityRenderCondition.SophisticatedEntityRenderCondition;

public sealed abstract class GameObjectRenderingConditions extends SophisticatedEntityRenderCondition{
	
	
	public static class DistanceToPlayer extends DistanceRenderOnlyToPlayer{
		
		public DistanceToPlayer(float dist, GameObject thisObject, WorldVariables worldVars) {
			super(dist, thisObject, worldVars);
		}
		@Override
		protected boolean $canUseNormalMap() {
			return distSqrToPlayer<this.dist*this.dist*0.0625f;
		}
		@Override
		protected float $getResolution() {
			return EntityRenderCondition.ResolutionCondition.USE_EXPONETIAL_MORE_LOW_THAN_HIGH.getResolutionPercent(distSqrToPlayer, this.dist);
		}
	}
	
	public static non-sealed class DistanceRenderOnlyToPlayer extends GameObjectRenderingConditions{
		protected final float dist;
		protected final GameObject thisObject;
		
		protected float distSqrToPlayer = 0;
		protected final WorldVariables worldVars;
		
		public DistanceRenderOnlyToPlayer(float dist, GameObject thisObject, WorldVariables worldVars) {
			this.dist=dist;
			this.thisObject=thisObject;
			this.worldVars=worldVars;
		}
		@Override
		protected boolean $precondition() {
			Vector3f dist = Vector3f.sub(thisObject.getPosition(), worldVars.playerVariables.player.getPosition(), null);
			distSqrToPlayer = dist.lengthSquared();
			return distSqrToPlayer<this.dist*this.dist;
		}
	}
	

}
