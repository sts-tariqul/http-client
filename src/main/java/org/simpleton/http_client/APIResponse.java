/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

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
import org.simpleton.http_client.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author tariqul :: Mar 9, 2023 :: 2:24:08 AM 
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
    
    @Getter
    private String contentTypeInString;
    
    private ContentType contentType;
    
    @Getter
    private Charset responseCharsets; 
    
    public APIResponse(){
    	this.response = new StringBuilder();
    	this.responseHeader = new HashMap<>();
    	this.jsonUtil = new JSONUtil();
    }

    public APIResponse(Status status, StringBuilder response) {
    	this.responseHeader = new HashMap<>();
        this.status = status; 
        this.response = response;
        this.jsonUtil = new JSONUtil();
    }
    
    public APIResponse(StringBuilder response) { 
    	this.responseHeader = new HashMap<>();
    	this.response = response;
    	this.jsonUtil = new JSONUtil();
	}

	public APIResponse(Status status) { 
		this.responseHeader = new HashMap<>();
		this.status = status;
		this.jsonUtil = new JSONUtil();
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
    
    public <T> List<T> toDtos(String key, Class<T> clazz) {
    	if(StringUtil.isBlank(key)) {
    		return Collections.emptyList();
    	}
    	JSONObject responseJsonObject = null;
    	if(contentType.isJSONType() && isJSONObject()) {
    		Optional<JSONObject> responseASJsonObject = responseAsJSONObject();
    		responseJsonObject = responseASJsonObject.isPresent() ? responseASJsonObject.get() : null; 
    	}
    	if(responseJsonObject == null) {
    		return Collections.emptyList();
		}
    	if(!responseJsonObject.has(key) || !(responseJsonObject.get(key) instanceof JSONArray)) {
    		return Collections.emptyList();
    	}
    	
    	JSONArray data = responseJsonObject.getJSONArray(key);
    	ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		try {
			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
			return  objectMapper.readValue(data.toString(), listType);
		} catch (Exception e) { 
			log.error(Constant.ERROR_LEVEL, e);
		}
		
    	return Collections.emptyList(); 
    }
    
    public <T> T toDto(String key, Class<T> clazz) {
    	if(StringUtil.isBlank(key)) {
    		return null;
    	}
    	JSONObject responseJsonObject = null;
    	if(contentType.isJSONType() && isJSONObject()) {
    		Optional<JSONObject> responseASJsonObject = responseAsJSONObject();
    		responseJsonObject = responseASJsonObject.isPresent() ? responseASJsonObject.get() : null; 
    	}
    	if(responseJsonObject == null) {
    		return null;
		}
    	if(!responseJsonObject.has(key) || !(responseJsonObject.get(key) instanceof JSONObject)) {
    		return null;
    	}
    	
    	JSONObject data = responseJsonObject.getJSONObject(key);
    	ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		try {
			return  objectMapper.readValue(data.toString(), clazz);
		} catch (Exception e) { 
			log.error(Constant.ERROR_LEVEL, e);
		}
		
    	return null; 
    }  
    
    public <T> List<T> toDtos(Class<T> clazz) {
    	
    	JSONArray responseJsonObject = null;
    	if(contentType.isJSONType() && isJSONArray()) {
    		Optional<JSONArray> responseASJsonObject = responseAsJSONArray();
    		responseJsonObject = responseASJsonObject.isPresent() ? responseASJsonObject.get() : null; 
    	}
    	if(responseJsonObject == null) {
    		return Collections.emptyList();
		}
    	
    	ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		try {
			CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
			return  objectMapper.readValue(responseJsonObject.toString(), listType);
		} catch (Exception e) { 
			log.error(Constant.ERROR_LEVEL, e);
		}
		
    	return Collections.emptyList(); 
    }
    
    public <T> T toDto(Class<T> clazz) {
    	
    	JSONObject responseJsonObject = null;
    	if(contentType.isJSONType() && isJSONObject()) {
    		Optional<JSONObject> responseASJsonObject = responseAsJSONObject();
    		responseJsonObject = responseASJsonObject.isPresent() ? responseASJsonObject.get() : null; 
    	}
    	if(responseJsonObject == null) {
    		return null;
		}
    	ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		
		try {
			return  objectMapper.readValue(responseJsonObject.toString(), clazz);
		} catch (Exception e) { 
			log.error(Constant.ERROR_LEVEL, e);
		}
		
    	return null; 
    }  
    

}
