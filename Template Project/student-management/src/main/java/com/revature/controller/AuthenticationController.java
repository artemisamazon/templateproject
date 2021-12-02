package com.revature.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.revature.dto.LoginDTO;
import com.revature.dto.MessageDTO;
import com.revature.model.User;
import com.revature.service.UserService;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class AuthenticationController implements Controller {

	// Whenever you work on an endpoint, you should ask yourself, what information do I need to send to the server?
	// For logging in, we need to send
	// 1. username
	// 2. password
	// Where should this information be contained inside the HTTP request?
		// 	1. Request body (JSON) <- WE CHOOSE JSON (Request body)
		//  2. Path parameters
		//  3. Query parameters
		//  4. Form-data
	// How do I receive this information inside of my endpoint "handler"?
		// If it's the JSON in the request body -> ctx.bodyAsClass
		// If it's a path parameter -> ctx.pathParam(<param name>)
		// If it's a query parameter -> ctx.queryParam(<query param name>)
		// If it's form data -> ctx.formParam(<key name>)
	
	private UserService userService;
	
	public AuthenticationController() {
		this.userService = new UserService();
	}
	
	private Handler login = (ctx) -> {
		LoginDTO loginDTO = ctx.bodyAsClass(LoginDTO.class);
		
		User user = this.userService.getUserByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
		
		// HttpSession is how we track which "client" (client-server) is sending subsequent requests to the server
		// An HttpSession is associated through the use of cookies
		// Whenever an HttpSession object is created for a certain request, a cookie will then be sent to the client from the server
		// The client will then send this cookie along with any subsequent requests they make to the server in order to identify themselves
		
		// When it comes to logging in, if we logged in successfully, we should create an HttpSession.
		// This will essentially automatically bind the client to any subsequent requests. That is how we can know who is logged in or not.
		
		// HttpSession objects that are identified for a certain client (through cookies) can contain different attributes
		// These attributes are key-value pairs, where the value can be any type of object
		// In our case here, we create an attribute called currentuser and store a User object inside it
		
		// So, for any subsequent requests to the server, we can grab the value of this attribute and use it to determine whether they should
		// be able to access an endpoint or not
		HttpServletRequest req = ctx.req;
	
		HttpSession session = req.getSession();
		session.setAttribute("currentuser", user);
			
		ctx.json(user);
	};
	
	private Handler logout = (ctx) -> {
		HttpServletRequest req = ctx.req;
		
		req.getSession().invalidate();
	};
	
	private Handler checkIfLoggedIn = (ctx) -> {
		HttpSession session = ctx.req.getSession();
		
		// Check if session.getAttribute("currentuser"); is null or not
		if (!(session.getAttribute("currentuser") == null)) {
			ctx.json(session.getAttribute("currentuser"));
			ctx.status(200);
		} else {
			ctx.json(new MessageDTO("User is not logged in"));
			ctx.status(401);
		}
	};

	@Override
	public void mapEndpoints(Javalin app) {
		app.post("/login", login);
		app.post("/logout", logout);
		app.get("/checkloginstatus", checkIfLoggedIn);
	}
		
}