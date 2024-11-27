package com.angeloni.nutricare.enums;

public enum PrimaryGoalEnum {
	
	 WEIGHT_LOSS("Weight loss"),
	 MUSCLE_GAIN("Muscle gain"),
	 ENERGY_IMPROVMENT("Energy improvment"),
	 GENERAL_HEALTH("General health");

	    private final String description;

	    
	    PrimaryGoalEnum(String description) {
	        this.description = description;
	    }

	    
	    public String getDescription() {
	        return description;
	    }

	    
	    public String getRoleName() {
	        return name();
	    }

}
