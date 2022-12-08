package game.physics.collisions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import game.objects.informations.Physics.Collidable;
import game.objects.informations.Physics.Collidable.MovingCollidable;
import game.objects.informations.Physics.Collidable.StaticCollidable;
import game.world.WorldChunk;

public record PhysicsCollidableGrid(HashSet<Collidable>[][] chunkGrid, HashMap<Point, HashSet<Collidable>> outsideOfChunkGrid){
	@SuppressWarnings("unchecked")
	public PhysicsCollidableGrid() {
		this(new HashSet[WorldChunk.SIZE][WorldChunk.SIZE], new HashMap<Point, HashSet<Collidable>>());
	}
	
	public HashSet<? extends Collidable> get(Point p) {
		HashSet<? extends Collidable> collides = null;
		
		if(0<=p.getX() && p.getX()<chunkGrid.length && 0<=p.getY() && p.getY()<chunkGrid[p.getX()].length) {
			final int x = p.getX(), z = p.getY();
			collides = chunkGrid[x][z];
		}else {
			collides = outsideOfChunkGrid.get(p);
		}
		
		return collides!=null?collides:new HashSet<Collidable>(0);
	}
	public void put(Collidable c) {
		if(c instanceof StaticCollidable s) {
			put(s);
		}else if(c instanceof MovingCollidable m) {
			put(m);
		}
	}
	public void remove(Collidable c) {
		if(c instanceof StaticCollidable s) {
			remove(s);
		}else if(c instanceof MovingCollidable m) {
			remove(m);
		}
	}
	public void put(StaticCollidable c) {
		HashSet<Point> collidablePoints = c.getAffectingNodes();
		for(Point p:collidablePoints) {
			put(p,c);
		}
	}
	private void put(Point p, Collidable c){
		if(0<=p.getX() && p.getX()<chunkGrid.length && 0<=p.getY() && p.getY()<chunkGrid[p.getX()].length) {
			final int x = p.getX(), z = p.getY();
			if(chunkGrid[x][z]==null) {chunkGrid[x][z] = new HashSet<Collidable>();};
			chunkGrid[x][z].add(c);
		}else {
			outsideOfChunkGrid.compute(p, (key, set) -> set==null?new HashSet<Collidable>():set).add(c);
		}
	}
	public void remove(StaticCollidable c) {
		HashSet<Point> collidablePoints = c.getAffectingNodes();
		for(Point p : collidablePoints) {
			remove(p,c);
		}
	}
	private void remove(Point p, Collidable c) {
		if(0<=p.getX() && p.getX()<chunkGrid.length && 0<=p.getY() && p.getY()<chunkGrid[p.getX()].length) {
			final int x = p.getX(), z = p.getY();
			if(chunkGrid[x][z]!=null) {
				chunkGrid[x][z].remove(c);
				if(chunkGrid[x][z].isEmpty()) {
					chunkGrid[x][z]=null;
				}
			}
			
		}else {
			HashSet<Collidable> set = outsideOfChunkGrid.get(p);
			if(set!=null) {
				set.remove(c);
				if(set.isEmpty()) {
					outsideOfChunkGrid.remove(p, set);
				}
			}
		}
	}
	
	public void put(MovingCollidable c) {
		this.put(c, c.getPosition());
	}
	public void put(MovingCollidable c, Vector3f toWorldPosition) {
		float radius = c.getAffectingRadius();
		Vector3f localChunkPos = WorldChunk.getChunkLocalPos(toWorldPosition);
		HashSet<Point> points = new HashSet<Point>();
		forEachPossiblePositionInRadius(localChunkPos, radius, 
			(x,z)->{
				Point p = new Point(x,z);
				points.add(p);
				put(p,c);
			}
		);
		c.setCurrentPoints(points);
	}
	public void remove(MovingCollidable c) {
		c.getCurrentPoints().forEach(p->remove(p,c));
		c.setCurrentPoints(new HashSet<Point>(0));
	}
	public void fix(MovingCollidable m, Vector3f destinationWorldPos) {
		float radius = m.getAffectingRadius();
		
		Vector3f localDestinationPos = WorldChunk.getChunkLocalPos(destinationWorldPos);
		HashSet<Point> originalPoints = m.getCurrentPoints();
		HashSet<Point> destinationPoints = getEachPossiblePositionInRadius(localDestinationPos, radius);
		originalPoints.forEach(p->{
			if(!destinationPoints.contains(p)) {
				remove(p,m);
			}
		});
		destinationPoints.forEach(p->{
			if(!originalPoints.contains(p)) {
				put(p,m);
			}
		});
		m.setCurrentPoints(destinationPoints);
	}
	
	public static void forEachPossiblePositionInRadius(Vector3f chunkLocalPos, float radius, BiConsumer<Integer, Integer> consumer){
		Objects.requireNonNull(consumer);
		final int leftX = (int)Math.floor(chunkLocalPos.x-radius), rightX = (int)Math.ceil(chunkLocalPos.x+radius);
		final int leftZ = (int)Math.floor(chunkLocalPos.z-radius), rightZ = (int)Math.ceil(chunkLocalPos.z+radius);
		for(int dx = leftX; dx<=rightX; dx++) for(int dz = leftZ; dz<=rightZ; dz++) { 
			consumer.accept(dx, dz);
		}
	}
	public static HashSet<Point> getEachPossiblePositionInRadius(Vector3f chunkLocalPos, float radius) {
		HashSet<Point> ps = new HashSet<Point>();
		forEachPossiblePositionInRadius(chunkLocalPos, radius, (x,z)->ps.add(new Point(x,z)));
		return ps;
	}

	
}

