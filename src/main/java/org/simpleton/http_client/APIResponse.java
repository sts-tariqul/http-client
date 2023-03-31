/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

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

	@Getter
    private Optional<Status> status = Optional.empty();
	
    private StringBuilder response;
    
    private Map<String,String> responseHeader;
    
    public APIResponse(){
    	this.response = new StringBuilder();
    	this.responseHeader = new HashMap<>();
    }

    public APIResponse(Status status, StringBuilder response) {
    	this.responseHeader = new HashMap<>();
        this.status = Optional.ofNullable(status); 
        this.response = response;
    }
    
    public APIResponse(StringBuilder response) { 
    	this.response = response;
	}

	public APIResponse(Status status) { 
		this.status = Optional.ofNullable(status);
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
    	this.status = Optional.ofNullable(status); 
    	return this;
    }
    
    public boolean isSuccess() {
    	return this.status.isPresent() 
    			&& this.status.get().getCode() >= 200 
    			&& this.status.get().getCode() < 300; 
    }

    public Optional<JSONObject> responseInJSON() {
    	String responseString = response.toString();
    	if(null == responseString) {
    		return Optional.empty();
    	}
        return Optional.ofNullable(new JSONObject(response.toString()));
    }
    
    public void print() {
    	log.info("response :: {} ", response.toString()); 
    	log.info("responseHeader :: {} ", responseHeader); 
    }
    

}
