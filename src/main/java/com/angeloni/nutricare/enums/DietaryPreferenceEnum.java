package com.angeloni.nutricare.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DietaryPreferenceEnum {
	
	 OMNIVORE("Omnivore"),
	 VEGETARIAN("Vegetarian"),
	 VEGAN("Vegan"),
	 PESCATARIAN("Pescatarian"),
	 KETO("Keto");

	    private final String value;
	    
	    private static final Map<String, DietaryPreferenceEnum> FORMAT_MAP = new HashMap<>();

	    static {
	        for (DietaryPreferenceEnum pref : DietaryPreferenceEnum.values()) {
	            FORMAT_MAP.put(pref.value.toLowerCase(), pref);
	        }
	    }

	    
	    DietaryPreferenceEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }
	    
	    @JsonCreator
	    public static DietaryPreferenceEnum fromString(String value) {
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
