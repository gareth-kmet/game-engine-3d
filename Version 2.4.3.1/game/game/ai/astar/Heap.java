package game.ai.astar;
import java.util.Arrays;


public class Heap<T extends HeapItem<T>>{
	
	private T[] items;
	private int currentItemCount;
	private boolean reset = false;
	
	public Heap() {
		
	}
	
	public Heap(int maxHeapSize, T[] defaultArray) {
		resetTo(maxHeapSize, defaultArray);
		
	}
	
	public void resetTo(int maxHeapSize, T[] defaultArray) {
		items = Arrays.copyOf(defaultArray, maxHeapSize);
		currentItemCount=0;
		reset=true;
		
	}
	
	public void add(T item) {
		item.setHeapIndex(currentItemCount);
		items[currentItemCount] = item;
		sortUp(item);
		currentItemCount++;
	}
	
	public T removeFirst() {
		T firstItem = items[0];
		currentItemCount--;items[0]=items[currentItemCount];
		items[0].setHeapIndex(0);
		sortDown(items[0]);
		return firstItem;
	}
	
	public void updateItem(T item) {
		sortUp(item);
//		sortDown(item);
	}
	
	public int getCount(){
		return currentItemCount;
	}
	
	public boolean contains(T item) {
		return item.equals(items[item.getHeapIndex()]);
	}
	
	private void sortDown(T item) {
		while(true) {
			int childIndexLeft = item.getHeapIndex()*2+1;
			int childIndexRight = item.getHeapIndex()*2+2;
			int swapIndex=0;
			
			if(childIndexLeft < currentItemCount) {
				swapIndex = childIndexLeft;
				
				if(childIndexRight < currentItemCount) {
					if(items[childIndexLeft].compareTo(items[childIndexRight])<0) {
						swapIndex=childIndexRight;
					}
				}
				
				if(item.compareTo(items[swapIndex])<0) {
					swap(item, items[swapIndex]);
				}else {
					return;
				}
			}else {
				return;
			}
		}
	}
	
	private void sortUp(T item) {
		int parentIndex = (item.getHeapIndex()-1)/2;
		
		while(true) {
			T parentItem = items[parentIndex];
			if(item.compareTo(parentItem)>0) {
				swap(item, parentItem);
			}else {
				break;
			}
			
			parentIndex = (item.getHeapIndex()-1)/2;
		}
	}
	
	private void swap(T itemA, T itemB) {
		items[itemA.getHeapIndex()]=itemB;
		items[itemB.getHeapIndex()]=itemA;
		int temp = itemA.getHeapIndex();
		itemA.setHeapIndex(itemB.getHeapIndex());
		itemB.setHeapIndex(temp);
	}

	public boolean isReset() {
		return reset;
	}

}
