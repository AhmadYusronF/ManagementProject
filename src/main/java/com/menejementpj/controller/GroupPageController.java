package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // You will need this import
import javafx.scene.Node; // And this one
import javafx.scene.Parent; // And this one
import javafx.scene.Scene; // And this one
import javafx.stage.Modality; // And this one
import javafx.stage.Stage; // And this one
import java.io.IOException; // And definitely this one

public class GroupPageController {

    @FXML
    void handleCreateProjectClick(ActionEvent event) {
        // CORRECTED PATH
        showPopup(event, "/com/menejementpj/popupCreateProject.fxml", "Create New Project");
    }

    @FXML
    void handleViewAllProjectsClick(ActionEvent event) {
        // CORRECTED PATH
        showPopup(event, "/com/menejementpj/popupProject.fxml", "All Projects");
    }

    @FXML
    void handleViewAllMembersClick(ActionEvent event) {
        // CORRECTED PATH - I assume you want a members popup here, not the project one.
        // I'll leave it as is for now, but you might want to create a popupMember.fxml
        showPopup(event, "/com/menejementpj/popupProject.fxml", "All Members");
    }

    // This is the CRUCIAL part. Your controller was missing this entire method.
    private void showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            // This is the standard way to load a resource from the classpath
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setTitle(title);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (IOException e) {
            // THIS IS THE MOST IMPORTANT LINE FOR DEBUGGING
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}