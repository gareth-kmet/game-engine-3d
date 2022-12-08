package game.world.environment;

import java.util.Random;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.objects.entities.MoveableGameEntity;
import game.objects.informations.Physics.Collidable.MovingCollidable;
import game.world.WorldChunk;
import game.world.WorldLoopManager;
import game.world.WorldVariables;

public class EnvironmentManager implements WorldLoopManager {
	
	static final int UPDATE_ENTITY_DISTANCE = 3;
	
	private EnvironmentGenerator generator;
	private final WorldVariables worldVariables;
	
	public EnvironmentManager(WorldVariables worldVariables) {
		this.worldVariables = worldVariables;
	}
	
	@Override
	public void create(Random random) {
		generator = new EnvironmentGenerator(worldVariables);
		
		updateNewChunk();
		update();

	}
	
	public void updateNewChunk() {
		generator.generate();
	}

	@Override
	public void update() {
		generator.updateGeneration();
		updateChunks();
		

	}
	
	private void updateChunks() {
		Point currentPlayerChunk = worldVariables.playerVariables.currentChunk;
		for(int x=-2; x<=2; x++) for(int y=-2;y<=2;y++){
			int cx=currentPlayerChunk.getX()+x;
			int cy=currentPlayerChunk.getY()+y;
			EnvironmentChunk c = generator.storage.getChunk(cx, cy);
			if(c!=null)c.update();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	public static void updateChunkOfMovableEntity(Vector3f originPos, Vector3f endPos, EnvironmentStorage storage, MoveableGameEntity entity) {
		if(originPos.equals(endPos)) {
			return;
		}
		Point startChunk = WorldChunk.getChunk(originPos);
		Point endChunk = WorldChunk.getChunk(endPos);
		EnvironmentChunk originChunk = storage.getChunk(startChunk);
		if(!startChunk.equals(endChunk)) {
			EnvironmentChunk destinationChunk = storage.getChunk(endChunk);
			if(entity instanceof MovingCollidable m) {// dont need to test for static collidable as moveable game entity cannot be static
				originChunk.getCollidables().remove(m);
				destinationChunk.getCollidables().put(m);
			}
			EnvironmentChunk.transferGameObject(originChunk, destinationChunk, entity);
		}else {
			if(entity instanceof MovingCollidable m) {
				originChunk.getCollidables().fix(m,endPos);
			}
		}
		
		
	}

	

}
