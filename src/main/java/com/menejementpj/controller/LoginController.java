package com.menejementpj.controller;

import com.menejementpj.db.*;
import com.menejementpj.test.Debug;
import com.menejementpj.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Button togglePasswordButton;

    private boolean isPasswordVisible = false;

    @FXML
    private void initialize() {
        passwordField.textProperty().bindBidirectional(visiblePasswordField.textProperty());
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("HIDE");
        } else {
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordButton.setText("SHOW");
        }
    }

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        Debug.log("Memulai Login");
        String email = emailField.getText();
        String password = passwordField.getText();
        Debug.log("Pengecekan 1 ");
        int hidden_uid = DatabaseManager.authenticateUser(email, password);

        if (hidden_uid != -1) {
            Debug.log("Pengecekan 2");
            DatabaseManager.getUserSession(hidden_uid);
            System.out.println("Login successful for HUID: " + hidden_uid);
            DatabaseManager.getUserProfile(App.userSession.getCurrentLoggedInUserID());

            try {
                App.setRoot("beranda", "\"Beranda - Management Project\"");

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error loading the main application page.");
            }
        } else {
            messageLabel.setText("Invalid email or password. Please try again.");
            System.out.println("Login failed for email: " + email);
        }
    }

    @FXML
    void handleRegisterButtonAction(ActionEvent event) {
        showPopup(event, "/com/menejementpj/components/accountCreate.fxml", "Create An Account");
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
