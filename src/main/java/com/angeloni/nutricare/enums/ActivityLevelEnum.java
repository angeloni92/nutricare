package com.angeloni.nutricare.enums;

public enum ActivityLevelEnum {
	
	 SEDENTARY("Sedentary"),
	 MODERATE("Moderate"),
	 ACTIVE("Active"),
	 VERY_ACTIVE("Very active");

	    private final String description;

	    
	    ActivityLevelEnum(String description) {
	        this.description = description;
	    }

	    
	    public String getDescription() {
	        return description;
	    }

	    
	    public String getRoleName() {
	        return name();
	    }

}
