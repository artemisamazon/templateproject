package com.revature.service;

import com.revature.exception.UnauthorizedException;
import com.revature.model.User;

// Authentication is different than Authorization
// Authentication is about providing credentials to identify who you are
// Authorization is about checking whether you have the permissions to access a particular thing
public class AuthorizationService {

	public void authorizeAssociateAndTrainer(User user) throws UnauthorizedException {
		// If the user is not either a regular role or admin role
		if (user == null || !(user.getUserRole().equals("associate") || user.getUserRole().equals("trainer"))) {
			throw new UnauthorizedException("You must be logged in and have a role of associate or trainer for this resource");
		}
	}
	
	public void authorizeTrainer(User user) throws UnauthorizedException {
		if (user == null || !user.getUserRole().equals("trainer")) {
			throw new UnauthorizedException("You must be logged in and have a role of trainer for this resource");
		}
	}

	public void authorizeAssociate(User user) throws UnauthorizedException {
		// if we are not logged in, or do not have a role of associate
		if (user == null || !user.getUserRole().equals("associate")) {
			throw new UnauthorizedException("You must be logged in and have a role of associate for this resource");
		}
	}
	
}