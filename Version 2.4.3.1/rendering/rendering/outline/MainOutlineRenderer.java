package rendering.outline;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import objects.models.RawModel;
import rendering.OpenGlUtils;
import rendering.loaders.Loader;

class MainOutlineRenderer {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private MainOutlineShader shader;
	
	public MainOutlineRenderer(Loader loader) {
		if(quad==null)quad = loader.loadToVAO(POSITIONS, 2);
		shader = new MainOutlineShader();
	}
	
	public void render(int texture) {
		start();
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		shader.loadSize(Display.getWidth(), Display.getHeight());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		shader.stop();
		end();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.forceGetVaoID());
		GL20.glEnableVertexAttribArray(0);
		OpenGlUtils.enableDepthTesting(false);
	}
	
	private static void end(){
		OpenGlUtils.enableDepthTesting(true);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}

}
