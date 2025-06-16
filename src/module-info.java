/**
 * 
 */
/**
 * 
 */
module ShoppingCartFX {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	requires org.apache.derby.server;

    opens application to javafx.fxml;
    opens home to javafx.fxml;
    opens cart to javafx.fxml;
    opens admin to javafx.fxml;

    exports application;
    exports home;
}