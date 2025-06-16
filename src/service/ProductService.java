package service;

import model.Product;
import java.sql.*;
import java.util.*;

public class ProductService {
	private static final String DB_URL = "jdbc:derby://localhost:1527/C:/Users/sandr/db-derby-10.17.1.0-bin/db-derby-10.17.1.0-bin/bin/Database;create=false;";
    private static final String USER = "SHOPPING_CART";
    private static final String PASSWORD = " ";
    
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM PRODUCTS"; 

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                float price = rs.getFloat("PRICE");
                String imagePath = rs.getString("IMAGE_PATH");
                
                products.add(new Product(id, name, price, imagePath));
            }
        }
        return products;
    }
}