package com.angeloni.nutricare.enums;

public enum AIModelEnum {

	GPT4O("gpt-4o");
		
	    private final String value;

	    
	    AIModelEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
