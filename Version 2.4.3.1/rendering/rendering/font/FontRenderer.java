package rendering.font;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import rendering.OpenGlUtils;
import rendering.loaders.Loader;

public class FontRenderer {

	private FontShader shader;
	private Loader loader;

	public FontRenderer(Loader loader) {
		shader = new FontShader();
		this.loader=loader;
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	public void render(Map<FontType, List<GUIText>> texts) {
		prepare();
		for(FontType font : texts.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas().getTexId(loader));
			for(GUIText text : texts.get(font)) {
				renderText(text);
			}
		}
		endRendering();
	}
	
	private void prepare(){
		OpenGlUtils.enableAlphaBlending();
		OpenGlUtils.enableDepthTesting(false);
		shader.start();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColour(text.getColour());
		shader.loadTranslation(text.getPosition());
		shader.loadEffects(text);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
	}

}
