package admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AdminLoginView {
	
	private Parent view;
	
	
	public AdminLoginView() throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("adminLogin.fxml"));
        this.view = loader.load();
	}
	
	public Parent getView() {
		return view;
	}
}
