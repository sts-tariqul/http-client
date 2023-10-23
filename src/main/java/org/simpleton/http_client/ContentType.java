/**
 * 
 */
package org.simpleton.http_client;

import lombok.Getter;

/**
 * @author tariqul
 *
 */
@Getter
public enum ContentType {

	APPLICATION_JSON("application/json"), APPLICATION_XML("application/xml"), TEXT_HTML("text/html"),
	TEXT_PLAIN("text/plain"), APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
	MULTIPART_FORM_DATA("multipart/form-data");

	private String value;

	ContentType(String value) {
		this.value = value;
	}

	public boolean isFormType() {
		return this == ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
	}

	public boolean isMultiPartType() {
		return this == ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
	}

	public boolean isTextType() {
		return this == ContentType.TEXT_PLAIN;
	}
	
	public boolean isJSONType() {
		return this == ContentType.APPLICATION_JSON;
	}
	
	public boolean isHtmlType() {
		return this == ContentType.TEXT_HTML;
	}
	
	public boolean isXMLType() {
		return this == ContentType.APPLICATION_XML;
	}

	public static ContentType valueOfIgnoreCase(String value) {
		for (ContentType e : ContentType.values()) {
			if (e.getValue().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return null; // Return null if the enum constant is not found
	}

}
