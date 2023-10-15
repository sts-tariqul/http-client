/**
 * 
 */
package org.simpleton.http_client;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tariqul
 *
 */
@Slf4j
public class JsonUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public static boolean isValidJsonObject(String jsonStr) {
		if(null == jsonStr) {
			return false;
		}
		try {
			new JSONObject(jsonStr);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}

}
