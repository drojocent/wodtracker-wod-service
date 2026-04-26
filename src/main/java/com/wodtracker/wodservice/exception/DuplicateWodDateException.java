package com.wodtracker.wodservice.exception;

public class DuplicateWodDateException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2127971069516561427L;

	public DuplicateWodDateException(String message) {
        super(message);
    }
}
