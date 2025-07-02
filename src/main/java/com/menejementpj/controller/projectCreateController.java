package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.ProjectTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;


public class projectCreateController {

    @FXML
    private TextField projectNameField;
    @FXML
    private TextArea projectDecriptionArea;
    @FXML
    private TextField repoUrlField;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<ProjectTask> membersTable;
    @FXML
    private TableColumn<ProjectTask, String> nameColumn;
    @FXML
    private TableColumn<ProjectTask, LocalDate> createdAtCollumn;
    @FXML
    private TableColumn<ProjectTask, String> memberCollumn;

    private final ObservableList<ProjectTask> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        createdAtCollumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        memberCollumn.setCellValueFactory(new PropertyValueFactory<>("assignedMemberName"));
        membersTable.setItems(taskList);
    }

    @FXML
    void handleSave(ActionEvent event) {
        String projectName = projectNameField.getText();
        String description = projectDecriptionArea.getText();
        String repoUrl = repoUrlField.getText();

        if (projectName == null || projectName.trim().isEmpty()) {
            // You should show an alert here
            return;
        }

        int newProjectId = DatabaseManager.createProject(projectName, description, repoUrl,
                App.userSession.getCurrentLoggedInGroupID());

        if (newProjectId != -1) {
            for (ProjectTask task : taskList) {
                // Note: The original 'createTask' method was updated to remove the description.
                DatabaseManager.createTask(newProjectId, task.getAssignedMemberId(), task.getTaskName());
            }
            // You should show a success alert here
            closeWindow();
        } else {
            // You should show an error alert here
        }
    }

    @FXML
    void handleAddTask(ActionEvent event) {
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
    void handleUpdateTask(ActionEvent event) {
        ProjectTask selectedTask = membersTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            // You should show a selection error alert here
            return;
        }
        try {
            TaskDialogController controller = showTaskDialog(selectedTask);
            if (controller.isSaved()) {
                taskList.set(membersTable.getSelectionModel().getSelectedIndex(), controller.getTask());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDeleteTask(ActionEvent event) {
        ProjectTask selectedTask = membersTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskList.remove(selectedTask);
        } else {
            // You should show a selection error alert here
        }
    }

    /**
     * Opens the task dialog FXML in a new modal window.
     * 
     * @param task The task to edit, or null for a new task.
     * @return The controller of the dialog.
     */
    private TaskDialogController showTaskDialog(ProjectTask task) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/menejementpj/components/project/TaskDialog.fxml"));
        VBox page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(task == null ? "Add Task" : "Edit Task");
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

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}