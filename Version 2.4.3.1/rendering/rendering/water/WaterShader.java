package rendering.water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;
import entities.Camera;
import entities.Light;
import rendering.ShaderProgram;

class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "rendering/rendering/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "rendering/rendering/water/waterFragment.txt";

	private int 
		location_modelMatrix,
		location_viewMatrix,
		location_projectionMatrix,
		location_reflectionTexture,
		location_refractionTexture,
		location_dudvMap,
		location_moveFactor,
		location_cameraPosition,
		location_normalMap,
		location_lightColour,
		location_lightPosition,
		location_depthMap,
		location_skyColour,
		location_density,
		location_gradient
	;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPosition = getUniformLocation("lightPosition");
		location_depthMap = getUniformLocation("depthMap");
		location_skyColour = getUniformLocation("skyColour");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
	}
	
	protected void loadSky(Vector3f colour, float density, float gradient){
		super.loadVector(location_skyColour, colour);
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}
	
	public void loadMoveFactor(float factor) {
		super.loadFloat(location_moveFactor, factor);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}
	
	public void loadLight(Light light) {
		super.loadVector(location_lightColour, light.getColour());
		super.loadVector(location_lightPosition, light.getPosition());
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}
	

}
