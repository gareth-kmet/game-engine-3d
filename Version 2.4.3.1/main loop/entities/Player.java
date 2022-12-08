package entities;

import static objects.models.animations.AnimalAnimations.IDLE_HIT$REACT_LEFT;
import static objects.models.animations.AnimalAnimations.IDLE_HIT$REACT_RIGHT;
import static objects.models.animations.AnimatedModels.BULL;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.objects.GameObject;
import game.objects.classes.animals.WaterIntelligence;
import game.objects.classes.environment.Box;
import game.objects.entities.GeniusGameEntity;
import game.objects.informations.Physics.Collidable.MovingCollidable;
import game.physics.collisions.PhysicsCollisionReturnData;
import game.world.WorldVariables;
import game.world.environment.EnvironmentChunk;
import game.world.terrain.TerrainVariables.Water;
import mainLoop.DisplayManager;
import objects.TexturedModels;
import rendering.animation.animate.Animator;
import toolbox.Maths;

public class Player extends GeniusGameEntity implements MovingCollidable{
	
//	private static final float WALK_SPEED = 20, TURN_SPEED = 160, JUMP_POWER = 30;
//	public static final float GRAVITY = -50;
	
	private static final float WALK_SPEED = 1.42f, TURN_SPEED = 160, JUMP_POWER = 4.429f;
	public static final float GRAVITY = -9.81f;
	
	private boolean isInAir = false;
	
	private boolean moving=false;
	private boolean sprinting=false;
	
	
	public RenderEntity playerEntity;
	public Animator playerAnimator;
	

	public Player(TexturedModels model, Vector3f position, Vector3f rotation, float scale) {
		super(new Vector3f(30,10,30), rotation, scale, 1f/9f);
		playerEntity = new RenderEntity(model, position, rotation.x, rotation.y, rotation.z, scale);
		addEntity(true, playerEntity);
		load();
		playerAnimator = playerEntity.getAnimator();
		playerAnimator.nextAnimation(true, IDLE_HIT$REACT_LEFT.get(BULL));
		playerAnimator.addAnimationsToQueue(true,IDLE_HIT$REACT_RIGHT.get(BULL));
		addToList(new WaterIntelligence(this), DECIDING_LIST);
		
		
	}
	
	
	@Override
	public void makeOwnDecision(WorldVariables vars) {
		checkInputs(vars);
//		setRotation(new Vector3f(rotation.x, rotation.y+physicsMovement.getXzSpeedRotation()*DisplayManager.getFrameTimeSeconds(), rotation.z));
	}
	
	@Override
	public void physicsMovementApplied(PhysicsCollisionReturnData collisionReturnData) {
		if((collisionReturnData.collisionType()&PhysicsCollisionReturnData.POSITIVE_Y_COLLISION)>0 || collisionReturnData.terrainCollision()) {
			physicsMovement.setySpeed(0);
			isInAir=false;
		}else {
			isInAir=position.y>Water.HEIGHT-1;
		}
	
		
		
	}
	
	private void jump() {
		if(!isInAir) {
			physicsMovement.setySpeed(JUMP_POWER*(position.y<Water.HEIGHT-1?0.5f:1f));
		}
		
	}
	
	private boolean keydown = false;
	private boolean keydown1 = false;
	private boolean keydown2 = false;
	private void checkInputs(WorldVariables vars) {
		
		boolean wasMoving = moving;
		boolean wasSprinting = sprinting;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			physicsMovement.setXzSpeedDistance(WALK_SPEED);
			moving=true;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			physicsMovement.setXzSpeedDistance(-WALK_SPEED);
			moving=true;
		}else {
			physicsMovement.setXzSpeedDistance(0);
			moving=false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			physicsMovement.setXzSpeedDistance(physicsMovement.getXzSpeedDistance()*50);
			sprinting=true;
		}else {
			sprinting=false;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			physicsMovement.setXzSpeedRotation(-TURN_SPEED);
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			physicsMovement.setXzSpeedRotation(TURN_SPEED);
		}else {
			physicsMovement.setXzSpeedRotation(0);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(!keydown) {
				playerAnimator.doInsertAnimation(false, IDLE_HIT$REACT_LEFT.get(BULL));
				
			}
			keydown=true;
		}else {
			keydown=false;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_L)) {
			if(!keydown1) {
				Vector2f placePos = Maths.getRotatedMovementFromDegrees(2, rotation.y);
				GameObject o = new Box(Vector3f.add(position, new Vector3f(placePos.x,1,placePos.y),null), rotation, 0.25f); 
				EnvironmentChunk.spawnObject(vars.environmentVariables.storage, o);
			}
			keydown1=true;
		}else {
			keydown1=false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_U)) {
			if(!keydown2) {
				System.out.println(DisplayManager.getFPS());
			}
			keydown2=true;
		}else {
			keydown2=false;
		}
		
		
		
		if(!moving) {
			if(wasMoving) {
				
				if(wasSprinting) {
//					((AnimatedModel)playerEntity.getRawModel(false)).doAnimation(false, BULL_JUMP_TO$IDLE.get());
//					((AnimatedModel)playerEntity.getRawModel(false)).doAnimationAfterCurrentAnimation(true, BULL_IDLE.get(),BULL_IDLE_2.get(),BULL_IDLE_HEADLOW.get());
				}else {
//					((AnimatedModel)playerEntity.getRawModel(false)).doAnimation(true, BULL_IDLE.get(),BULL_IDLE_2.get(),BULL_IDLE_HEADLOW.get());
				}
			}
		}else if(sprinting) {
			if(!wasMoving || !wasSprinting) {
//				((AnimatedModel)playerEntity.getRawModel(false)).doAnimation(true,BULL_GALLOP.get());

			}
		}else {
			if(!wasMoving || wasSprinting) {
//				((AnimatedModel)playerEntity.getRawModel(false)).doAnimation(true,BULL_WALK.get());
			}
		}
		
		/*
		if(moving && sprinting && (!wasMoving || !wasSprinting)) {
			playerAnimator.doAnimation(true, AnimalAnimations.GALLOP.get(BULL));
		}else if(!sprinting && wasSprinting) {
			playerAnimator.doAnimation(true, IDLE.get(BULL));
			playerAnimator.addAnimationsToQueue(true,IDLE_2.get(BULL),IDLE_HEADLOW.get(BULL));
		}
		*/
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
	

}
