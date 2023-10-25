/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.simpleton.http_client.util.JSONUtil;
import org.simpleton.http_client.util.JacksonObjectMapperConfig;
import org.simpleton.http_client.util.StringUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author tariqul <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since Oct 26, 2023 :: 1:32:46 AM
 * 
 */
@ToString
@Slf4j
public class APIResponse {
	
	// Define regular expressions for Content-Type and charset extraction
    private static final  Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([^;]+)");

	@Getter
    private Status status;
	
    private StringBuilder response;
    
    @Getter
    private Map<String,String> responseHeader;
    
    private JSONUtil jsonUtil;
    
    private JacksonObjectMapperConfig mapperConfig;
    
    @Getter
    private String contentTypeInString;
    
    private ContentType contentType;
    
    @Getter
    private Charset responseCharsets; 
    
    public APIResponse(){
    	this.response = new StringBuilder();
    	this.responseHeader = new HashMap<>();
    	this.jsonUtil = new JSONUtil();
    	this.mapperConfig = new JacksonObjectMapperConfig();
    }

    public APIResponse(Status status, StringBuilder response) {
    	this.responseHeader = new HashMap<>();
        this.status = status; 
        this.response = response;
        this.jsonUtil = new JSONUtil();
        this.mapperConfig = new JacksonObjectMapperConfig();
    }
    
    public APIResponse(StringBuilder response) { 
    	this.responseHeader = new HashMap<>();
    	this.response = response;
    	this.jsonUtil = new JSONUtil();
    	this.mapperConfig = new JacksonObjectMapperConfig();
	}

	public APIResponse(Status status) { 
		this.responseHeader = new HashMap<>();
		this.status = status;
		this.jsonUtil = new JSONUtil();
		this.mapperConfig = new JacksonObjectMapperConfig();
	}

	public APIResponse responseHeader(String key, String value) {
    	this.responseHeader.put(key, value);
    	return this;
    }
    
    public APIResponse response(StringBuilder response) {
    	this.response = response;
    	return this;
    }
    
    public APIResponse status(Status status) {
    	this.status = status; 
    	return this;
    }
    
    public APIResponse contentType(String contentType) {
    	this.contentTypeInString = contentType; 
    	parseContentTypeString();
    	return this;
    }
    
    public boolean isSuccess() {
    	return this.status != null
    			&& this.status.isSuccess();
    }

    public Optional<JSONObject> responseAsJSONObject() {
    	if(null == response) {
    		return Optional.empty();
    	}
    	Optional<JSONObject> responseJSONObject = jsonUtil.toJSONObject(response.toString());
    	if(responseJSONObject.isEmpty()) { 
    		log.error(Constant.ERROR_LEVEL, "Response is not json object");
    	}
        return responseJSONObject;
    }
    
    public Optional<JSONArray> responseAsJSONArray() {
    	if(null == response) {
    		return Optional.empty();
    	}
    	Optional<JSONArray> responseJSONObject = jsonUtil.toJSONJSONArray(response.toString());
    	if(responseJSONObject.isEmpty()) { 
    		log.error(Constant.ERROR_LEVEL, "Response is not json Array");
    	}
        return responseJSONObject;
    }
    
    public String responseAsString() {
    	if(null == response) {
    		log.error(Constant.ERROR_LEVEL, "Response is empty");
    		return "";
    	}
        return response.toString(); 
    }
    
    public boolean isJSONObject() {
    	return jsonUtil.isValidJsonObject(response.toString());
    }
    
    public boolean isJSONArray() {
    	return jsonUtil.isValidJsonArray(response.toString());
    }
    
    public ContentType getContentType() {
		return this.contentType;
    }
    
    private void parseContentTypeString() {
    	if(null == contentTypeInString) {
    		return; 
    	}
    	// Match the Content-Type and charset using regular expressions
        Matcher contentTypeMatcher = CONTENT_TYPE_PATTERN.matcher(contentTypeInString);
        Matcher charsetMatcher = CHARSET_PATTERN.matcher(contentTypeInString);
        // Extract Content-Type
        if (contentTypeMatcher.find()) {
            String parsedContentType = contentTypeMatcher.group(1).trim();
            ContentType responseContentType = ContentType.valueOfIgnoreCase(parsedContentType);
        	this.contentType = responseContentType;
        }
        // Extract charset
        if (charsetMatcher.find()) {
            String charset = charsetMatcher.group(1).trim();
            mapCharsets(charset); 
        }
    }
    
    
    private void mapCharsets(String charset) {
    	if (StandardCharsets.ISO_8859_1.name().equalsIgnoreCase(charset)) {
    		responseCharsets = StandardCharsets.ISO_8859_1;
		}else if(StandardCharsets.US_ASCII.name().equalsIgnoreCase(charset)) {
			responseCharsets = StandardCharsets.US_ASCII;
		}else if(StandardCharsets.UTF_16.name().equalsIgnoreCase(charset)) {
			responseCharsets = StandardCharsets.UTF_16;
		}else if(StandardCharsets.UTF_16BE.name().equalsIgnoreCase(charset)) {
			responseCharsets = StandardCharsets.UTF_16BE;
		}else if(StandardCharsets.UTF_16LE.name().equalsIgnoreCase(charset)) {
			responseCharsets = StandardCharsets.UTF_16LE;
		}else if(StandardCharsets.UTF_8.name().equalsIgnoreCase(charset)) {
			responseCharsets = StandardCharsets.UTF_8;
		}
    }
    
    public void print() {
    	log.info("response :: {} ", response.toString()); 
    	log.info("responseHeader :: {} ", responseHeader); 
    }
    
    /**
     * Deserialize a JSON array at a specified key from the HTTP response into a list of Java objects of the given class.
     * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
     * <br>
     * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
     * <br>
     * @since : Apr 30, 2023 12:39:22 AM
     * @param key   The key that represents the JSON array to be deserialized.
     * @param clazz The class of the Java objects in the list.
     * @return A list of deserialized Java objects, or an empty list if deserialization fails or the key is not found.
     */
    public <T> List<T> toDtos(String key, Class<T> clazz) {
    	
        // Check if the key is empty or if the response is not in JSON format or not a JSON object.
		if (StringUtil.isBlank(key) || !contentType.isJSONType() || !isJSONObject()) {
			return Collections.emptyList();
	    }
		
	    // Attempt to retrieve the response as a JSON object.
		Optional<JSONObject> responseASJsonObject = responseAsJSONObject();
    			
    	if(!responseASJsonObject.isPresent()) {
    		return Collections.emptyList();
    	}
    	
    	JSONObject responseJsonObject = responseASJsonObject.get();
    	
        // Check if the key exists in the JSON object and is a JSON array.
    	if(!responseJsonObject.has(key) || !(responseJsonObject.get(key) instanceof JSONArray)) {
    		return Collections.emptyList();
    	}
    	
        // Deserialize the JSON array at the specified key into a list of Java objects.
    	JSONArray data = responseJsonObject.getJSONArray(key);
		
		try {
			ObjectMapper objectMapper = mapperConfig.getObjectMapper();
			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
			return  objectMapper.readValue(data.toString(), listType);
		} catch (IOException e) { 
			log.error("Error deserializing JSONArray response {}", e);
		}
		
    	return Collections.emptyList(); 
    }
    
    /**
     * Deserialize a JSON object at a specified key from the HTTP response into a Java object of the given class.
     * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
     * <br>
     * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
     * <br>
     * @since : Apr 30, 2023 12:39:22 AM
     *  @param key   The key that represents the JSON object to be deserialized.
     * @param clazz The class of the Java object to deserialize to.
     * @return The deserialized Java object, or null if deserialization fails or the key is not found.
     */
	public <T> T toDto(String key, Class<T> clazz) {
		
	    // Check if the key is empty or if the response is not in JSON format or not a JSON object.
		if (StringUtil.isBlank(key) || !contentType.isJSONType() || !isJSONObject()) {
	        return null;
	    }
		
	    // Attempt to retrieve the response as a JSON object.
		Optional<JSONObject> responseAsJsonObject = responseAsJSONObject();
		
	    if (!responseAsJsonObject.isPresent()) {
	        return null;
	    }
	    
	    JSONObject responseJsonObject = responseAsJsonObject.get();
	    // Check if the key exists in the JSON object and is a JSON object itself.
		if (!responseJsonObject.has(key) || !(responseJsonObject.get(key) instanceof JSONObject)) {
			return null;
		}

	    // Deserialize the JSON object at the specified key into a Java object.
		JSONObject data = responseJsonObject.getJSONObject(key);
		
		try {
			return mapperConfig.getObjectMapper().readValue(data.toString(), clazz);
		} catch (IOException e) {
			log.error("Error deserializing JSON response {}", e);
			return null;
		}

	} 
    
    /**
     * Deserialize a JSON array from the response into a list of Java objects of the given class.
     * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
     * <br>
     * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
     * <br>
     * @since : Apr 30, 2023 12:39:22 AM
     * @param clazz The class of the Java objects in the list.
     * @return A list of deserialized Java objects, or an empty list if deserialization fails or the response is not a JSON array.
     */
    public <T> List<T> toDtos(Class<T> clazz) {
    	
        // Check if the response is not in JSON format or not a JSON array.
    	if (!contentType.isJSONType() || !isJSONArray()) {
            return Collections.emptyList();
        }
    	
        // Attempt to retrieve the response as a JSON array.
    	Optional<JSONArray> responseASJsonObject = responseAsJSONArray();
    	
    	if(!responseASJsonObject.isPresent()) {
    		return Collections.emptyList();
		}
		
        // Deserialize the JSON array into a list of Java objects of the specified class.
		try {
			ObjectMapper objectMapper = mapperConfig.getObjectMapper();
			return  objectMapper.readValue(responseASJsonObject.get().toString(), mapperConfig.getListType(clazz)); 
		} catch (IOException e) { 
			log.error("Error deserializing JSONArray response {}", e);
			return Collections.emptyList(); 
		}
		
    	
    }
    
    /**
     * Deserialize the JSON response into a Java object of the given class.
     * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
     * <br>
     * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
     * <br>
     * @since : Apr 30, 2023 12:39:22 AM
     * @param clazz The class of the Java object to deserialize to.
     * @return The deserialized Java object, or null if deserialization fails or the response is not JSON.
     */
    public <T> T toDto(Class<T> clazz) {
    	
        // Check if the response is not in JSON format or not a JSON object.
    	if (!contentType.isJSONType() || !isJSONObject()) {
            return null;
        }
    	
        // Attempt to retrieve the response as a JSON object.
    	Optional<JSONObject> responseAsJsonObject = responseAsJSONObject();
    	
        if (!responseAsJsonObject.isPresent()) {
            return null;
        }
        
        // Deserialize the JSON object into a Java object of the specified class.
        try {
            return mapperConfig.getObjectMapper().readValue(responseAsJsonObject.get().toString(), clazz);
        } catch (IOException e) {
            log.error("Error deserializing JSON response {}", e);
            return null;
        }
		
    }  
    

}
