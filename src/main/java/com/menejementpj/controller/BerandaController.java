package com.menejementpj.controller;

import java.io.IOException;

import com.menejementpj.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BerandaController {

    @FXML
    private VBox activitylogContainer;

    @FXML
    private VBox taskContainer;

    @FXML
    private ImageView b;

    @FXML
    private Button btnChat;

    @FXML
    private Button btnGroub;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnProject;

    @FXML
    private ListView<?> myListView;

    @FXML
    private StackPane myStackPane;

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) throws IOException {
        App.setRoot("groupPage", "GroubPage - myGroub");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {

    }

    @FXML
    void toggleGotoProject(ActionEvent event) {

    }

    @FXML
    void goToHome(ActionEvent event) {

    }

}
