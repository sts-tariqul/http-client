/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.simpleton.http_client.util.HttpHeaderBuilderHelper;
import org.simpleton.http_client.util.HttpRequestProcessor;
import org.simpleton.http_client.util.JSONUtil;
import org.simpleton.http_client.util.RequestBodyValidator;
import org.simpleton.http_client.util.RequestDataBuilder;
import org.simpleton.http_client.util.URIBuildHelper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Base http request wrapper class
 * @author tariqul :: Mar 9, 2023 :: 2:01:23 AM 
 *
 */
@ToString
@Slf4j
public class APIRequest implements Request {
	
	private static final String MISMATCH_MEDIA_TYPE = "Media Type Not Matched";
	
	private URIBuildHelper uriBuildHelper;
	
	private HttpHeaderBuilderHelper headerBuilderHelper; 
	
	private HttpRequestProcessor httpRequestProcessor;
	
	private RequestBodyValidator requestBodyValidator;
	
	private RequestDataBuilder requestDataBuilder;

	private String url;
	
	private URI uri;
	
	private Map<String, String> headers;
	
	private List<String> pathParams;
	
	private Map<String, String> queryParams;
	
	private Map<String, String> formParams;
	
	private ContentType contentType;
	
	private List<Attachment> attachments;
	
	private String reqeustBody;
	
	private String errorMessage;
	
	private JSONUtil jsonUtil;

    public APIRequest(final String url) {
    	this.url = url;
    	this.attachments = new ArrayList<>();
    	this.queryParams = new HashMap<>();
    	this.formParams = new HashMap<>();
    	this.headers = new HashMap<>(); 
    	this.pathParams = new ArrayList<>();
    	this.uriBuildHelper = new URIBuildHelper();
    	this.headerBuilderHelper = new HttpHeaderBuilderHelper();
    	this.httpRequestProcessor = new HttpRequestProcessor();
    	this.requestBodyValidator = new RequestBodyValidator();
    	this.requestDataBuilder = new RequestDataBuilder();
    	this.jsonUtil = new JSONUtil();
    }
    
    public static APIRequest of(final String url) {
    	return new APIRequest(url);
    }
    
    public APIRequest pathParam(final String pathParam) {
        this.pathParams.add(pathParam);
        return this;
    }
    
    public APIRequest pathParam(String... pathParamArray) {
    	for(String pathParam:pathParamArray) {
    		  this.pathParams.add(pathParam);
    	}
        return this;
    }
    
    public APIRequest pathParam(List<String> pathParamArray) {
    	for(String pathParam:pathParamArray) {
    		  this.pathParams.add(pathParam);
    	}
        return this;
    }

    public APIRequest queryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }
    
    public APIRequest queryParam(Map<String,String> queryParams) {
        this.queryParams.putAll(queryParams); 
        return this;
    }
    
    public APIRequest formParam(String key, String value) {
        this.formParams.put(key, value);
        return this;
    }
    
    public APIRequest formParam(Map<String,String> formParams) {
        this.formParams.putAll(formParams); 
        return this;
    }

    public APIRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }
    
    public APIRequest header(Map<String,String> headers) {
        this.headers.putAll(headers); 
        return this;
    }
    
    public APIRequest contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }
    
    public APIRequest attachments(Attachment... attachments) {
    	for(Attachment attachment : attachments) {
  		  this.attachments.add(attachment);
    	}
        return this;
    }
    
    public APIRequest attachments(List<Attachment> attachments) { 
    	for(Attachment attachment : attachments) {
  		  this.attachments.add(attachment);
    	}
        return this;
    }
    
    public APIRequest attachments(Attachment attachment) {
  		this.attachments.add(attachment);
        return this;
    }
    
    /**
     * This method to use initialize <code>CloseableHttpClient</code>
     * @return <code>CloseableHttpClient</code> instance
     */
    private CloseableHttpClient initClient() {
    	HttpClientConfig httpClientConfig = HttpClientConfig.builder().build();
    	RequestConfig config = RequestConfig.custom()
    			  .setConnectTimeout(httpClientConfig.getConnectTimeout() * Constant.ONE_SECOND_MILLISECOND)
    			  .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout() * Constant.ONE_SECOND_MILLISECOND)
    			  .setSocketTimeout(httpClientConfig.getSocketTimeout() * Constant.ONE_SECOND_MILLISECOND).build();
		return HttpClientBuilder.create().setDefaultRequestConfig(config).build(); 
    }
    
	@Override
	public APIResponse get() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
        boolean valid = requestBodyValidator.isValidRequestBody(contentType, reqeustBody, formParams, attachments);
        
        APIResponse response = new APIResponse();
        
        if(!valid) {
        	response.status(Status.of(HttpStatus.SC_BAD_REQUEST, MISMATCH_MEDIA_TYPE));
        	response.contentType(ContentType.TEXT_PLAIN.name()); 
        	response.response(new StringBuilder().append(requestBodyValidator.getErrorMessage()).append(":").append(MISMATCH_MEDIA_TYPE));
        	return response;
        }
        
        // Create a HttpGetWithEntity request
        HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpGetWithEntity, headers); 
        
        requestDataBuilder.buildRequestData(httpGetWithEntity, contentType, formParams, reqeustBody, attachments); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpGetWithEntity);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
        
	}

	@Override
	public APIResponse post() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
        boolean valid = requestBodyValidator.isValidRequestBody(contentType, reqeustBody, formParams, attachments);
        
        APIResponse response = new APIResponse();
        
        if(!valid) {
        	response.status(Status.of(HttpStatus.SC_BAD_REQUEST, MISMATCH_MEDIA_TYPE));
        	response.contentType(ContentType.TEXT_PLAIN.name()); 
        	response.response(new StringBuilder().append(requestBodyValidator.getErrorMessage()).append(":").append(MISMATCH_MEDIA_TYPE));
        	return response;
         }
        
        
        // Create a HttpPost request
        HttpPost httpPost = new HttpPost(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpPost, headers); 
        
        requestDataBuilder.buildRequestData(httpPost, contentType, formParams, reqeustBody, attachments); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpPost);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
        
	}

	@Override
	public APIResponse put() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
        boolean valid = requestBodyValidator.isValidRequestBody(contentType, reqeustBody, formParams, attachments);
        
        APIResponse response = new APIResponse();
        
        if(!valid) {
        	response.status(Status.of(HttpStatus.SC_BAD_REQUEST, MISMATCH_MEDIA_TYPE));
        	response.contentType(ContentType.TEXT_PLAIN.name()); 
        	response.response(new StringBuilder().append(requestBodyValidator.getErrorMessage()).append(":").append(MISMATCH_MEDIA_TYPE));
        	return response;
        }
        
        // Create a HttpPut request
        HttpPut httpPut = new HttpPut(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpPut, headers); 
        
        requestDataBuilder.buildRequestData(httpPut, contentType, formParams, reqeustBody, attachments); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpPut);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
        
	}

	@Override
	public APIResponse delete() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
		boolean valid = requestBodyValidator.isValidRequestBody(contentType, reqeustBody, formParams, attachments);
        
        APIResponse response = new APIResponse();
        
        if(!valid) {
        	response.status(Status.of(HttpStatus.SC_BAD_REQUEST, MISMATCH_MEDIA_TYPE));
        	response.contentType(ContentType.TEXT_PLAIN.name()); 
        	response.response(new StringBuilder().append(requestBodyValidator.getErrorMessage()).append(":").append(MISMATCH_MEDIA_TYPE));
        	return response;
         }
		
        // Create a HttpDeleteWithEntity request
		HttpDeleteWithEntity httpDeleteWithEntity = new HttpDeleteWithEntity(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpDeleteWithEntity, headers); 
        
        requestDataBuilder.buildRequestData(httpDeleteWithEntity, contentType, formParams, reqeustBody, attachments); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpDeleteWithEntity);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
	}

	@Override
	public APIResponse head() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
		APIResponse response = new APIResponse();
		
		// Create an HttpHead request
        HttpHead httpHead = new HttpHead(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpHead, headers); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpHead);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
	}

	@Override
	public APIResponse patch() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
		
        boolean valid = requestBodyValidator.isValidRequestBody(contentType, reqeustBody, formParams, attachments);
        
        APIResponse response = new APIResponse();
        
        if(!valid) {
        	response.status(Status.of(HttpStatus.SC_BAD_REQUEST, MISMATCH_MEDIA_TYPE));
        	response.contentType(ContentType.TEXT_PLAIN.name()); 
        	response.response(new StringBuilder().append(requestBodyValidator.getErrorMessage()).append(":").append(MISMATCH_MEDIA_TYPE));
        	return response;
         }
        
        
        // Create a HttpPatch request
        HttpPatch httpPatch = new HttpPatch(this.uri.toString());
        
        headerBuilderHelper.appendHeader(httpPatch, headers); 
        
        requestDataBuilder.buildRequestData(httpPatch, contentType, formParams, reqeustBody, attachments); 
        
        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpPatch);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error(Constant.ERROR_LEVEL, e); 
		}

        return response;
        
	}
	


	@Override
	public APIResponse options() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
        
        // Create an HTTP OPTIONS request
        HttpOptions httpOptions = new HttpOptions(this.uri.toString()); 
        
        headerBuilderHelper.appendHeader(httpOptions, headers); 
        
        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, httpOptions);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessPostRequest Error ", e); 
		}

        return response;
	}
	
	@Override
	public APIResponse trace() {
		
		uri = uriBuildHelper.buildURI(url, pathParams, queryParams);
		
		if(uri == null) {
         	return new APIResponse(new StringBuilder(uriBuildHelper.getErrorMessage())); 
        }
    	 
        // Create a TRACE request
        HttpTrace traceRequest = new HttpTrace(this.uri.toString());
        
        headerBuilderHelper.appendHeader(traceRequest, headers); 
        
        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = httpRequestProcessor.processRequest(client, traceRequest);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessPostRequest Error ", e); 
		}

        return response;
	}
	
	static class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
		public HttpGetWithEntity(String uri) {
			setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return "GET";
		}
	}
	
	
	static class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {
		public HttpDeleteWithEntity(String uri) {
			setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return "DELETE";
		}
	}
	
	
	public static void main(String[] args) {
	    	
	    	APIRequest apiRequest = new APIRequest("https://reqres.in");
	    	
	    	apiRequest.pathParam("api","users");
	    	apiRequest.queryParam("page", "2");

	    	try {
	    		System.out.println("Start Process"); 
	  			APIResponse apiResponse = apiRequest.get();
	  			System.out.println("StatusCode: "+apiResponse.getStatus());
	  			System.out.println("ContentType: "+apiResponse.getContentType());
	  			System.out.println("ResponseHeader: "+apiResponse.getResponseHeader());
	  			System.out.println("responseAsString: "+apiResponse.responseAsString());
	  			System.out.println("ResponseCharsets: "+apiResponse.getResponseCharsets());
	  			System.out.println("ResponseData: "+apiResponse.toDtos("data", Student.class));
	  		} catch (Exception e) {
	  			e.printStackTrace();
	  			log.error("Error ",e); 
	  		}
	    	
	}
	
	/**
	 * {"id":8,"email":"lindsay.ferguson@reqres.in","first_name":"Lindsay","last_name":"Ferguson","avatar":"https://reqres.in/img/faces/8-image.jpg"}
	 * @author tariqul
	 *
	 */
	@ToString
	@Getter
	@Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Student{
		
		private int id;
		
		private String email;
		
		@JsonProperty("first_name")
		private String firstName;
		
		@JsonProperty("last_name")
		private String lastName;
		
		@JsonProperty("avatar")
		private String avatar;
		
	}
}
