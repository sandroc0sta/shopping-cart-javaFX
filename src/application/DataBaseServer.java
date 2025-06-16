package application;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.drda.NetworkServerControl;

public class DataBaseServer {

    private static NetworkServerControl server;

    public static void startServer() {
        try {
            server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
            // Start the server in a separate thread so it doesn't block the main thread
            new Thread(() -> {
                try {
                    server.start(null);
                    System.out.println("Derby Network Server started on port 1527.");
                } catch (Exception e) {
                    System.err.println("Failed to start Derby Network Server: " + e.getMessage());
                }
            }).start();
            
            waitForServerToStart();
        } catch (Exception e) {
            System.err.println("Failed to start Derby Network Server: " + e.getMessage());
        }
    }

    private static void waitForServerToStart() {
        
        int attempts = 0;
        while (attempts < 20) {
            try {
                
            	Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/C:/Users/sandr/db-derby-10.17.1.0-bin/db-derby-10.17.1.0-bin/bin/Database;create=false;");
                if (connection != null) {
                    connection.close();
                    System.out.println("Server is ready.");
                    return;
                }
            } catch (SQLException e) {
                attempts++;
                try {
                    Thread.sleep(500); 
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.err.println("Failed to connect to Derby Network Server after several attempts.");
    }

    public static void stopServer() {
        try {
            if (server != null) {
                server.shutdown();
                System.out.println("Derby Network Server stopped.");
            }
        } catch (Exception e) {
            System.err.println("Error stopping Derby Network Server: " + e.getMessage());
        }
    }
}