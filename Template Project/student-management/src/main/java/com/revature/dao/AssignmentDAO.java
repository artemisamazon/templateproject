package com.revature.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.model.Assignment;
import com.revature.utility.JDBCUtility;

public class AssignmentDAO {

	public List<Assignment> getAllAssignments() throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			List<Assignment> assignments = new ArrayList<>();
			
			String sql = "SELECT assignment_id, assignment_name, grade, grader_id, author_id FROM assignments";
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("assignment_id");
				String assignmentName = rs.getString("assignment_name");
				int grade = rs.getInt("grade");
				int graderId = rs.getInt("grader_id");
				int authorId = rs.getInt("author_id");
				
				Assignment assignment = new Assignment(id, assignmentName, grade, graderId, authorId);
				
				assignments.add(assignment);
			}
			
			return assignments;
		}
	}
	
	public List<Assignment> getAllAssignmentsByAssociate(int associateId) throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			List<Assignment> assignments = new ArrayList<>();
			
			String sql = "SELECT assignment_id, assignment_name, grade, grader_id, author_id FROM assignments WHERE author_id = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, associateId);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("assignment_id");
				String assignmentName = rs.getString("assignment_name");
				int grade = rs.getInt("grade");
				int graderId = rs.getInt("grader_id");
				int authorId = rs.getInt("author_id");
				
				Assignment assignment = new Assignment(id, assignmentName, grade, graderId, authorId);
				
				assignments.add(assignment);
			}
			
			return assignments;
		}
	}

	public Assignment getAssignmentById(int assignmentId) throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			
			String sql = "SELECT assignment_id, assignment_name, grade, grader_id, author_id FROM assignments WHERE assignment_id= ?";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, assignmentId);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				int id = rs.getInt("assignment_id");
				String assignmentName = rs.getString("assignment_name");
				int grade = rs.getInt("grade");
				int graderId = rs.getInt("grader_id");
				int authorId = rs.getInt("author_id");
				
				return new Assignment(id, assignmentName, grade, graderId, authorId);
			} else {
				return null;
			}
			
		}
	}

	public void changeGrade( int id, int grade, int graderId ) throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			String sql = "UPDATE assignments "
					+ "SET "
					+ "grade = ?, "
					+ "grader_id = ? "
					+ "WHERE assignment_id = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, grade);
			pstmt.setInt(2, graderId);
			pstmt.setInt(3, id);
			
			int updatedCount = pstmt.executeUpdate();
			
			if (updatedCount != 1) {
				throw new SQLException("Something bad occurred when trying to update grade");
			}
		}
		
	}
	
	public Assignment addAssignment(String assignmentName, int authorId, InputStream image) throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			con.setAutoCommit(false); // Turn off autocommit
			
			String sql = "INSERT INTO assignments (assignment_name, author_id, assignment_image)"
					+ " VALUES (?, ?, ?);";
			
			PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, assignmentName);
			pstmt.setInt(2, authorId);
			pstmt.setBinaryStream(3, image);
			
			int numberOfInsertedRecords = pstmt.executeUpdate();
			
			if (numberOfInsertedRecords != 1) {
				throw new SQLException("Issue occurred when adding assignment");
			}
			
			ResultSet rs = pstmt.getGeneratedKeys();
			
			rs.next();
			int generatedId = rs.getInt(1);
			
			con.commit(); // COMMIT
			
			return new Assignment(generatedId, assignmentName, 0, 0, authorId);
		}
	}

	public InputStream getImageFromAssignmentById(int id) throws SQLException {
		try(Connection con = JDBCUtility.getConnection()) {
			String sql = "SELECT assignment_image FROM httpsession_demo.assignments WHERE id = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				InputStream image = rs.getBinaryStream("assignment_image");
				
				return image;
			}
			
			return null;
		}
	}
	
}