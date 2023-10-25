package org.simpleton.http_client;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author tariqul <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since Oct 26, 2023 :: 1:30:23 AM
 */
@Getter
@Setter
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
	
	public boolean isSuccess() {
		return getCode() >= 200 
				&& getCode() < 300; 
	}
	
	public static Status of(final int code, final String statusMessage) {
		return new Status(code, statusMessage);
	}

}
