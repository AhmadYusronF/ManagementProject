package com.menejementpj.utils;

import java.io.IOException;

import com.menejementpj.App;
import javafx.event.ActionEvent;

public class Utils {

    public static void closeWindow(ActionEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();

        stage.close();
    }

    public static void logout() throws IOException {
        App.group.setNull();
        App.mygroup.setNul();
        App.user.setNull();
        App.userSession.setNull();
        App.setRoot("login", "Login - Management Project");
    }

}
