package com.menejementpj.components;

import com.menejementpj.App;
import com.menejementpj.auth.UserData; 
import com.menejementpj.controller.GroupPageController; 
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.Group;
import com.menejementpj.model.GroupMemberDisplay;
import com.menejementpj.model.Role;
import com.menejementpj.test.Debug; 

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
    private Group groupToEdit; 
    private ObservableList<GroupMemberDisplay> memberList = FXCollections.observableArrayList();
    private List<GroupMemberDisplay> originalMembers; 
    private GroupPageController parentController; 

    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    
    public void setParentController(GroupPageController parentController) {
        this.parentController = parentController;
    }

    
    public void setGroupToEdit(Group group) {
        this.groupToEdit = group;
        loadGroupData();
    }

    @FXML
    public void initialize() {
       
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        joinedAtColumn.setCellValueFactory(new PropertyValueFactory<>("joinedAt"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roleName")); 
        membersTable.setItems(memberList);
    }

   
    private void loadGroupData() {
        if (groupToEdit != null) {
            groupNameField.setText(groupToEdit.nama);
            descriptionArea.setText(groupToEdit.describ);

            try {
                originalMembers = DatabaseManager.getGroupMembers(groupToEdit.id);
                memberList.setAll(originalMembers); 
                System.out.println("DEBUG: GroupOptionPopUpController.loadGroupData - memberList size after loading: " + memberList.size());
                Debug.success("Loaded " + originalMembers.size() + " members for group ID: " + groupToEdit.id);
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to load group members: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load group members.");
            }
        } else {
            Debug.warn("No group to edit provided to GroupOptionPopUpController.");
           
        }
    }

   
    @FXML
    void handleAddMember(ActionEvent event) {
        try {
            AddMemberController controller = showAddMemberPopup();

            if (controller != null && controller.isUserSelected()) {
                UserData selectedUser = controller.getSelectedUser();

              
                if (memberList.stream().anyMatch(m -> m.getUserId() == selectedUser.getUserId())) {
                    showAlert(Alert.AlertType.WARNING, "Duplicate Member", "This user is already in the member list.");
                    return;
                }

               
                memberList.add(new GroupMemberDisplay(selectedUser.getUserId(), selectedUser.getUsername(), "Member", LocalDate.now()));
                Debug.success("Added member: " + selectedUser.getUsername() + " (ID: " + selectedUser.getUserId() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Debug.error("Error opening add member popup: " + e.getMessage());
        }
    }

    
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
              
                DatabaseManager.updateGroupMemberRole(groupToEdit.id, selectedMember.getUserId(),
                        DatabaseManager.getRoles().stream()
                                .filter(r -> r.getRoleName().equalsIgnoreCase(newRoleName))
                                .mapToInt(Role::getRoleId)
                                .findFirst()
                                .orElse(2)); 

                
                selectedMember.setRoleName(newRoleName);
                membersTable.refresh();
                Debug.success("Updated role for " + selectedMember.getUsername() + " to " + newRoleName);
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to update role for member: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member role.");
            }
        });
    }

   
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
             
                DatabaseManager.removeMemberFromGroup(groupToEdit.id, selectedMember.getUserId());
                
                memberList.remove(selectedMember);
                Debug.success("Kicked member: " + selectedMember.getUsername() + " (ID: " + selectedMember.getUserId() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                Debug.error("Failed to kick member: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to kick member.");
            }
        }
    }

  
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
           
            boolean groupUpdated = DatabaseManager.updateGroup(groupToEdit.id, newGroupName, newDescription);
            if (!groupUpdated) {
                showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to update group details.");
                return;
            }
            Debug.success("Group details updated for ID: " + groupToEdit.id);

           
            List<GroupMemberDisplay> membersToAdd = memberList.stream()
                    .filter(m -> originalMembers.stream().noneMatch(om -> om.getUserId() == m.getUserId()))
                    .collect(Collectors.toList());

            for (GroupMemberDisplay member : membersToAdd) {
                DatabaseManager.addMemberToGroup(groupToEdit.id, member.getUserId());
                Debug.success("Added new member to DB during save: " + member.getUsername());
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Group details and members saved successfully!");
            dialogStage.close();
            if (parentController != null) {
                parentController.refreshAllGroupData(); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Debug.error("Database error during save: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving changes.");
        }
    }

   
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
                
                    if (App.userSession.getCurrentLoggedInGroupID() == groupToEdit.id) {
                        App.userSession.setCurrentLoggedInGroupID(0);
                    }
                    dialogStage.close(); 
                    if (parentController != null) {
                        parentController.refreshAllGroupData(); 
                        App.setRoot("groupTab", "Groups - My Groups"); 
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

  
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    private AddMemberController showAddMemberPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/menejementpj/components/group/addMemberPopup.fxml")); 
        Parent root = loader.load();
        AddMemberController controller = loader.getController();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(new Scene(root));
        controller.setDialogStage(popupStage);

        popupStage.showAndWait(); 

        return controller;
    }
}
