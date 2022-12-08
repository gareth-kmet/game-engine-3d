package game.world.terrain;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.world.WorldChunk;
import game.world.terrain.TerrainGenerator.Vertex;
import game.world.terrain.heights.HeightsDataClass;
import objects.models.RawModel;
import toolbox.Maths;

public class Terrain {
	
	private RawModel model;
	@SuppressWarnings("unused")
	private TerrainModelData modelData;
	final float[][] heights;
	int currentResolution = 0;
	final int x, z;
	final Point position;
	final Vector3f modelMatrixPos;
	boolean exists = true;
	
	Vertex[][] vertices;
	
	Terrain(HeightsDataClass heights, Point position){
		this.heights = heights.getMap();
		this.x = position.getX(); this.z=position.getY();
		this.position = position;
		modelMatrixPos = new Vector3f(x*WorldChunk.SIZE,0,z*WorldChunk.SIZE);
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX;
		float terrainZ = worldZ;
		float gridSquareSize = WorldChunk.SIZE/((float)heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if(gridX >=heights.length-1 || gridZ >= heights.length-1 || gridX<0 || gridZ<0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize)/gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
		float answer;
		if (xCoord <= (1-zCoord)) {
			answer = Maths.barycentricHeight(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barycentricHeight(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
		
	}
	
	public Vector3f getNormalOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX;
		float terrainZ = worldZ;
		float gridSquareSize = WorldChunk.SIZE/((float)heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if(gridX >=heights.length-1 || gridZ >= heights.length-1 || gridX<0 || gridZ<0) {
			return null;
		}
		float xCoord = (terrainX % gridSquareSize)/gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize)/gridSquareSize;
		Vector3f weights;
		Vertex[] vs = new Vertex[3];
		if (xCoord <= (1-zCoord)) {
			weights = Maths.barycentricWeights(new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(0, 1), new Vector2f(xCoord, zCoord));
			vs[0] = vertices[gridX][gridZ];
			vs[1] = vertices[gridX + 1][gridZ];
			vs[2] = vertices[gridX][gridZ + 1];
		} else {
			weights = Maths.barycentricWeights(new Vector2f(1, 0), new Vector2f(1, 1), new Vector2f(0, 1), new Vector2f(xCoord, zCoord));
			vs[0] = vertices[gridX + 1][gridZ];
			vs[1] = vertices[gridX + 1][gridZ + 1];
			vs[2] = vertices[gridX][gridZ + 1];
		}
		
		Vector3f nA = new Vector3f(vs[0].normal);
		Vector3f nB = new Vector3f(vs[1].normal);
		Vector3f nC = new Vector3f(vs[2].normal);
		
		nA.scale(weights.x); nB.scale(weights.y); nC.scale(weights.z);
		Vector3f normal = Vector3f.add(nA, Vector3f.add(nB, nC, null), null);
		normal.normalise();
		return normal;
		
	
	}
	
	void setModel(TerrainModelData modelData) {
		
		RawModel oldModel=model;
		model=modelData.loadToModel();
		if(oldModel!=null)oldModel.delete();
		
		this.modelData=modelData;
	}
	
	void setVertices(Vertex[][] vs) {
		this.vertices = vs;
	}
	
	public RawModel getModel() {
		return model;
	}
	
	public Point getPosition() {
		return position;
	}
	public Vector3f getModelMatrixPos() {
		return modelMatrixPos;
	}
	
	public boolean exists() {
		return exists;
	}
	
	void delete() {
		exists=false;
		if(model!=null)model.delete();
	}

}
