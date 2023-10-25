/**
 * 
 */
package org.simpleton.http_client;

import java.io.File;

import lombok.Builder;
import lombok.Getter;
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
@ToString
@Getter
@Setter
@Builder
public class Attachment {
	
	private File  file;
	
	private String title;
	

}
