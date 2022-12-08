package rendering.water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import game.world.terrain.TerrainVariables.Water;
import mainLoop.DisplayManager;
import mainLoop.LoopManager.GivenMainLoopRequiredVariables;
import objects.models.RawModel;
import rendering.MasterRenderer;
import rendering.OpenGlUtils;
import rendering.entities.EntityRenderer;
import rendering.loaders.Loader;
import rendering.skybox.SkyBoxVariables;
import toolbox.Maths;

public class WaterRenderer {
	
	private static final String DUDV_MAP = "res/waterDUDV";
	private static final String NORMAL_MAP = "res/normal";
	private static final float WAVE_SPEED = 0.03f;
	private RawModel quad;
	private final WaterShader shader;
	private final WaterFrameBuffers fbos;
	
	private Loader.TEX dudvTexture, normalMap;
	private float moveFactor = 0;
	
	private Loader loader;

	public WaterRenderer(Loader loader, Matrix4f projectionMatrix) {
		shader = new WaterShader();
		fbos = new WaterFrameBuffers();
		dudvTexture = loader.loadGameTexture(DUDV_MAP);
		normalMap = loader.loadGameTexture(NORMAL_MAP);
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.loader=loader;
		setUpVAO();
		
	}
	
	public void renderReflection(MasterRenderer.SceneRenderer sceneRenderer) {
		Camera camera = sceneRenderer.mainLoopRequiredVariables.camera();
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		fbos.bindReflectionFrameBuffer();
		float distance = 2* (camera.getPosition().y - Water.HEIGHT);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		
		sceneRenderer.render(new Vector4f(0,1,0,-Water.HEIGHT), EntityRenderer.RenderType.REFLECTION);
		camera.getPosition().y+=distance;
		camera.invertPitch();
		fbos.bindRefractionFrameBuffer();
		sceneRenderer.render(new Vector4f(0,-1,0,Water.HEIGHT+1f), EntityRenderer.RenderType.REFLECTION);
		fbos.unbindCurrentFrameBuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	public void render(GivenMainLoopRequiredVariables vars) {
		
		prepareRender(vars.camera(), vars.sun());	
		for (WaterTile tile : vars.waterTiles()) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
					WaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	
	private void prepareRender(Camera camera, Light light){
		shader.start();
		shader.loadViewMatrix(camera);
		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();//TODO change water update time
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLight(light);
		shader.loadSky(SkyBoxVariables.Fog.colour, SkyBoxVariables.Fog.density, SkyBoxVariables.Fog.gradient);
		GL30.glBindVertexArray(quad.getVaoID(loader));
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture.id);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap.id);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		
		OpenGlUtils.enableAlphaBlending();

	}
	
	private void unbind(){
		OpenGlUtils.disableBlending();
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp() {
		dudvTexture.delete();
		normalMap.delete();
		fbos.cleanUp();
		shader.cleanUp();
	}

	private void setUpVAO() {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}

}
