package game.world.environment;

import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

public class EnvironmentStorage {
	
	private final HashMap<Point, EnvironmentChunk> chunks = new HashMap<Point,EnvironmentChunk>();
	
	private HashSet<EnvironmentChunk> lastRendered = new HashSet<EnvironmentChunk>();
	
	void setChunk(int x, int y, EnvironmentChunk chunk) {
		chunks.put(new Point(x,y),chunk);
	}
	
	public EnvironmentChunk getChunk(Point p) {
		return chunks.get(p);
	}
	
	public EnvironmentChunk getChunk(int x, int y) {
		return getChunk(new Point(x,y));
	}
	
	void removeChunkFromStorage(int x, int y, EnvironmentChunk chunk) {
		removeChunkFromStorage(new Point(x,y), chunk);
	}
	
	void removeChunkFromStorage(Point p, EnvironmentChunk chunk) {
		chunks.remove(p, chunk);
		chunk.delete();
	}
	
	
	void renderChunks(HashSet<EnvironmentChunk> chunks, Point playerChunk) {
		for(EnvironmentChunk t : chunks) {
			
			float distSquare = new Vector2f(t.x-playerChunk.getX(), t.z-playerChunk.getY()).lengthSquared();
			
			if(lastRendered.contains(t)) {
				t.setNewChunkDistance(distSquare);
				lastRendered.remove(t);
				continue;
			}
			
			t.add(distSquare);
		}
		
		for(EnvironmentChunk t : lastRendered) {
			
			t.remove();
			
			removeChunkFromStorage(t.x, t.z, t);
		}
		
		lastRendered=chunks;
	}
	
	


}
