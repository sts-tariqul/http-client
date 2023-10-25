/**
 * 
 */
package org.simpleton.http_client.util;

/**
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since : Apr 30, 2023 12:39:22 AM
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
