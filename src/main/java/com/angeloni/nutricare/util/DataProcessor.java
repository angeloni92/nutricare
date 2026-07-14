package com.angeloni.nutricare.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataProcessor {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, String> flattenObject(Object obj) {
		try {
			Map<?, ?> map = objectMapper.convertValue(obj, Map.class);
			return flattenMap(map, "");
		} catch (Exception e) {
			log.error("Error flattening object: {}", e.getMessage(), e);
			return new HashMap<>();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> flattenMap(Map<?, ?> map, String prefix) {
		Map<String, String> result = new HashMap<>();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = entry.getKey().toString();
			String fullKey = prefix.isEmpty() ? key : prefix + "_" + key;
			Object value = entry.getValue();

			if (value instanceof Map<?, ?> nestedMap) {
				result.putAll(flattenMap(nestedMap, fullKey));
			} else if (value instanceof Collection<?> collection) {
				int index = 0;
				for (Object item : collection) {
					if (item instanceof Map<?, ?> itemMap) {
						result.putAll(flattenMap(itemMap, fullKey + "_" + index));
					} else {
						result.put(fullKey + "_" + index, nullSafeToString(item));
					}
					index++;
				}
			} else {
				result.put(fullKey, nullSafeToString(value));
			}
		}
		return result;
	}

	public String replacePromptTemplate(String template, Map<String, String> data) {
		String result = template;
		for (Map.Entry<String, String> entry : data.entrySet()) {
			String placeholder = "[" + entry.getKey().toUpperCase() + "]";
			result = result.replace(placeholder, entry.getValue());
		}
		return result;
	}

	private String nullSafeToString(Object value) {
		return value == null ? "" : value.toString();
	}
}

