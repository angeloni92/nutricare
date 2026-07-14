package com.angeloni.nutricare.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static Map<String, Object> parseJson(String json) {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (Exception e) {
			log.error("Error parsing JSON: {}", e.getMessage(), e);
			return Map.of();
		}
	}

	public static String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			log.error("Error converting to JSON: {}", e.getMessage(), e);
			return "{}";
		}
	}
}

