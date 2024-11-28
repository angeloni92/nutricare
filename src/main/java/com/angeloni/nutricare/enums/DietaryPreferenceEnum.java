package com.angeloni.nutricare.enums;

public enum DietaryPreferenceEnum {
	
	 OMNIVORE("Omnivore"),
	 VEGETARIAN("Vegetarian"),
	 VEGAN("Vegan"),
	 PESCATARIAN("Pescatarian"),
	 KETO("Keto");

	    private final String value;

	    
	    DietaryPreferenceEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
