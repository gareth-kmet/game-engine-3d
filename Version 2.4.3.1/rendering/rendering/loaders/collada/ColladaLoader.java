package rendering.loaders.collada;
import game.physics.collisions.loaders.CollisionDataLoader;
import rendering.loaders.collada.structures.AnimatedModelData;
import rendering.loaders.collada.structures.AnimationData;
import rendering.loaders.collada.structures.MeshData;
import rendering.loaders.collada.structures.SkeletonData;
import rendering.loaders.collada.structures.SkinningData;
import rendering.loaders.collada.xml.XmlNode;
import rendering.loaders.collada.xml.XmlParser;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String colladaFile, int maxWeights, CollisionDataLoader collisionDataLoader) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node, skinningData.verticesSkinData, collisionDataLoader);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(String colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
