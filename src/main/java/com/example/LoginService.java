package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.models.Admin;
import com.example.models.LoginCredentials;
import com.example.models.User;

public class LoginService {
	public static Admin checkAdminAccess(LoginCredentials l) {
		try(Connection conn = DBControllerManager.getConnection()){
			PreparedStatement st = conn.prepareStatement("SELECT (email,firstName,lastName,phoneNumber,address,employeeID,position) FROM admin WHERE email = ? and password = ?");
			st.setString(1,l.getEmail());
			st.setString(2,l.getPassword());
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return new Admin(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7));
			}
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static User checkUserLogin(LoginCredentials l) {
		PreparedStatement st=null;
		try(Connection conn = DBControllerManager.getConnection()){
			st = conn.prepareStatement("SELECT email,name,phone FROM users WHERE email = ? and password = ?");
			st.setString(1,l.getEmail());
			st.setString(2,l.getPassword());
			ResultSet rs = st.executeQuery();
			
			if(rs.next()) {
				return new User(rs.getString(1),rs.getString(2),rs.getString(3));
			} return null;
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
}
