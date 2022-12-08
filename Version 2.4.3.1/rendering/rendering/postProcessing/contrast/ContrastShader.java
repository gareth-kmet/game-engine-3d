package rendering.postProcessing.contrast;

import rendering.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "rendering/rendering/postProcessing/contrast/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "rendering/rendering/postProcessing/contrast/contrastFragment.txt";
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
