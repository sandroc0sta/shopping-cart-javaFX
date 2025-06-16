package cart;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class CartView {
	
	private Parent view;
	private CartController controller;
	
	public CartView() throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("cart.fxml"));
        this.view = loader.load();
        this.controller = loader.getController();
	}
	
	public Parent getView() {
		return this.view;
	}
	
	public CartController getController() {
		return controller;
	}
}
