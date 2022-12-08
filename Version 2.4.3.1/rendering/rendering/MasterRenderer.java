package rendering;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import mainLoop.LoopManager.GivenMainLoopRequiredVariables;
import rendering.entities.EntityRenderer;
import rendering.entities.EntityRenderer.RenderType;
import rendering.font.TextMaster;
import rendering.guis.GuiRenderer;
import rendering.guis.GuiTexture;
import rendering.loaders.Loader;
import rendering.outline.MasterOutlineRenderer;
import rendering.particles.ParticleMaster;
import rendering.postProcessing.Fbo;
import rendering.postProcessing.PostProcessing;
import rendering.shadows.ShadowMapMasterRenderer;
import rendering.skybox.SkyboxRenderer;
import rendering.terrain.TerrainRenderer;
import rendering.water.WaterRenderer;

public class MasterRenderer {
	
	public static final float FOV = 70, NEAR_PLANE = 0.1f, FAR_PLANE = 10000;
	
	
	private Matrix4f projectionMatrix;
	
	private final SceneRenderer scene = new SceneRenderer();
	
	private final EntityRenderer entitiyRenderer;
	private final WaterRenderer waterRenderer;
	private final SkyboxRenderer skyboxRenderer;
	private final TerrainRenderer terrainRenderer;
	private final ShadowMapMasterRenderer shadowMasterRenderer;
	private final GuiRenderer guiRenderer;
	private final MasterOutlineRenderer outlineRenderer;
	
	private final Fbo multisampleFbo;
	private final Fbo outputFbo;
	private final Fbo outputFbo2;
	
	
	
	
	public MasterRenderer(Loader loader) {
		OpenGlUtils.cullBackFaces(true);
		createProjectionMatrix();
		
		entitiyRenderer = new EntityRenderer(loader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(loader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		shadowMasterRenderer = new ShadowMapMasterRenderer(loader);
		outlineRenderer = new MasterOutlineRenderer(loader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, projectionMatrix);
		guiRenderer = new GuiRenderer(loader);
		
		multisampleFbo= new Fbo(Display.getWidth(), Display.getHeight());
		outputFbo= new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		outputFbo2= new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		
		PostProcessing.init(loader);
		ParticleMaster.init(loader, projectionMatrix);
		TextMaster.init(loader);
		
		
	}
	
	public void renderScene(GivenMainLoopRequiredVariables mainLoopVariables, Vector4f clipPlane, EntityRenderer.RenderType renderType) {
		prepare();
		entitiyRenderer.render(clipPlane, mainLoopVariables, renderType);
		terrainRenderer.render(mainLoopVariables, clipPlane, shadowMasterRenderer.getToShadowMapSpaceMatrix());
		skyboxRenderer.render(mainLoopVariables.camera());
	}
	
	public void render(GivenMainLoopRequiredVariables mainLoopVariables, Vector4f clipPlane) {
		if(mainLoopVariables==null || mainLoopVariables.camera()==null || mainLoopVariables.sun()==null || mainLoopVariables.renderConditions()==null)return;
		
		scene.loadVariables(mainLoopVariables, clipPlane);
		
		outlineRenderer.render(mainLoopVariables.camera(), mainLoopVariables.outlinedEntities());
		
		shadowMasterRenderer.render(mainLoopVariables);
		
		
		waterRenderer.renderReflection(scene);
		multisampleFbo.bindFrameBuffer();
		scene.render(RenderType.REGULAR);
		waterRenderer.render(mainLoopVariables);
		
		ParticleMaster.renderParticles(mainLoopVariables.camera());
		
		multisampleFbo.unbindFrameBuffer();
		multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
		multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
		
		PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
		
		guiRenderer.render(mainLoopVariables.guis());
		
		TextMaster.render();
		
	}
	
	public int getShadowMaptexture() {
		return shadowMasterRenderer.getShadowMap();
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public void prepare() {
		OpenGlUtils.enableDepthTesting(true);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMaptexture());
		
	}
	
    private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
	
	public GuiTexture createOutlineGui() {
		return outlineRenderer.createOutlineGui();
	}
	
	public final class SceneRenderer {
		
		public GivenMainLoopRequiredVariables mainLoopRequiredVariables;
		private Vector4f clipPlane;
		
		private void loadVariables(GivenMainLoopRequiredVariables mainLoopRequiredVariables, Vector4f clipPlane) {
			this.clipPlane=clipPlane;
			this.mainLoopRequiredVariables=mainLoopRequiredVariables;
		}
		
		public void render(EntityRenderer.RenderType renderType) {
			render(this.clipPlane, renderType);
		}
		
		public void render(Vector4f clipPlane, EntityRenderer.RenderType renderType) {
			renderScene(mainLoopRequiredVariables, clipPlane, renderType);
		}
	}
	
	
	
	public void cleanUp() {
		terrainRenderer.cleanUp();
		entitiyRenderer.cleanUp();
		shadowMasterRenderer.cleanUp();
		PostProcessing.cleanUp();
		multisampleFbo.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		waterRenderer.cleanUp();
		outlineRenderer.cleanUp();
	}
}
