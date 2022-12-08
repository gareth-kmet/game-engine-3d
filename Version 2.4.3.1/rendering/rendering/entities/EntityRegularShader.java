package rendering.entities;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Light;
import rendering.animation.animate.JointTransform;

final class EntityRegularShader extends EntityRenderer.EntityShader{
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "rendering/rendering/entities/VShader.glsl";
	private static final String FRAGMENT_FILE = "rendering/rendering/entities/FShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_modelTexture;
	private int location_useSpecularMap;
	private int location_specularMap;
	private int location_useFakeLighting;
	private int location_density;
	private int location_gradient;
	private int location_useAnimation;
	private int location_jointTransforms[];
	private int location_useMaterial;

	public EntityRegularShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "in_jointIndices");
		super.bindAttribute(5, "in_weights");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_modelTexture = super.getUniformLocation("modelTexture");
		location_specularMap = super.getUniformLocation("specularMap");
		location_useSpecularMap = super.getUniformLocation("useSpecularMap");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for(int i=0;i<MAX_LIGHTS;i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_useAnimation = super.getUniformLocation("useAnimation");
		location_jointTransforms = new int[JointTransform.MAX_JOINT_TRANSFORMS];
		for(int i =0; i<JointTransform.MAX_JOINT_TRANSFORMS; i++) {
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
		location_useMaterial = super.getUniformLocation("useMaterial");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_specularMap, 2);
	}
	
	@Override
	protected void loadClipPlane(Vector4f plane){
		super.loadVector(location_plane, plane);
	}
	
	@Override
	protected void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	@Override
	protected void loadOffset(float x, float y){
		super.loadVector(location_offset, new Vector2f(x,y));
	}
	
	@Override
	protected void loadSky(Vector3f colour, float density, float gradient){
		super.loadVector(location_skyColour, colour);
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}
	
	@Override
	protected void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	@Override
	protected void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	@Override
	protected void loadLights(List<Light> lights, Matrix4f viewMatrix){
		for(int i=0;i<MAX_LIGHTS;i++){
			if(i<lights.size()){
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}else{
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	@Override
	protected void loadUseAnimation(boolean use) {
		super.loadBoolean(location_useAnimation, use);
	}
	
	@Override
	protected void loadUseSpecularMap(boolean useMap) {
		super.loadBoolean(location_useSpecularMap, useMap);
	}
	
	@Override
	protected void loadFakeLightingVariable(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	@Override
	protected void loadViewMatrix(Matrix4f viewMatrix){
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	protected void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}

	@Override
	protected void loadAnimationTransforms(Matrix4f[] transforms) {
		for(int i=0; i<transforms.length; i++) {
			super.loadMatrix(location_jointTransforms[i], transforms[i]);
		}
		
	}

	@Override
	protected void loadUseMaterial(boolean use) {
		super.loadBoolean(location_useMaterial, use);
		
	}
	
	

}
