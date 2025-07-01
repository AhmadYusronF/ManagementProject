package com.menejementpj.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.menejementpj.App;
import com.menejementpj.test.Debug;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BerandaController {
    @FXML
    private VBox activityLogContainer;

    @FXML
    private Label welcomeUser;

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

    // @FXML
    // private ListView<?> myListView;

    @FXML
    private StackPane myStackPane;

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) throws IOException {
        if (App.userSession.getCurrentLoggedInGroupID() != 0) {
            App.setRoot("components/group/groupPage", "GroubPage - myGroub");
        } else {
            App.setRoot("groupTab", "Group - join or create");
        }
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {

    }

    @FXML
    void toggleGotoProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project");
    }

    @FXML
    void goToHome(ActionEvent event) {

    }

    @FXML
    private void initialize() {
        // Hint: initialize() will be called when the associated FXML has been
        // completely loaded.
        welcomeUser.setText("Welcome, " + App.user.getUsername());
    }
}
