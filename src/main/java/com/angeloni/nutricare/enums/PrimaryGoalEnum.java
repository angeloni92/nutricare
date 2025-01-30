package com.angeloni.nutricare.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PrimaryGoalEnum {
	
	 WEIGHT_LOSS("Weight loss"),
	 MUSCLE_GAIN("Muscle gain"),
	 ENERGY_IMPROVMENT("Energy improvment"),
	 GENERAL_HEALTH("General health");

	    private final String value;
	    
	    private static final Map<String, PrimaryGoalEnum> FORMAT_MAP = new HashMap<>();

	    static {
	        for (PrimaryGoalEnum pref : PrimaryGoalEnum.values()) {
	            FORMAT_MAP.put(pref.value.toLowerCase(), pref);
	        }
	    }
	    
	    PrimaryGoalEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }
	    
	    @JsonCreator
	    public static PrimaryGoalEnum fromString(String value) {
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
