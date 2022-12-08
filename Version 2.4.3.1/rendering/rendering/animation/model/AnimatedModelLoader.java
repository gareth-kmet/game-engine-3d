package rendering.animation.model;
import java.util.ArrayList;
import java.util.List;

import game.physics.collisions.loaders.CollisionDataLoader;
import rendering.loaders.Loader.LoaderInfo;
import rendering.loaders.Loader.LoaderInfo4f2i;
import rendering.loaders.LoaderVaoIdentity;
import rendering.loaders.collada.ColladaLoader;
import rendering.loaders.collada.structures.AnimatedModelData;
import rendering.loaders.collada.structures.JointData;
import rendering.loaders.collada.structures.MeshData;
import rendering.loaders.collada.structures.SkeletonData;

public class AnimatedModelLoader {
	
	private static final int MAX_WEIGHTS = 3;

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(String modelFile, CollisionDataLoader collisionDataLoader) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, MAX_WEIGHTS, collisionDataLoader);
		LoaderVaoIdentity model = createVao(entityData.getMeshData());
		SkeletonData skeletonData = entityData.getJointsData();
		List<Joint> headJoint = createJointsForHeads(skeletonData.headJoints);
		return new AnimatedModel(entityData.getMeshData().getIndices().length, model, headJoint, skeletonData.jointCount);
	}
	
	private static List<Joint> createJointsForHeads(List<JointData> datas){
		List<Joint> joints = new ArrayList<Joint>();
		for(JointData data : datas) {
			joints.add(createJoints(data));
		}
		return joints;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static LoaderVaoIdentity createVao(MeshData data) {
		LoaderInfo info = new LoaderInfo4f2i(data.getIndices(), data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getJointIds(), data.getVertexWeights());
		return new LoaderVaoIdentity(info);
	}

}
