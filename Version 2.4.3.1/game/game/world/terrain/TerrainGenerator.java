package game.world.terrain;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.world.WorldChunk;
import game.world.WorldLoaderThread;
import game.world.WorldVariables;
import game.world.environment.styles.Environment;
import game.world.terrain.heights.EnvironmentHeight;
import game.world.terrain.heights.Height;
import game.world.terrain.heights.Noise;
import rendering.water.WaterTile;
import toolbox.Maths;

public class TerrainGenerator {
	
	private final static boolean TEST_CHUNK_BORDERS = true;
	
	private final WorldVariables worldVariables;

	final ArrayList<WaterTile> waterTiles = new ArrayList<WaterTile>();
	
	private final TerrainStorage storage;
	
	private TerrainGenerationRequest nextTerrainRequest = null;
	private TerrainGenerationRequest currentTerrainRequest = null;

	public int octaves=4;
	public float persistance=0.25f;
	public float scale = 100;
	public float lacunarity=2.0f;
	
	TerrainGenerator(WorldVariables variables) {
		
		worldVariables=variables;
		
		storage = new TerrainStorage();
		
		for(int x = -worldVariables.terrainVariables.CHUNK_LOAD; x<=worldVariables.terrainVariables.CHUNK_LOAD; x++) {
			for(int z = -worldVariables.terrainVariables.CHUNK_LOAD; z<=worldVariables.terrainVariables.CHUNK_LOAD; z++) {
				for(int xx = 0; xx<WorldChunk.SIZE/100; xx++) {
					for(int zz=0; zz<WorldChunk.SIZE/100; zz++) {
						WaterTile water = new WaterTile(WorldChunk.SIZE*(x+(xx+0.5f)/(WorldChunk.SIZE/100f)), WorldChunk.SIZE*(z+(zz+0.5f)/(WorldChunk.SIZE/100f)), TerrainVariables.Water.HEIGHT);
						waterTiles.add(water);
						worldVariables.mainLoopVariables.waterTiles.add(water);
					}
				}
			}
		}
	}
	
	void generate() {
		
		if(nextTerrainRequest!=null) {
			nextTerrainRequest.removeFromThread(); // just incase it somehow did get threaded
		}
		nextTerrainRequest = new TerrainGenerationRequest(worldVariables.playerVariables.currentChunk);
		
	}
	
	void updateGeneration() {
		
		if(currentTerrainRequest!=null) {
			if(currentTerrainRequest.isComplete()) {
				currentTerrainRequest.finish();
				currentTerrainRequest=null;
			}
		}
		
		if(currentTerrainRequest==null && nextTerrainRequest != null) {
			currentTerrainRequest = nextTerrainRequest;
			nextTerrainRequest=null;
			currentTerrainRequest.prepare();
			worldVariables.worldThreadMethods.addRequestToThread(currentTerrainRequest);
			
		}
		
		
	}
	
	void updateWaters() {
		Point newPlayerChunk = worldVariables.playerVariables.currentChunk;
		Point oldPlayerChunk = worldVariables.playerVariables.lastChunk;
		
		int xDif = newPlayerChunk.getX()-oldPlayerChunk.getX();
		xDif *= WorldChunk.SIZE;
		int zDif = newPlayerChunk.getY()-oldPlayerChunk.getY();
		zDif *= WorldChunk.SIZE;
		for(WaterTile water : waterTiles) {
			water.setX(water.getX()+xDif);
			water.setZ(water.getZ()+zDif);
			updateWaterRendering(water, newPlayerChunk);
		}
	}
	
	private void updateWaterRendering(WaterTile water, Point playerChunk) {
		int x = (int) Math.floor(water.getX()/WorldChunk.SIZE);
		int z = (int) Math.floor(water.getZ()/WorldChunk.SIZE);
		x-=playerChunk.getX();
		z-=playerChunk.getY();
		
		if(x*x+z*z>worldVariables.terrainVariables.CHUNK_LOAD_SQR) {
			worldVariables.mainLoopVariables.waterTiles.remove(water);
		}else if(!worldVariables.mainLoopVariables.waterTiles.contains(water)) {
			worldVariables.mainLoopVariables.waterTiles.add(water);
		}
	}
	
	
	
	
	private class TerrainGenerationRequest extends WorldLoaderThread.Request{
		
		private final Point playerChunk;
		private final Terrain[] terrains;
		private final TerrainModelData[] models;
		
		private final int chunkLoad, chunkLoadSqr;
		private HashSet<Terrain> toBeRendered;
		
		private TerrainGenerationRequest(Point playerPos) {
			this.playerChunk=playerPos;
			chunkLoad = worldVariables.terrainVariables.CHUNK_LOAD;
			chunkLoadSqr = worldVariables.terrainVariables.CHUNK_LOAD_SQR;
			terrains = new Terrain[(2*chunkLoad+1)*(2*chunkLoad+1)];
			models = new TerrainModelData[terrains.length];
		}
		
		private void prepare() {
			for(int i=0, tx = playerChunk.getX()-chunkLoad; tx<=playerChunk.getX()+chunkLoad; tx++) {
				for(int tz = playerChunk.getY()-chunkLoad; tz<=playerChunk.getY()+chunkLoad; tz++, i++) {
					terrains[i] = storage.getTerrain(tx, tz);
				}
			}
		}

		@Override
		protected boolean run() {
			
			for(int i=0, tx = playerChunk.getX()-chunkLoad; tx<=playerChunk.getX()+chunkLoad; tx++) {
				for(int tz = playerChunk.getY()-chunkLoad; tz<=playerChunk.getY()+chunkLoad; tz++, i++) {
					if(terrains[i]!=null) {
						continue;
					}
					
					Point position = new Point(tx,tz);
					int ox = tx * (TerrainModelData.VERTEX_COUNT-1);
					int oz = tz * (TerrainModelData.VERTEX_COUNT-1);
					Vector2f offset = new Vector2f(ox-1, oz-1);
					
					Noise.Data noiseMap = Noise.generateNoiseMap(
							TerrainModelData.VERTEX_COUNT, TerrainModelData.VERTEX_COUNT, 
							scale*worldVariables.styles.landHeight.getScale(), 
							worldVariables.seed, 
							octaves, 
							persistance, 
							lacunarity, 
							offset
						);
					
					
					Height.Data heightMap = Height.generateHeightMap(noiseMap, worldVariables.styles.landHeight, worldVariables.styles.waterHeight);
					EnvironmentHeight.Data environmentHeightMap = EnvironmentHeight.generateEnvironmentHeightMap(heightMap, worldVariables.styles.environment);
					worldVariables.styles.falloutHeight.fallout(environmentHeightMap, (int)offset.x, (int)offset.y);
					
					Terrain t = new Terrain(environmentHeightMap, position);
					terrains[i] = t;
					storage.setTerrain(t.x, t.z, t);
				}
			}
			
			for(int k=0; k<terrains.length; k++) {
				Terrain t = terrains[k];
				
				int xTest = t.x-playerChunk.getX();
				int zTest = t.z-playerChunk.getY();
				int cTest = chunkLoadSqr;
				
				int dist = xTest*xTest + zTest*zTest;
				
				
				
				if(dist>cTest) {
					t.currentResolution=0;
					continue;
				}
				
				int resolution=TerrainModelData.RESOLUTIONS[TerrainModelData.RESOLUTIONS.length-1];
				
				for(int res : TerrainModelData.RESOLUTIONS) {
					if(dist<=res*res+1) {
						resolution=res;
						break;
					}
				}
				
				if(t.currentResolution!=resolution) {
					int vertexCount = (TerrainModelData.VERTEX_COUNT-1) / resolution + 1;
					int count = vertexCount*vertexCount;
					TerrainModelData data = new TerrainModelData(count, vertexCount, resolution);
					
					Vertex[][] vertexs = new Vertex[vertexCount+2][vertexCount+2];
					
					for(int i = -resolution, z = -1; z<vertexCount+1; i+=resolution, z++) {
						for (int j = -resolution, x = -1; x<vertexCount+1; j+=resolution, x++) {
							
							float xpos = x/((float)vertexCount-1)*WorldChunk.SIZE;
							float zpos = z/((float)vertexCount - 1) *WorldChunk.SIZE;
							
							boolean out = isVertexOutOfTerrain(j, i);
							
							float ypos = getTerrainVertexHeight(t, j, i, out);
							
							Vertex v = new Vertex(xpos, ypos, zpos, out);
							v.texCoords.x = x/((float)vertexCount - 1);
							v.texCoords.y = z/((float)vertexCount - 1);
							
							vertexs[z+1][x+1]=v;
				
						}
					}
					
					ArrayList<Triangle> triangles = new ArrayList<Triangle>();
					
					for(int gz=0;gz<vertexCount+1;gz++){
						for(int gx=0;gx<vertexCount+1;gx++){
							
							Vertex tl = vertexs[gz][gx];
							Vertex tr = vertexs[gz][gx+1];
							Vertex bl = vertexs[gz+1][gx];
							Vertex br = vertexs[gz+1][gx+1];
							
							triangles.add(new Triangle(tl,bl,tr));
							triangles.add(new Triangle(tr,bl,br));
						}
					}
					
					for(int i = 0; i<triangles.size(); i++) {
						Triangle tri = triangles.get(i);
						Vector3f triangleNormal = surfaceNormal(tri.a, tri.b, tri.c);
						tri.addSurfaceNormal(triangleNormal);
					}
					for(Vertex[] vs : vertexs) {
						for(Vertex v : vs) {
							v.normal.normalise();
						}
					}
					
					
					t.setVertices(vertexs);
					
					int vertexPointer = 0;
					for(int x = 0; x<vertexs.length; x++) {
						for(int y = 0; y<vertexs[x].length; y++) {
							
							Vertex v = vertexs[x][y];
							
							if(v.out) {
								continue;
							}
							
							v.index=vertexPointer;
							
							data.vertices[vertexPointer*3]=v.x;
							data.vertices[vertexPointer*3+1]=v.y;
							data.vertices[vertexPointer*3+2]=v.z;
							
							data.textureCoords[vertexPointer*2]=v.texCoords.x;
							data.textureCoords[vertexPointer*2+1]=v.texCoords.y;
							
							data.normals[vertexPointer*3]=v.normal.x;
							data.normals[vertexPointer*3+1]=v.normal.y;
							data.normals[vertexPointer*3+2]=v.normal.z;
							
							worldVariables.styles.environment.generateTexture(vertexs, x, y);
							
							data.textureColours[vertexPointer]=v.textureID;
							
							vertexPointer++;
						}
					}
					
					int pointer = 0;
					for(Triangle tri : triangles) {
						boolean out = tri.a.out || tri.b.out || tri.c.out;
	
						if(out) {
							continue;
						}
						
						data.indices[pointer++] = tri.a.index;
						data.indices[pointer++] = tri.b.index;
						data.indices[pointer++] = tri.c.index;
					}
					
					models[k]=data;
					
				}	
				

				toBeRendered = new HashSet<Terrain>();
				
				for(int i=0; i<terrains.length; i++) {
					
					Terrain ti = terrains[i];
					if(ti==null)continue;
					
					TerrainModelData m = models[i];
					
					if(m!=null) {
						ti.setModel(m);
						ti.currentResolution=m.resolution;
					}
					
					if(ti.currentResolution!=0)
						toBeRendered.add(ti);
				}
				
				
			}
			
			return true;
		}
		
		private void finish() {
			
			storage.renderTerrains(worldVariables.mainLoopVariables,toBeRendered);
		}
		
		private Vector3f surfaceNormal(Vertex a, Vertex b, Vertex c) {
			Vector3f sideAB = Vector3f.sub(b, a, new Vector3f());
			Vector3f sideAC = Vector3f.sub(c, a, new Vector3f());
			
			Vector3f cross = Vector3f.cross(sideAB, sideAC, new Vector3f());
			return cross;
		}
		
		private float getTerrainVertexHeight(Terrain t, int i, int j, boolean out) {
			if(!out) {
				return t.heights[i][j];
			}
			
			
			int tx = t.x;
			int tz = t.z;
			
			if(i<0) {
				tx--;
				i+=TerrainModelData.VERTEX_COUNT-1;
			}else if(i>=TerrainModelData.VERTEX_COUNT) {
				tx++;
				i-=TerrainModelData.VERTEX_COUNT-1;
			}
			
			if(j<0) {
				tz--;
				j+=TerrainModelData.VERTEX_COUNT-1;
			}else if(j>=TerrainModelData.VERTEX_COUNT) {
				tz++;
				j-=TerrainModelData.VERTEX_COUNT-1;
			}
			
			Terrain t2 = storage.getTerrain(tx, tz);
			
			if((!TEST_CHUNK_BORDERS)) {
				if(t2 == null) {
					return 0;
				}else {
					return t2.heights[i][j];
				}
			}else {
				return -1000;
			}
			
		}
		
		private boolean isVertexOutOfTerrain(int i, int j) {
			if(i>=0 && i<TerrainModelData.VERTEX_COUNT && j>=0 && j<TerrainModelData.VERTEX_COUNT) {
				return false;
			}
			return true;
		}
		

	}
	
	
	static class Triangle{
	
		Vertex a, b, c;
		
		private Triangle(Vertex va, Vertex vb, Vertex vc) {
			a=va; b=vb; c=vc;
		}
		
		void addSurfaceNormal(Vector3f normal) {
			Vector3f.add(a.normal, normal, a.normal);
			Vector3f.add(b.normal, normal, b.normal);
			Vector3f.add(c.normal, normal, c.normal);
		}
	}
	
	@SuppressWarnings("serial")
	public static class Vertex extends Vector3f{
		
		public Vector3f normal = new Vector3f();
		public Vector2f texCoords = new Vector2f();
		public int textureID = Environment.TEX_FLAGS.NULL_TEX_FLAG;
		
		boolean out = false;
		int index = -1;
		
		
		private Vertex(float x, float y, float z, boolean index) {
			super(x,y,z);
			this.out=index;
		}
		
	}
	
	
	float getHeightOfVertex(int x, int z) {
		
		
		int tx = Math.floorDiv(x, TerrainModelData.VERTEX_COUNT-1);
		int tz = Math.floorDiv(z, TerrainModelData.VERTEX_COUNT-1);
		
		Terrain terrain = storage.getTerrain(tx, tz);
		if(terrain==null)return 0;
		
		int fx = x % (TerrainModelData.VERTEX_COUNT-1);
		int fz = z % (TerrainModelData.VERTEX_COUNT-1);
		
		if(fx<0)fx=(TerrainModelData.VERTEX_COUNT-1)+fx;
		if(fz<0)fz=(TerrainModelData.VERTEX_COUNT-1)+fz;
		
		
		float height = terrain.heights[fx][fz];
		
		return height;
	}
	
	
	public class TerrainHeightFinder{
		
		public float getHeight(Vector3f pos) {
			return getHeight(Maths.toVector2f(pos));
		}
		
		public float getHeight(Vector2f pos) {
			return getHeight(pos.x, pos.y);
		}
		
		public float getHeight(float x, float z) {
			int tx = (int) Math.floor(x / WorldChunk.SIZE);
			int tz = (int) Math.floor(z / WorldChunk.SIZE);
			
			Terrain terrain = storage.getTerrain(tx, tz);
			if(terrain==null)return 0;
			
			float fx = x % WorldChunk.SIZE;
			float fz = z % WorldChunk.SIZE;
			
			if(fx<0)fx=WorldChunk.SIZE+fx;
			if(fz<0)fz=WorldChunk.SIZE+fz;
			
			return terrain.getHeightOfTerrain(fx, fz);
		}
		
		public Vector3f getNormal(Vector3f pos) {
			return getNormal(Maths.toVector2f(pos));
		}
		public Vector3f getNormal(Vector2f pos) {
			return getNormal(pos.x, pos.y);
		}
		public Vector3f getNormal(float x, float z) {
			int tx = (int) Math.floor(x / WorldChunk.SIZE);
			int tz = (int) Math.floor(z / WorldChunk.SIZE);
			
			Terrain terrain = storage.getTerrain(tx, tz);
			if(terrain==null)return null;
			
			float fx = x % WorldChunk.SIZE;
			float fz = z % WorldChunk.SIZE;
			
			if(fx<0)fx=WorldChunk.SIZE+fx;
			if(fz<0)fz=WorldChunk.SIZE+fz;
			
			return terrain.getNormalOfTerrain(fx, fz);
		}
	}

}
