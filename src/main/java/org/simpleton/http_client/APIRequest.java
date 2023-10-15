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
 * Base http request wrapper class
 * @author tariqul :: Mar 9, 2023 :: 2:01:23 AM 
 *
 */
@ToString
@Slf4j
public class APIRequest {

	private String url;
	private Optional<URIBuilder> uri;
	private Map<String, String> headers;
	private Map<String, String> queryParams;
	private Map<String, String> formParams;
	private List<String> pathParams;
	private HttpClientConfig httpClientConfig;

    public APIRequest(final String url) {
    	this.url = url;
    	this.uri = Optional.empty();
    	this.queryParams = new HashMap<>();
    	this.formParams = new HashMap<>();
    	this.headers = new HashMap<>(); 
    	this.pathParams = new ArrayList<>();
    	this.httpClientConfig = HttpClientConfig.builder().build();
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
    
    private void baseURIBuilder() { 
    	try {
    		 this.uri = Optional.ofNullable(new URIBuilder(this.url));
		} catch (Exception e) {
			log.error("BaseURIBuilder Error ", e); 
		}
    }
    
    /**
     * This method to use initialize <code>CloseableHttpClient</code>
     * @return <code>CloseableHttpClient</code> instance
     */
    private CloseableHttpClient initClient() {
    	RequestConfig config = RequestConfig.custom()
    			  .setConnectTimeout(httpClientConfig.getConnectTimeout() * 1000)
    			  .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout() * 1000)
    			  .setSocketTimeout(httpClientConfig.getSocketTimeout() * 1000).build();
		return HttpClientBuilder.create().setDefaultRequestConfig(config).build(); 
    }
    
    private void appendPathParams() {
    	this.uri.get().setPathSegments(pathParams);
    }
    
    private void appendQueryParams() {
    	 for (Map.Entry<String, String> entry : this.queryParams.entrySet()) { 
         	this.uri.get().addParameter(entry.getKey(), entry.getValue());
         }
    }
    
    private HttpGet appendHeaderToHttpGet(HttpGet get) { 
    	 for (Map.Entry<String, String> entry : this.headers.entrySet()) {
             get.addHeader(entry.getKey(), entry.getValue());
         }
    	 return get;
    }
    
    private HttpPost appendHeaderToHttpPost(HttpPost post) { 
   	 	for (Map.Entry<String, String> entry : this.headers.entrySet()) {
   	 		post.addHeader(entry.getKey(), entry.getValue());
        }
   	 	return post;
    }
    
    private HttpDelete appendHeaderToHttpDelete(HttpDelete delete) { 
   	 	for (Map.Entry<String, String> entry : this.headers.entrySet()) {
   	 		delete.addHeader(entry.getKey(), entry.getValue());
        }
   	 	return delete;
    }

    public APIResponse get(){

        baseURIBuilder(); 
        
        if(!this.uri.isPresent()) {
        	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
         
        appendPathParams();
 
        appendQueryParams();
        
        HttpGet get = new HttpGet(this.uri.get().toString());
        
        get = appendHeaderToHttpGet(get);

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

    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	 
    	appendPathParams();
    	 
        appendQueryParams();

        HttpPost post = new HttpPost(this.uri.get().toString());

        post = appendHeaderToHttpPost(post);

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
    	
    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	appendPathParams();
   	 
        appendQueryParams();

        HttpPost post = new HttpPost(this.uri.get().toString());
        
        List<NameValuePair> formParameters = new ArrayList<>();

        for (Map.Entry<String, String> entry : this.formParams.entrySet()) {
        	formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        post = appendHeaderToHttpPost(post);

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
    	
    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	appendPathParams();
      	 
        appendQueryParams();

        HttpDelete delete = new HttpDelete(uri.get().toString()); 

        delete = appendHeaderToHttpDelete(delete);

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
    	
    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	appendPathParams();
     	 
        appendQueryParams();

        HttpPatch patch = new HttpPatch(this.uri.get().toString());

        List<NameValuePair> formParameters = new ArrayList<>();

        for (Map.Entry<String, String> entry : this.formParams.entrySet()) {
        	formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
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
    	
    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	appendPathParams();
    	 
        appendQueryParams();

        HttpPost post = new HttpPost(this.uri.get().toString());

        JSONObject paramJSON = new JSONObject();

        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            paramJSON.put(entry.getKey(), entry.getValue());
        }

        post = appendHeaderToHttpPost(post);

        post.setEntity(new StringEntity(paramJSON.toString(), "utf-8"));

        CloseableHttpClient client = initClient();
        
        APIResponse response = null;

        try {
            response = this.processPostRequest(client, post);
        } finally {

            if (client != null) {
                client.close();
            }
            client = null;
        }

        return response;
    }
    
    public APIResponse postBody(String requestBody) throws Exception {
    	
    	baseURIBuilder(); 
    	
    	if(!this.uri.isPresent()) {
         	return new APIResponse(new StringBuilder("Invalid Base Url."));
        }
    	
    	appendPathParams();
    	 
        appendQueryParams();

        HttpPost post = new HttpPost(this.uri.get().toString());

        post = appendHeaderToHttpPost(post);
        
        post.setEntity(new StringEntity(requestBody, "utf-8"));

        CloseableHttpClient client = initClient();
        
        APIResponse response = null;

        try {
            response = this.processPostRequest(client, post);
        } finally {

            if (client != null) {
                client.close();
            }
            client = null;
            
        }

        return response;
    }

    private APIResponse processPatchRequest(CloseableHttpClient client, HttpPatch patch) throws IOException {
    	
    	Timer timer = new Timer(true);
    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (patch != null) {
    	        	patch.abort();
    	        } 
    	        timer.cancel();
    	    }
    	};
    	timer.schedule(task, httpClientConfig.getHardTimeout() * 1000);

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

    	Timer timer = new Timer(true);
    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (delete != null) {
    	        	delete.abort();
    	        } 
    	        timer.cancel();
    	    }
    	};
    	timer.schedule(task, httpClientConfig.getHardTimeout() * 1000);
    	
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

    	Timer timer = new Timer(true);
    	
    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (post != null) {
    	        	post.abort();
    	        } 
    	        timer.cancel();
    	    }
    	};
    	timer.schedule(task, httpClientConfig.getHardTimeout() * 1000);
        
        APIResponse apiResponse = null;

        try( CloseableHttpResponse response = client.execute(post);) {

            apiResponse = processResponse(response);

        }catch (Exception e) {
			log.error("Error :{}", e); 
		}

        return apiResponse;
    }

    private APIResponse processGetRequest(CloseableHttpClient client, HttpGet get){

    	Timer timer = new Timer(true);
    	
    	TimerTask task = new TimerTask() {
    	    @Override
    	    public void run() {
    	        if (get != null) {
    	        	get.abort();
    	        } 
    	        timer.cancel();
    	    }
    	};
    	timer.schedule(task, httpClientConfig.getHardTimeout() * 1000);
    	
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
    	
    	APIRequest apiRequest = new APIRequest("https://reqres.in");
    	
    	apiRequest.pathParam("api","users");
    	apiRequest.queryParam("page", "2");

    	try {
    		System.out.println("Start Process"); 
  			APIResponse apiResponse = apiRequest.get();
  			System.out.println("StatusCode: "+apiResponse.getStatus());
  		} catch (Exception e) {
  			log.error("Error ",e); 
  		}
    	
    }
}
