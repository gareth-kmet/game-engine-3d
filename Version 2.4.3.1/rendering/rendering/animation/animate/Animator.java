package rendering.animation.animate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import rendering.animation.model.AnimatedModel;
import rendering.animation.model.Joint;

/**
 * 
 * This class contains all the functionality to apply an animation to an
 * animated entity. An Animator instance is associated with just one
 * {@link AnimatedModel}. It also keeps track of the running time (in seconds)
 * of the current animation, along with a reference to the currently playing
 * animation for the corresponding entity.
 * 
 * An Animator instance needs to be updated every frame, in order for it to keep
 * updating the animation pose of the associated entity. The currently playing
 * animation can be changed at any time using the doAnimation() method. The
 * Animator will keep looping the current animation until a new animation is
 * chosen.
 * 
 * The Animator calculates the desired current animation pose by interpolating
 * between the previous and next keyframes of the animation (based on the
 * current animation time). The Animator then updates the transforms all of the
 * joints each frame to match the current desired animation pose.
 * 
 * @author Karl
 *
 */
public class Animator {
	
	private final static float TRANSITION_TIME = 0.1f;

	private final AnimatedModel entity;

	private Animation currentAnimation;
	private List<Animation> nextAnimations = new ArrayList<Animation>();
	private List<Animation> loopQueue = new ArrayList<Animation>();
	private Animation nextAnimation=null;
	private float animationTime = 0;
	private final Matrix4f[] currentJointTransforms;

	/**
	 * @param entity
	 *            - the entity which will by animated by this animator.
	 */
	public Animator(AnimatedModel entity) {
		this.entity = entity;
		currentJointTransforms=entity.getJointTransforms();
	}

	/**
	 * Resets the animation time and clears the animation queue and loop queue setting it to the given values. Animations will start after a short allotted time.
	 * TODO
	 */
	public void doAnimation(boolean loop, Animation animation) {
		if(currentAnimation==null) {
			forceAnimation(loop, animation);
		}else {
			Animation a = createTransitionAnimatiom(animation);
			forceAnimation(false, a);
			addAnimationsToQueue(loop, animation);
		}
	}
	
	/**
	 * Clears the animation queue and the loop queue and sets it to the given values. Animations will start after the completion of the current animation.
	 * @param animation
	 */
	public void nextAnimation(boolean loop, Animation animation) {
		if(currentAnimation==null) {
			forceAnimation(loop, animation);
		}else {
			nextAnimations.clear();
			loopQueue.clear();
			addAnimationsToQueue(loop, animation);
		}
	}
	
	/**
	 * Does not clear the animation queue and the loop queue. Inserts the animations into the begining of the next queue but not the loop queue. Animations will start after the completion of the current animation.
	 */
	public void insertAnimationsToQueue(boolean loop, Animation animation, Animation...animations) {
		int offset=0;
		if(currentAnimation==null) {
			forceAnimation(false, animation);
			nextAnimations.add(animation);
		}else {
			nextAnimations.add(0,animation);
			offset=1;
		}
		
		if(loop)loopQueue.add(0,animation);
		for(int i=0; i<animations.length; i++) {
			nextAnimations.add(i+offset,animations[i]);
			loopQueue.add(i+1, animations[i]);
		}
		
	}
	
	/**
	 * Does not clear the animation queue and loop queue. Animations will start after a short allotted time.
	 * TODO
	 */
	public void doInsertAnimation(boolean loop, Animation animation) {
		if(currentAnimation==null) {
			forceInsertAnimation(loop, animation);
		}else {
			Animation a = createTransitionAnimatiom(animation);
			forceInsertAnimation(loop, a);
			insertAnimationsToQueue(loop, animation);
		}
	}
	
	/**
	 * Does not clear the animation queue and loop queue. Forces the animation to be played immediately.
	 */
	public void forceInsertAnimation(boolean loop, Animation animation) {
		currentAnimation=animation;
		animationTime=0;
		nextAnimation=null;
	}
	
	/**
	 * Clears the animation queue and loop queue. Forces the animation to be played immediately.
	 */
	public void forceAnimation(boolean loop, Animation animation) {
		this.animationTime = 0;
		clearQueues();
		this.currentAnimation = animation;
		if(loop)loopQueue.add(animation);
	}
	
	/**
	 * Adds animations to the end of the queues.
	 */
	public void addAnimationsToQueue(boolean loop, Animation...animations) {
		for(Animation anim:animations) {
			nextAnimations.add(anim);
			if(loop) {
				loopQueue.add(anim);
			}
		}
	}
	
	private void clearQueues() {
		nextAnimations.clear();
		loopQueue.clear();
		nextAnimation=null;
	}

	/**
	 * This method should be called each frame to update the animation currently
	 * being played. This increases the animation time (and loops it back to
	 * zero if necessary), finds the pose that the entity should be in at that
	 * time of the animation, and then applies that pose to all the model's
	 * joints by setting the joint transforms.
	 */
	public void update(float deltaTime) {
		
		if (currentAnimation == null)return;
		increaseAnimationTime(deltaTime);
		if (currentAnimation == null)return;
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		for(Joint rootJoint : entity.getRootJoints()) {
			applyPoseToJoints(currentPose, rootJoint, new Matrix4f());
		}
		
	}
	
	public Matrix4f[] getCurrentJointTransforms() {
		return currentJointTransforms;
	}

	/**
	 * Increases the current animation time which allows the animation to
	 * progress. If the current animation has reached the end then the timer is
	 * reset, causing the animation to loop.
	 */
	private void increaseAnimationTime(float deltaTime) {
		animationTime += deltaTime;
		if (animationTime > currentAnimation.getLength()) {
			this.animationTime %= currentAnimation.getLength();
			setNextAnimation();
			this.currentAnimation=nextAnimation;
			nextAnimation=null;
		}
	}
	
	private Animation createTransitionAnimatiom(Animation a) {
		
		CurrentPoseCalculation firstFrames = getPreviousAndNextFrames();
		
		Map<String, JointTransform> currentPose = new HashMap<String, JointTransform>();
		for (String jointName : firstFrames.previous.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = firstFrames.previous.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = firstFrames.next.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, firstFrames.progression);
			currentPose.put(jointName, currentTransform);
		}
		
		KeyFrame firstAnim 	= 	new KeyFrame(0.f			, 	currentPose);
		KeyFrame secondAnim =	new KeyFrame(TRANSITION_TIME, 	currentPose);
		return new Animation(TRANSITION_TIME, new KeyFrame[] {firstAnim,secondAnim});
	}

	/**
	 * This method returns the current animation pose of the entity. It returns
	 * the desired local-space transforms for all the joints in a map, indexed
	 * by the name of the joint that they correspond to.
	 * 
	 * The pose is calculated based on the previous and next keyframes in the
	 * current animation. Each keyframe provides the desired pose at a certain
	 * time in the animation, so the animated pose for the current time can be
	 * calculated by interpolating between the previous and next keyframe.
	 * 
	 * This method first finds the preious and next keyframe, calculates how far
	 * between the two the current animation is, and then calculated the pose
	 * for the current animation time by interpolating between the transforms at
	 * those keyframes.
	 * 
	 * @return The current pose as a map of the desired local-space transforms
	 *         for all the joints. The transforms are indexed by the name ID of
	 *         the joint that they should be applied to.
	 */
	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		CurrentPoseCalculation frames = getPreviousAndNextFrames();
		return interpolatePoses(frames.previous, frames.next, frames.progression);
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "joint transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given joint, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current joint, before applying it to the joint. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent joint). This can be done by multiplying the local-transform of
	 * the joint with the model-space transform of the parent joint.
	 * 
	 * The same thing is then done to all the child joints.
	 * 
	 * Finally the inverse of the joint's bind transform is multiplied with the
	 * model-space transform of the joint. This basically "subtracts" the
	 * joint's original bind (no animation applied) transform from the desired
	 * pose transform. The result of this is then the transform required to move
	 * the joint from its original model-space transform to it's desired
	 * model-space posed transform. This is the transform that needs to be
	 * loaded up to the vertex shader and used to transform the vertices into
	 * the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the joints for
	 *            the desired pose. The map is indexed by the name of the joint
	 *            which the transform corresponds to.
	 * @param joint
	 *            - the current joint which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent joint for
	 *            the pose.
	 */
	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		if(currentLocalTransform==null) {
			System.out.println(joint.name+" "+null);
		}
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		currentJointTransforms[joint.index]=currentTransform;
//		joint.setAnimationTransform(currentTransform);
	}

	/**
	 * Finds the previous keyframe in the animation and the next keyframe in the
	 * animation, and returns them in an array of length 2. If there is no
	 * previous frame (perhaps current animation time is 0.5 and the first
	 * keyframe is at time 1.5) then the first keyframe is used as both the
	 * previous and next keyframe. The last keyframe is used for both next and
	 * previous if there is no next keyframe.
	 * 
	 * @return The previous and next keyframes, in an array which therefore will
	 *         always have a length of 2.
	 */
	private CurrentPoseCalculation getPreviousAndNextFrames() {
		KeyFrame[] allFrames = currentAnimation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		float firstStamp = previousFrame.getTimeStamp();
		float secondStamp = nextFrame.getTimeStamp();
		if(nextFrame==allFrames[allFrames.length-1]) {
			setNextAnimation();
			if(nextAnimation!=null)
				nextFrame=nextAnimation.getKeyFrames()[0];
		}	
		return new CurrentPoseCalculation(previousFrame, nextFrame, firstStamp, secondStamp);
	}
	
	private void setNextAnimation() {
		if(nextAnimation!=null)return;
		if(nextAnimations.size()>0) {
			nextAnimation=nextAnimations.remove(0);
		}else if(loopQueue.size()>0){
			nextAnimations.addAll(loopQueue);
			setNextAnimation();
		}else {
			nextAnimation=null;
		}
	}

	/**
	 * Calculates how far between the previous and next keyframe the current
	 * animation time is, and returns it as a value between 0 and 1.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @return A number between 0 and 1 indicating how far between the two
	 *         keyframes the current animation time is.
	 */
	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		return calculateProgression(previousFrame.getTimeStamp(), nextFrame.getTimeStamp());
	}
	
	private float calculateProgression(float timeStampA, float timeStampB) {
		float totalTime = timeStampB - timeStampA;
		float currentTime = animationTime - timeStampA;
		return currentTime / totalTime;
	}

	/**
	 * Calculates all the local-space joint transforms for the desired current
	 * pose by interpolating between the transforms at the previous and next
	 * keyframes.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @param progression
	 *            - a number between 0 and 1 indicating how far between the
	 *            previous and next keyframes the current animation time is.
	 * @return The local-space transforms for all the joints for the desired
	 *         current pose. They are returned in a map, indexed by the name of
	 *         the joint to which they should be applied.
	 */
	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}
	
	private final class CurrentPoseCalculation {
		private final KeyFrame previous;
		private final KeyFrame next;
		private final float progression;
		
		private CurrentPoseCalculation(KeyFrame previous, KeyFrame next, float previousTime, float nextTime) {
			this.previous=previous;
			this.next=next;
			this.progression=calculateProgression(previousTime, nextTime);
		}
	}

}
