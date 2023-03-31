/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simpleton.http_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author tariqul :: Mar 9, 2023 :: 2:01:23 AM 
 *
 */
@ToString
@Slf4j
public class APIRequest {

	private String url;
	private Map<String, String> headers;
	private Map<String, String> queryParams;
	private Map<String, String> formParams;
	private List<String> pathParams;

    public APIRequest(final String url) {
    	this.url = url;
    	this.queryParams = new HashMap<>();
    	this.formParams = new HashMap<>();
    	this.headers = new HashMap<>(); 
    	this.pathParams = new ArrayList<>();
    }
    
    public static APIRequest of(final String url) {
    	return new APIRequest(url);
    }
    
    public APIRequest pathParam(final String pathParam) {
        this.pathParams.add(pathParam);
        return this;
    }

    public APIRequest queryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }
    
    public APIRequest formParam(String key, String value) {
        this.formParams.put(key, value);
        return this;
    }

    public APIRequest header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }
    
    private Optional<URIBuilder> baseURIBuilder() { 
    	
    	try {
    		 return Optional.ofNullable(new URIBuilder(this.url));
		} catch (Exception e) {
			log.error("BaseURIBuilder Error ", e); 
		}
    	
		return Optional.empty(); 
    	
    }
    
    private CloseableHttpClient initClient() {
    	RequestConfig config = RequestConfig.custom()
    			  .setConnectTimeout(HttpClientConfig.CONNECT_TIMEOUT * 1000)
    			  .setConnectionRequestTimeout(HttpClientConfig.CONNECTION_REQUEST_TIMEOUT * 1000)
    			  .setSocketTimeout(HttpClientConfig.SOCKET_TIMEOUT * 1000).build();
		return HttpClientBuilder.create().setDefaultRequestConfig(config).build(); 
    	
    }

    public APIResponse get(){

        Optional<URIBuilder> uri = baseURIBuilder(); 
        
        if(!uri.isPresent()) {
        	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
        
        uri.get().setPathSegments(pathParams);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) { 
            uri.get().addParameter(entry.getKey(), entry.getValue());
        }
        
        HttpGet get = new HttpGet(uri.get().toString());
        
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addHeader(entry.getKey(), entry.getValue());
        }

        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = processGetRequest(client, get);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessGetRequest Error ", e); 
		}

        return response;
    }

    public APIResponse postURL() {

    	Optional<URIBuilder> uri = baseURIBuilder(); 
    	
    	if(!uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	 
    	uri.get().setPathSegments(pathParams);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            uri.get().addParameter(entry.getKey(), entry.getValue());
        }

        HttpPost post = new HttpPost(uri.get().toString());

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }

        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = processPostRequest(client, post);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessPostRequest Error ", e); 
		}

        return response;
    }

    public APIResponse postForm() {
    	
    	Optional<URIBuilder> uri = baseURIBuilder(); 
    	
    	if(!uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	uri.get().setPathSegments(pathParams);
    	
    	for (Map.Entry<String, String> entry : queryParams.entrySet()) {
    		uri.get().addParameter(entry.getKey(), entry.getValue());
    	}

        HttpPost post = new HttpPost(uri.get().toString());
        
        List<NameValuePair> formParameters = new ArrayList<>();

        for (Map.Entry<String, String> entry : formParams.entrySet()) {
        	formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }

        post.setEntity(new UrlEncodedFormEntity(formParameters, StandardCharsets.UTF_8));

        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = this.processPostRequest(client, post);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessPostRequest Error ", e); 
		}

        return response;
    }
   
    
    public APIResponse delete() {
    	
    	Optional<URIBuilder> uri = baseURIBuilder(); 
    	
    	if(!uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	uri.get().setPathSegments(pathParams);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            uri.get().addParameter(entry.getKey(), entry.getValue());
        }

        HttpDelete delete = new HttpDelete(uri.get().toString()); 

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            delete.addHeader(entry.getKey(), entry.getValue());
        }

        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = this.processDeleteRequest(client, delete);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessDeleteRequest Error ", e); 
		}

        return response;
    }
    
    public APIResponse patch() throws Exception {
    	
    	Optional<URIBuilder> uri = baseURIBuilder(); 
    	
    	if(!uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	uri.get().setPathSegments(pathParams);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            uri.get().addParameter(entry.getKey(), entry.getValue());
        }

        HttpPatch patch = new HttpPatch(uri.get().toString());

        List<NameValuePair> formParameters = new ArrayList<>();

        for (Map.Entry<String, String> entry : formParams.entrySet()) {
        	formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            patch.addHeader(entry.getKey(), entry.getValue()); 
        }

        patch.setEntity(new UrlEncodedFormEntity(formParameters, StandardCharsets.UTF_8));

        APIResponse response = new APIResponse();

        try(CloseableHttpClient client = initClient();) {
            response = this.processPatchRequest(client, patch);
        } catch (Exception e) {
        	response.response(new StringBuilder(e.getMessage())); 
        	log.error("ProcessDeleteRequest Error ", e); 
        }

        return response;
    }

    public APIResponse postJSON() throws Exception {

        HttpPost post = new HttpPost(this.url);

        JSONObject paramJSON = new JSONObject();

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            paramJSON.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }

        post.setEntity(new StringEntity(paramJSON.toString(), "utf-8"));

        CloseableHttpClient client = initClient();
        
        APIResponse response = null;

        try {
            response = this.process(client, post);
        } finally {

            if (client != null) {
                client.close();
            }
            client = null;
        }

        return response;
    }
    
    public APIResponse postJSON(JSONObject requestBody) throws Exception {

        HttpPost post = new HttpPost(this.url);


        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }
        post.setEntity(new StringEntity(requestBody.toString(), "utf-8"));

        CloseableHttpClient client = initClient();
        
        APIResponse response = null;

        try {
            response = this.process(client, post);
        } finally {

            if (client != null) {
                client.close();
            }
            client = null;
            
        }

        return response;
    }

    private APIResponse processPatchRequest(CloseableHttpClient client, HttpPatch patch) throws IOException {
    	
    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (patch != null) {
    	        	patch.abort();
    	        }
    	    }
    	};
    	new Timer(true).schedule(task, HttpClientConfig.HARD_TIMEOUT * 1000);

        CloseableHttpResponse response = client.execute(patch);
        
        APIResponse apiResponse = null;

        try {

            apiResponse = processResponse(response);

        } finally {
            if (response != null) {
                response.close();
            }
        }

        return apiResponse;
    }
    
    private APIResponse processDeleteRequest(CloseableHttpClient client, HttpDelete delete) throws IOException {

    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (delete != null) {
    	        	delete.abort();
    	        }
    	    }
    	};
    	new Timer(true).schedule(task, HttpClientConfig.HARD_TIMEOUT * 1000);
    	
        CloseableHttpResponse response = client.execute(delete);
        
        APIResponse apiResponse = null;

        try {

            apiResponse = processResponse(response);

        } finally {
            if (response != null) {
                response.close();
            }
        }

        return apiResponse;
    }
 
    
    private APIResponse processPostRequest(CloseableHttpClient client, HttpPost post) throws IOException {

    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (post != null) {
    	        	post.abort();
    	        }
    	    }
    	};
    	new Timer(true).schedule(task, HttpClientConfig.HARD_TIMEOUT * 1000);
    	
        CloseableHttpResponse response = client.execute(post);
        
        APIResponse apiResponse = null;

        try {

            apiResponse = processResponse(response);

        } finally {
            if (response != null) {
                response.close();
            }
        }

        return apiResponse;
    }

    private APIResponse processGetRequest(CloseableHttpClient client, HttpGet get){

    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (get != null) {
    	        	get.abort();
    	        }
    	    }
    	};
    	new Timer(true).schedule(task, HttpClientConfig.HARD_TIMEOUT * 1000);
    	
    	APIResponse apiResponse = new APIResponse();
    	
    	try (CloseableHttpResponse response = client.execute(get);){
    		return processResponse(response);
		} catch (Exception e) { 
			apiResponse.response(new StringBuilder(e.getMessage()));
			log.error("ProcessResponse Error" , e); 
		}

        return apiResponse;
    }

    private APIResponse processResponse(CloseableHttpResponse response) {

        APIResponse apiResponse = new APIResponse(new Status(response.getStatusLine().getStatusCode()));

        BufferedReader bufferedReader = null;

        try {

        	if(null!=response.getEntity() 
        			&& null!=response.getEntity().getContent()) {
        		  bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                  StringBuilder stringBuilder = new StringBuilder();
                  String line = null;

                  while ((line = bufferedReader.readLine()) != null) {
                      stringBuilder.append(line);
                  }

                  apiResponse.response(stringBuilder);
        	}
            
            for (Header header: response.getAllHeaders()) {
            	apiResponse.responseHeader(header.getName(), header.getValue());
            }

        } catch (UnsupportedOperationException e) { 
        	apiResponse.response(new StringBuilder(e.getMessage()));
        	log.error("ProcessResponse UnsupportedOperationException Error " , e ); 
		} catch (IOException e) {
			apiResponse.response(new StringBuilder(e.getMessage()));
			log.error("ProcessResponse IOException Error " , e ); 
		} finally {
            if (bufferedReader != null) {
                try {
					bufferedReader.close();
				} catch (IOException e) {
					apiResponse.response(new StringBuilder(e.getMessage()));
					log.error("ProcessResponse IOException Error " , e ); 
				}
            }
        }

        return apiResponse;
    }
    
    public static void main(String[] args) {
    	
    	APIRequest apiRequest = new APIRequest("https://core-api.pypepro.io/v1/contactflows/config");

    	try {
    		System.out.println("Start Process"); 
  			APIResponse apiResponse = apiRequest.get();
  			System.out.println("StatusCode: "+apiResponse.getStatus());
  		} catch (Exception e) {
  			log.error("Error ",e); 
  		}
    	
    }
}
