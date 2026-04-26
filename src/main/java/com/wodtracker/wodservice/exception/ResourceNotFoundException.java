package com.wodtracker.wodservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3941099027371514320L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}
