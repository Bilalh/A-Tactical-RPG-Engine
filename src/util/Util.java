package util;

/**
 * @author Bilal Hussain
 */
public class Util {

	public static <E> String array2d(E[][] arr,int startX, int endX, int startY, int endY, boolean newline){
		StringBuffer b = new StringBuffer(100);
		if (newline) b.append("\n");
		for (int i = startX; i < endX; i++) {
			b.append("[");
			for (int j = startX; j <endY; j++) {
				b.append(arr[i][j]);
				if (j != endY-1) b.append(", ");
			}
			b.append("]");
			if (i != endX-1) b.append("\n");
		}
		return b.toString();
	}

	public static String numberedArray2d(int[][] arr){
		StringBuffer b = new StringBuffer(arr.length * arr[0].length * 10);
		for (int i = 0; i < arr.length; i++) {
			b.append("[");
			for (int j = 0; j < arr[i].length; j++) {
				b.append(String.format("(%s,%s) %2s", i,j, arr[i][j]));
				if (j != arr[i].length){
					b.append(", ");
				}
			}
			b.append("]\n");
		}
		return b.toString();
	}
	
}
