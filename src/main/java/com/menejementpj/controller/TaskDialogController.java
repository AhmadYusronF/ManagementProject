package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.auth.UserData;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.GroupMemberDisplay;
import com.menejementpj.model.ProjectTask;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class TaskDialogController {

    @FXML
    private TextField taskNameField;
    @FXML
    private ComboBox<UserData> memberComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private ProjectTask task;
    private boolean isSaved = false;

    @FXML
    public void initialize() {
        List<GroupMemberDisplay> groupMembersRaw;
        try {
            groupMembersRaw = DatabaseManager.getGroupMembers(App.userSession.getCurrentLoggedInGroupID());
            List<UserData> groupMembers = groupMembersRaw.stream()
                    .map(gm -> new UserData(gm.getUserId(), gm.getUsername()))
                    .toList();
            memberComboBox.setItems(FXCollections.observableArrayList(groupMembers));
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            memberComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    /**
     * Sets the stage of this dialog.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the task to be edited in the dialog.
     */
    public void setTask(ProjectTask task) {
        this.task = task;
        if (task != null) {
            taskNameField.setText(task.getTaskName());
            // Find and select the existing member
            memberComboBox.getItems().stream()
                    .filter(u -> u.getUserId() == task.getAssignedMemberId())
                    .findFirst()
                    .ifPresent(memberComboBox::setValue);
        }
    }

    /**
     * Returns true if the user clicked Save, false otherwise.
     */
    public boolean isSaved() {
        return isSaved;
    }

    /**
     * Returns the created/updated task.
     */
    public ProjectTask getTask() {
        UserData selectedUser = memberComboBox.getSelectionModel().getSelectedItem();
        // Create a new task object from the dialog fields
        return new ProjectTask(taskNameField.getText(), selectedUser.getUsername(), selectedUser.getUserId());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isInputValid()) {
            isSaved = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }

    private boolean isInputValid() {
        // Add validation logic here (e.g., check if fields are empty)
        if (taskNameField.getText() == null || taskNameField.getText().trim().isEmpty()) {
            // Show alert
            return false;
        }
        if (memberComboBox.getSelectionModel().getSelectedItem() == null) {
            // Show alert
            return false;
        }
        return true;
    }
}