package com.angeloni.nutricare.enums;

public enum DietaryPreferenceEnum {
	
	 OMNIVORE("Omnivore"),
	 VEGETARIAN("Vegetarian"),
	 VEGAN("Vegan"),
	 PESCATARIAN("Pescatarian"),
	 KETO("Keto");

	    private final String description;

	    
	    DietaryPreferenceEnum(String description) {
	        this.description = description;
	    }

	    
	    public String getDescription() {
	        return description;
	    }

	    
	    public String getRoleName() {
	        return name();
	    }

}
