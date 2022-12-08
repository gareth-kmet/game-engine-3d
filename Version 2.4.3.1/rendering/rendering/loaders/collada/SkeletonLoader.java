package rendering.loaders.collada;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import rendering.loaders.collada.structures.JointData;
import rendering.loaders.collada.structures.SkeletonData;
import rendering.loaders.collada.xml.XmlNode;

public class SkeletonLoader {

	private XmlNode armatureData;
	
	private List<String> boneOrder;
	
	private int jointCount = 0;
	
	private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 1, 0));

	public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "type", "NODE");
		this.boneOrder = boneOrder;
	}
	
	public SkeletonData extractBoneData(){
		List<XmlNode> headNodes = armatureData.getChildren("node");
		List<JointData> headJoints = new ArrayList<JointData>();
		for(XmlNode head : headNodes) {
			if(head.getAttribute("type").equals("JOINT")) {
				headJoints.add(loadJointData(head, true));
			}
		}
		return new SkeletonData(jointCount, headJoints);
	}
	
	private JointData loadJointData(XmlNode jointNode, boolean isRoot){
		JointData joint = extractMainJointData(jointNode, isRoot);
		for(XmlNode childNode : jointNode.getChildren("node")){
			JointData data = loadJointData(childNode, false);
			if(data!=null) {
				joint.addChild(data);
			}
		}
		return joint;
	}
	
	private JointData extractMainJointData(XmlNode jointNode, boolean isRoot){
		String nameId = jointNode.getAttribute("sid");
		int index = boneOrder.indexOf(nameId);
		if(index==-1) {
			return null;
		}
		XmlNode matrixNode = jointNode.getChild("matrix");
		Matrix4f matrix = new Matrix4f();
		if(matrixNode!=null) {
			String[] matrixData = matrixNode.getData().split(" ");
			matrix.load(convertData(matrixData));
			matrix.transpose();
		}else {
			String[] translate = jointNode.getChild("translate").getData().split(" ");
			Vector3f trans  = new Vector3f(Float.parseFloat(translate[0]), Float.parseFloat(translate[1]), Float.parseFloat(translate[2]));
			matrix.translate(trans);
			for(XmlNode rotationNode : jointNode.getChildren("rotate")) {
				String[] rotate = rotationNode.getData().split(" ");
				float[] rot = new float[4];
				for(int i=0; i<4; i++) {
					rot[i]=Float.parseFloat(rotate[i]);
				}
				matrix.rotate((float)Math.toRadians(rot[3]), new Vector3f(rot[0], rot[1], rot[2]));
			}
			String[] scale = jointNode.getChild("scale").getData().split(" ");
			Vector3f sc = new Vector3f(Float.parseFloat(scale[0]), Float.parseFloat(scale[1]), Float.parseFloat(scale[2]));
			matrix.scale(sc);
		}
		
		if(isRoot){
			//because in Blender z is up, but in our game y is up.
//			Matrix4f.mul(CORRECTION, matrix, matrix);
		}
		jointCount++;
		return new JointData(index, nameId, matrix);
	}
	
	private FloatBuffer convertData(String[] rawData){
		float[] matrixData = new float[16];
		for(int i=0;i<matrixData.length;i++){
			matrixData[i] = Float.parseFloat(rawData[i]);
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(matrixData);
		buffer.flip();
		return buffer;
	}

}
