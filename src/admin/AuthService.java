package admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
	
	public boolean isValidLogin(String username, String password) {
		String sql="SELECT * FROM users WHERE username = ? AND password = ?";
		
		try (Connection conn = application.DataBaseConnection.getConnection();
			PreparedStatement stmt= conn.prepareStatement(sql)){
				
				stmt.setString(1, username);
				stmt.setString(2, password);
				
				ResultSet rs= stmt.executeQuery();
				return rs.next();
		}
		catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
