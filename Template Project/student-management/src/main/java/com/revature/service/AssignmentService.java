package com.revature.service;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.revature.dao.AssignmentDAO;
import com.revature.exception.AssignmentAlreadyGradedException;
import com.revature.exception.AssignmentImageNotFoundException;
import com.revature.exception.AssignmentNotFoundException;
import com.revature.exception.UnauthorizedException;
import com.revature.model.Assignment;
import com.revature.model.User;

public class AssignmentService {

	private AssignmentDAO assignmentDao;
	
	public AssignmentService() {
		this.assignmentDao = new AssignmentDAO();
	}
	
	public AssignmentService(AssignmentDAO assignmentDao) {
		this.assignmentDao = assignmentDao;
	}
	
	// If the currently logged in User is an associate, we should only grab the assignments that belong to that associate
	// If the currently logged in User is a trainer, we should grab ALL assignments
	public List<Assignment> getAssignments(User currentlyLoggedInUser) throws SQLException {
		List<Assignment> assignments = null;
		
		if (currentlyLoggedInUser.getUserRole().equals("trainer")) {
			assignments = this.assignmentDao.getAllAssignments();
		} else if (currentlyLoggedInUser.getUserRole().equals("associate")) {
			assignments = this.assignmentDao.getAllAssignmentsByAssociate(currentlyLoggedInUser.getId());
		}
		
		return assignments;
	}

	// We only want to be able to assign a grade once
	// Once we have already graded something, it is permanent, you can't change it from there
	
	// 0. Check if the assignment exists or not
	// 1. Check if the assignment already has a grade
	// 		- if it does, throw an AlreadyGradedException
	// 2. If it doesn't already have a grade, proceed onwards to assign a grade to the assignment
	public Assignment changeGrade(User currentlyLoggedInUser, String assignmentId, int grade) throws SQLException, AssignmentNotFoundException, AssignmentAlreadyGradedException {
		try {
			int id = Integer.parseInt(assignmentId);
			
			Assignment assignment = this.assignmentDao.getAssignmentById(id);
			
			// 0
			if (assignment == null) {
				throw new AssignmentNotFoundException("Assignment with id " + assignmentId + " was not found");
			}
			
			// 1
			if (assignment.getGraderId() == 0) { // if it's 0, it means there's no grader for the assignment yet
				this.assignmentDao.changeGrade(id, grade, currentlyLoggedInUser.getId());
			} else { // if it has already been graded by someone, and we're trying to change the grade here, that shouldn't be allowed
				throw new AssignmentAlreadyGradedException("Assignment has already been graded, so we cannot assign another grade to the"
						+ " assignment");
			}
			
			return this.assignmentDao.getAssignmentById(id);
		} catch(NumberFormatException e) {
			throw new InvalidParameterException("Assignment id supplied must be an int");
		}
		
		
	}

	// Business logic
	// check if the mimetype is either image/jpeg, image/png, image/gif
	// and if not, throw a InvalidParameterException
	public Assignment addAssignment(User currentlyLoggedInUser, String mimeType, String assignmentName, InputStream content) throws SQLException {
		Set<String> allowedFileTypes = new HashSet<>();
		allowedFileTypes.add("image/jpeg");
		allowedFileTypes.add("image/png");
		allowedFileTypes.add("image/gif");
		
		if (!allowedFileTypes.contains(mimeType)) {
			throw new InvalidParameterException("When adding an assignment image, only PNG, JPEG, or GIF are allowed");
		}
		
		// Author, assignment name, file content (bytes, 0s and 1s)
		int authorId = currentlyLoggedInUser.getId(); // whoever is logged in, will be the actual author of the assignment
		
		Assignment addedAssignment = this.assignmentDao.addAssignment(assignmentName, authorId, content);
		
		return addedAssignment;
	}

	// Business logic
	// Trainers will be able to view any images that belong to anybody
	// Associates can only view images for assignments that belong to them
	public InputStream getImageFromAssignmentById(User currentlyLoggedInUser, String assignmentId) throws SQLException, UnauthorizedException, AssignmentImageNotFoundException {
		
		try {
			int id = Integer.parseInt(assignmentId);
			
			// Check if they are an associate
			if (currentlyLoggedInUser.getUserRole().equals("associate")) {
				// Grab all of the assignments that belong to the associate
				int associateId = currentlyLoggedInUser.getId();
				List<Assignment> assignmentsThatBelongToAssociate = this.assignmentDao.getAllAssignmentsByAssociate(associateId);
				
				Set<Integer> assignmentIdsEncountered = new HashSet<>();
				for (Assignment a : assignmentsThatBelongToAssociate) {
					assignmentIdsEncountered.add(a.getId());
				}
				
				// Check to see if the image they are trying to grab for a particular assignment is actually their own assignment
				if (!assignmentIdsEncountered.contains(id)) {
					throw new UnauthorizedException("You cannot access the images of assignments that do not belong to yourself");
				}
			}
			
			// Grab the image from the DAO
			InputStream image = this.assignmentDao.getImageFromAssignmentById(id);
			
			if (image == null) {
				throw new AssignmentImageNotFoundException("Image was not found for assignment id " + id);
			}
			
			return image;
			
		} catch(NumberFormatException e) {
			throw new InvalidParameterException("Assignment id supplied must be an int");
		}
		
	}
	
}