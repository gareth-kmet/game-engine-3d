package toolbox;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

public class PoissonDiscSampling {
	
	private static Random random = new Random();
	private static final float SQRT_2 = (float)Math.sqrt(2);
	
	public static ArrayList<Vector2f> generatePoints(float radius, Vector2f sampleRegionSize, int numSamplesBeforeRejection, int seed, Vector2f offset){
		
		random.setSeed(seed);
		
		float cellSize = radius/SQRT_2;
		
		int[][] grid = new int[(int) Math.ceil(sampleRegionSize.x/cellSize)][(int) Math.ceil(sampleRegionSize.y/cellSize)];
		
		ArrayList<Vector2f> points = new ArrayList<Vector2f>();
		ArrayList<Vector2f> spawnPoints = new ArrayList<Vector2f>();
		
		spawnPoints.add(new Vector2f(sampleRegionSize.x/2, sampleRegionSize.y/2));
		while(spawnPoints.size() > 0) {
			int spawnIndex = random.nextInt(spawnPoints.size());
			Vector2f spawnCentre = spawnPoints.get(spawnIndex);
			boolean candidateAccepted = false;
			
			for(int i=0; i<numSamplesBeforeRejection; i++) {
				float angle = (float) (random.nextFloat() * Math.PI * 2);
				Vector2f dir = new Vector2f((float)Math.sin(angle), (float) Math.cos(angle));
				float rand = random.nextFloat() * (2*radius-radius)+radius;
				Vector2f candidate = new Vector2f(
						spawnCentre.x + dir.x * rand,
						spawnCentre.y + dir.y * rand
				);
				
				if(isValid(candidate, sampleRegionSize, cellSize, radius, points, grid)) {
					points.add(candidate);
					spawnPoints.add(candidate);
					grid[(int)(candidate.x/cellSize)][(int)(candidate.y/cellSize)] = points.size();
					candidateAccepted=true;
					break;
				}
			}
			if(!candidateAccepted) {
				spawnPoints.remove(spawnIndex);
			}
			
		}
		
		return points;
		
	}
	
	private static boolean isValid(Vector2f candidate, Vector2f sampleRegionSize, float cellSize, float radius, ArrayList<Vector2f> points, int[][] grid) {
		if(candidate.x >= 0 && candidate.x < sampleRegionSize.x && candidate.y >= 0 && candidate.y < sampleRegionSize.y) {
			int cellX = (int)(candidate.x/cellSize);
			int cellY = (int)(candidate.y/cellSize);
			
			int searchX = Math.max(0, cellX-2);
			int searchEndX = Math.min(cellX+2, grid.length-1);
			int searchY = Math.max(0, cellY-2);
			int searchEndY = Math.min(cellY+2, grid[0].length-1);
			
			for(int x = searchX ; x<= searchEndX; x++) {
				for (int y = searchY; y <= searchEndY; y++) {
					int pointIndex = grid[x][y]-1;
					if(pointIndex != -1) {
						float dist = (Vector2f.sub(candidate, points.get(pointIndex), new Vector2f())).lengthSquared();
						if(dist < radius*radius) {
							return false;
						}
					}
					
				}
			}
			
			
			float closeX = candidate.x - radius;
			float farX = candidate.x + radius;
			float closeY = candidate.y - radius;
			float farY = candidate.y + radius;
			
			if(closeX < 0 || closeY < 0 || farX >= sampleRegionSize.x || farY >= sampleRegionSize.y) {
				return false;
			}
			
			
			return true;
		}
		return false;
	}
	

}
