package game.world.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

import game.world.WorldChunk;

public class EnvironmentPoissonDiscSampling {
	
	private static Random random = new Random();
	private static final float SQRT_2 = (float) Math.sqrt(2), PI_2 = (float) (Math.PI*2f);
	private static final int GRID_EXTENSION_DIST = 2;
	
	public static ArrayList<Vector2f> generatePoints(float radius, int numSamplesBeforeRejection, int seed, Point chunkPoint){
		
		float cellSize = radius/SQRT_2;
		int gridSize = (int) Math.ceil(WorldChunk.SIZE/cellSize) + GRID_EXTENSION_DIST*2;
		int[][] grid = new int[gridSize][gridSize];
		
		ArrayList<Vector2f> allPoints = new ArrayList<Vector2f>();
		HashSet<Vector2f> inPoints = new HashSet<Vector2f>();
		
		borderGenerate(radius, grid, allPoints, inPoints, seed, chunkPoint, cellSize);
		centreGenerate(radius, grid, allPoints, inPoints, seed, chunkPoint, cellSize, numSamplesBeforeRejection);
		
		return new ArrayList<Vector2f>(inPoints);
	}
	
	private static void setRandom(Point chunk, int seed, int extra) {
		long randomSeed = seed*164682l + chunk.getX()*6815l - chunk.getY()*154l + extra;
		random.setSeed(randomSeed);
	}
	
	private static void centreGenerate(float radius, int[][] grid, ArrayList<Vector2f> allPoints, HashSet<Vector2f> inPoints, int seed, Point chunkPoint, float cellSize, int numSamplesBeforeRejection) {
		setRandom(chunkPoint, seed, 0);
		
		ArrayList<Vector2f> spawnPoints = new ArrayList<Vector2f>(inPoints);
		
		while(spawnPoints.size()>0) {
			int spawnIndex = random.nextInt(spawnPoints.size());
			Vector2f spawnCentre = spawnPoints.get(spawnIndex);
			boolean candidateAccepted = false;
			
			for(int i=0; i<numSamplesBeforeRejection; i++) {
				float angle = random.nextFloat()*PI_2;
				Vector2f dir = new Vector2f((float)Math.sin(angle), (float)Math.cos(angle));
				float rand = random.nextFloat() * (2*radius-radius)+radius;  //(max-min)+min
				Vector2f candidate = new Vector2f(
						spawnCentre.x + dir.x * rand,
						spawnCentre.y + dir.y * rand
				);
				
				if(isValid(candidate, WorldChunk.SIZE, cellSize, radius, allPoints, grid)) {
					allPoints.add(candidate);
					inPoints.add(candidate);
					spawnPoints.add(candidate);
					grid[(int)(candidate.x/cellSize)+GRID_EXTENSION_DIST][(int)(candidate.y/cellSize)+GRID_EXTENSION_DIST] = allPoints.size();
					candidateAccepted=true;
					break;
				}
				
			}
			
			if(!candidateAccepted) {
				spawnPoints.remove(spawnIndex);
			}
		}
		
	}
	
	private static boolean overlap(Vector2f candidate, int index, ArrayList<? extends Vector2f> allPoints, float radius) {
		if(index==-1) return false;
		return overlap(candidate, allPoints.get(index), radius);
	}
	
	private static boolean overlap(Vector2f candidate, Vector2f test, float radius) {
		float dist = (Vector2f.sub(candidate, test, new Vector2f())).lengthSquared();
		return dist < radius*radius;
		
	}
	
	
	private static boolean isValid(Vector2f candidate, int sampleRegionSize, float cellSize, float radius, ArrayList<? extends Vector2f> points, int[][] grid) {
		if(candidate.x >= 0 && candidate.x < sampleRegionSize && candidate.y >= 0 && candidate.y < sampleRegionSize) {
			int cellX = (int)(candidate.x/cellSize)+GRID_EXTENSION_DIST;
			int cellY = (int)(candidate.y/cellSize)+GRID_EXTENSION_DIST;
			
			int searchX = Math.max(0, cellX-2);
			int searchEndX = Math.min(cellX+2, grid.length-1);
			int searchY = Math.max(0, cellY-2);
			int searchEndY = Math.min(cellY+2, grid[0].length-1);
			
			for(int x = searchX ; x<= searchEndX; x++) {
				for (int y = searchY; y <= searchEndY; y++) {
					int pointIndex = grid[x][y]-1;
					if(pointIndex != -1) {
						if(overlap(candidate, pointIndex, points, radius)) {
							return false;
						}
					}
					
				}
			}

		
			float closeX = candidate.x - radius/2;
			float farX = candidate.x + radius/2;
			float closeY = candidate.y - radius/2;
			float farY = candidate.y + radius/2;
			
			if(closeX < 0 || closeY < 0 || farX >= sampleRegionSize || farY >= sampleRegionSize) {
				return false;
			}
			
			
			return true;
		}
		return false;
	}
	
	
	private static void borderGenerate(float radius, int[][] grid, ArrayList<Vector2f> allPoints, HashSet<Vector2f> inPoints, int seed, Point chunkPoint, float cellSize) {
		//y max border
		setRandom(new Point(chunkPoint.getX(),chunkPoint.getY()+1), seed, 2);
		borderGenerate(false, false, radius, grid, allPoints, inPoints, cellSize);
		//x max border
		setRandom(new Point(chunkPoint.getX()+1,chunkPoint.getY()), seed, 1);
		borderGenerate(false, true, radius, grid, allPoints, inPoints, cellSize);
		//y min border
		setRandom(chunkPoint, seed, 2);
		borderGenerate(true, false, radius, grid, allPoints, inPoints, cellSize);
		//x min border
		setRandom(chunkPoint, seed, 1);
		borderGenerate(true, true, radius, grid, allPoints, inPoints, cellSize);
	}
	
	private static boolean borderOverlap(Vector2f candidate, HashSet<Vector2f> borderPoints, float radius) {
		for(Vector2f test : borderPoints) {
			if(overlap(candidate, test, radius)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void borderGenerate(boolean b0, boolean isX, float radius, int[][] grid, ArrayList<Vector2f> allPoints, HashSet<Vector2f> inPoints, float cellSize) {
		Vector2f top;
		
		HashSet<Vector2f> borderPoints = new HashSet<Vector2f>();
		
		if(isX) {
			top = new Vector2f(b0?0:WorldChunk.SIZE,0);
		}else {
			top= new Vector2f(0,b0?0:WorldChunk.SIZE);
		}
		
		allPoints.add(top);
		if(b0&&isX)inPoints.add(top);
		grid[(int)(top.x/cellSize)+GRID_EXTENSION_DIST][(int)(top.y/cellSize)+GRID_EXTENSION_DIST] = allPoints.size();
		
		float totalDistOnAxe = 0;
		int attempts = 0;
		borderPoints.add(top);
		
		while(true) {
			if(attempts > 10) {
				totalDistOnAxe+=random.nextFloat()*radius;
				attempts=0;
				continue;
			}
			
			float randDistAwayFromLastPoint = random.nextFloat()*radius+radius;
			float randDistAwayFromAxe = random.nextFloat() * randDistAwayFromLastPoint/SQRT_2;
			float dist = (float) Math.sqrt(randDistAwayFromLastPoint*randDistAwayFromLastPoint-randDistAwayFromAxe*randDistAwayFromAxe);
			float newTotalDist = totalDistOnAxe+dist;
			
			if(newTotalDist>=(WorldChunk.SIZE-radius)) {
				break;
			}
			
			int randNeg = random.nextInt(2)*2-1;
			float distFromAxe = randNeg * randDistAwayFromAxe;
			distFromAxe += b0?0:WorldChunk.SIZE;
			
			Vector2f point;
			if(isX) {
				point = new Vector2f(distFromAxe, newTotalDist);
			}else {
				point = new Vector2f(newTotalDist, distFromAxe);
			}
			
			if(borderOverlap(point, borderPoints, radius)) {
				attempts++;
				continue;
			}else {
				attempts = 0;
				borderPoints.add(point);
			}
			
			
			totalDistOnAxe=newTotalDist;
			
			if((randNeg>0 && b0) || (randNeg<0 && !b0)) {
				inPoints.add(point);
			}
			
			allPoints.add(point);
			grid[(int)(point.x/cellSize)+GRID_EXTENSION_DIST][(int)(point.y/cellSize)+GRID_EXTENSION_DIST] = allPoints.size();
			
		}
		/*
		float randA = 0;
		while((randA+=random.nextFloat()*radius + radius)<(WorldVariables.Finals.CHUNK_SIZE-radius)) {
			float randB = random.nextFloat()*2*radius/SQRT_2;
			if(randB==0)randB=1;
			randB *= random.nextInt(2)*2-1;
			randB += b0?0:WorldVariables.Finals.CHUNK_SIZE;
			
			Vector2f point;
			if(isX) {
				point = new Vector2f(randB, randA);
			}else {
				point = new Vector2f(randA, randB);
			}
			
			
			if((randB<WorldVariables.Finals.CHUNK_SIZE&&!b0)||(randB>0&&b0)) {
				inPoints.add(point);
			}
			
			allPoints.add(point);
			grid[(int)(point.x/cellSize)+2][(int)(point.y/cellSize)+2] = allPoints.size();		
		}
		*/
	}
}
