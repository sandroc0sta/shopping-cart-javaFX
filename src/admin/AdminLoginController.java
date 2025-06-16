package admin;

import java.io.IOException;

import application.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class AdminLoginController {
	@FXML
	private TextField userNameField;
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private BorderPane contentPane;
	
	private final AuthService authService= new AuthService();
	
	private AppController appController;
	
	@FXML
	public void initialize() {
	    javafx.application.Platform.runLater(() -> userNameField.requestFocus());
	    
	    userNameField.setOnAction(e -> passwordField.requestFocus());
	    passwordField.setOnAction(e -> {
	        try {
	            handleLogin();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    });
	}
	
	public void handleLogin() throws IOException {
	    String username = userNameField.getText();
	    String password = passwordField.getText();

	    if (authService.isValidLogin(username, password)) {
	        System.out.println("Login erfolgreich!");
	        
	        appController.addExtraButton(); 
	        appController.setAdminLoggedin(username);

	    } else {
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText("Fehler bei der Anmeldung ");
	        alert.setContentText("Falsche Benutzername oder Kennwort");
	        alert.showAndWait();
	    }
	}
	
	public void setAppController(AppController appController) {
	    this.appController = appController;
	}
}
