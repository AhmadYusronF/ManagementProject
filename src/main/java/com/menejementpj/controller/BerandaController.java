package com.menejementpj.controller;

import java.io.IOException;

import com.menejementpj.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        showPopup(event, "/com/menejementpj/chatPopUp.fxml", "Group Chat");
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

        welcomeUser.setText("Welcome, " + App.user.getUsername());
    }

    private void showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Scene popupScene = new Scene(root);

            String cssPath = "/com/menejementpj/css/chatMessage.css";
            popupScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(popupScene);
            popupStage.initModality(Modality.NONE);
            popupStage.setResizable(false);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(ownerStage);

            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
