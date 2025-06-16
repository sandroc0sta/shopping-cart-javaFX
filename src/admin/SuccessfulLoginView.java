package admin;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SuccessfulLoginView {

	private Parent view;
	private SuccessfulLoginController controller;
	
	public SuccessfulLoginView() throws IOException {
		
		FXMLLoader loader= new FXMLLoader(getClass().getResource("SuccessfulLoginView.fxml"));
		this.view= loader.load();
		this.controller = loader.getController();
	}
	public Parent getView() {
		return view;
	}
	
	public SuccessfulLoginController getController() {
		return controller;
	}

}
