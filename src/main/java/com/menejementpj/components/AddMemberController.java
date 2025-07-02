
// File: AddMemberController.java (Updated to show users initially)
package com.menejementpj.components;

import com.menejementpj.db.DatabaseManager;
import com.menejementpj.auth.UserData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AddMemberController {
    @FXML private TextField searchField;
    @FXML private ListView<UserData> userListView;
    
    private Stage dialogStage;
    private UserData selectedUser;
    private boolean userSelected = false;

    @FXML
    public void initialize() {
        loadUsers("");

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            loadUsers(newText);
        });
    }
    

    private void loadUsers(String query) {
        List<UserData> users = DatabaseManager.searchAvailableUsers(query);
        userListView.setItems(FXCollections.observableArrayList(users));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isUserSelected() {
        return userSelected;
    }

    public UserData getSelectedUser() {
        return selectedUser;
    }

   @FXML
private void handleAdd() {
    selectedUser = userListView.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
        System.out.println("DEBUG: AddMemberController - Selected User ID: " + selectedUser.getUserId() + ", Username: " + selectedUser.getUsername());
        userSelected = true;
        dialogStage.close();
    } else {
        System.out.println("DEBUG: AddMemberController - No user selected in list.");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText(null);
        alert.setContentText("Please select a user from the list to add.");
        alert.showAndWait();
    }
}

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}