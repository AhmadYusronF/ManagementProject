package com.menejementpj.components;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.TaskSelection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class CreateActivity {

    @FXML
    private TextField activityDescription;
    @FXML
    private TextField statusProgress;
    @FXML
    private ChoiceBox<TaskSelection> taskName;

    private Stage dialogStage;

    @FXML
    public void initialize() {

        int projectId = App.userSession.getCurrentProjectID();

        List<TaskSelection> tasks = DatabaseManager.getTasksForProject(projectId);

        taskName.setItems(FXCollections.observableArrayList(tasks));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    void handleConfirmAction(ActionEvent event) {
        TaskSelection selectedTask = taskName.getSelectionModel().getSelectedItem();
        String description = activityDescription.getText();
        int progress = Integer.parseInt(statusProgress.getText());

        if (selectedTask == null || description.trim().isEmpty()) {
            showAlert("Validation Error", "Please select a task and provide a description.");
            return;
        }

        DatabaseManager.createActivityLog(
                App.userSession.getCurrentProjectID(),
                selectedTask.getTaskId(),
                App.userSession.getCurrentLoggedInUserID(),
                description);

        DatabaseManager.updateTaskStatus(selectedTask.getTaskId(), progress);

        dialogStage.close();
    }

    @FXML
    void handleCancelAction(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}