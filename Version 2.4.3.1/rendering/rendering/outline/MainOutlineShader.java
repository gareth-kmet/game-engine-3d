package rendering.outline;

import org.lwjgl.util.vector.Vector2f;

import rendering.ShaderProgram;

class MainOutlineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "rendering/rendering/outline/mainVertex.txt";
	private static final String FRAGMENT_FILE = "rendering/rendering/outline/mainFragment.txt";
	
	private int 
		location_size;
	
	public MainOutlineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
		location_size = super.getUniformLocation("size");
	}
	
	public void loadSize(float width, float height) {
		super.loadVector(location_size, new Vector2f(width, height));
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
