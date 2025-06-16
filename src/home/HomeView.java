package home;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class HomeView {
	
	private Parent view;
	
	public HomeView() throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        this.view = loader.load();
		
	}
	
	public Parent getView() {
		return view;
	}
}
