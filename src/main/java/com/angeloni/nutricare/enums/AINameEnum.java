package com.angeloni.nutricare.enums;

public enum AINameEnum {

	 CHATGPT("CHATGPT"),
	 CLAUDE("CLAUDE"),
	 GITHUB_COPILOT("GITHUB_COPILOT");
		
	    private final String value;

	    
	    AINameEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
