package com.menejementpj.components;

import java.time.LocalDate;

import com.menejementpj.App; // Import App to access userSession
import com.menejementpj.db.DatabaseManager; // Import DatabaseManager
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert; // For showing alerts
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class GroupCard {

    @FXML
    private Label createat;

    @FXML
    private Text describ;

    @FXML
    private Label title;

    // New field to store the group ID
    private int groupId;

    /**
     * Handles the action when the "Join" button is clicked.
     * This method attempts to add the current logged-in user to this group.
     * It uses the DatabaseManager to perform the database operation and
     * provides feedback to the user via an Alert.
     * 
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    void handleJoin(ActionEvent event) {
        int currentUserId = App.userSession.getCurrentLoggedInUserID();

        if (this.groupId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid group ID. Cannot join this group.");
            return;
        }

        boolean success = DatabaseManager.addMemberToGroup(this.groupId, currentUserId);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "You have successfully joined the group: " + this.title.getText());
            try {
                App.setRoot("components/group/groupPage", "GroubPage - myGroub");
                App.userSession.setCurrentLoggedInGroupID(this.groupId);
            } catch (java.io.IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error",
                        "Failed to load the group page: " + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to Join",
                    "Could not join the group. You might already be a member or an error occurred.");
        }
    }

    public void setData(int groupId, String title, String describ, LocalDate createAt) {
        this.groupId = groupId; // Store the group ID
        this.title.setText(title);
        this.createat.setText("Created At: " + createAt); // Changed to "Created At" for clarity
        this.describ.setText(describ);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text for simplicity
        alert.setContentText(message);
        alert.showAndWait();
    }
}
