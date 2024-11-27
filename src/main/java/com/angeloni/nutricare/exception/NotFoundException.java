package com.angeloni.nutricare.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3063491296819523090L;

	public NotFoundException(String message) {
	        super(message);
	    }

	public NotFoundException(String message, Throwable cause) {
	        super(message, cause);
	    }

	public NotFoundException(Throwable cause) {
	        super(cause);
	    }
}
