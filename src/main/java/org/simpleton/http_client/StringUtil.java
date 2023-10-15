/**
 * 
 */
package org.simpleton.http_client;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tariqul
 *
 */
@Slf4j
public class StringUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public static boolean isEmpty(String targetString) {
		return null == targetString || targetString.trim().isEmpty();
	}
	
}
