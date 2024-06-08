package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBControllerManager {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3308/RailwayReservation";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(JDBC_URL, USERNAME,PASSWORD);
	}
}
