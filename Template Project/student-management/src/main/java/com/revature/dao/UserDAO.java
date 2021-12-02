package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.revature.model.User;
import com.revature.utility.JDBCUtility;

public class UserDAO {

	public User getUserByUsernameAndPassword(String username, String password) throws SQLException {
		try (Connection con = JDBCUtility.getConnection()) {
			String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				int id = rs.getInt("user_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String user = rs.getString("username");
				String pass = rs.getString("password");
				String userRole = rs.getString("user_role");
				
				return new User(id, firstName, lastName, user, pass, userRole);
			} else {
				return null;
			}
		}
	}
	
}