package rendering.outline;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import rendering.ShaderProgram;
import rendering.animation.animate.JointTransform;
import toolbox.Maths;

class OutlineShader extends ShaderProgram {
	
	
	private static final String VERTEX_FILE = "rendering/rendering/outline/vertexShader.txt", 
			FRAGMENT_FILE = "rendering/rendering/outline/fragmentShader.txt";
	
	private int 
		location_transformationMatrix, 
		location_projectionMatrix, 
		location_viewMatrix, 
		location_numberOfRows,
		location_offset,
		location_textureSampler,
		location_useAnimation,
		location_jointTransforms[]
	;
	
	
	public OutlineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(4, "in_jointIndices");
		super.bindAttribute(5, "in_weights");
		
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix=super.getUniformLocation("transformationMatrix");
		location_projectionMatrix=super.getUniformLocation("projectionMatrix");
		location_viewMatrix=super.getUniformLocation("viewMatrix");
		location_numberOfRows=super.getUniformLocation("numberOfRows");
		location_offset=super.getUniformLocation("offset");
		location_textureSampler=super.getUniformLocation("textureSampler");
		location_useAnimation = super.getUniformLocation("useAnimation");
		location_jointTransforms = new int[JointTransform.MAX_JOINT_TRANSFORMS];
		for(int i =0; i<JointTransform.MAX_JOINT_TRANSFORMS; i++) {
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
		
	}
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadOffset(float x, float y) {
		super.loadVector(location_offset, new Vector2f(x,y));
	}
	
	
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_textureSampler, 0);
	}
	
	public void loadUseAnimation(Matrix4f[] transforms) {
		if(transforms==null) {
			super.loadBoolean(location_useAnimation, false);
		}else {
			super.loadBoolean(location_useAnimation, true);
			for(int i=0; i<transforms.length; i++) {
				super.loadMatrix(location_jointTransforms[i], transforms[i]);
			}
		}
	}

}
