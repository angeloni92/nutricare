package com.angeloni.nutricare.exception;

public class QueueException extends RuntimeException {

	private static final long serialVersionUID = 3063491296819523090L;

	public QueueException(String message) {
	        super(message);
	    }

	public QueueException(String message, Throwable cause) {
	        super(message, cause);
	    }

	public QueueException(Throwable cause) {
	        super(cause);
	    }
}
