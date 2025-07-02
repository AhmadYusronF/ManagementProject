// In ManagementProject/src/main/java/com/menejementpj/controller/TaskDialogController.java
package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.auth.UserData;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.GroupMemberDisplay;
import com.menejementpj.model.ProjectTask;
import com.menejementpj.components.PopUpAlert; // <--- Import PopUpAlert

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

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
    private ProjectTask originalTask;
    private boolean isSaved = false;

    @FXML
    public void initialize() {
        List<GroupMemberDisplay> groupMembersRaw;
        try {
            groupMembersRaw = DatabaseManager.getGroupMembers(App.userSession.getCurrentLoggedInGroupID());
            List<UserData> groupMembers = groupMembersRaw.stream()
                    .map(gm -> new UserData(gm.getUserId(), gm.getUsername()))
                    .collect(Collectors.toList());
            memberComboBox.setItems(FXCollections.observableArrayList(groupMembers));
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            memberComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTask(ProjectTask task) {
        this.originalTask = task;
        if (task != null) {
            taskNameField.setText(task.getTaskName());
            memberComboBox.getItems().stream()
                    .filter(u -> u.getUserId() == task.getAssignedMemberId())
                    .findFirst()
                    .ifPresent(memberComboBox::setValue);
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    public ProjectTask getTask() {
        UserData selectedUser = memberComboBox.getSelectionModel().getSelectedItem();
        if (originalTask != null) {
            return new ProjectTask(originalTask.getProjectTaskId(),
                                   taskNameField.getText(),
                                   selectedUser.getUsername(),
                                   selectedUser.getUserId());
        } else {
            return new ProjectTask(taskNameField.getText(),
                                   selectedUser.getUsername(),
                                   selectedUser.getUserId());
        }
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
        if (taskNameField.getText() == null || taskNameField.getText().trim().isEmpty()) {
            PopUpAlert.popupWarn("Validation Error", "Task Name Required", "Please enter a name for the task."); // <--- UNCOMMENTED/ADDED
            return false;
        }
        if (memberComboBox.getSelectionModel().getSelectedItem() == null) {
            PopUpAlert.popupWarn("Validation Error", "Assignee Required", "Please select a member to assign the task to."); // <--- UNCOMMENTED/ADDED
            return false;
        }
        return true;
    }
}