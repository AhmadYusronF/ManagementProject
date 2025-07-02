package com.menejementpj.components;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.GroupMemberDisplay;
import com.menejementpj.model.Role;
import com.menejementpj.auth.UserData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupCreatePopupController {

    @FXML
    private TextField groupNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button saveButton;

    @FXML
    private TableView<GroupMemberDisplay> membersTable;
    @FXML
    private TableColumn<GroupMemberDisplay, String> nameColumn;
    @FXML
    private TableColumn<GroupMemberDisplay, LocalDate> joinedAtColumn;
    @FXML
    private TableColumn<GroupMemberDisplay, String> rolesColumn;

    private final ObservableList<GroupMemberDisplay> memberList = FXCollections.observableArrayList();
    private List<Role> availableRoles;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        joinedAtColumn.setCellValueFactory(new PropertyValueFactory<>("joinedAt"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        membersTable.setItems(memberList);

        availableRoles = DatabaseManager.getRoles();

        // --- CORRECTED LOGIC HERE ---
        int currentLoggedInUserId = App.userSession.getCurrentLoggedInUserID();

        // IMPORTANT: You need a way to get the username for this ID.
        // Let's assume you have a method in DatabaseManager like:
        // public static UserData getUserById(int userId) { ... }
        // or
        // public static String getUsernameById(int userId) { ... }

        if (currentLoggedInUserId != -1) { // Ensure a user is actually logged in
            // Option 1: If App.user is reliably updated after login
            // and contains the username for the current session.
            // This assumes App.user.setUserId() and App.user.setUsername() are called after
            // login.
            String currentLoggedInUsername = App.user.getUsername();

            // Option 2 (Safer): Fetch username from DB using the session ID
            // You'll need to implement this in DatabaseManager if it doesn't exist
            // try {
            // UserData loggedInUser = DatabaseManager.getUserById(currentLoggedInUserId);
            // if (loggedInUser != null) {
            // memberList.add(
            // new GroupMemberDisplay(loggedInUser.getUserId(), loggedInUser.getUsername(),
            // "Owner", LocalDate.now()));
            // } else {
            // System.err.println("Error: Logged in user data not found in DB for ID: " +
            // currentLoggedInUserId);
            // }
            // } catch (SQLException e) {
            // e.printStackTrace();
            // System.err.println("Database error fetching logged in user data.");
            // }

            // For now, let's assume App.user's username is also set correctly after login
            // along with App.userSession.getCurrentLoggedInUserID()
            memberList.add(
                    new GroupMemberDisplay(currentLoggedInUserId, currentLoggedInUsername, "Owner", LocalDate.now()));

        } else {
            System.err.println("WARNING: No user logged in, cannot add creator as member.");
            // Perhaps show an alert to the user, or handle this case appropriately
        }
        // --- END CORRECTED LOGIC ---
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String groupName = groupNameField.getText();
        String description = descriptionArea.getText();

        if (groupName == null || groupName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Group name cannot be empty.");
            return;
        }

        boolean success = DatabaseManager.createGroupWithMembers(groupName, description, memberList);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Group created successfully!");
            App.userSession.setCurrentLoggedInGroupID(DatabaseManager.getLastInsertedGroupId());
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create the group.");
        }
    }

    @FXML
    public void handleAddMember(ActionEvent event) {
        try {
            AddMemberController controller = showAddMemberPopup();

            if (controller.isUserSelected()) {
                UserData selectedUser = controller.getSelectedUser();
                System.out.println("DEBUG: Main Controller - User selected from popup. ID: " + selectedUser.getUserId()
                        + ", Username: " + selectedUser.getUsername());

                if (memberList.stream().anyMatch(m -> m.getUserId() == selectedUser.getUserId())) {
                    showAlert(Alert.AlertType.WARNING, "Duplicate Member", "This user is already in the member list.");
                    return;
                }

                memberList.add(new GroupMemberDisplay(selectedUser.getUserId(), selectedUser.getUsername(), "Member",
                        LocalDate.now()));
            } else {
                System.out.println("DEBUG: Main Controller - Popup closed without user selection.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditRoles(ActionEvent event) {
        GroupMemberDisplay selectedMember = membersTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to edit their role.");
            return;
        }

        if ("Owner".equalsIgnoreCase(selectedMember.getRoleName())) {
            showAlert(Alert.AlertType.INFORMATION, "Action Not Allowed", "The group owner's role cannot be changed.");
            return;
        }

        List<String> roleNames = availableRoles.stream().map(Role::getRoleName).collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedMember.getRoleName(), roleNames);
        dialog.setTitle("Edit Role");
        dialog.setHeaderText("Select a new role for " + selectedMember.getUsername());
        dialog.setContentText("Role:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newRole -> {
            selectedMember.setRoleName(newRole);
            membersTable.refresh();
        });
    }

    @FXML
    public void handleKickMember(ActionEvent event) {
        GroupMemberDisplay selectedMember = membersTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to kick.");
            return;
        }

        if ("Owner".equalsIgnoreCase(selectedMember.getRoleName())) {
            showAlert(Alert.AlertType.ERROR, "Action Not Allowed", "You cannot kick the group owner.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Kick");
        confirmDialog.setHeaderText("Are you sure you want to kick " + selectedMember.getUsername() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            memberList.remove(selectedMember);
        }
    }

    private AddMemberController showAddMemberPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/menejementpj/components/group/addMemberPopup.fxml"));
        Parent page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Member");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner((Stage) saveButton.getScene().getWindow());

        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        AddMemberController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        dialogStage.showAndWait();

        return controller;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}