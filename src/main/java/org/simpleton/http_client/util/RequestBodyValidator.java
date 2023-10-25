/**
 * 
 */
package org.simpleton.http_client.util;

import java.util.List;
import java.util.Map;

import org.simpleton.http_client.Attachment;
import org.simpleton.http_client.Constant;
import org.simpleton.http_client.ContentType;

import lombok.Getter;
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
public class RequestBodyValidator {

	@Getter
	private String errorMessage;
	
	public boolean isValidRequestBody(ContentType contentType, String reqeustBody, Map<String, String> formParams, List<Attachment> attachments) {
		if(contentType == null
				&& StringUtil.isBlank(reqeustBody) 
				&& formParams.isEmpty() 
				&& attachments.isEmpty()) {
			return true;
		}
		if(contentType == null) {
			errorMessage = "Reqeust ContentType Is Required";
			log.error(Constant.ERROR_LEVEL, errorMessage); 
			return false;
		}
		if(!StringUtil.isBlank(reqeustBody) 
				&& contentType == ContentType.APPLICATION_JSON) {
			boolean isRequestValidJSON = reqeustBodyIsValidJson(reqeustBody);
			if(!isRequestValidJSON) {
				errorMessage = "Reqeust ContentType Is Not Valid JSON";
				log.error(Constant.ERROR_LEVEL, errorMessage); 
			}
			return isRequestValidJSON;
		}
		return true;
	}
	
	public boolean reqeustBodyIsValidJson(String reqeustBody) {
		JSONUtil jsonUtil = new JSONUtil();
		return jsonUtil.isValidJson(reqeustBody);
	}

}
