package com.angeloni.nutricare.exception;

public class AuthException extends RuntimeException {

	private static final long serialVersionUID = -2312266655670761112L;
	
	public AuthException(String message) {
        super(message);
    }

	public AuthException(String message, Throwable cause) {
	        super(message, cause);
	    }
	
	public AuthException(Throwable cause) {
	        super(cause);
	    }

}
