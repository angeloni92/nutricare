package com.angeloni.nutricare.config;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Configuration
public class CommonTestConfig {
	
	@Bean
	@Primary
	public Gson gson() {
		return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
				.registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
				.registerTypeAdapter(LocalDate.class, new LocalDateTimeSerializer()).create();
	}
	
	public static class LocalDateSerializer implements JsonSerializer<LocalDate> {
		public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
		}
	}
	
	public static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
		public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
			LocalDate returnValue = null;
			if(!json.isJsonNull()) {
				returnValue = LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
			}
			return returnValue;
		}
	}
	
	public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
		public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		}
	}
	
	public static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
		public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
			LocalDateTime returnValue = null;
			if(!json.isJsonNull()) {
				returnValue = LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			}
			return returnValue;
		}
	}

}
