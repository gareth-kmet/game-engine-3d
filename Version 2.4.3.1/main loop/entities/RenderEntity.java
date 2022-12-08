package entities;

import org.lwjgl.util.vector.Vector3f;

import game.physics.collisions.CollideCondition;
import game.physics.collisions.data.CollisionData;
import objects.TexturedModels;
import objects.models.Models;
import objects.models.RawModel;
import objects.models.animations.AnimatedModels;
import rendering.animation.animate.Animator;
import rendering.animation.model.AnimatedModel;
import rendering.entities.EntityRenderCondition;

public class RenderEntity{
	
	private final TexturedModels modelType;
	private final boolean hasAnimation;
	private Animator animator=null;
	private final boolean canCollide;
	private CollideCondition collideCondition = null;
	private boolean hasLoadedModelType = false;
	
	private Vector3f position;
	private float rotX, rotY, rotZ, scale;
	
	private int textureIndex = 0;
	private EntityRenderCondition renderCondition = EntityRenderCondition.NULL_CONDITION;
	
	private boolean killRender = false;
	private boolean isDeleted = false;

	
	public RenderEntity(TexturedModels model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this(model, 0, position, rotX, rotY, rotZ, scale);
	}
	
	public RenderEntity(TexturedModels model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.textureIndex = index;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.modelType=model;
//		if(modelType==null)throw new NullPointerException("ModelType can not be null for an Entity");
		if(modelType!=null) {
			canCollide = modelType.getModel().getCollisionType().canCollide();
			hasAnimation = modelType.getModel() instanceof AnimatedModels;
		}else {
			canCollide =false;
			hasAnimation = false;
		}
		
	}
	
	public boolean canCollide() {
		return canCollide;
	}
	
	public void updateAnimator(float deltaTime) {
		if(hasAnimation)
			animator.update(deltaTime);
	}
	
	public boolean hasAnimation() {
		return hasAnimation;
	}
	
	public Animator getAnimator() {
		return animator;
	}
	
	
	public CollideCondition getCollideCondition() {
		return collideCondition;
	}

	public void setCollideCondition(CollideCondition collideCondition) {
		this.collideCondition = collideCondition;
	}


	public void loadTexturedModelType() {
		
		if(hasLoadedModelType) {
			return;
		}
		modelType.use();
		if(hasAnimation)animator = new Animator((AnimatedModel)modelType.getModel().getRawModel());
		hasLoadedModelType=true;
		return;
	}
	
	public CollisionData getCollisionData() {
		return modelType.getModel().getCollisionData();
	}
	
	public float getTextureXOffset() {
		int column = textureIndex%modelType.getTexture().getNumberOfRows();
		return (float)column/(float)modelType.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset() {
		int row = textureIndex/modelType.getTexture().getNumberOfRows();
		return (float)row/(float)modelType.getTexture().getNumberOfRows();
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX+=dx;
		this.rotY+=dy;
		this.rotZ+=dz;
	}
	
	public TexturedModels getTexturedModel() {
		return modelType;
	}
	
	public RawModel getRawModel(boolean useResolution, int frameId) {
		Models m = modelType.getModel();
		if(!m.hasResolutions() || !useResolution) {
			return m.getRawModel();
		}
		return m.getRawModel(getResolution(frameId));
	}
	
	public RawModel getRawModel(float res, int frameId) {
		Models m =modelType.getModel();
		if(!m.hasResolutions()) {
			return m.getRawModel();
		}
		if(res>0) {
			res = Math.min(res, getResolution(frameId));
		}
		return m.getRawModel(res);
	}

	public Vector3f getPosition() {
		return position;
	}


	public void setPosition(Vector3f position) {
		this.position = position;
	}


	public float getRotX() {
		return rotX;
	}


	public void setRotX(float rotX) {
		this.rotX = rotX;
	}


	public float getRotY() {
		return rotY;
	}


	public void setRotY(float rotY) {
		this.rotY = rotY;
	}


	public float getRotZ() {
		return rotZ;
	}


	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	
	public Vector3f getRotationVector() {
		return new Vector3f(rotX, rotY, rotZ);
	}


	public float getScale() {
		return scale;
	}


	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public final void delete() {
		modelType.unUse();
		isDeleted = true;
	}
	
	public final boolean isDeleted() {
		return isDeleted;
	}
	
	public final void forceNoRender() {
		this.killRender=true;
	}
	
	public final boolean isForceNoRender() {
		return killRender;
	}
	
	public final void resetForceRender() {
		this.killRender=false;
	}
	
	public void addRenderCondition(EntityRenderCondition condition) {
		this.renderCondition=condition;
		
	}
	
	public void removeRenderCondition() {
		renderCondition = EntityRenderCondition.NULL_CONDITION;
	}
	public float getResolution(int frameId) {
		return renderCondition.getResolution(frameId);
	}
	public boolean canUseNormalMap(int frameId) {
		return renderCondition.canUseNormalMap(frameId);
	}
	public boolean precondition(int frameId) {
		return renderCondition.precondition(frameId);
	}
	

}
