package com.railway.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import com.railway.DBManager;

public class UserService {
	// getProfile - get the profile details when the username is given
	// checkPassword - checks if the username and password are valid
	// setUserProfile - updates the profile 
	// setPassword - sets the new password after validating the old password and username;
	// registerUser - used to register the user profile username and password
	
	public static JSONObject getUserProfile(String username) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement(
					"SELECT first_name,last_name,date_of_birth,gender,address from users where user_id = ?");
			p.setString(1, username);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				j.put("firstname", rs.getString(1));
				j.put("lastname", rs.getString(2));
				j.put("dob", rs.getString(3));
				j.put("gender", rs.getString(4));
				j.put("address", rs.getString(5));
			} else {
				j.put("success", false);
				j.put("message", "The profile for the email is not found");
			}
			rs.close();
			p.close();
			
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static byte checkPassword(String username, String pass) {

		/// return values
		/// 1 - password is correct for the user
		/// 0 - password is incorrect for the user
		/// -1 - username is not present
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement("SELECT password from users where user_id = ?");
			p.setString(1, username);
			ResultSet rs = p.executeQuery();
			if (rs.next()) {
				String password = rs.getString(1);
				System.out.println(password);
				
				if (password.equals(pass)) {
					return 1;
				} else {
					return 0;
				}
			}
			rs.close();
			p.close();
			return -1;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static JSONObject setUserProfile(String username, String firstname, String lastname, String gender,
			String dateOfBirth, String address) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement(
					"UPDATE users SET first_name=?,last_name=?,gender=?,date_of_birth=Date(?),address=? where user_id = ?");
			p.setString(1, firstname);
			p.setString(2, lastname);
			p.setString(3, gender);
			p.setString(4, dateOfBirth);
			p.setString(5, address);
			p.setString(6, username);
			int rs = p.executeUpdate();
			if (rs > 0) {
				j.put("success", true);
				j.put("message", "User Profile updated successfully");
			}
			p.close();
			System.out.print(j.toString());
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject setPassword(String username, String oldPassword, String newPassword) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {

			if (checkPassword(username, oldPassword) <= 0) {
				j.put("success", false);
				j.put("message", "Invalid Username or password");
				return j;
			}
			c = DBManager.getConnection();
			PreparedStatement p = c.prepareStatement("UPDATE users SET password=? where user_id = ?");
			p.setString(1, newPassword);
			p.setString(2, username);
			int rs = p.executeUpdate();
			if (rs > 0) {
				j.put("success", true);
				j.put("message", "Password updated successfully");
			}
			p.close();
//			System.out.print(j.toString());
//			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			if (c == null) {
		        return j;
		    }
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}

	public static JSONObject registerUser(String username, String password, String firstname, String lastname,
			String gender, String dateOfBirth, String address) {
		JSONObject j = new JSONObject();
		Connection c = null;
		try {
			c = DBManager.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT user_id from users where user_id=?");
			s.setString(1, username);
			ResultSet r = s.executeQuery();
			if (r.next()) {
				j.put("success", false);
				j.put("message", "User already exists");
				return j;
			}
			PreparedStatement p = c.prepareStatement(
					"INSERT INTO users(user_id,password,first_name,last_name,gender,date_of_birth,address) values (?,?,?,?,?,Date(?),?)");

			p.setString(1, username);
			p.setString(2, password);
			p.setString(3, firstname);
			p.setString(4, lastname);
			p.setString(5, gender);
			p.setString(6, dateOfBirth);
			p.setString(7, address);
			int rs = p.executeUpdate();
			if (rs > 0) {
				j.put("success", true);
				j.put("message", "User Registered successfully");
			}
			p.close();
//			System.out.print(j.toString());
			return j;
		} catch (ClassNotFoundException e) {
			j.put("success", false);
			j.put("message", "Internal Server error");
			e.printStackTrace();
		} catch (SQLException e) {
			j.put("success", false);
			j.put("message", "DB Error");
			e.printStackTrace();
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return j;
	}
}
