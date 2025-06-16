package application;

import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application{
	
	
	@Override
	public void start(Stage stage) throws Exception {

		DataBaseServer.startServer();
		
		try {
            DataBaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;  // Exit if fail
        }
		
		Parent root= FXMLLoader.load(getClass().getResource("cartui.fxml"));
		Scene scene= new Scene(root,Color.TRANSPARENT);
		stage.setTitle("CartUI");
		stage.setResizable( true);
		stage.setFullScreen(false);
		stage.setFullScreenExitHint(null);
		
		stage.setScene(scene);
		stage.show();
		
	}
	
	@Override
	public void stop() {
		
		DataBaseConnection.closeConnection();
		
		DataBaseServer.stopServer();
	}
	
	public static void main(String[] args) {
		
		launch(args);

	}
}