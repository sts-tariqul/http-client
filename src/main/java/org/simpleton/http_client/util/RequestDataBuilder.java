/**
 * 
 */
package org.simpleton.http_client.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.simpleton.http_client.Attachment;
import org.simpleton.http_client.Constant;
import org.simpleton.http_client.ContentType;

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
public class RequestDataBuilder {

	public void buildRequestData(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, ContentType contentType,
			Map<String, String> formParams, String requestBody, List<Attachment> attachments) {
		if(null == contentType) {
			return;
		}
		switch (contentType) {
			case APPLICATION_X_WWW_FORM_URLENCODED:
				appendFormParams(httpEntityEnclosingRequestBase, formParams);
				break;
			case MULTIPART_FORM_DATA:
				appendMultiPartFormData(httpEntityEnclosingRequestBase, formParams, attachments);
				break;
			case APPLICATION_JSON, APPLICATION_XML, TEXT_PLAIN, TEXT_HTML:
				appendTextBody(httpEntityEnclosingRequestBase, requestBody);
				break;
			default:
				log.error(Constant.ERROR_LEVEL, "Invalid request body type"); 
				break;
		}

	}

	private void appendMultiPartFormData(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase,
			Map<String, String> formParams, List<Attachment> attachments) {

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		for (Map.Entry<String, String> entry : formParams.entrySet()) {

			if (StringUtil.isNotBlank(entry.getKey()) && StringUtil.isNotBlank(entry.getValue())) {
				builder.addTextBody(entry.getKey(), entry.getValue());
			}
		}

		for (Attachment attachment : attachments) {
			builder.addBinaryBody(attachment.getTitle(), attachment.getFile());
		}

		HttpEntity multipart = builder.build();

		httpEntityEnclosingRequestBase.setEntity(multipart);

	}

	private void appendTextBody(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, String requestBody) {
		httpEntityEnclosingRequestBase.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
	}

	private void appendFormParams(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase,
			Map<String, String> formParams) {
		List<NameValuePair> formParameters = new ArrayList<>();

		for (Map.Entry<String, String> entry : formParams.entrySet()) {
			formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(formParameters, StandardCharsets.UTF_8));
	}

}
