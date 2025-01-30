package com.angeloni.nutricare.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityLevelEnum {
	
	 SEDENTARY("Sedentary"),
	 MODERATE("Moderate"),
	 ACTIVE("Active"),
	 VERY_ACTIVE("Very active");

	    private final String value;
	    
	    private static final Map<String, ActivityLevelEnum> FORMAT_MAP = new HashMap<>();

	    
	    ActivityLevelEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

	    static {
	        for (ActivityLevelEnum level : ActivityLevelEnum.values()) {
	            FORMAT_MAP.put(level.value.toLowerCase(), level); 
	        }
	    }

	    @JsonCreator
	    public static ActivityLevelEnum fromString(String value) {
	        if (value == null) {
	            return null;
	        }
	        return FORMAT_MAP.get(value.toLowerCase()); 
	    }

	    @JsonValue
	    public String toValue() {
	        return value; 
	    }

}
