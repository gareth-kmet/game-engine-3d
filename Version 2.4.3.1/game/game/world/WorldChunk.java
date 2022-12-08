package game.world;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.ai.astar.AStarGrid;

public class WorldChunk {

	public static final int SIZE = 1000; // must be >=800 (otherwise the renderer freaks out, also must be multiple of WaterTile.TILE_SIZE*2;
	
	public static final Point getChunk(Vector3f pos) {
		int tx = (int) Math.floor (pos.x / WorldChunk.SIZE);
		int tz = (int) Math.floor(pos.z / WorldChunk.SIZE);
		return new Point(tx,tz);
	}
	
	public static final Vector3f getChunkLocalPos(Vector3f pos) {
		float fx = pos.x % WorldChunk.SIZE;
		float fz = pos.z % WorldChunk.SIZE;
		
		if(fx<0)fx=WorldChunk.SIZE+fx;
		if(fz<0)fz=WorldChunk.SIZE+fz;
		
		return new Vector3f(fx,pos.y,fz);
	}
	
	public static AStarGrid newAStarGrid(int x, int z) {
		return new AStarGrid(SIZE).fillWithEmptyNodes(x*SIZE, z*SIZE, 1, 1);
	}
	

}
