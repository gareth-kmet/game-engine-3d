package rendering.loaders.collada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import game.physics.collisions.loaders.CollisionDataLoader;
import rendering.loaders.Vertex;
import rendering.loaders.collada.structures.MeshData;
import rendering.loaders.collada.structures.VertexSkinData;
import rendering.loaders.collada.xml.XmlNode;

/**
 * Loads the mesh data for a model from a collada XML file.
 * @author Karl
 *
 */
public class GeometryLoader {

	private static final Matrix4f CORRECTION = new Matrix4f().rotate((float) Math.toRadians(-90), new Vector3f(1, 0,0));
	
	private final CollisionDataLoader collisionDataLoader;
	
	private final XmlNode mainNode;
	private final XmlNode meshData;
	private final List<XmlNode> triangleNodes;

	private final List<VertexSkinData> vertexWeights;
	
	private float[] verticesArray;
	private float[] normalsArray;
	private float[] texturesArray;
	private int[] indicesArray;
	private int[] jointIdsArray;
	private float[] weightsArray;

	List<Vertex> vertices = new ArrayList<Vertex>();
	List<Vector3f> textures = new ArrayList<Vector3f>();
	private final boolean texExist;
	List<Vector3f> normals = new ArrayList<Vector3f>();
	private final boolean normalsExist;
	List<Integer> indices = new ArrayList<Integer>();
	
	Map<String, Integer> materials = new LinkedHashMap<String, Integer>();
	
	private enum Inputs{
		VERTEX,NORMAL,TEXCOORD;
		private enum Offset{
			ZERO(0), ONE(1), TWO(2), THREE(3), NULL(-1);
			private final int value;
			private Offset(int v) {this.value=v;}
			private static Offset get(int i) {
				for(Offset v:values()) {
					if(i==v.value)return v;
				}
				return Offset.NULL;
			}
			private static int getIndexData(Offset o, int i, String[] indexData) {
				if(o==NULL)return 0;
				return Integer.parseInt(indexData[i+o.value]);
			}
		}
		private Offset getOffset(XmlNode trianglesNode) {
			XmlNode inputNode = getNode(trianglesNode);
			if(inputNode==null)return Offset.NULL;
			String offsets = inputNode.getAttribute("offset");
			int offset = Integer.parseInt(offsets);
			return Offset.get(offset);
		}
		private XmlNode getNode(XmlNode trianglesNode) {
			return trianglesNode.getChildWithAttribute("input", "semantic", this.toString());
		}
		private boolean exists(XmlNode trianglesNode) {
			return getNode(trianglesNode)!=null;
		}
	}
	
	public GeometryLoader(XmlNode mainNode, List<VertexSkinData> vertexWeights, CollisionDataLoader collisions) {
		this.collisionDataLoader=collisions==null?CollisionDataLoader.Loaders.NULL_COLLISIONS.New():collisions;
		this.vertexWeights = vertexWeights;
		this.mainNode=mainNode;
		this.meshData = mainNode.getChild("library_geometries").getChild("geometry").getChild("mesh");
		if(meshData.getChild("triangles")==null) {
			this.triangleNodes=meshData.getChildren("polylist");
		}else {
			this.triangleNodes=meshData.getChildren("triangles");
		}
		normalsExist = Inputs.NORMAL.exists(triangleNodes.get(0));
		texExist = Inputs.TEXCOORD.exists(triangleNodes.get(0));
		
	}
	
	public MeshData extractModelData(){
		readRawData();
		assembleAllVertices();
		removeUnusedVertices();
		initArrays();
		convertDataToArrays();
		convertIndicesListToArray();
		return new MeshData(verticesArray, texturesArray, normalsArray, indicesArray, jointIdsArray, weightsArray);
	}

	private void readRawData() {
		readPositions();
		readNormals();
		if(texExist) {
			readTextureCoords();
		}else {
			readMaterials();
		}
		
	}
	
	private void readMaterials() {
		XmlNode materialsNode = mainNode.getChild("library_materials");
		HashMap<String, String> effect_mat = new HashMap<String, String>();
		List<XmlNode> materials = materialsNode.getChildren("material");
		for(XmlNode material : materials) {
			String effectUrl = material.getChild("instance_effect").getAttribute("url");
			String materialId = material.getAttribute("id");
			effect_mat.put(effectUrl, materialId);
		}
		
		XmlNode effectsNode = mainNode.getChild("library_effects");
		List<XmlNode> effects = effectsNode.getChildren("effect");
		int i =0;
		for(XmlNode effect:effects) {
			String id = '#'+effect.getAttribute("id");
			String matId = effect_mat.get(id);
			String[] colourString = effect.getChild("profile_COMMON").getChild("technique").getChild("lambert").getChild("diffuse").getChild("color").getData().split(" ");
			Vector3f colour = new Vector3f(Float.parseFloat(colourString[0]), Float.parseFloat(colourString[1]), Float.parseFloat(colourString[2]));
			textures.add(colour);
			this.materials.put(matId, i++);
		}
		
	}

	private void readPositions() {
		String positionsId = meshData.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode positionsData = meshData.getChildWithAttribute("source", "id", positionsId).getChild("float_array");
		int count = Integer.parseInt(positionsData.getAttribute("count"));
		String[] posData = positionsData.getData().split(" ");
		for (int i = 0; i < count/3; i++) {
			float x = Float.parseFloat(posData[i * 3]);
			float y = Float.parseFloat(posData[i * 3 + 1]);
			float z = Float.parseFloat(posData[i * 3 + 2]);
			Vector4f position = new Vector4f(x, y, z, 1);
//			Matrix4f.transform(CORRECTION, position, position);
			vertices.add(new Vertex(vertices.size(), new Vector3f(position.x, position.y, position.z), vertexWeights.get(vertices.size())));
		}
	}

	private void readNormals() {
		if(!normalsExist) {
			normals.add(new Vector3f(0,1,0));
			return;
		}
		String normalsId = Inputs.NORMAL.getNode(triangleNodes.get(0)).getAttribute("source").substring(1);
		XmlNode normalsData = meshData.getChildWithAttribute("source", "id", normalsId).getChild("float_array");
		int count = Integer.parseInt(normalsData.getAttribute("count"));
		String[] normData = normalsData.getData().split(" ");
		for (int i = 0; i < count/3; i++) {
			float x = Float.parseFloat(normData[i * 3]);
			float y = Float.parseFloat(normData[i * 3 + 1]);
			float z = Float.parseFloat(normData[i * 3 + 2]);
			Vector4f norm = new Vector4f(x, y, z, 0f);
//			Matrix4f.transform(CORRECTION, norm, norm);
			normals.add(new Vector3f(norm.x, norm.y, norm.z));
		}
	}

	private void readTextureCoords() {
		XmlNode texCoordsIdChild = Inputs.TEXCOORD.getNode(triangleNodes.get(0));
		XmlNode texCoordsData = meshData.getChildWithAttribute("source", "id", texCoordsIdChild.getAttribute("source").substring(1)).getChild("float_array");
		int count = Integer.parseInt(texCoordsData.getAttribute("count"));
		String[] texData = texCoordsData.getData().split(" ");
		for (int i = 0; i < count/2; i++) {
			float s = Float.parseFloat(texData[i * 2]);
			float t = Float.parseFloat(texData[i * 2 + 1]);
			textures.add(new Vector3f(s, 1-t,0));
		}
	}
	
	private void assembleAllVertices() {
		for(XmlNode triangleNode : triangleNodes) {
			assembleVertices(triangleNode);
		}
	}
	
	private void assembleVertices(XmlNode poly){
		
		int typeCount = poly.getChildren("input").size();
		String[] indexData = poly.getChild("p").getData().split(" ");
		
		Inputs.Offset vertexOffset = Inputs.VERTEX.getOffset(poly);
		Inputs.Offset normalsOffset = Inputs.NORMAL.getOffset(poly);
		Inputs.Offset texturesOffset = Inputs.TEXCOORD.getOffset(poly);
		
		for(int i=0;i<indexData.length/typeCount;i++){
			int positionIndex = Inputs.Offset.getIndexData(vertexOffset, i*typeCount, indexData);
			int normalIndex = Inputs.Offset.getIndexData(normalsOffset, i*typeCount, indexData);
			int texCoordIndex;
			if(texExist) {
				texCoordIndex = Inputs.Offset.getIndexData(texturesOffset, i*typeCount, indexData);
			}else {
				String material = poly.getAttribute("material");
				texCoordIndex = materials.get(material);
			}
			
			processVertex(positionIndex, normalIndex, texCoordIndex);
		}
	}
	

	private Vertex processVertex(int posIndex, int normIndex, int texIndex) {
		Vertex currentVertex = vertices.get(posIndex);
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(texIndex);
			currentVertex.setNormalIndex(normIndex);
			indices.add(posIndex);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, texIndex, normIndex);
		}
	}

	private int[] convertIndicesListToArray() {
		this.indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private void convertDataToArrays() {
		collisionDataLoader.initiate();
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			collisionDataLoader.loadVertex(currentVertex);
			Vector3f position = currentVertex.getPosition();
			Vector3f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 3] = textureCoord.x;
			texturesArray[i * 3 + 1] = textureCoord.y;
			texturesArray[i * 3 + 2] = textureCoord.z;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			VertexSkinData weights = currentVertex.getWeightsData();
			jointIdsArray[i * 3] = weights.jointIds.get(0);
			jointIdsArray[i * 3 + 1] = weights.jointIds.get(1);
			jointIdsArray[i * 3 + 2] = weights.jointIds.get(2);
			weightsArray[i * 3] = weights.weights.get(0);
			weightsArray[i * 3 + 1] = weights.weights.get(1);
			weightsArray[i * 3 + 2] = weights.weights.get(2);

		}
		collisionDataLoader.finish(vertices);
	}

	private Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition(), previousVertex.getWeightsData());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}

		}
	}
	
	private void initArrays(){
		this.verticesArray = new float[vertices.size() * 3];
		this.texturesArray = new float[vertices.size() * 3];
		this.normalsArray = new float[vertices.size() * 3];
		this.jointIdsArray = new int[vertices.size() * 3];
		this.weightsArray = new float[vertices.size() * 3];
	}

	private void removeUnusedVertices() {
		for (Vertex vertex : vertices) {
			vertex.averageTangents();
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
	
}