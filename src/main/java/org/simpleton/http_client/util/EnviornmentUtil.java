/**
 * 
 */
package org.simpleton.http_client.util;

/**
 * 
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * CreatedAt : Apr 30, 2023 12:39:22 AM
 *
 */
public class EnviornmentUtil {
	
	public static int getEnvVar(String key, int defaultValue) {
		String vaule = System.getenv(key);
		if (NumberUtil.isNumeric(vaule)) {  
			return Integer.parseInt(vaule); 
		}
		return defaultValue;
	}

}
