module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.menejementpj to javafx.fxml;
    opens com.menejementpj.controller to javafx.fxml;
    opens com.menejementpj.components to javafx.fxml;

    exports com.menejementpj;
    exports com.menejementpj.auth;
    exports com.menejementpj.components;
}
