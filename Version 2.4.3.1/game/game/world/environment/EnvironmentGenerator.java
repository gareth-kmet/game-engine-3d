package game.world.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.objects.GameObject;
import game.world.WorldChunk;
import game.world.WorldLoaderThread;
import game.world.WorldVariables;
import game.world.environment.styles.Environment;

public class EnvironmentGenerator {
	
	private final WorldVariables worldVariables;
	final EnvironmentStorage storage;
	
	private EnvironmentGenerationRequest currentEnvironmentRequest = null;
	private EnvironmentGenerationRequest nextEnvironmentRequest = null;

	

	private static final Random RANDOM = new Random();
	
	EnvironmentGenerator(WorldVariables worldVariables) {
		this.worldVariables=worldVariables;
		storage = new EnvironmentStorage();
		worldVariables.environmentVariables.storage=storage;
	}
	
	private class EnvironmentGenerationRequest extends WorldLoaderThread.Request{
		
		private Point playerChunk;
		private HashSet<EnvironmentChunk> chunksToBeRendered;
		
		
		private EnvironmentGenerationRequest(Point playerChunk) {
			this.playerChunk = playerChunk;
			chunksToBeRendered = new HashSet<EnvironmentChunk>();
		}
		private int chunk_load = 1;
		@Override
		protected boolean run() {
			for(int x = playerChunk.getX()-chunk_load; x<=playerChunk.getX()+chunk_load; x++) {
				for(int y= playerChunk.getY()-chunk_load; y<=playerChunk.getY()+chunk_load; y++) {
					
					EnvironmentChunk chunkFromStorage = storage.getChunk(x, y);
					EnvironmentChunk chunkToBeRendered;
					if(chunkFromStorage!=null) {
						chunkToBeRendered = chunkFromStorage;
					}else {
						Point chunkPoint = new Point(x,y);
						ArrayList<Vector2f> larges = 
									EnvironmentPoissonDiscSampling.generatePoints(40, 40, worldVariables.seed, chunkPoint);
						ArrayList<Vector2f> mediums = 
								EnvironmentPoissonDiscSampling.generatePoints(20, 20, worldVariables.seed, chunkPoint);
						ArrayList<Vector2f> smalls = 
								EnvironmentPoissonDiscSampling.generatePoints(10, 20, worldVariables.seed, chunkPoint);
						ArrayList<Vector2f> others = new ArrayList<Vector2f>(0);
						EnvironmentChunk chunk = dealWithEntityGeneration(worldVariables, smalls, mediums, larges, others, chunkPoint);
						storage.setChunk(x, y, chunk);
						chunkToBeRendered=chunk;
					}
					chunksToBeRendered.add(chunkToBeRendered);
					chunkToBeRendered.loadModelsToEntities();
				}
			}
			
			return true;
		}
		
		private void finish() {
			storage.renderChunks(chunksToBeRendered, playerChunk);
		}
		
	}
	

	public static final byte SMALL=0,MEDIUM=1,LARGE=2,OTHER=3;
	
	
	private static EnvironmentChunk dealWithEntityGeneration(WorldVariables vars, ArrayList<Vector2f> small, ArrayList<Vector2f> medium, ArrayList<Vector2f> large, ArrayList<Vector2f> other, Point chunkLoc){
		
		HashSet<GameObject> largeEs = getEntity(vars, large, chunkLoc,LARGE);
		HashSet<GameObject> mediumEs = getEntity(vars, medium, chunkLoc, MEDIUM);
		HashSet<GameObject> smallEs = getEntity(vars, small, chunkLoc, SMALL);
		HashSet<GameObject> otherEs = getEntity(vars, other, chunkLoc,OTHER);
		
		EnvironmentChunk e = new EnvironmentChunk(vars, chunkLoc.getX(), chunkLoc.getY(), otherEs, largeEs, mediumEs, smallEs);
		return e;
	}
	
	private static HashSet<GameObject> getEntity(WorldVariables vars, ArrayList<Vector2f> points, Point chunkLoc, byte type){
		
		Environment environment = vars.styles.environment;
		
		setRandom(chunkLoc, vars.seed, type);
		
		HashSet<GameObject> ents = new HashSet<GameObject>();
		for(Vector2f point : points) {
			Vector3f pos = new Vector3f(
					point.x + chunkLoc.getX()*WorldChunk.SIZE, 
					0, 
					point.y + chunkLoc.getY()*WorldChunk.SIZE
			);
			
			pos.y = vars.terrainVariables.heights.getHeight(pos);
			GameObject e;
			switch(type) {
				case SMALL:
					e=environment.dealWithSmallGenerationPoint(pos, RANDOM, vars); break;
				case MEDIUM:
					e=environment.dealWithMediumGenerationPoint(pos, RANDOM, vars); break;
				case LARGE:
					e=environment.dealWithLargeGenerationPoint(pos, RANDOM, vars); break;
				case OTHER: default:
					e=environment.dealWithOtherGenerationPoint(pos, RANDOM, vars); break;
			}
			if(e!=null) {
				ents.add(e);
			}
				
		}
		return ents;
	}
	
	
	private static void setRandom(Point chunkLoc, long seed, byte type) {
		RANDOM.setSeed(seed*416+chunkLoc.getX()*5184+chunkLoc.getY()*845184+type*8451841581l);
	}
	
	void generate() {
		if(nextEnvironmentRequest != null) {
			nextEnvironmentRequest.removeFromThread();
		}
	
		nextEnvironmentRequest = new EnvironmentGenerationRequest(worldVariables.playerVariables.currentChunk);
		
		
	}
	
	void updateGeneration() {
		
		if(currentEnvironmentRequest!=null) {
			if(currentEnvironmentRequest.isComplete()) {
				currentEnvironmentRequest.finish();
				currentEnvironmentRequest=null;
			}
		}
		
		if(currentEnvironmentRequest==null && nextEnvironmentRequest != null) {
			currentEnvironmentRequest = nextEnvironmentRequest;
			nextEnvironmentRequest=null;
			worldVariables.worldThreadMethods.addRequestToThread(currentEnvironmentRequest);
			
		}
		
	}
}
