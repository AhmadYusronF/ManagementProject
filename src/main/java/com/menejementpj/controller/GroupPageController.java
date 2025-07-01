package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.menejementpj.App;

import java.io.IOException;

public class GroupPageController {
    @FXML
    private Button createProjectButton;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnGroub;

    @FXML
    private Button btnProject;

    @FXML
    private ImageView b;

    @FXML
    private Button btnChat;

    @FXML
    private Button btnLogout;

    @FXML
    private Button createProjectButton1;

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "\"Beranda - Management Project\"");
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) throws IOException {
        App.setRoot("groupPage", "GroupPage - myGroup");
    }

    @FXML
    void toggleGotoProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project - myGroup");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) throws IOException {
        App.setRoot("chatPage", "Chat - myGroup");
    }

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void handleCreateProjectClick(ActionEvent event) {
        showPopup(event, "/com/menejementpj/popupCreateProject.fxml", "Create New Project");
    }

    @FXML
    void handleViewAllProjectsClick(ActionEvent event) {
        showPopup(event, "/com/menejementpj/popupProject.fxml", "All Projects");
    }

    @FXML
    void handleViewAllMembersClick(ActionEvent event) {
        showPopup(event, "/com/menejementpj/popupProject.fxml", "All Members");
    }

    private void showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            System.err.println("⚠️ Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
