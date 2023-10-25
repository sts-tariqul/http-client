/**
 * 
 */
package org.simpleton.http_client.util;

import java.util.Map;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since : Apr 30, 2023 12:39:22 AM
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
