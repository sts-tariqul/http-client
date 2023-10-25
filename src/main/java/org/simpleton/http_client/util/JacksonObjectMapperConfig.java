/**
 * 
 */
package org.simpleton.http_client.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NoArgsConstructor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
 * <br>
 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
 * <br>
 * @since : Apr 30, 2023 12:39:22 AM
 *
 */
@NoArgsConstructor
public class JacksonObjectMapperConfig {
	
	public ObjectMapper getObjectMapper() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	    return objectMapper;
	}
	
	/**
	 * Get the Jackson CollectionType for a list of objects of the given class.
	 * @author <a href="https://www.linkedin.com/in/tariqulislam">Tariqul Islam</a>
	 * <br>
	 * Email: <a href="mailto:sumon.screenfusion@gmail.com">sumon.screenfusion@gmail.com</a>
	 * <br>
	 * @since : Apr 30, 2023 12:39:22 AM
	 * @param clazz The class of the objects in the list.
	 * @return The CollectionType.
	 */
	public CollectionType getListType(Class<?> clazz) {
	    return getObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, clazz);
	}


}
