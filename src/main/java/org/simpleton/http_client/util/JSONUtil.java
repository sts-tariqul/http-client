/**
 * 
 */
package org.simpleton.http_client.util;

import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since : Apr 30, 2023 12:39:22 AM
 *
 */
@Slf4j
public class JSONUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public  Optional<JSONObject> toJSONObject(String jsonStr) {
		if(null == jsonStr) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(new JSONObject(jsonStr));
		} catch (Exception e) {
			
		}
		return Optional.empty();
	}
	
	public  Optional<JSONArray> toJSONJSONArray(String jsonStr) {
		if(null == jsonStr) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(new JSONArray(jsonStr));
		} catch (Exception e) {
			
		}
		return Optional.empty();
	}
	
	public boolean isValidJsonObject(String jsonStr) {
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
	
	public boolean isValidJsonArray(String jsonStr) {
		if(null == jsonStr) {
			return false;
		}
		try {
			new JSONArray(jsonStr);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	public boolean isValidJson(String jsonStr) { 
		return isValidJsonObject(jsonStr) || isValidJsonArray(jsonStr);
	}

}
