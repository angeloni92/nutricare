package com.angeloni.nutricare.enums;

public enum PrimaryGoalEnum {
	
	 WEIGHT_LOSS("Weight loss"),
	 MUSCLE_GAIN("Muscle gain"),
	 ENERGY_IMPROVMENT("Energy improvment"),
	 GENERAL_HEALTH("General health");

	    private final String value;

	    
	    PrimaryGoalEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }
}
