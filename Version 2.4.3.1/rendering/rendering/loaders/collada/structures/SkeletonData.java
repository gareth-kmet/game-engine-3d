package rendering.loaders.collada.structures;

import java.util.List;

public class SkeletonData {
	
	public final int jointCount;
	public final List<JointData> headJoints;
	
	public SkeletonData(int jointCount, List<JointData> headJoint){
		this.jointCount = jointCount;
		this.headJoints = headJoint;
	}

}
