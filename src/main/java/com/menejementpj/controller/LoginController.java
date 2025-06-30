package com.menejementpj.controller;

import com.menejementpj.db.*;
import com.menejementpj.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        String email = emailField.getText();
        String password = passwordField.getText(); // Reminder: For production apps, always hash passwords!

        // Authenticate user using the DatabaseManager
        // DatabaseManager.authenticateUser now returns the user's ID or -1
        int hidden_uid = DatabaseManager.authenticateUser(email, password);
        int users_id = DatabaseManager.getUserID(hidden_uid);
        int groupID = DatabaseManager.getGroupID(users_id);

        if (hidden_uid != -1) { // Authentication successful
            App.userSession.setUserSession(hidden_uid, users_id, groupID);
            System.out.println("Login successful for HUID: " + hidden_uid);

            try {
                App.setRoot("beranda", "\"Beranda - Management Project\""); // Call the static method in App
                                                                            // to switch to Beranda scene
            } catch (IOException e) {
                e.printStackTrace(); // Print stack trace for debugging
                messageLabel.setText("Error loading the main application page."); // Display error to user
            }
        } else { // Authentication failed
            messageLabel.setText("Invalid email or password. Please try again.");
            System.out.println("Login failed for email: " + email);
        }
    }
}