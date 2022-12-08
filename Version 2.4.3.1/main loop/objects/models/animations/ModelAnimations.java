package objects.models.animations;

import rendering.animation.animate.Animation;

public interface ModelAnimations {
	/**
	 * Counts to total uses and loads if uses was less than 1
	 * @return an Animation that is not null 
	 */
	public Animation use(AnimatedModels m);
	public void unUse(AnimatedModels m);
	void setUses(AnimatedModels m, int use);
	/**
	 * Does not load nor count to total uses.
	 * @return an Animation or null.
	 */
	public Animation get(AnimatedModels m);
	
	public static void forceUnUseAll() {
		AnimalAnimations.forceUnUseAll();
	}

}
