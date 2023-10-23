package org.simpleton.http_client.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleton.http_client.APIRequest;
import org.simpleton.http_client.APIResponse;

/**
 * 
 * @author tariqul
 *
 */
class APIRequestTest {

	private APIRequest apiRequest;
	
	public APIRequestTest() {
		
	}
	
	/**
	 * @author tariqul
	 */
	@Test
	@DisplayName("GET")
	void getTest() {
		
		String url = "https://webhook.site/cfa5fced-d9df-4041-bff6-3154c8fereewrewe074f";
		this.apiRequest = new APIRequest(url);
		
		APIResponse apiResponse = apiRequest.get();
		assertTrue(404 == apiResponse.getStatus().getCode());
	}

		
}
