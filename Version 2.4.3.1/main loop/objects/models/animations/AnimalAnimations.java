package objects.models.animations;

import java.util.Arrays;

import rendering.animation.animate.Animation;
import rendering.animation.model.AnimationLoader;
import toolbox.StringUtils;

public enum AnimalAnimations implements ModelAnimations{
	
	ATTACK_HEADBUTT,		
	ATTACK_KICK,
	DEATH,					
	EATING,					
	GALLOP_JUMP, 			
	GALLOP,					
	IDLE,IDLE_2, 			
	IDLE_HEADLOW,
	IDLE_HIT$REACT_LEFT,	
	IDLE_HIT$REACT_RIGHT,
	JUMP_TO$IDLE,
	WALK;
	
	private static final String FIRST_ANIMAL = "ALPACA", LAST_ANIMAL = "COW";
	private static Integer FIRST_ANIMAL_ORD=null, LAST_ANIMAL_ORD=null;
	private final static int NUMBER_OF_ANIMALS = 3;
	private final Animation[] animations = new Animation[NUMBER_OF_ANIMALS];
	private final int[] uses = new int[NUMBER_OF_ANIMALS];
	
	public final String toLocationString(AnimatedModels m) {
		requireIsUseableAnimal(m);
		String s = m.toLocationString()+'_';
		return s+StringUtils.enumToString(this);
	}

	@Override
	public Animation use(AnimatedModels m) {
		int animOrd = getAnimationOrdinal(m);
		if(animations[animOrd]==null) {
			animations[animOrd]=AnimationLoader.loadAnimation(toLocationString(m));
		}
		uses[animOrd]++;
		return animations[animOrd];
	}

	@Override
	public void unUse(AnimatedModels m) {
		int animOrd = getAnimationOrdinal(m);
		uses[animOrd]--;
		if(uses[animOrd]<=0 && animations[animOrd]!=null) {
			animations[animOrd].delete();
			animations[animOrd]=null;
		}
	}

	@Override
	public void setUses(AnimatedModels m, int use) {
		
		int animOrd = getAnimationOrdinal(m);
		uses[animOrd]=use;
	}

	@Override
	public Animation get(AnimatedModels m) {
		int animOrd = getAnimationOrdinal(m);
		return animations[animOrd];
	}
	
	private static void requireIsUseableAnimal(AnimatedModels m) {
		if(!isUseableModel(m)) throw new IllegalArgumentException(m+" is not a useable model for AnimalAnimations");
	}
	
	private int getAnimationOrdinal(AnimatedModels m) {
		requireIsUseableAnimal(m);
		return m.ordinal()-FIRST_ANIMAL_ORD;
	}
	
	public static boolean isUseableModel(AnimatedModels m) {
		if(FIRST_ANIMAL_ORD==null) {
			FIRST_ANIMAL_ORD=AnimatedModels.valueOf(FIRST_ANIMAL).ordinal();
			LAST_ANIMAL_ORD=AnimatedModels.valueOf(LAST_ANIMAL).ordinal();
		}
		int thisAnimal = m.ordinal();
		return FIRST_ANIMAL_ORD<=thisAnimal && thisAnimal<=LAST_ANIMAL_ORD;
	}
	
	public static void forceUnUseAll() {
		for(AnimalAnimations anim : values()) {
			Arrays.fill(anim.uses, 0);
			for(int i=0; i<NUMBER_OF_ANIMALS; i++) {
				if(anim.animations[i]!=null) {
					anim.animations[i].delete();
					anim.animations[i]=null;
				}
			}
		}
	}

}
