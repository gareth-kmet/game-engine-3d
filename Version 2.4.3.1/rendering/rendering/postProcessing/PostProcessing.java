package rendering.postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import objects.models.RawModel;
import rendering.OpenGlUtils;
import rendering.loaders.Loader;
import rendering.postProcessing.bloom.BrightFilter;
import rendering.postProcessing.bloom.CombineFilter;
import rendering.postProcessing.gaussianBlur.HorizontalBlur;
import rendering.postProcessing.gaussianBlur.VerticalBlur;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	
	//private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur;
	private static CombineFilter combineFilter;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		//contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(Display.getWidth()/2, Display.getHeight()/2);
		hBlur = new HorizontalBlur(Display.getWidth()/5, Display.getHeight()/5);
		vBlur = new VerticalBlur(Display.getWidth()/5, Display.getHeight()/5);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colourTexture, int brightTexture){
		start();
		hBlur.render(brightTexture);
		vBlur.render(hBlur.getOutputTexture());
		//contrastChanger.render(vBlur.getOutputTexture());
		combineFilter.render(colourTexture, vBlur.getOutputTexture());
		end();
	}
	
	
	public static void cleanUp(){
		//contrastChanger.cleanUp();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		combineFilter.cleanUp();
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


}
