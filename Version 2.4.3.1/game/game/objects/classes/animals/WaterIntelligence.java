package game.objects.classes.animals;

import entities.Player;
import game.objects.entities.IntelligentGameEntity;
import game.objects.entities.GeniusGameEntity.Intelligence;
import game.world.WorldVariables;
import game.world.terrain.TerrainVariables.Water;

public class WaterIntelligence extends Intelligence {

	public WaterIntelligence(IntelligentGameEntity thisEntity) {
		super(thisEntity, true);
	}

	@Override
	protected void decide(WorldVariables worldVariables) {
		if(thisEntity.getPosition().y<Water.HEIGHT-1) {
			thisEntity.getPhysicsMovement().setySpeed(1f);
		}else if(thisEntity.getPosition().y>Water.HEIGHT) {
			thisEntity.getPhysicsMovement().setyAcceleration(Player.GRAVITY);
		}

	}

	@Override
	protected void load(WorldVariables worldVariables) {}

}
