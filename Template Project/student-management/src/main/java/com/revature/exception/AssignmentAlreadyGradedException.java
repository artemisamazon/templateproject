package com.revature.exception;

public class AssignmentAlreadyGradedException extends Exception {

	public AssignmentAlreadyGradedException() {
		super();
	}

	public AssignmentAlreadyGradedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AssignmentAlreadyGradedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignmentAlreadyGradedException(String message) {
		super(message);
	}

	public AssignmentAlreadyGradedException(Throwable cause) {
		super(cause);
	}

}