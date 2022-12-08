package rendering.outline;

import java.util.Collection;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import entities.RenderEntity;
import rendering.guis.GuiTexture;
import rendering.loaders.Loader;
import rendering.postProcessing.Fbo;

public class MasterOutlineRenderer {
	
	private final Fbo outline;
	private final Fbo outline2;
	private final OutlineRenderer outlineRenderer;
	private final MainOutlineRenderer outlineRenderer2;
	
	public MasterOutlineRenderer(Loader loader, Matrix4f projectionMatrix) {
		outline = new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
		outlineRenderer = new OutlineRenderer(loader, projectionMatrix);
		outline2 = new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
		outlineRenderer2 = new MainOutlineRenderer(loader);
	}
	
	public GuiTexture createOutlineGui() {
		return new GuiTexture(outline2.getColourTexture(), new Vector2f(0f, 0f), new Vector2f(1f, 1f));
	}
	
	public void render(Camera camera, Collection<? extends RenderEntity> entities) {
		outline.bindFrameBuffer();
		outlineRenderer.render(camera, entities);
		outline.unbindFrameBuffer();
		outline2.bindFrameBuffer();
		outlineRenderer2.render(outline.getColourTexture());
		outline2.unbindFrameBuffer();
		
	}
	
	public void cleanUp() {
		outline2.cleanUp();
		outlineRenderer2.cleanUp();
		outlineRenderer.cleanUp();
		outline.cleanUp();
	}

}
