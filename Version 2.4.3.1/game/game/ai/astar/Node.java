package game.ai.astar;
import java.util.EnumSet;
import java.util.HashSet;

import org.lwjgl.util.vector.Vector3f;

import game.ai.astar.AStar.AStarEffect;

public class Node implements HeapItem<Node>{
	
	public final int x, y;
	public final Vector3f worldPos;
	public boolean walkable = true;
	private int walkableValue = 0;
	protected int heapIndex=0;
	public final EnumSet<NeighbourDirections> moveableDirections = EnumSet.allOf(NeighbourDirections.class);
	
	public int gCost=0, hCost=0;
	public int fCost() {return gCost+hCost;}
	public Node parent;
	public int penalty = 0;
	
	public boolean visited = false;
	

	public Node(Vector3f worldPos, int x, int y) {
		this.worldPos = worldPos;
		this.x = x;
		this.y = y;
	}
	
	public Node(Node node, int x, int y) {
		this(node.worldPos, x, y);
		this.penalty=node.penalty;
		this.walkable=node.walkable;
	}
	
	
	public void resetData() {
		gCost=0; hCost=0;
		parent=null;
		visited=false;
		heapIndex=0;
	}
	
	public enum NeighbourDirections{
		N(0,1),NE(1,1),E(1,0),SE(1,-1),S(0,-1),SW(-1,-1),W(-1,0),NW(-1,1);
		private final int x,y;
		private NeighbourDirections(int x, int y) {
			this.x=x;this.y=y;
		}
	}
	public HashSet<Node> getNeighbours(Node[][] grid) {
		HashSet<Node> neighbours = new HashSet<Node>();
		for(NeighbourDirections direction : moveableDirections) {
			int x = this.x+direction.x, y = this.y+direction.y;
			if((0<=x) && (x<grid.length) && (0<=y) && (y<grid[x].length)) {
//				Can be null for some reason TODO
				if(grid[x][y]!=null)neighbours.add(grid[x][y]);
			}
		}
		return neighbours;
	}
	
	@Override
	public String toString() {
		return String.format("Node[x:%d, y:%d, walkable:%b, gCost:%d, hCost:%d, fCost:%d]", x,y,walkable,gCost,hCost,fCost());
//		return ""+walkable;
	}

	@Override
	public int compareTo(Node o) {
		
		int compare = ((Integer)fCost()).compareTo(o.fCost());
		if(compare==0) {
			compare = ((Integer)hCost).compareTo(o.hCost);
		}
		return -compare;
		
		
	}

	@Override
	public int getHeapIndex() {
		return heapIndex;
	}

	@Override
	public void setHeapIndex(int i) {
		heapIndex=i;
	}
	
	public void addNodeEffect(AStarEffect e) {
		penalty+=e.penalty();
		if(!e.walkable()) {
			walkableValue+=1;
			walkable=false;
		}
		
	}
	
	public void removeNodeEffect(AStarEffect e) {
		penalty-=e.penalty();
		if(!e.walkable()) {
			walkableValue-=1;
			walkable=walkableValue<=0;
		}
	}
	
	
	
	

}
