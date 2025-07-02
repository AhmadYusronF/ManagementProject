package com.menejementpj.components;

import com.menejementpj.App;
import com.menejementpj.auth.UserData; // Assuming UserData is used for selecting new members
import com.menejementpj.controller.GroupPageController; // Assuming this is the parent controller
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.Group;
import com.menejementpj.model.GroupMemberDisplay;
import com.menejementpj.model.Role; // For roles
import com.menejementpj.test.Debug; // Your Debug utility

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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupOptionPopUpController {

    @FXML private TextField groupNameField;
    @FXML private TextArea descriptionArea;
    @FXML private TableView<GroupMemberDisplay> membersTable;
    @FXML private TableColumn<GroupMemberDisplay, String> nameColumn;
    @FXML private TableColumn<GroupMemberDisplay, LocalDate> joinedAtColumn;
    @FXML private TableColumn<GroupMemberDisplay, String> rolesColumn;
    @FXML private Button deleteButton;
    @FXML private Button saveButton1;

    private Stage dialogStage;
    private Group groupToEdit; // The group object being edited
    private ObservableList<GroupMemberDisplay> memberList = FXCollections.observableArrayList();
    private List<GroupMemberDisplay> originalMembers; // To track changes for saving
    private GroupPageController parentController; // Reference to refresh parent view

    /**
     * Sets the dialog stage for this popup.
     * @param dialogStage The stage of the popup.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the parent controller to allow refreshing the main group page.
     * @param parentController The GroupPageController instance.
     */
    public void setParentController(GroupPageController parentController) {
        this.parentController = parentController;
    }

    /**
     * Sets the Group object to be edited by this popup.
     * This method should be called by the parent controller before showing the popup.
     * @param group The Group object whose details are to be displayed and edited.
     */
    public void setGroupToEdit(Group group) {
        this.groupToEdit = group;
        loadGroupData();
    }

    @FXML
    public void initialize() {
        // Initialize TableView columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        joinedAtColumn.setCellValueFactory(new PropertyValueFactory<>("joinedAt"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roleName")); // Still uses "roleName" for display
        membersTable.setItems(memberList);
    }

    /**
     * Loads the group's data into the UI fields and member table.
     */
    private void loadGroupData() {
        if (groupToEdit != null) {
            groupNameField.setText(groupToEdit.nama);
            descriptionArea.setText(groupToEdit.describ);

            try {
                originalMembers = DatabaseManager.getGroupMembers(groupToEdit.id);
                memberList.setAll(originalMembers); // Populate the ObservableList
                System.out.println("DEBUG: GroupOptionPopUpController.loadGroupData - memberList size after loading: " + memberList.size());
                Debug.success("Loaded " + originalMembers.size() + " members for group ID: " + groupToEdit.id);
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to load group members: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load group members.");
            }
        } else {
            Debug.warn("No group to edit provided to GroupOptionPopUpController.");
            // Optionally, disable save/delete buttons or show an error
        }
    }

    /**
     * Handles adding a new member to the group. Opens a popup to select a user.
     * @param event The ActionEvent.
     */
    @FXML
    void handleAddMember(ActionEvent event) {
        try {
            AddMemberController controller = showAddMemberPopup();

            if (controller != null && controller.isUserSelected()) {
                UserData selectedUser = controller.getSelectedUser();

                // Check for duplicates in the current member list
                if (memberList.stream().anyMatch(m -> m.getUserId() == selectedUser.getUserId())) {
                    showAlert(Alert.AlertType.WARNING, "Duplicate Member", "This user is already in the member list.");
                    return;
                }

                // Add the new member to the observable list with default "Member" role
                memberList.add(new GroupMemberDisplay(selectedUser.getUserId(), selectedUser.getUsername(), "Member", LocalDate.now()));
                Debug.success("Added member: " + selectedUser.getUsername() + " (ID: " + selectedUser.getUserId() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Debug.error("Error opening add member popup: " + e.getMessage());
        }
    }

    /**
     * Handles editing the role of a selected member.
     * @param event The ActionEvent.
     */
    @FXML
    void handleEditRoles(ActionEvent event) {
        GroupMemberDisplay selectedMember = membersTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to edit their role.");
            return;
        }

        List<String> roleNames = DatabaseManager.getRoles().stream()
                                                .map(Role::getRoleName)
                                                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedMember.getRoleName(), roleNames);
        dialog.setTitle("Edit Role");
        dialog.setHeaderText("Change role for " + selectedMember.getUsername());
        dialog.setContentText("Choose new role:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newRoleName -> {
            try {
                // Update the role in the database
                DatabaseManager.updateGroupMemberRole(groupToEdit.id, selectedMember.getUserId(),
                        DatabaseManager.getRoles().stream()
                                .filter(r -> r.getRoleName().equalsIgnoreCase(newRoleName))
                                .mapToInt(Role::getRoleId)
                                .findFirst()
                                .orElse(2)); // Default to role ID 2 if not found

                // Update the ObservableList to reflect the change in UI
                selectedMember.setRoleName(newRoleName);
                membersTable.refresh(); // Refresh the table view to show the updated role
                Debug.success("Updated role for " + selectedMember.getUsername() + " to " + newRoleName);
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to update role for member: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member role.");
            }
        });
    }

    /**
     * Handles kicking (removing) a selected member from the group.
     * @param event The ActionEvent.
     */
    @FXML
    void handleKickMember(ActionEvent event) {
        GroupMemberDisplay selectedMember = membersTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to kick.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Kick");
        confirmAlert.setHeaderText("Remove " + selectedMember.getUsername() + " from the group?");
        confirmAlert.setContentText("Are you sure you want to remove this member?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Remove from database
                DatabaseManager.removeMemberFromGroup(groupToEdit.id, selectedMember.getUserId());
                // Remove from ObservableList to update UI
                memberList.remove(selectedMember);
                Debug.success("Kicked member: " + selectedMember.getUsername() + " (ID: " + selectedMember.getUserId() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to kick member: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to kick member.");
            }
        }
    }

    /**
     * Handles saving changes to the group (name, description) and synchronizing members.
     * @param event The ActionEvent.
     */
    @FXML
    void handleSave(ActionEvent event) {
        if (groupToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No group selected for editing.");
            return;
        }

        String newGroupName = groupNameField.getText();
        String newDescription = descriptionArea.getText();

        if (newGroupName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Group name cannot be empty.");
            return;
        }

        try {
            // 1. Update group details (name, description)
            boolean groupUpdated = DatabaseManager.updateGroup(groupToEdit.id, newGroupName, newDescription);
            if (!groupUpdated) {
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to update group details.");
                return;
            }
            Debug.success("Group details updated for ID: " + groupToEdit.id);

            // 2. Synchronize members (add new ones, remove old ones)
            // Members added via handleAddMember are already in memberList.
            // Members kicked via handleKickMember are already removed from memberList.
            // Roles edited via handleEditRoles are already updated in DB and memberList.

            // We only need to handle members that were added through the popup
            // and are not in the originalMembers list.
            List<GroupMemberDisplay> membersToAdd = memberList.stream()
                    .filter(m -> originalMembers.stream().noneMatch(om -> om.getUserId() == m.getUserId()))
                    .collect(Collectors.toList());

            for (GroupMemberDisplay member : membersToAdd) {
                DatabaseManager.addMemberToGroup(groupToEdit.id, member.getUserId());
                Debug.success("Added new member to DB during save: " + member.getUsername());
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Group details and members saved successfully!");
            dialogStage.close(); // Close the popup
            if (parentController != null) {
                parentController.refreshAllGroupData(); // Call the new refresh method
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Debug.error("Database error during save: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving changes.");
        }
    }

    /**
     * Handles deleting the entire group.
     * @param event The ActionEvent.
     */
    @FXML
    void handleDelete(ActionEvent event) {
        if (groupToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No group selected for deletion.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Group: " + groupToEdit.nama + "?");
        confirmAlert.setContentText("This action cannot be undone. All group data and members will be removed. Are you sure?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = DatabaseManager.deleteGroup(groupToEdit.id);
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Group deleted successfully!");
                    // Reset current group in session if the deleted group was active
                    if (App.userSession.getCurrentLoggedInGroupID() == groupToEdit.id) {
                        App.userSession.setCurrentLoggedInGroupID(0);
                    }
                    dialogStage.close(); // Close the popup
                    if (parentController != null) {
                        parentController.refreshAllGroupData(); // Call the new refresh method
                        App.setRoot("groupTab", "Groups - My Groups"); // Navigate back to list after delete
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete the group.");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                Debug.error("Error deleting group: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during group deletion.");
            }
        }
    }

    /**
     * Helper method to display an alert dialog.
     * @param alertType The type of alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title The title of the alert window.
     * @param message The message content of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Placeholder method to show the Add Member popup.
     * You might want to move this to a utility class or the parent controller.
     * @return The AddMemberController instance if a user was selected, null otherwise.
     * @throws IOException If the FXML cannot be loaded.
     */
    private AddMemberController showAddMemberPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menejementpj/components/group/addMemberPopup.fxml")); // Adjust path as needed
        Parent root = loader.load();
        AddMemberController controller = loader.getController();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));
        controller.setDialogStage(popupStage); // Pass the stage to the controller

        popupStage.showAndWait(); // Show and wait for the popup to close

        return controller;
    }
}
