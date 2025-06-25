package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CreateProjectController {

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea titleTextArea;

    @FXML
    void handleConfirm(ActionEvent event) {
        String title = titleTextArea.getText();
        String description = descriptionTextArea.getText();

        if (title.isBlank()) {
            System.out.println("Title cannot be empty.");
            // You could show an alert here for better user feedback
            return;
        }

        // --- Your database or logic to save the project would go here ---
        System.out.println("CONFIRMED: Saving Project");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);

        closeWindow(event);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        System.out.println("CANCELLED: Project creation aborted.");
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        // Gets the stage from the button that was clicked and closes it
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
