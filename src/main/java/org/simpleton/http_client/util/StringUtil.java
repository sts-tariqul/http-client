/**
 * 
 */
package org.simpleton.http_client.util;

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
public class StringUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	public static boolean isNotBlank(String string) {
		return null!=string && !string.trim().isEmpty();
	}

	public static boolean isEmpty(String targetString) {
		return null == targetString || targetString.trim().isEmpty();
	}

	public static int length(final CharSequence cs) {
		return cs == null ? 0 : cs.length();
	}

	public static boolean isBlank(final CharSequence cs) {
		final int strLen = length(cs);
		if (strLen == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
