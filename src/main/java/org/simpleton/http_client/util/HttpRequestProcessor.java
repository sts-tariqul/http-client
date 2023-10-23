/**
 * 
 */
package org.simpleton.http_client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.simpleton.http_client.APIResponse;
import org.simpleton.http_client.Constant;
import org.simpleton.http_client.HttpClientConfig;
import org.simpleton.http_client.Status;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tariqul
 *
 */
@Slf4j
public class HttpRequestProcessor {

	public APIResponse processRequest(CloseableHttpClient client, HttpRequestBase httpRequestBase)
			throws IOException {

		Timer timer = new Timer(true);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (httpRequestBase != null) {
					httpRequestBase.abort();
				}
				timer.cancel();
			}
		};
		HttpClientConfig httpClientConfig = HttpClientConfig.builder().build();
		timer.schedule(task, (httpClientConfig.getHardTimeout() * Constant.ONE_SECOND_MILLISECOND));

		APIResponse apiResponse = null;

		try (CloseableHttpResponse response = client.execute(httpRequestBase);) {

			apiResponse = processResponse(response);

		} catch (Exception e) {
			apiResponse = new APIResponse(Status.of(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage())); 
			apiResponse.response(new StringBuilder().append(e.getMessage()));
			log.error(Constant.ERROR_LEVEL, e);
		}

		return apiResponse;
	}
	
	public APIResponse processRequest(CloseableHttpClient client, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase)
			throws IOException {

		Timer timer = new Timer(true);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (httpEntityEnclosingRequestBase != null) {
					httpEntityEnclosingRequestBase.abort();
				}
				timer.cancel();
			}
		};
		HttpClientConfig httpClientConfig = HttpClientConfig.builder().build();
		timer.schedule(task, (httpClientConfig.getHardTimeout() * Constant.ONE_SECOND_MILLISECOND));

		APIResponse apiResponse = null;

		try (CloseableHttpResponse response = client.execute(httpEntityEnclosingRequestBase);) {

			return processResponse(response);

		} catch (Exception e) {
			apiResponse = new APIResponse(Status.of(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage())); 
			apiResponse.response(new StringBuilder().append(e.getMessage()));
			log.error(Constant.ERROR_LEVEL, e);
		}

		return apiResponse;
	}

	private APIResponse processResponse(CloseableHttpResponse response) {

		APIResponse apiResponse = new APIResponse(new Status(response.getStatusLine().getStatusCode()));

		BufferedReader bufferedReader = null;

		try {

			if (null != response.getEntity() && null != response.getEntity().getContent()) {
				bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}

				apiResponse.response(stringBuilder);

				Header contentTypeHeader = response.getEntity().getContentType();

				if (null != contentTypeHeader) {
					apiResponse.contentType(contentTypeHeader.getValue());
				}
			}

			for (Header header : response.getAllHeaders()) {
				apiResponse.responseHeader(header.getName(), header.getValue());
			}

		} catch (UnsupportedOperationException e) {
			apiResponse.response(new StringBuilder(e.getMessage()));
			log.error("ProcessResponse UnsupportedOperationException Error ", e);
		} catch (IOException e) {
			apiResponse.response(new StringBuilder(e.getMessage()));
			log.error("ProcessResponse IOException Error ", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					apiResponse.response(new StringBuilder(e.getMessage()));
					log.error("ProcessResponse IOException Error ", e);
				}
			}
		}

		return apiResponse;
	}

}
