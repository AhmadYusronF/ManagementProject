package com.menejementpj.components;

import com.menejementpj.controller.TaskDialogController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.Project;
import com.menejementpj.model.ProjectTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import java.util.Optional;

public class OptionProjectController {

    @FXML
    private TextField projectNameField;
    @FXML
    private TextArea projectDecriptionArea;
    @FXML
    private TextField repoUrlField;
    @FXML
    private Button saveButton;

    @FXML
    private TableView<ProjectTask> taskTable;
    @FXML
    private TableColumn<ProjectTask, String> nameColumn;
    @FXML
    private TableColumn<ProjectTask, LocalDate> createdAtCollumn;
    @FXML
    private TableColumn<ProjectTask, String> memberCollumn;

    private Stage dialogStage;
    private Project currentProject;
    private boolean projectWasDeleted = false;
    private final ObservableList<ProjectTask> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        createdAtCollumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        memberCollumn.setCellValueFactory(new PropertyValueFactory<>("assignedMemberName"));
        taskTable.setItems(taskList);

        // Load tasks for the current project when initialized, if a project is set
        // This might need to be called after setProject()
        // if (currentProject != null) {
        // taskList.setAll(DatabaseManager.getProjectTasksForTable(currentProject.id));
        // }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProject(Project project) {
        this.currentProject = project;
        projectNameField.setText(project.title);
        projectDecriptionArea.setText(project.description);
        repoUrlField.setText(project.repoUrl);
        // Load tasks related to this project
        taskList.setAll(DatabaseManager.getProjectTasksForTable(currentProject.id)); // Load tasks here
    }

    public boolean wasProjectDeleted() {
        return projectWasDeleted;
    }

    @FXML
    void handleSave(ActionEvent event) {
        // Logic to update the project details
        currentProject.title = projectNameField.getText();
        currentProject.description = projectDecriptionArea.getText();
        currentProject.repoUrl = repoUrlField.getText();

        if (DatabaseManager.updateProject(currentProject)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project details updated successfully!");
            dialogStage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to update project details.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Project: " + currentProject.title);
        alert.setContentText("This action is permanent and cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (DatabaseManager.deleteProject(currentProject.id)) {
                this.projectWasDeleted = true; // Set the flag to true on successful deletion
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project deleted successfully!");
                dialogStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete the project.");
            }
        }
    }

    @FXML
    void handleAddTask(ActionEvent event) {
        try {
            // Note: when creating a new task, it needs to be associated with
            // currentProject.id
            // This is typically handled when the task is saved to the DB via
            // DatabaseManager.createTask()
            TaskDialogController controller = showPopup(event, "/com/menejementpj/components/project/TaskDialog.fxml",
                    "Add New Task").getController(); // Get controller directly

            if (controller.isSaved()) {
                ProjectTask newTask = controller.getTask(); // Get the new task from the dialog
                // Add to UI list
                taskList.add(newTask);
                // Persist to DB, associating with the current project
                if (DatabaseManager.createTask(currentProject.id, newTask.getAssignedMemberId(),
                        newTask.getTaskName())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Task added successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add task to database.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", "Error opening task dialog.");
        }
    }

    @FXML
    void handleUpdateTask(ActionEvent event) {
        ProjectTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to update.");
            return;
        }
        try {
            // 1. Load the FXML and get the controller
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/project/TaskDialog.fxml"));
            Parent root = loader.load();
            TaskDialogController controller = loader.getController();

            // 2. Create and configure the stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Task");
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner((Stage) ((Node) event.getSource()).getScene().getWindow());
            popupStage.setScene(new Scene(root));

            // 3. Set the data BEFORE showing the stage
            controller.setDialogStage(popupStage);
            controller.setTask(selectedTask); // This now happens before the dialog is shown

            // 4. Show the dialog and wait for it to be closed
            popupStage.showAndWait();

            // 5. Process the result after the dialog is closed
            if (controller.isSaved()) {
                ProjectTask updatedTask = controller.getTask();
                // Update the UI
                taskList.set(taskTable.getSelectionModel().getSelectedIndex(), updatedTask);

                // Update the database
                if (DatabaseManager.updateTask(updatedTask.getProjectTaskId(),
                        updatedTask.getTaskName(),
                        updatedTask.getAssignedMemberId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Task updated successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to update task in database. Check console for details.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", "Error opening task dialog.");
        }
    }

    @FXML
    void handleDeleteTask(ActionEvent event) {
        ProjectTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {

            taskList.remove(selectedTask);
            showAlert(Alert.AlertType.INFORMATION, "Info",
                    "Task removed from list. (Database deletion not yet implemented)");

        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to delete.");
        }
    }

    private FXMLLoader showPopup(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);

        // Determine the owner window from the event source or the main dialog stage
        Node source = (Node) event.getSource();
        Stage ownerStage = (source != null) ? (Stage) source.getScene().getWindow() : this.dialogStage;
        popupStage.initOwner(ownerStage);

        popupStage.setTitle(title);
        popupStage.setScene(new Scene(root));

        // Pass the stage to the dialog's controller if it needs to close itself
        if (loader.getController() instanceof TaskDialogController) {
            TaskDialogController controller = loader.getController();
            controller.setDialogStage(popupStage);
            // The task is now passed via setTask method, not constructor
        }

        popupStage.showAndWait();
        return loader;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}