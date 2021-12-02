package com.revature.exception;

public class AssignmentImageNotFoundException extends Exception {

	public AssignmentImageNotFoundException() {
		super();
	}

	public AssignmentImageNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AssignmentImageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignmentImageNotFoundException(String message) {
		super(message);
	}

	public AssignmentImageNotFoundException(Throwable cause) {
		super(cause);
	}

}