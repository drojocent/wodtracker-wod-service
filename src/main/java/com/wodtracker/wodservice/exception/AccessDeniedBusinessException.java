package com.wodtracker.wodservice.exception;

public class AccessDeniedBusinessException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3270162434836929992L;

	public AccessDeniedBusinessException(String message) {
        super(message);
    }
}
