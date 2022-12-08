package game.objects.classes.animals;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.RenderEntity;
import game.ai.GameAStarTools;
import game.ai.GameAStarTools.AStarFollower;
import game.ai.GameAStarTools.AStarRequest;
import game.objects.entities.GeniusGameEntity;
import game.objects.entities.GeniusGameEntity.Intelligence;
import game.world.WorldVariables;
import objects.models.animations.AnimalAnimations;
import objects.models.animations.AnimatedModels;
import rendering.animation.animate.Animator;
import toolbox.Maths;

public class AfraidAnimalIntelligence extends Intelligence {
	
	protected enum Mindset{
		AFRAID{// Walking slowly away from player
			@Override
			protected void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model) {
				anim.doAnimation(true, AnimalAnimations.WALK.get(model));
			}
		}, 	
		CAREFULL{// Walking slowly towards player
			@Override
			protected void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model) {
				anim.doAnimation(true, AnimalAnimations.WALK.get(model));
			}
		},	
		CLOSE{
			@Override
			protected void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model) {
				anim.doAnimation(false, null);
			}
		},		
		THREATEND{	// Sprint away as fast as possible
			@Override
			protected void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model) {
				anim.doAnimation(true, AnimalAnimations.GALLOP.get(model));
				anim.addAnimationsToQueue(true, AnimalAnimations.GALLOP.get(model), AnimalAnimations.GALLOP.get(model),AnimalAnimations.GALLOP.get(model),AnimalAnimations.GALLOP_JUMP.get(model));
			}
		},
		FINE{// Idle every thing is good
			@Override
			protected void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model) {
				anim.doAnimation(true, AnimalAnimations.IDLE.get(model));
				anim.addAnimationsToQueue(true, AnimalAnimations.IDLE_2.get(model), AnimalAnimations.IDLE_HEADLOW.get(model), AnimalAnimations.EATING.get(model));
			}
		};		
		
		protected abstract void doMindsetAnimation(WorldVariables vars, Animator anim, AnimatedModels model);
		
	};
	
	protected static final float RUN_AWAY_DISTANCE_VARIABLE_PERCENT = 0.1f;
	protected static final int PLAYER_ANGLE_VARIANCE_DEGREES = 30;
	protected static final float CLOSE_WAIT_TIME = 1f;
	
	protected final float fearDistance, runAwayDistance;
	
	
	protected Mindset currentMindset=Mindset.FINE;
	protected Intelligence currentIntelligence=null;
	protected Mindset loadingMindset=null;
	protected Intelligence loadingIntelligence=null;
	

	public <T extends GeniusGameEntity & FearfullAnimalGameEntity> AfraidAnimalIntelligence(T thisEntity, float fearDistance, float runAwayDistance) {
		super(thisEntity, false);
		this.fearDistance=fearDistance;
		this.runAwayDistance=runAwayDistance;
	}

	@Override
	protected void decide(WorldVariables worldVariables) {
		Mindset load = getMindset(worldVariables);
//		load = Mindset.CAREFULL;
		if(load!=loadingMindset && (load != currentMindset || currentIntelligence.isComplete() || currentIntelligence.isUseless())) {
			loadingMindset=load;
			loadingIntelligence=createMindset(worldVariables);
			thisGenius.addToList(loadingIntelligence, GeniusGameEntity.NEED_LOAD_LIST);
		}
		
		if(loadingIntelligence!=null && loadingIntelligence.isLoaded()) {
			thisGenius.removeFromList(currentIntelligence, GeniusGameEntity.ALL_LIST);
			final boolean doAnim = currentMindset != loadingMindset;
			currentMindset=loadingMindset;
			loadingMindset=null;
			currentIntelligence=loadingIntelligence;
			loadingIntelligence=null;
			if(doAnim)doMindsetAnimation(worldVariables);
			thisGenius.transferToList(currentIntelligence, GeniusGameEntity.DECIDING_LIST);
		}
		
		
	}
	
	private Mindset getMindset(WorldVariables vars) {
		Vector3f distanceVector = Vector3f.sub(vars.playerVariables.player.getPosition(), thisEntity.getPosition(), null);
		float distanceSqr = distanceVector.lengthSquared();
		float fearDistanceSqr = fearDistance*fearDistance;
		if(distanceSqr<fearDistanceSqr) {
			return Mindset.THREATEND;
		}else {
			return switch(currentMindset) {
				case AFRAID -> {
					final Mindset yieldValue;
					if(distanceSqr<fearDistanceSqr*4 || !currentIntelligence.isComplete()) {
						yieldValue = Mindset.AFRAID;
					}else if(distanceSqr<fearDistanceSqr*9) {
						yieldValue = Mindset.CLOSE;
					}else {
						yieldValue = Mindset.CAREFULL;
					}
					yield yieldValue;
				}case CAREFULL-> {
					final Mindset yieldValue;
					if(distanceSqr<fearDistanceSqr*2) {
						yieldValue=Mindset.CLOSE;
					}else if(currentIntelligence.isComplete() && distanceSqr<fearDistanceSqr*4) {
						yieldValue=Mindset.FINE;
					}else {
						yieldValue = Mindset.CAREFULL;
					}
					yield yieldValue;
				}case CLOSE -> {
					final Mindset yieldValue;
					if(currentIntelligence.isComplete()) {
						if(distanceSqr<fearDistanceSqr*2) {
							yieldValue = Mindset.AFRAID;
						}else if(distanceSqr<fearDistanceSqr*4){
							yieldValue = Mindset.FINE;
						}else {
							yieldValue = Mindset.CAREFULL;
						}
					}else {
						yieldValue = Mindset.CLOSE;
					}
					
					yield yieldValue;
				}case THREATEND -> {
					final Mindset yieldValue;
					if(currentIntelligence.isComplete()) {
						if(distanceSqr<fearDistanceSqr*4) {
							yieldValue = Mindset.AFRAID;
						}else if(distanceSqr<fearDistanceSqr*9) {
							yieldValue = Mindset.CLOSE;
						}else {
							yieldValue = Mindset.CAREFULL;
						}
					}else {
						yieldValue = Mindset.THREATEND;
					}
					yield yieldValue;
				}case FINE -> {
					final Mindset yieldValue;
					if(distanceSqr<fearDistanceSqr*2) {
						yieldValue = Mindset.CLOSE;
					}else if(distanceSqr>fearDistanceSqr*4) {
						yieldValue = Mindset.CAREFULL;
					}else {
						yieldValue = Mindset.FINE;
					}
					yield yieldValue;
				}default -> Mindset.FINE;
			};
		}
	}
	
	private Intelligence createMindset(WorldVariables vars) {
		Intelligence target = switch(loadingMindset) {
			case AFRAID 	-> createFarAwayAstarFollower(vars, ((FearfullAnimalGameEntity)thisEntity).getWalkSpeed());
			case THREATEND	-> createFarAwayAstarFollower(vars, ((FearfullAnimalGameEntity)thisEntity).getRunSpeed());
			case CAREFULL 	-> createAstarFollower(vars, Maths.toVector2f(vars.playerVariables.player.getPosition()), ((FearfullAnimalGameEntity)thisEntity).getWalkSpeed()); 
			case CLOSE 		-> new TimedCompletionIntelligence(thisEntity, CLOSE_WAIT_TIME) {
				@Override
				protected void decide(WorldVariables worldVariables) {
					super.decide(worldVariables);
					thisEntity.getPhysicsMovement().setXzSpeedDistance(0);
				}
			};
			case FINE 		-> new TimedCompletionIntelligence(thisEntity, Float.POSITIVE_INFINITY) {
				@Override
				protected void decide(WorldVariables worldVariables) {
					super.decide(worldVariables);
					thisEntity.getPhysicsMovement().setXzSpeedDistance(0);
				}
			};
		};
		return target;
	}
	
	private void doMindsetAnimation(WorldVariables vars) {
		for(RenderEntity e : thisEntity.getEntities(((FearfullAnimalGameEntity)thisEntity).getAnimateEntityIds())){
			currentMindset.doMindsetAnimation(vars, e.getAnimator(), (AnimatedModels)e.getTexturedModel().getModel());
		}
		
	}
	
	private AStarFollower createFarAwayAstarFollower(WorldVariables vars, float speed) {
		Vector2f target = getFarAwayPoint(vars);
		return createAstarFollower(vars, target, speed);
	}
	private AStarFollower createAstarFollower(WorldVariables vars, Vector2f target, float speed) {
		AStarRequest request = vars.aiVariables.gameAITools.createAStarRequest(vars.environmentVariables.storage, thisEntity.getPosition(), Maths.toVector3f(target), GameAStarTools.AStarRequest.GRID_COMBINATION_TYPE.SMALL);
		return new AStarFollower(request, thisEntity, speed);
	}
	
	private Vector2f getFarAwayPoint(WorldVariables vars) {
		
		Vector2f playerToEntityDistanceVector = Vector2f.sub(Maths.toVector2f(thisEntity.getPosition()), Maths.toVector2f(vars.playerVariables.player.getPosition()), null);
		float toPlayerAngle = Maths.getRotationInDegrees(playerToEntityDistanceVector.x, playerToEntityDistanceVector.y);
		float randAngle = vars.aiVariables.aiRandom.nextFloat(360-2*PLAYER_ANGLE_VARIANCE_DEGREES);
		float angle = (toPlayerAngle+PLAYER_ANGLE_VARIANCE_DEGREES+randAngle)%360f;
		
		float distance = Maths.lerp(runAwayDistance*(1-RUN_AWAY_DISTANCE_VARIABLE_PERCENT), runAwayDistance*(1+RUN_AWAY_DISTANCE_VARIABLE_PERCENT), vars.aiVariables.aiRandom.nextFloat());
		
		return Maths.getRotatedMovementFromDegrees(distance, angle);
	}
	
	

	@Override
	protected void load(WorldVariables worldVariables) {
		currentMindset=Mindset.FINE;
		currentIntelligence=Intelligence.BLANK_INTELLIGENCE;
		doMindsetAnimation(worldVariables);
		loaded=true;
	}
	
	public interface FearfullAnimalGameEntity{
		public int[] getAnimateEntityIds();
		public float getWalkSpeed();
		public float getRunSpeed();
	}

}
