package toolbox;

public class ToolBox {
	
	public static final <T> void reverseArray(T[] array){
		for(int i = 0; i < array.length / 2; i++)
		{
		    T temp = array[i];
		    array[i] = array[array.length - i - 1];
		    array[array.length - i - 1] = temp;
		}
	}

}
