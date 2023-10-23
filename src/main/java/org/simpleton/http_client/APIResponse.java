/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.simpleton.http_client.util.JSONUtil;

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
    
    private Optional<ContentType> contentType;
    
    @Getter
    private Charset responseCharsets; 
    
    public APIResponse(){
    	this.response = new StringBuilder();
    	this.responseHeader = new HashMap<>();
    	this.jsonUtil = new JSONUtil();
    	this.contentType = Optional.empty();
    }

    public APIResponse(Status status, StringBuilder response) {
    	this.responseHeader = new HashMap<>();
        this.status = status; 
        this.response = response;
        this.contentType = Optional.empty();
    }
    
    public APIResponse(StringBuilder response) { 
    	this.responseHeader = new HashMap<>();
    	this.response = response;
    	this.contentType = Optional.empty();
	}

	public APIResponse(Status status) { 
		this.responseHeader = new HashMap<>();
		this.status = status;
		this.contentType = Optional.empty();
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
    
    public Optional<String> responseAsString() {
    	if(null == response) {
    		log.error(Constant.ERROR_LEVEL, "Response is empty");
    		return Optional.empty();
    	}
        return Optional.ofNullable(response.toString()); 
    }
    
    public Optional<ContentType> getContentType() {
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
        	this.contentType = Optional.ofNullable(responseContentType);
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
    

}
