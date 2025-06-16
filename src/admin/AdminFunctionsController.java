package admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AdminFunctionsController {

	@FXML
	private TextField IDorNameField;

	@FXML
	private TextField NameField;

	@FXML
	private TextField PriceField;

	@FXML
	private TextField IDorNameFieldEdit;

	@FXML
	private TextField IDorNameFieldNew;

	@FXML
	private TextField PriceFieldNew;

	@FXML
	private StackPane imageDropArea;

	@FXML
	private Button selectImageButton;

	@FXML
	private ImageView productImageView;

	@FXML
	private StackPane imageDropAreaEdit;

	@FXML
	private Button selectImageButtonEdit;

	@FXML
	private ImageView productImageViewEdit;

	private File selectedImageFileAdd;

	private File selectedImageFileEdit;

	@FXML
	public void initialize() {

		setupImageDropArea(imageDropArea, true);
		setupImageDropArea(imageDropAreaEdit, false);
	}

	private void setupImageDropArea(StackPane dropArea, boolean isAdd) {
		dropArea.setOnDragOver(event -> {
			if (event.getGestureSource() != dropArea && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});

		dropArea.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;

			if (db.hasFiles()) {
				File draggedFile = db.getFiles().get(0);

				if (isSupportedImage(draggedFile)) {
					try {
						Path destDir = Path.of("src/resources/images");
						Files.createDirectories(destDir);

						Path destFile = destDir.resolve(draggedFile.getName());
						Files.copy(draggedFile.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);

						Label fileNameLabel = new Label(destFile.getFileName().toString());
						fileNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

						dropArea.getChildren().clear();
						dropArea.getChildren().add(fileNameLabel);

						if (isAdd) {
							selectedImageFileAdd = destFile.toFile();
							loadAddImage(selectedImageFileAdd);
						} else {
							selectedImageFileEdit = destFile.toFile();
							loadEditImage(selectedImageFileEdit);
						}
						success = true;
					} catch (IOException e) {
						e.printStackTrace();
						showAlert("Fehler", "Bild konnte nicht kopiert werden.");
					}
				} else {
					showAlert("Ungültiges Bild", "Bitte ein unterstütztes Bildformat verwenden (JPG, PNG, GIF).");
				}
			}
			event.setDropCompleted(success);
			event.consume();
		});
	}

	private File copyImageToResources(File sourceFile) throws IOException {
		Path destDir = Path.of("src/resources/images");
		Files.createDirectories(destDir);
		Path destFile = destDir.resolve(sourceFile.getName());
		Files.copy(sourceFile.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);
		return destFile.toFile();
	}

	private void loadAddImage(File file) {
		Image image = new Image(file.toURI().toString());
		productImageView.setImage(image);
	}

	private void loadEditImage(File file) {
		Image image = new Image(file.toURI().toString());
		productImageViewEdit.setImage(image);
	}

	@FXML
	private void onSelectImageAdd() {
		File file = chooseImageFile();
		if (file != null) {
			selectedImageFileAdd = file;
			imageDropArea.getChildren().clear();
			imageDropArea.getChildren().add(new Label(file.getName()));
			productImageView.setImage(null);
		}
	}

	@FXML
	private void onSelectImageEdit() {
		File file = chooseImageFile();
		if (file != null) {
			selectedImageFileEdit = file;
			imageDropAreaEdit.getChildren().clear();
			imageDropAreaEdit.getChildren().add(new Label(file.getName()));
			productImageViewEdit.setImage(null);
		}
	}

	private File chooseImageFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Bild auswählen");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Bilddateien", "*.png", "*.jpg", "*.jpeg", "*.gif"));

		Window window = null;

		if (selectImageButton != null && selectImageButton.getScene() != null) {
			window = selectImageButton.getScene().getWindow();
		}
		return fileChooser.showOpenDialog(window);
	}

	private boolean isSupportedImage(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	private void addProduct(ActionEvent event) {

		String name = NameField.getText();

		BigDecimal price;

		try {
			price = new BigDecimal(PriceField.getText());
		} catch (NumberFormatException e) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Ungültiger Preis");
			alert.setHeaderText(null);
			alert.setContentText("Bitte geben Sie einen gültigen Preis im Format 00.00 ein.");
			alert.showAndWait();
			return;
		}

		String checkSQL = "SELECT COUNT(*) FROM PRODUCTS WHERE NAME = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {

			checkStmt.setString(1, name);
			ResultSet resultSet = checkStmt.executeQuery();

			if (resultSet.next() && resultSet.getInt(1) > 0) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Produkt existiert bereits");
				alert.setHeaderText(null);
				alert.setContentText("Ein Produkt mit diesem Namen existiert bereits.");
				alert.showAndWait();
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Fehler");
			errorAlert.setHeaderText("Datenbankfehler");
			errorAlert.setContentText("Beim Überprüfen des Produkts gab es einen Fehler.");
			errorAlert.showAndWait();
			return;
		}

		String selectedImagePath = null;
		if (selectedImageFileAdd != null) {
			try {
				if (!selectedImageFileAdd.getAbsolutePath().contains("src" + File.separator + "resources")) {
					selectedImageFileAdd = copyImageToResources(selectedImageFileAdd);
				}
				selectedImagePath = "images/" + selectedImageFileAdd.getName();
			} catch (IOException ex) {
				ex.printStackTrace();
				showAlert("Fehler", "Das Bild konnte nicht kopiert werden.");
				return;
			}
		}

		String insertSQL;
		boolean hasImage = selectedImagePath != null && !selectedImagePath.isEmpty();

		if (hasImage) {
			insertSQL = "INSERT INTO PRODUCTS (NAME, PRICE, IMAGE_PATH) VALUES (?, ?, ?)";
		} else {
			insertSQL = "INSERT INTO PRODUCTS (NAME, PRICE) VALUES (?, ?)";
		}

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

			stmt.setString(1, name);
			stmt.setBigDecimal(2, price);

			if (hasImage) {
				stmt.setString(3, selectedImagePath);
			}

			stmt.executeUpdate();

			Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
			successAlert.setTitle("Erfolg");
			successAlert.setHeaderText(null);
			successAlert.setContentText("Produkt erfolgreich eingefügt.");
			successAlert.showAndWait();

			NameField.clear();
			PriceField.clear();
			imageDropArea.getChildren().clear();
			selectedImageFileAdd = null;
			productImageView.setImage(null);

		} catch (SQLException e) {
			e.printStackTrace();
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Fehler");
			errorAlert.setHeaderText("Datenbankfehler");
			errorAlert.setContentText("Das Produkt konnte nicht hinzugefügt werden.");
			errorAlert.showAndWait();
		}
	}

	@FXML
	private void removeProduct(ActionEvent e) {

		String input = IDorNameField.getText();
		String deleteSQL = "DELETE FROM PRODUCTS WHERE ID = ? OR NAME = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {

			Integer id = null;

			if (isInteger(input)) {
				id = Integer.parseInt(input);
			}

			if (id != null) {
				deleteStmt.setInt(1, id);
			} else {
				deleteStmt.setNull(1, java.sql.Types.INTEGER);
			}

			deleteStmt.setString(2, input);

			int rowsAffected = deleteStmt.executeUpdate();

			if (rowsAffected > 0) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Erfolg");
				alert.setHeaderText(null);
				alert.setContentText("Produkt wurde erfolgreich gelöscht.");
				alert.showAndWait();
			} else {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Nicht gefunden");
				alert.setHeaderText(null);
				alert.setContentText("Kein Produkt mit diesem Namen oder dieser ID gefunden.");
				alert.showAndWait();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			showAlert("Fehler", "Fehler beim Löschen des Produkts.");
		}

		IDorNameField.clear();
	}

	@FXML
	private void editProduct(ActionEvent e) {

		String nameOrID = IDorNameFieldEdit.getText();
		String newName = IDorNameFieldNew.getText();

		BigDecimal price;
		try {
			price = new BigDecimal(PriceFieldNew.getText());
		} catch (NumberFormatException ex) {
			showAlert("Ungültiger Preis", "Bitte geben Sie einen gültigen Preis im Format 00.00 ein.");
			return;
		}

		String selectedImagePath = null;
		if (selectedImageFileEdit != null) {
			try {
				if (!selectedImageFileEdit.getAbsolutePath().contains("src" + File.separator + "resources")) {
					selectedImageFileEdit = copyImageToResources(selectedImageFileEdit);
				}
				selectedImagePath = "images/" + selectedImageFileEdit.getName();
			} catch (IOException ex) {
				ex.printStackTrace();
				showAlert("Fehler", "Das Bild konnte nicht kopiert werden.");
				return;
			}
		}

		String updateSQL;
		boolean hasImage = selectedImagePath != null && !selectedImagePath.isEmpty();

		if (isInteger(nameOrID)) {
			// Searching by ID (integer)
			if (hasImage) {
				updateSQL = "UPDATE PRODUCTS SET PRICE = ?, NAME = ?, IMAGE_PATH = ? WHERE ID = ?";
			} else {
				updateSQL = "UPDATE PRODUCTS SET PRICE = ?, NAME = ? WHERE ID = ?";
			}
		} else {
			// Searching by NAME (string)
			if (hasImage) {
				updateSQL = "UPDATE PRODUCTS SET PRICE = ?, NAME = ?, IMAGE_PATH = ? WHERE NAME = ?";
			} else {
				updateSQL = "UPDATE PRODUCTS SET PRICE = ?, NAME = ? WHERE NAME = ?";
			}
		}

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {

			updateStmt.setBigDecimal(1, price);
			updateStmt.setString(2, newName);

			int paramIndex = 3;
			if (hasImage) {
				updateStmt.setString(paramIndex, selectedImagePath);
				paramIndex++;
			}

			if (isInteger(nameOrID)) {
				updateStmt.setInt(paramIndex, Integer.parseInt(nameOrID));
			} else {
				updateStmt.setString(paramIndex, nameOrID);
			}

			int affectedRows = updateStmt.executeUpdate();

			if (affectedRows > 0) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Erfolg");
				alert.setHeaderText(null);
				alert.setContentText("Produkt erfolgreich aktualisiert.");
				alert.showAndWait();

				IDorNameFieldEdit.clear();
				IDorNameFieldNew.clear();
				PriceFieldNew.clear();
				imageDropAreaEdit.getChildren().clear();
				selectedImageFileEdit = null;
				productImageViewEdit.setImage(null);
			} else {
				showAlert("Fehler", "Kein Produkt mit dem angegebenen Namen oder der ID gefunden.");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			showAlert("Fehler", "Beim Aktualisieren des Produkts ist ein Fehler aufgetreten.");
		}
	}

	public static boolean isInteger(String input) {
		if (input == null) {
			return false;
		}
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@FXML
	private void seeProductDetails(ActionEvent e) throws SQLException {

		StringBuilder productInfo = new StringBuilder();

		try (Connection conn = DataBaseConnection.getConnection()) {
			try (Statement stmt = conn.createStatement()) {
				stmt.execute("SET SCHEMA SHOPPING_CART");

				String query = "SELECT * FROM PRODUCTS";
				try (ResultSet rs = stmt.executeQuery(query)) {

					while (rs.next()) {

						int id = rs.getInt("ID");
						String name = rs.getString("NAME");
						double price = rs.getDouble("PRICE");

						productInfo.append("ID: ").append(id).append("\n");
						productInfo.append("Name: ").append(name).append("\n");
						productInfo.append("Price: €").append(price).append("\n");
						productInfo.append("---------------------------\n");
					}
				}
			}
		}

		TextArea textArea = new TextArea(productInfo.toString());
		textArea.setEditable(false);

		Scene scene = new Scene(textArea, 400, 300);

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.F) {
				event.consume();
				showSearchPopup(textArea);
			}
		});

		Stage detailsStage = new Stage();
		detailsStage.setTitle("Produkt Details");
		detailsStage.setScene(scene);
		detailsStage.show();
	}

	private void showSearchPopup(TextArea textArea) {
		Stage popup = new Stage();
		//popup.initModality(Modality.APPLICATION_MODAL);
		popup.setTitle("Find");

		TextField searchField = new TextField();
		searchField.setPromptText("Search...");

		Button findButton = new Button("Find All");
		searchField.setOnAction(e -> findButton.fire());
		
		Button nextButton = new Button("→");
	    Button prevButton = new Button("←");
		Label resultLabel = new Label();
		
		List<Integer> matchIndexes = new ArrayList<>();
	    final int[] currentIndex = { -1 };

		findButton.setOnAction(e -> {
			
			 matchIndexes.clear();
		     currentIndex[0] = -1;
			
			String content = textArea.getText().toLowerCase();
			String search = searchField.getText().toLowerCase();
			
			 if (search.isEmpty()) {
		            resultLabel.setText("Zu suchenden Text eingeben:");
		            return;
		        }

			int index = content.indexOf(search);
			
			 while (index >= 0) {
		            matchIndexes.add(index);
		            index = content.indexOf(search, index + search.length());
		        }
			 
			 if (!matchIndexes.isEmpty()) {
		            currentIndex[0] = 0;
		            selectMatch(textArea, matchIndexes, currentIndex[0], search.length());
		            resultLabel.setText(matchIndexes.size() + " match(es) gefunden.");
			 } else {
				 resultLabel.setText("Kein match gefunden.");
			 }
		});
		
		nextButton.setOnAction(e -> {
	        if (!matchIndexes.isEmpty()) {
	            currentIndex[0] = (currentIndex[0] + 1) % matchIndexes.size();
	            selectMatch(textArea, matchIndexes, currentIndex[0], searchField.getText().length());
	        }
	    });

	    prevButton.setOnAction(e -> {
	        if (!matchIndexes.isEmpty()) {
	            currentIndex[0] = (currentIndex[0] - 1 + matchIndexes.size()) % matchIndexes.size();
	            selectMatch(textArea, matchIndexes, currentIndex[0], searchField.getText().length());
	        }
	    });

	    HBox navButtons = new HBox(5, prevButton, nextButton);
	    VBox box = new VBox(10, searchField, findButton, navButtons, resultLabel);
	    box.setPadding(new Insets(10));

	    popup.setScene(new Scene(box));
	    popup.show();
	}
	
	private void selectMatch(TextArea textArea, List<Integer> indexes, int currentIndex, int length) {
	    int start = indexes.get(currentIndex);
	    textArea.selectRange(start, start + length);
	}
}
