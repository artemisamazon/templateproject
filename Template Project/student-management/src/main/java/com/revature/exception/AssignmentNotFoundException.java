package com.revature.exception;

public class AssignmentNotFoundException extends Exception {

	public AssignmentNotFoundException() {
		super();
	}

	public AssignmentNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AssignmentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignmentNotFoundException(String message) {
		super(message);
	}

	public AssignmentNotFoundException(Throwable cause) {
		super(cause);
	}

}