package com.revature.dto;

import java.util.Objects;

public class ChangeGradeDTO {

	private int grade;

	public ChangeGradeDTO() {
		super();
	}

	public ChangeGradeDTO(int grade) {
		super();
		this.grade = grade;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	@Override
	public int hashCode() {
		return Objects.hash(grade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeGradeDTO other = (ChangeGradeDTO) obj;
		return grade == other.grade;
	}

	@Override
	public String toString() {
		return "ChangeGradeDTO [grade=" + grade + "]";
	}
	
}