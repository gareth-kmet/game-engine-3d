package rendering.outline;

import java.util.Collection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import entities.RenderEntity;
import objects.TexturedModels;
import objects.models.RawModel;
import objects.textures.Textures;
import rendering.OpenGlUtils;
import rendering.loaders.Loader;
import toolbox.Maths;

class OutlineRenderer {

	
	private OutlineShader shader;
	private Loader loader;
	
	public OutlineRenderer(Loader loader, Matrix4f projectionMatrix) {
		this.shader = new OutlineShader();
		
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
		this.loader=loader;
	}
	
	
	
	public void render(Camera camera, Collection<? extends RenderEntity> entities) {
		prepare();
		shader.start();
		for(RenderEntity entity : entities) {
			shader.loadViewMatrix(camera);
			prepareTexturedModel(entity);
			prepareInstance(entity);
			GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getTexturedModel().getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
		shader.stop();
		
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	private void prepareTexturedModel(RenderEntity e) {
		TexturedModels texturedModel = e.getTexturedModel();
		RawModel model = texturedModel.getModel().getRawModel();
		GL30.glBindVertexArray(model.getVaoID(loader));
		GL20.glEnableVertexAttribArray(0);
		Matrix4f[] transforms=null;
		if(e.hasAnimation()) {
			transforms=e.getAnimator().getCurrentJointTransforms();
			GL20.glEnableVertexAttribArray(4);
			GL20.glEnableVertexAttribArray(5);
		}shader.loadUseAnimation(transforms);
		
		Textures texture = texturedModel.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if(texture.hasTransparency()) {
			OpenGlUtils.cullBackFaces(false);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID(loader));
	}
	
	private void unbindTexturedModel() {
		OpenGlUtils.cullBackFaces(true);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
	}
	
	private void prepareInstance(RenderEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
//		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 0, 0, 0, entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
	
	
	
}
