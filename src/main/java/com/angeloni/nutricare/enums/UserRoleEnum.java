package com.angeloni.nutricare.enums;

public enum UserRoleEnum {
	
	 USER("User role with basic access"),
	 ADMIN("Admin role with full access");

	    private final String description;

	    
	    UserRoleEnum(String description) {
	        this.description = description;
	    }

	    
	    public String getDescription() {
	        return description;
	    }

	    
	    public String getRoleName() {
	        return name();
	    }

}
