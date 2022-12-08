package game.ai.astar;

public interface HeapItem<T> extends Comparable<T>{
	public int getHeapIndex();
	public void setHeapIndex(int i);
}
