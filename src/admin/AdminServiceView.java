package admin;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AdminServiceView {
private Parent view;
	
	public AdminServiceView() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("adminService.fxml"));
        this.view = loader.load();
	}
	
	public Parent getView() {
		return this.view;
	}
}
