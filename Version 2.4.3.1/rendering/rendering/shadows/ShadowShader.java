package rendering.shadows;

import org.lwjgl.util.vector.Matrix4f;

import rendering.ShaderProgram;
import rendering.animation.animate.JointTransform;

class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "rendering/rendering/shadows/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "rendering/rendering/shadows/shadowFragmentShader.txt";
	
	private int 
		location_mvpMatrix,
		location_useAnimation,
		location_jointTransforms[];

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		location_useAnimation = super.getUniformLocation("useAnimation");
		location_jointTransforms = new int[JointTransform.MAX_JOINT_TRANSFORMS];
		for(int i =0; i<JointTransform.MAX_JOINT_TRANSFORMS; i++) {
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
		super.bindAttribute(4, "in_jointIndices");
		super.bindAttribute(5, "in_weights");
		
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
