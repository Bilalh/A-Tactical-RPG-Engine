package config;

/**
 * @author Bilal Hussain
 */
public class Args {

	private Args(){}
	
	private static boolean isInRange(int amount, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("min:" + min + ">" + "max:" + max);
		}
		return amount >= min && amount < max;
	}

	public static void validateRange(int amount, int min, int max) {
		validateRange(amount, min, max,"");
	}
	public static void validateRange(int amount, int min, int max, String err) {
		if (!isInRange(amount, min, max)) {
			illegalArgumentExceptionf("%s not in (%s,%s)",amount, min,max);
		}
	}

	public static void nullCheck(Object ...arr){
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null){
				illegalArgumentExceptionf("argument %s null", i);
			}
		}
	}
	
	public static void assetNonNull(Object ...arr){
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null){
				assert(arr[i] != null);
			}
		}
	}	
	
	public static void illegalArgumentExceptionf(String fmt, Object ...value){
		throw new IllegalArgumentException(String.format(fmt, value));
	}
	
	
	
}
