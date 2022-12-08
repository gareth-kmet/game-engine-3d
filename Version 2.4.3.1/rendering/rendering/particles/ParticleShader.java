package rendering.particles;

import org.lwjgl.util.vector.Matrix4f;

import rendering.ShaderProgram;

class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "rendering/rendering/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "rendering/rendering/particles/particleFShader.txt";

	private int 
		location_numberOfRows,
		location_projectionMatrix;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}
	protected void loadNumberOfRows(float num){
		super.loadFloat(location_numberOfRows, num);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
