package game.objects.classes.animals;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.objects.classes.animals.AfraidAnimalIntelligence.FearfullAnimalGameEntity;
import game.objects.entities.GeniusGameEntity;
import game.objects.informations.Death.CanDie.DiesOnDespawn;
import game.objects.informations.Physics.Collidable.MovingCollidable;
import game.physics.collisions.PhysicsCollisionReturnData;
import game.world.WorldVariables;
import game.world.environment.EnvironmentChunk;
import game.world.environment.EnvironmentStorage;
import objects.TexturedModels;
import toolbox.Maths;

public class AfraidBullAnimal extends GeniusGameEntity implements FearfullAnimalGameEntity, DiesOnDespawn, MovingCollidable{
	
	private final AfraidAnimalIntelligence intel;
	private final static Random spawnRandom = new Random();
	private boolean dead = false;

	private static final float WALK_SPEED=1.5f, RUN_SPEED=8.94f;
	

	public AfraidBullAnimal(Vector3f pos, Vector3f rot, float scale) {
		super(pos, rot, scale, 1/2f);
		RenderEntity ent = new RenderEntity(TexturedModels.BULL, pos, rot.x, rot.y, rot.z, scale);
		ent.loadTexturedModelType();
		addEntity(true, 0x4A6, ent);
		intel = new AfraidAnimalIntelligence(this, 20, 100);
		addToList(intel, DECIDING_LIST|NEED_LOAD_LIST);
		addToList(new WaterIntelligence(this), DECIDING_LIST);
	}
	
	@Override
	protected void makeOwnDecision(WorldVariables vars) {}
	@Override
	public void physicsMovementApplied(PhysicsCollisionReturnData collisionReturnData) {}

	@Override
	public int[] getAnimateEntityIds() {
		return new int[] {0x4a6};
	}
	
	@Override
	public float getWalkSpeed() {
		return WALK_SPEED;
	}

	@Override
	public float getRunSpeed() {
		return RUN_SPEED;
	}
	
	
	public static AfraidBullAnimal spawn(Vector3f playerPos, EnvironmentStorage storage) {
		float angle = spawnRandom.nextFloat(360f);
		float distance = spawnRandom.nextFloat(200f, 300f);
		Vector2f pos = Maths.getRotatedMovementFromDegrees(distance, angle);
		Vector3f pos3 = Maths.toVector3f(pos);
		Vector3f.add(pos3, playerPos, pos3);
		AfraidBullAnimal a = new AfraidBullAnimal(pos3, new Vector3f(), 0.25f);
		EnvironmentChunk.spawnObject(storage, a);
		return a;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public boolean die() {
		return (dead=true);
	}
}