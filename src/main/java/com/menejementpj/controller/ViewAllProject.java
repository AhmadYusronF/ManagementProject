package com.menejementpj.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ViewAllProject {

    @FXML
    private Button closeButton;

    @FXML
    void handleClose(ActionEvent event) {
        // Gets the stage from the button and closes it
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
