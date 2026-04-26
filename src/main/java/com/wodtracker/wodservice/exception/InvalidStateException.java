package com.wodtracker.wodservice.exception;

public class InvalidStateException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4304961664314023408L;

	public InvalidStateException(String message) {
        super(message);
    }
}
