/**
 * 
 */
package org.simpleton.http_client.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.simpleton.http_client.Constant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tariqul
 *
 */
@Slf4j

public class URIBuildHelper {

	@Getter
	private String errorMessage;
	
	private String url;
	
	private List<String> pathParams;
	
	private Map<String, String> queryParams;
	
	public URIBuildHelper() {
	}

	/**
	 * 
	 */
	public URIBuildHelper(final String url, final List<String> pathParams, final Map<String, String> queryParams) {
		this.url = url;
		this.pathParams = pathParams;
		this.queryParams = queryParams;
	}
	
	public URI buildURI() {
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			this.appendPathParams(uriBuilder);
			this.appendQueryParams(uriBuilder);
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			errorMessage = "Eroor to build uri from : " + url;
			log.error(Constant.ERROR_LEVEL, e);
		}
		return null;
	}
	
	public URI buildURI(final String url, final List<String> pathParams, final Map<String, String> queryParams) {
		try {
			this.url = url;
			this.pathParams = pathParams;
			this.queryParams = queryParams;
			URIBuilder uriBuilder = new URIBuilder(url);
			this.appendPathParams(uriBuilder);
			this.appendQueryParams(uriBuilder);
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			errorMessage = "Eroor to build uri from : " + url;
			log.error(Constant.ERROR_LEVEL, e);
		}
		return null;
	}

	private void appendPathParams(URIBuilder uriBuilder) {
		List<String> pathSegments = new ArrayList<>(uriBuilder.getPathSegments());
		pathSegments.addAll(this.pathParams); 
		uriBuilder.setPathSegments(pathSegments);
	}

	private void appendQueryParams(URIBuilder uriBuilder) {
		for (Map.Entry<String, String> entry : this.queryParams.entrySet()) {
			uriBuilder.addParameter(entry.getKey(), entry.getValue());
		}
	}

}
