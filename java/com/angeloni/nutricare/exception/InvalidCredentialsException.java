package com.angeloni.nutricare.exception;

public class InvalidCredentialsException extends RuntimeException {

	private static final long serialVersionUID = 3063491296819523090L;

	public InvalidCredentialsException(String message) {
	        super(message);
	    }

	public InvalidCredentialsException(String message, Throwable cause) {
	        super(message, cause);
	    }

	public InvalidCredentialsException(Throwable cause) {
	        super(cause);
	    }
}
