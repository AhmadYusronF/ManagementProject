module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    

    opens com.menejementpj to javafx.fxml;
    opens com.menejementpj.controller to javafx.fxml;

    exports com.menejementpj;
}
