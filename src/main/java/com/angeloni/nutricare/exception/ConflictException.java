package com.angeloni.nutricare.exception;

public class ConflictException extends RuntimeException {

	private static final long serialVersionUID = 3063491296819523090L;

	public ConflictException(String message) {
	        super(message);
	    }

	public ConflictException(String message, Throwable cause) {
	        super(message, cause);
	    }

	public ConflictException(Throwable cause) {
	        super(cause);
	    }
}
