/**
 * @author Mar 10, 2023 10:39:10 PM 
 * 
 */
package org.simpleton.http_client;

import lombok.Getter;
import lombok.ToString;

/**
 * @author tariqul :: Mar 10, 2023 :: 10:39:10 PM 
 *
 */
@ToString
@Getter
public class Status {
	
	private int code;
	
	private String statusMessage;
	
	public Status(int code, String statusMessage) {
		this.code = code;
		this.statusMessage = statusMessage;
	}
	
	public Status(int code) {
		this.code = code;
	}

}
