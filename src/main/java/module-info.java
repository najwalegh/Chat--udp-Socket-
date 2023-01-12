module com.example.test2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.test2 to javafx.fxml;
    exports com.example.client;
    exports com.example.db;
    opens com.example.db to javafx.fxml;
    opens com.example.client to javafx.fxml;
}