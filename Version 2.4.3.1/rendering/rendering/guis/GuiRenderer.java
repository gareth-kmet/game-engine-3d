package rendering.guis;

import java.util.Collection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import objects.models.RawModel;
import rendering.OpenGlUtils;
import rendering.loaders.Loader;
import toolbox.Maths;

public class GuiRenderer {
	
	private final RawModel quad;
	private final GuiShader shader;
	private final Loader loader;
	
	public GuiRenderer(Loader loader) {
		float[] positions = {-1,1, -1,-1, 1,1, 1,-1};
		quad = loader.loadToVAO(positions, 2);
		shader = new GuiShader();
		this.loader=loader;
	}
	
	public void render(Collection<GuiTexture> guis) {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID(loader));
		GL20.glEnableVertexAttribArray(0);
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.enableDepthTesting(false);
		for(GuiTexture gui : guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}

}
