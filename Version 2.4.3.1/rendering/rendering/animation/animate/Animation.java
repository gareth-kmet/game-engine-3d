package rendering.animation.animate;

import rendering.animation.model.AnimatedModel;

/**
 * 
 * * Represents an animation that can applied to an {@link AnimatedModel} . It
 * contains the length of the animation in seconds, and a list of
 * {@link KeyFrame}s.
 * 
 * @author Karl
 * 
 *
 */
public class Animation {

	private final float length;//in seconds
	private final KeyFrame[] keyFrames;

	/**
	 * @param lengthInSeconds
	 *            - the total length of the animation in seconds.
	 * @param frames
	 *            - all the keyframes for the animation, ordered by time of
	 *            appearance in the animation.
	 */
	public Animation(float lengthInSeconds, KeyFrame[] frames) {
		this.keyFrames = frames;
		this.length = lengthInSeconds;
	}

	/**
	 * @return The length of the animation in seconds.
	 */
	public float getLength() {
		return length;
	}

	/**
	 * @return An array of the animation's keyframes. The array is ordered based
	 *         on the order of the keyframes in the animation (first keyframe of
	 *         the animation in array position 0).
	 */
	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}
	
	public static class Loop extends Animation{

		public Loop(Animation...animations) {
			super(createLength(animations), createKeyFrames(animations));
		}
		
		private static float createLength(Animation[] animations) {
			float length = 0;
			for(Animation a:animations) {
				length+=a.getLength();
			}
			return length;
		}
		
		private static KeyFrame[] createKeyFrames(Animation[] animations) {
			int length = 0;
			for(int i=0; i<animations.length;length+=animations[i++].getKeyFrames().length-1);
			KeyFrame[] keyFrames = new KeyFrame[length];
			int i=0;
			int timeLength=0;
			for(Animation anim:animations) {
				KeyFrame[] animFrames = anim.getKeyFrames();
				for(int k=0; k<animFrames.length-1;k++) {
					float timeStamp=animFrames[k].getTimeStamp()+timeLength;
					KeyFrame key = new KeyFrame(timeStamp, animFrames[k].getJointKeyFrames());
					keyFrames[i++]=key;
				}
				timeLength+=anim.getLength();
			}
			return keyFrames;
		}
		
	}
	
	public void delete() {};

}
