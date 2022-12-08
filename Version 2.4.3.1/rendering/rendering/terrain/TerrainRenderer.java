package rendering.terrain;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import game.world.WorldRandomStyles;
import game.world.environment.styles.EnvironmentStyles;
import game.world.terrain.Terrain;
import mainLoop.LoopManager.GivenMainLoopRequiredVariables;
import objects.models.RawModel;
import rendering.OpenGlUtils;
import rendering.RenderConditions;
import rendering.loaders.Loader;
import rendering.skybox.SkyBoxVariables;
import toolbox.Maths;

public class TerrainRenderer {
	
	private TerrainShader shader;
	private final Loader loader;
	
	public TerrainRenderer(Loader loader, Matrix4f projectionMatrix) {
		shader=new TerrainShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
		this.loader=loader;
	}
	
	public void render(GivenMainLoopRequiredVariables mainLoopRequiredVariables, Vector4f clipPlane, Matrix4f toShadowSpace) {
		
		prepare(mainLoopRequiredVariables.lights(), clipPlane, mainLoopRequiredVariables.camera(), toShadowSpace, mainLoopRequiredVariables.renderConditions());
		
		for(Terrain terrain:mainLoopRequiredVariables.terrains()) {
			if(!terrain.exists())continue;
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
		OpenGlUtils.goWireframe(false);
		shader.stop();
	}
	
	private void prepare(List<Light> lights, Vector4f clipPlane, Camera camera, Matrix4f toShadowSpace, RenderConditions[] conditions) {
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSky(SkyBoxVariables.Fog.colour, SkyBoxVariables.Fog.density, SkyBoxVariables.Fog.gradient);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_6)) {
			OpenGlUtils.goWireframe(true);
		}
		
		if(RenderConditions.getLoopCondition(conditions)==RenderConditions.LoopCondition.GAME) {
			((EnvironmentStyles)WorldRandomStyles.getStyle(conditions, WorldRandomStyles.EVIRONMENT_STYLE)).bindTextures(loader);
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel model = terrain.getModel();
		GL30.glBindVertexArray(model.getVaoID(loader));
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		shader.loadShineVariables(1,0);
		
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(terrain.getModelMatrixPos(),
				0,0,0,1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}

}
