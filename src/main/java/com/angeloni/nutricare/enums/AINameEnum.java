package com.angeloni.nutricare.enums;

public enum AINameEnum {

	 CHATGPT("ChatGPT");
		
	    private final String value;

	    
	    AINameEnum(String value) {
	        this.value = value;
	    }

	    
	    public String getValue() {
	        return value;
	    }

}
