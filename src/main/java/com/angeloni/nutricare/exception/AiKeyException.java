package com.angeloni.nutricare.exception;

public class AiKeyException extends RuntimeException {

	private static final long serialVersionUID = 3063491296819523090L;

	public AiKeyException(String message) {
	        super(message);
	    }

	public AiKeyException(String message, Throwable cause) {
	        super(message, cause);
	    }

	public AiKeyException(Throwable cause) {
	        super(cause);
	    }
}
