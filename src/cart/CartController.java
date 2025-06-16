package cart;

import service.ProductService;
import model.Product;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CartController {

	@FXML private VBox cartContent;
	@FXML private HBox totalBox;
	
	private Label totalPriceLabel;

	private ProductService productService;

	public CartController() {
		//productService = new ProductService();
		// TODO: Used to load all products. Currently unused.
	}

	@FXML
	public void initialize() throws Exception {
		
		refreshCartView();
	}

	private HBox cartEntryView(Product product) {
		HBox layout = new HBox();
		layout.setAlignment(Pos.CENTER);
		layout.setMaxWidth(Double.MAX_VALUE);

		Label productName = new Label(product.getName());
		productName.setPrefWidth(300);
		productName.setStyle("-fx-font-size:15pt; -fx-padding:5px");

		Label quantity = new Label(String.valueOf(ShoppingCart.getInstance().getQuantity(product)));
		quantity.setStyle("-fx-padding:5px");

		Button plusButton = new Button("+");
		plusButton.getStyleClass().add("interactionButtons");
		plusButton.setUserData(product.getName());
		plusButton.setOnAction(e -> {

			if (ShoppingCart.getInstance().getQuantity(product) > 0) {
				ShoppingCart.getInstance().addProduct(product);
				quantity.setText(String.valueOf(ShoppingCart.getInstance().getQuantity(product)));
				totalPriceLabel.setText(String.valueOf(ShoppingCart.getInstance().calculateTotal()));
			}
		});

		Button minusButton = new Button("-");
		minusButton.getStyleClass().add("interactionButtons");
		minusButton.setUserData(product.getName());
		minusButton.setOnAction(e -> {
			
			ShoppingCart.getInstance().removeProduct(product);
			quantity.setText(String.valueOf(ShoppingCart.getInstance().getQuantity(product)));
			
		    if (ShoppingCart.getInstance().getQuantity(product) == 0) {
		        cartContent.getChildren().remove(layout);
		    }

		    totalPriceLabel.setText(String.valueOf(ShoppingCart.getInstance().calculateTotal()));
		});

		Label price = new Label(String.valueOf(product.getPrice()));
		price.setStyle("-fx-padding:5px");

		layout.getChildren().addAll(productName, minusButton, quantity, plusButton, price);

		return layout;
	}

	private HBox totalView(float totalPrice) {
		HBox layout = new HBox();
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(20);
		layout.setPadding(new Insets(10));

		Label totalLabel = new Label("Gesamt : ");
		totalLabel.setStyle("-fx-font-size:15pt;");

		totalPriceLabel = new Label(String.valueOf(totalPrice));
		
		Button clearButton = new Button("Löschen Alles");
		clearButton.getStyleClass().add("removalInteractionButtons");
		
		clearButton.setOnAction(e-> {
			ShoppingCart.getInstance().clear();
			try {
				refreshCartView();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		});
		
		layout.getChildren().addAll(totalLabel, totalPriceLabel, clearButton);
		
		return layout;
	}
	
	public void refreshCartView() {
		
		cartContent.setFillWidth(true);
		cartContent.getChildren().clear();
		
		List<CartEntry> cartEntries = ShoppingCart.getInstance().getEntries();
		
		totalBox.getChildren().clear();
		
		if(cartEntries.isEmpty()) {
			Label emptyLabel = new Label("Leerer Warenkorb");
			emptyLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
			cartContent.getChildren().add(emptyLabel);
		}
		else {
			
			Label shoppingCartTitle = new Label("Warenkorb");
			shoppingCartTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
			cartContent.getChildren().add(shoppingCartTitle);
			
			for(CartEntry entry: cartEntries) {
				HBox productView = cartEntryView(entry.getProduct());
				cartContent.getChildren().add(productView);
			}
			
			HBox totalView = totalView(ShoppingCart.getInstance().calculateTotal());
			totalBox.getChildren().addAll(totalView.getChildren());
		}
	}
}