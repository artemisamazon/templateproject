package com.revature.model;

import java.util.Objects;

public class Assignment {

	private int id;
	private String assignmentName;
	private int grade;
	private int graderId;
	private int authorId;
	
	public Assignment() {
		super();
	}

	public Assignment(int id, String assignmentName, int grade, int graderId, int authorId) {
		super();
		this.id = id;
		this.assignmentName = assignmentName;
		this.grade = grade;
		this.graderId = graderId;
		this.authorId = authorId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getGraderId() {
		return graderId;
	}

	public void setGraderId(int graderId) {
		this.graderId = graderId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assignmentName, authorId, grade, graderId, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assignment other = (Assignment) obj;
		return Objects.equals(assignmentName, other.assignmentName) && authorId == other.authorId
				&& grade == other.grade && graderId == other.graderId && id == other.id;
	}

	@Override
	public String toString() {
		return "Assignment [id=" + id + ", assignmentName=" + assignmentName + ", grade=" + grade + ", graderId="
				+ graderId + ", authorId=" + authorId + "]";
	}

}