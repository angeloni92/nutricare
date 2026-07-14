package com.angeloni.nutricare.enums;

public enum AIModelEnum {

	GPT3TURBO("GPT3TURBO"),
	GPT4O("GPT4O"),
	OPENAIO1("OPENAIO1"),
	CLAUDE3SONNET("CLAUDE3SONNET"),
	CLAUDE35SONNET("CLAUDE35SONNET"),
	COPILOT_GPT4O("COPILOT_GPT4O");
		
	    private final String value;

	    
	    AIModelEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
