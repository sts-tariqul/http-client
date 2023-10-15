/**
 * 
 */
package org.simpleton.http_client;

/**
 * @author tariqul
 *
 */
public class NumberUtil {
	
	private static final String NUMBER_REGEX = "\\d+";
	
	public static boolean isNumeric(String numericString) {
		return null != numericString 
				&& !numericString.trim().isEmpty() 
				&& numericString.matches(NUMBER_REGEX);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
