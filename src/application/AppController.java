package application;

import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import admin.AdminLoginController;
import admin.AdminLoginView;
import admin.AdminServiceView;
import admin.SuccessfulLoginController;
import admin.SuccessfulLoginView;
import cart.CartView;
import home.HomeView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class AppController {

	@FXML
	private Button exitButton;
	
	@FXML
	private BorderPane contentPane;
	
	@FXML
	private Button adminButton;
	
	@FXML
	private VBox userOptions;
	
	@FXML
	private Button logoutButton;

	private Button dynamicButton;
	
	private boolean adminLoggedin = false;
	
	private String adminUsername = null;
	
	private SuccessfulLoginView successfulLoginView = null;
	
	private SuccessfulLoginController sessionController;
	
	private CartView cartView;
	
	@FXML
	public void initialize() {
	    try {
	        showHome();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void closeApp() {
		Stage stage= (Stage) exitButton.getScene().getWindow();
		stage.close();
	}
	
	public void showHome(MouseEvent event) throws IOException {
		contentPane.setCenter(new HomeView().getView());
	}
	
	public void showHome() throws IOException {
	    showHome(null);
	}
	
	public void showCart(MouseEvent event) throws IOException {
		if(cartView == null) {
			cartView= new CartView();
		}
		contentPane.setCenter(cartView.getView());
		cartView.getController().refreshCartView();
	}
	
	public void showAdminLogin(MouseEvent event) throws Exception {
	    if (adminUsername == null) {
	        
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/AdminLogin.fxml"));
	        Parent view = loader.load();

	        AdminLoginController controller = loader.getController();
	        controller.setAppController(this);

	        contentPane.setCenter(view);
	    } else {

	        contentPane.setCenter(successfulLoginView.getView());
	    }
	}
	
	public void showAdminLogin() throws Exception{
		showAdminLogin(null);
	}
	
	public void addExtraButton() {
		
	    if (dynamicButton == null) {
	        dynamicButton = new Button();
	        dynamicButton.setPrefSize(40, 40);
	        dynamicButton.getStyleClass().add("userButton");

	        
	        ImageView icon = new ImageView(getClass().getResource("/application/logos/settings.png").toString());
	        icon.setFitHeight(40);
	        icon.setFitWidth(40);
	        icon.setPreserveRatio(true);
	        dynamicButton.setGraphic(icon);

	        VBox.setMargin(dynamicButton, new Insets(20, 0, 0, 20));
	        
	        dynamicButton.setOnAction(event -> {
	        	try {
	        		showAdminFunctions();
	        	} catch (IOException e) {
	        		e.printStackTrace();
	        	}
	        });

	        Platform.runLater(() -> {
	            if (userOptions.getChildren().contains(adminButton)) {
	                int index = userOptions.getChildren().indexOf(adminButton);
	                userOptions.getChildren().add(index, dynamicButton);
	            } else {
	                userOptions.getChildren().add(dynamicButton);
	            }
	        });
	    }
	    
	}
	
	public void showAdminFunctions() throws IOException {
		contentPane.setCenter(new AdminServiceView().getView());
	}
	
	public boolean isAdminLoggedin() {
		return adminLoggedin;
	}
	
	public void setAdminLoggedin(String username) throws IOException {
		this.adminLoggedin = true;
		this.adminUsername = username;
		
		addExtraButton();
		
		if (successfulLoginView == null) {
	        successfulLoginView = new SuccessfulLoginView();
	        
	        SuccessfulLoginController controller = successfulLoginView.getController();
	        controller.setAppController(this);  
	        sessionController = controller;
	       
	    }
		
		successfulLoginView.getController().showSessionData(username);
   
		contentPane.setCenter(successfulLoginView.getView());
	}
	
	public void logout() throws Exception {
		adminLoggedin = false;
		adminUsername = null;
		
		removeExtraButton();
		
		if(sessionController != null) {
			sessionController.stopSessionTimer();
			sessionController.resetTimerState();
		}
		
		showAdminLogin();
	}
	
	public void removeExtraButton() {
		if(dynamicButton!=null) {
			
			userOptions.getChildren().remove(dynamicButton);
			dynamicButton=null;
		}
	}
}
