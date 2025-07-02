module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens com.menejementpj to javafx.fxml;
    opens com.menejementpj.controller to javafx.fxml;
    opens com.menejementpj.components to javafx.fxml;
    opens com.menejementpj.model to javafx.base;

    requires java.desktop;

    exports com.menejementpj;
    exports com.menejementpj.auth;
    exports com.menejementpj.model;
    exports com.menejementpj.components;
    exports com.menejementpj.controller;
}
