/**
 * 
 */
package org.simpleton.http_client;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author tariqul :: Mar 9, 2023 :: 1:58:37 AM 
 *
 */
@Slf4j
public class HttpClientConfig {

	private HttpClientConfig() {
		/**
		 * make it private for singleton pattern
		 */
	}

	public static final int CONNECT_TIMEOUT = Integer.parseInt(readConfigVar("CONNECT_TIMEOUT", "30"));
	public static final int CONNECTION_REQUEST_TIMEOUT = Integer.parseInt(readConfigVar("CONNECTION_REQUEST_TIMEOUT", "30"));
	public static final int SOCKET_TIMEOUT = Integer.parseInt(readConfigVar("SOCKET_TIMEOUT", "30"));
	public static final int HARD_TIMEOUT = Integer.parseInt(readConfigVar("HARD_TIMEOUT", "60"));

	private static String getEnvVar(String key, String defaultValue) {

		if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
			return System.getenv(key);
		}

		return defaultValue;
	}

	private static String readConfigVar(String key, String defaultValue) {
		return getEnvVar(key, defaultValue); 
	}
	
	public static void print() {
		log.info("CONNECT_TIMEOUT :: {} ", CONNECT_TIMEOUT); 
		log.info("CONNECTION_REQUEST_TIMEOUT :: {} ", CONNECTION_REQUEST_TIMEOUT); 
		log.info("SOCKET_TIMEOUT :: {} ", SOCKET_TIMEOUT); 
		log.info("HARD_TIMEOUT :: {} ", HARD_TIMEOUT); 
	}

}
