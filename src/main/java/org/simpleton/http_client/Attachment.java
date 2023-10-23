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
 * @author tariqul
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
