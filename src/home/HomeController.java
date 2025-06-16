package home;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.DataBaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import cart.CartController;
import cart.CartView;
import cart.ShoppingCart;
import model.Product;
import static admin.AdminFunctionsController.isInteger;

public class HomeController {

	@FXML
	private FlowPane productFlowPane;

	@FXML
	private VBox cartContainer;

	@FXML
	private VBox productContainer;

	@FXML
	private HBox mainHBox;

	@FXML
	private TextField searchBar;

	@FXML
	private Button searchButton;

	@FXML
	private ScrollPane productScrollPane;

	private CartController cartController;

	private List<Pair<Product, Button>> productButtonPairs = new ArrayList<>();

	private int productsLoaded = 0;

	private final int batchSize = 50;

	private boolean loading = false;

	@FXML
	public void initialize() throws SQLException {

		try {
			CartView cartView = new CartView();
			Parent cartRoot = cartView.getView();

			cartController = cartView.getController();

			VBox.setVgrow(cartRoot, Priority.ALWAYS);
			HBox.setHgrow(cartRoot, Priority.ALWAYS);
			cartRoot.maxWidth(Double.MAX_VALUE);
			cartRoot.maxHeight(Double.MAX_VALUE);

			if (cartRoot instanceof Region region) {
				region.setMaxWidth(Double.MAX_VALUE);
				region.setMaxHeight(Double.MAX_VALUE);
			}

			productContainer.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.5));
			cartContainer.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.5));

			cartContainer.getChildren().add(cartRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}

		searchBar.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				handleEnterPressed(event);
				event.consume();
			}
		});

		productFlowPane.getChildren().clear();
		productFlowPane.setHgap(20);
		productFlowPane.setVgap(20);
		productFlowPane.setPadding(new Insets(10));
		productFlowPane.setPrefWrapLength(600);

		productScrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
			if (!loading && newVal.doubleValue() >= 0.9) {
				try {
					loadNextBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		productsLoaded = 0;
		loadNextBatchAsync();
		
		Platform.runLater(() -> searchBar.requestFocus());
	}

	private Button productView(Product product) {

		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(10);
		layout.setPadding(new Insets(10));
		
		ImageView imageView = new ImageView();
		try {
		    String imagePath = product.getImagePath(); 
		    File imageFile = new File("resources/" + imagePath); 
		    
		    if (imageFile.exists()) {
		        Image image = new Image(imageFile.toURI().toString());
		        imageView.setImage(image);
		        imageView.setFitHeight(80);
		        imageView.setPreserveRatio(true);
		    } else {
		        //System.out.println("Image file not found: " + imageFile.getAbsolutePath());
		       
		    }
		} catch (Exception e) {
		    //System.out.println("Bild für das Produkt konnte nicht geladen werden: " + product.getName());
		    e.printStackTrace();
		}

		Label nameLabel = new Label(product.getName());
		Label priceLabel = new Label(String.format("€%.2f", product.getPrice()));
		
		layout.getChildren().add(imageView);
		layout.getChildren().addAll(nameLabel, priceLabel);

		Button cleanButton = new Button();
		cleanButton.setGraphic(layout);
		cleanButton.getStyleClass().add("addProductButton");
		cleanButton.setUserData(product);

		cleanButton.setOnAction(e -> {
			Product clickedProduct = (Product) cleanButton.getUserData();

			TextInputDialog dialog = new TextInputDialog("1");
			dialog.setTitle("Menge auswählen");
			dialog.setHeaderText("Menge eingeben: " + clickedProduct.getName());
			dialog.setContentText("Menge:");

			DialogPane pane = dialog.getDialogPane();
			pane.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
			pane.getStyleClass().add("custom-dialog");

			Stage stage = (Stage) pane.getScene().getWindow();
			stage.getIcons().clear();

			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {
				try {
					int quantity = Integer.parseInt(result.get().trim());
					addProduct(clickedProduct,quantity);

				} catch (NumberFormatException ex) {

					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Ungültige Menge");
					alert.setHeaderText(null);
					alert.setContentText("Bitte geben Sie eine gültige positive Ganzzahl als Menge ein.");
					alert.showAndWait();
				}
			}
		});

		return cleanButton;
	}

	@FXML
	private void searchProduct(ActionEvent e) {

		String input = searchBar.getText().trim();

		productFlowPane.getChildren().clear();

		if (input.isEmpty()) {

			for (Pair<Product, Button> pair : productButtonPairs) {
				productFlowPane.getChildren().add(pair.getValue());
			}
			return;
		}

		boolean isID = isInteger(input);

		for (Pair<Product, Button> pair : productButtonPairs) {
			Product product = pair.getKey();

			boolean matches = isID ? (product.getId() == Integer.parseInt(input))
					: product.getName().toLowerCase().contains(input.toLowerCase());

			if (matches) {
				productFlowPane.getChildren().add(pair.getValue());
			}
		}
	}

	private void loadNextBatch() throws SQLException {
		loading = true;

		try (Connection conn = DataBaseConnection.getConnection()) {
			try (Statement schemaStmt = conn.createStatement()) {
				schemaStmt.execute("SET SCHEMA SHOPPING_CART");
			}
			String query = "SELECT ID, NAME, PRICE, IMAGE_PATH FROM SHOPPING_CART.PRODUCTS ORDER BY ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setInt(1, productsLoaded);
				stmt.setInt(2, batchSize);

				try (ResultSet rs = stmt.executeQuery()) {
					int rowsFetched = 0;

					while (rs.next()) {
						int id = rs.getInt("ID");
						String name = rs.getString("NAME");
						double price = rs.getDouble("PRICE");
						String imagePath = rs.getString("IMAGE_PATH");

						Product product = new Product(id, name, (float) price, imagePath);
						Button productView = productView(product);

						FlowPane.setMargin(productView, new Insets(10));

						Platform.runLater(() -> {
						    FlowPane.setMargin(productView, new Insets(10));
						    productFlowPane.getChildren().add(productView);
						});

						productButtonPairs.add(new Pair<>(product, productView));
						rowsFetched++;
					}

					if (rowsFetched > 0) {
						productsLoaded += rowsFetched;
					}

				}
			}
		}
		Platform.runLater(() -> {
			loading = false;
		});
	}
	
	private void loadNextBatchAsync() {
		Task<Void> task = new Task<>() {
		
		protected Void call() throws Exception {
			loadNextBatch();
			return null;
		}
	};
	
	task.setOnFailed(e -> {
		Throwable ex = task.getException();
		ex.printStackTrace();
	});
	
	new Thread(task).start();
	}
	
	private void addProduct(Product product, int quantity) {
		if (quantity <= 0) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ungültige Menge");
			alert.setHeaderText(null);
			alert.setContentText("Bitte geben Sie eine gültige positive Ganzzahl als Menge ein.");
			alert.showAndWait();
			return;
		}

		ShoppingCart shoppingCart = ShoppingCart.getInstance();
		shoppingCart.setProductQuantity(product, quantity);

		if (cartController != null) {
			cartController.refreshCartView();
		}
	}

	private void handleEnterPressed(KeyEvent event) {
		String input = searchBar.getText().trim();

		if (input.isEmpty()) {

			searchProduct(new ActionEvent());
			return;
		}

		if (isInteger(input)) {

			boolean success = quickAddProduct(Integer.parseInt(input));
			if (!success) {

				searchProduct(new ActionEvent());
			}
		} else {

			searchProduct(new ActionEvent());
		}
		
		searchBar.clear();
	}
	
	private boolean quickAddProduct(int productId) {
		for (Pair<Product, Button> pair : productButtonPairs) {
			Product product = pair.getKey();
			if (product.getId() == productId) {
				addProduct(product, 1);  
				return true;
			}
		}

		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Produkt nicht gefunden");
		alert.setHeaderText(null);
		alert.setContentText("Kein Produkt mit der ID " + productId + " gefunden.");
		alert.showAndWait();
		return false;
	}
}