package com.revature.controller;

import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;

import com.revature.dto.ChangeGradeDTO;
import com.revature.dto.MessageDTO;
import com.revature.model.Assignment;
import com.revature.model.User;
import com.revature.service.AssignmentService;
import com.revature.service.AuthorizationService;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;

public class AssignmentController implements Controller {

	private AuthorizationService authService;
	private AssignmentService assignmentService;
	
	public AssignmentController() {
		this.authService = new AuthorizationService();
		this.assignmentService = new AssignmentService();
	}
	
	private Handler getAssignments = (ctx) -> {
		// guard this endpoint
		// roles permitted: trainer, associate
		User currentlyLoggedInUser = (User) ctx.req.getSession().getAttribute("currentuser");
		this.authService.authorizeAssociateAndTrainer(currentlyLoggedInUser);
		
		// If the above this.authService.authorizeAssociateAndTrainer(...) method did not throw an exception, that means
		// our program will continue to proceed to the below functionality
		List<Assignment> assignments = this.assignmentService.getAssignments(currentlyLoggedInUser);
		
		ctx.json(assignments);
	};
	
	// Who should be able to access this endpoint?
	// only trainers
	private Handler changeGrade = (ctx) -> {
		User currentlyLoggedInUser = (User) ctx.req.getSession().getAttribute("currentuser");
		this.authService.authorizeTrainer(currentlyLoggedInUser);
		
		String assignmentId = ctx.pathParam("assignment_id");
		ChangeGradeDTO dto = ctx.bodyAsClass(ChangeGradeDTO.class); // Taking the request body -> putting the data into a new object
		
		Assignment changedAssignment = this.assignmentService.changeGrade(currentlyLoggedInUser, assignmentId, dto.getGrade());
		ctx.json(changedAssignment);
	};
	
	private Handler addAssignment = (ctx) -> {
		// Protect endpoint
		User currentlyLoggedInUser = (User) ctx.req.getSession().getAttribute("currentuser");
		this.authService.authorizeAssociate(currentlyLoggedInUser);
		
		
		String assignmentName = ctx.formParam("assignment_name");
		
		/*
		 * Extracting file from HTTP Request
		 */
		UploadedFile file = ctx.uploadedFile("assignment_image");
		
		if (file == null) {
			ctx.status(400);
			ctx.json(new MessageDTO("Must have an image to upload"));
			return;
		}
		
		InputStream content = file.getContent(); // This is the most important. It is the actual content of the file

		Tika tika = new Tika();
		
		// We want to disallow users from uploading files that are not jpeg, gif, or png
		// So, in the controller layer, figure out the MIME type of the file
		// jpeg = image/jpeg
		// gif = image/gif
		// png = image/png
		String mimeType = tika.detect(content);
		
		// Service layer invocation
		Assignment addedAssignment = this.assignmentService.addAssignment(currentlyLoggedInUser, mimeType, assignmentName, content);
		ctx.json(addedAssignment);
		ctx.status(201);
	};
	
	private Handler getImageFromAssignmentById = (ctx) -> {
		// protect endpoint
		User currentlyLoggedInUser = (User) ctx.req.getSession().getAttribute("currentuser");
		this.authService.authorizeAssociateAndTrainer(currentlyLoggedInUser);
		
		String assignmentId = ctx.pathParam("id");
		
		InputStream image = this.assignmentService.getImageFromAssignmentById(currentlyLoggedInUser, assignmentId);
		
		Tika tika = new Tika();
		String mimeType = tika.detect(image);
		
		ctx.contentType(mimeType); // specifying to the client what the type of the content actually is
		ctx.result(image); // Sending the image back to the client
	};
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.get("/assignments", getAssignments);
		app.patch("/assignments/{assignment_id}/grade", changeGrade);
		app.post("/assignments", addAssignment);
		app.get("/assignments/{assignment_id}/image", getImageFromAssignmentById);
	}

}