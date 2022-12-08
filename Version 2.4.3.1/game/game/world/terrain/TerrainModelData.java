package game.world.terrain;

import objects.models.RawModel;
import rendering.loaders.Loader.LoaderInfo_f1i;

public class TerrainModelData {
	
	static final int[] RESOLUTIONS = {1,2,3,4,5,6};
	static final int VERTEX_COUNT = 241;
	
	public final float[] vertices, normals, textureColours, textureCoords;
	public final int[] indices;
	public final int count, vertexCount, resolution;
	
	
	
	TerrainModelData(int count, int vertexCount, int resolution) {
		this.count=count; this.vertexCount=vertexCount; this.resolution=resolution;
		vertices = new float[count*3];
		normals = new float[count*3];
		textureColours = new float[count];
		textureCoords = new float[count*2];
		indices = new int[6*(vertexCount-1)*(vertexCount-1)];
	}
	
	RawModel loadToModel() {
		LoaderInfo_f1i loaderInfo = new LoaderInfo_f1i(indices, new int[] {3,3,1,2}, vertices, normals, textureColours, textureCoords);
		return loaderInfo.createRawModel();
	}

}
