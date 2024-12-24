package com.angeloni.nutricare.enums;

public enum AIModelEnum {

	GPT4O("Gpt-4o"),
	OPENAIO1("OpenAI o1");
		
	    private final String value;

	    
	    AIModelEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
