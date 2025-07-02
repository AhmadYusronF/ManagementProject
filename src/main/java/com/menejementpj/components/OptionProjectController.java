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
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProject(Project project) {
        this.currentProject = project;
        projectNameField.setText(project.title);
        projectDecriptionArea.setText(project.description);
        repoUrlField.setText(project.repoUrl);
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
            dialogStage.close();
        } else {
            // Show an error alert
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
                dialogStage.close();
            } else {
                // Show an error alert
            }
        }
    }

    @FXML
    void handleAddTask(ActionEvent event) {
        try {
            FXMLLoader loader = showPopup(event, "/com/menejementpj/components/project/TaskDialog.fxml",
                    "Add New Task");
            TaskDialogController controller = loader.getController();

            if (controller.isSaved()) {
                taskList.add(controller.getTask());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            FXMLLoader loader = showPopup(event, "/com/menejementpj/components/project/TaskDialog.fxml", "Edit Task");
            TaskDialogController controller = loader.getController();
            controller.setTask(selectedTask); // Pre-fill the dialog with existing task data

            if (controller.isSaved()) {
                taskList.set(taskTable.getSelectionModel().getSelectedIndex(), controller.getTask());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteTask(ActionEvent event) {
        ProjectTask selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskList.remove(selectedTask);
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