/**
 * 
 */
package org.simpleton.http_client;

import org.simpleton.http_client.util.EnviornmentUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since : Apr 30, 2023 12:39:22 AM
 *
 */
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HttpClientConfig {

	@Setter
	private Integer connectTimeout;
	
	@Setter
	private Integer connectionRequestTimeout;
	
	@Setter
	private Integer socketTimeout;
	
	@Setter
	private Integer hardTimeout;
	
	public int getConnectTimeout() {
		if(null == connectTimeout) {
			connectTimeout = EnviornmentUtil.getEnvVar("CONNECT_TIMEOUT", 30); 
		}
		return connectTimeout;
	}
	
	public int getConnectionRequestTimeout() {
		if(null == connectionRequestTimeout) {
			connectionRequestTimeout = EnviornmentUtil.getEnvVar("CONNECTION_REQUEST_TIMEOUT", 30); 
		}
		return connectionRequestTimeout;
	}
	
	public int getSocketTimeout() {
		if(null == socketTimeout) {
			socketTimeout = EnviornmentUtil.getEnvVar("SOCKET_TIMEOUT", 30); 
		}
		return socketTimeout;
	}
	
	public int getHardTimeout() {
		if(null == hardTimeout) {
			hardTimeout = EnviornmentUtil.getEnvVar("HARD_TIMEOUT", 60); 
		}
		return hardTimeout;
	}
	
	
	public void print() {
		log.info("CONNECT_TIMEOUT :: {} ", connectTimeout); 
		log.info("CONNECTION_REQUEST_TIMEOUT :: {} ", connectionRequestTimeout); 
		log.info("SOCKET_TIMEOUT :: {} ", socketTimeout); 
		log.info("HARD_TIMEOUT :: {} ", hardTimeout); 
		
	}
	
	public static void main(String[] args ) { 
		 
	    HttpClientConfig httpClientConfig = HttpClientConfig.builder().build();
	    System.out.println(httpClientConfig);
	}

}
