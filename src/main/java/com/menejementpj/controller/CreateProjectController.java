package com.menejementpj.controller;

import java.io.IOException;
import java.time.LocalDate;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.ProjectTask;
import com.menejementpj.utils.Utils;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateProjectController {

    @FXML
    private TextField projectNameField;
    @FXML
    private TextArea projectDecriptionArea;
    @FXML
    private TextField repoUrlField;

    @FXML
    private TableView<ProjectTask> taskTable;
    @FXML
    private TableColumn<ProjectTask, String> nameColumn;
    @FXML
    private TableColumn<ProjectTask, LocalDate> createdAtCollumn;
    @FXML
    private TableColumn<ProjectTask, String> memberCollumn;

    @FXML
    private TextArea descriptionTextArea; 
    @FXML
    private TextArea titleTextArea;

    @FXML
    void handleConfirm(ActionEvent event) {
        String title = titleTextArea.getText();
        String description = descriptionTextArea.getText();

        if (title.isBlank()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        System.out.println("CONFIRMED: Saving Project");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);

        closeWindow(event);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        System.out.println("CANCELLED: Project creation aborted.");
        Utils.closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private Button saveButton;

    private final ObservableList<ProjectTask> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        createdAtCollumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        memberCollumn.setCellValueFactory(new PropertyValueFactory<>("assignedMemberName"));
        taskTable.setItems(taskList);
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String projectName = projectNameField.getText();
        String description = projectDecriptionArea.getText();
        String repoUrl = repoUrlField.getText();
        if (projectName == null || projectName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Project name is required.");
            return;
        }
        int newProjectId = DatabaseManager.createProject(projectName, description, repoUrl,
                App.userSession.getCurrentLoggedInGroupID());

        if (newProjectId != -1) {
           
            for (ProjectTask task : taskList) {
                DatabaseManager.createTask(newProjectId, task.getAssignedMemberId(), task.getTaskName());
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project has been created successfully!");
            Utils.closeWindow(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create the project.");
        }
    }

    @FXML
    public void handleAddTask(ActionEvent event) {
        try {
            TaskDialogController controller = showTaskDialog(null); 
            if (controller.isSaved()) {
                taskList.add(controller.getTask());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateTask(ActionEvent event) {
        ProjectTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to update.");
            return;
        }
        try {
            TaskDialogController controller = showTaskDialog(selectedTask);
            if (controller.isSaved()) {
                ProjectTask updatedTask = controller.getTask();
                taskList.set(taskTable.getSelectionModel().getSelectedIndex(), updatedTask);

                if (DatabaseManager.updateTask(selectedTask.getProjectTaskId(), // Use selectedTask.getProjectTaskId()
                        updatedTask.getTaskName(),
                        updatedTask.getAssignedMemberId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Task updated successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update task in database.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", "Error opening task dialog.");
        }
    }

    @FXML
    public void handleDeleteTask(ActionEvent event) {
        ProjectTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
           
            taskList.remove(selectedTask);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a task to delete.");
        }
    }

    private TaskDialogController showTaskDialog(ProjectTask task) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/menejementpj/components/project/TaskDialog.fxml"));
        VBox page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(task == null ? "Add New Task" : "Edit Task");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(((Stage) saveButton.getScene().getWindow()));
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        TaskDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setTask(task);
        dialogStage.showAndWait();
        return controller;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}