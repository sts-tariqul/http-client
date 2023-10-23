/**
 * 
 */
package org.simpleton.http_client.util;

import java.util.Map;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tariqul
 *
 */
@Slf4j
public class HttpHeaderBuilderHelper {

	public void appendHeader(HttpRequestBase httpRequestBase, final Map<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpRequestBase.addHeader(entry.getKey(), entry.getValue());
		}
	}

	public void appendHeader(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, final Map<String, String> headers) {
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			httpEntityEnclosingRequestBase.addHeader(entry.getKey(), entry.getValue());
		}
	}


}
