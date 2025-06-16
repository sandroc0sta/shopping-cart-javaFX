package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnection {
	private static final String URL = "jdbc:derby://localhost:1527/C:/Users/sandr/db-derby-10.17.1.0-bin/db-derby-10.17.1.0-bin/bin/Database;create=false;";
	private static final String USER = "SHOPPING_CART";
	private static final String PASSWORD = " ";
	private static Connection connection;

	public static Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			//System.out.println("Connecting to database: " + URL);
			connection = DriverManager.getConnection(URL, USER, PASSWORD);

			try (Statement stmt = connection.createStatement()) {
				stmt.execute("SET SCHEMA SHOPPING_CART");

			}
		}
		return connection;
	}

	public static void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				System.out.println("Database connection closed.");
			}
		} catch (SQLException e) {
			System.err.println("Error closing database connection: " + e.getMessage());
		}
	}
}