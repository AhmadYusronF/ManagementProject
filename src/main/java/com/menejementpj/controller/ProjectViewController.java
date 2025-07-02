package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.auth.UserData;
import com.menejementpj.components.ActivityLogCardController;
import com.menejementpj.components.CreateActivity;
import com.menejementpj.components.OptionProjectController; // Correct import
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.ActivityLog;
import com.menejementpj.model.MemberTask;
import com.menejementpj.model.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ProjectViewController {

    @FXML
    private Label projectNameLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private Label updatedAtLabel;
    @FXML
    private Text projectDescriptionText;
    @FXML
    private VBox activityLogContainer;
    @FXML
    private VBox memberTaskContainer;

    private Project currentProject;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        int projectId = App.userSession.getCurrentProjectID();
        if (projectId == 0) {
            projectNameLabel.setText("No Project Selected");
            return;
        }
        refreshProjectView();
    }

    private void refreshProjectView() {
        int projectId = App.userSession.getCurrentProjectID();
        loadProjectDetails(projectId);
        loadActivityLogs(projectId);
        loadMemberTasks(projectId);
    }

    private void loadProjectDetails(int projectId) {
        currentProject = DatabaseManager.getProjectDetails(projectId);
        if (currentProject != null) {
            projectNameLabel.setText(currentProject.title);
            projectDescriptionText.setText(currentProject.description);
            createdAtLabel.setText(currentProject.createdAt.format(formatter));
            updatedAtLabel.setText(currentProject.updateAt.format(formatter));
        }
    }

    private void loadActivityLogs(int projectId) {
        activityLogContainer.getChildren().clear();
        List<ActivityLog> logs = DatabaseManager.getActivityLogs(projectId);
        for (ActivityLog log : logs) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/project/activityLogCard.fxml"));
                HBox cardNode = loader.load();
                ActivityLogCardController controller = loader.getController();
                controller.setData(log);
                activityLogContainer.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMemberTasks(int projectId) {
        memberTaskContainer.getChildren().clear();
        Map<UserData, List<MemberTask>> tasksByMember = DatabaseManager.getTasksGroupedByMember(projectId);
        for (Map.Entry<UserData, List<MemberTask>> entry : tasksByMember.entrySet()) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/project/memberTaskCard.fxml"));
                VBox cardNode = loader.load();
                MemberTaskCardController controller = loader.getController();
                controller.setData(entry.getKey().getUsername(), entry.getValue());
                memberTaskContainer.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleViewOnGithub(ActionEvent event) {
        try {
            if (currentProject != null && Desktop.isDesktopSupported() && currentProject.repoUrl != null) {
                Desktop.getDesktop().browse(new URI(currentProject.repoUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleOption(ActionEvent event) {
        if (currentProject == null)
            return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/project/projectOptionPopUp.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Project Options");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(projectNameLabel.getScene().getWindow());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            OptionProjectController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProject(currentProject);

            dialogStage.showAndWait();

            if (controller.wasProjectDeleted()) {

                ((Stage) projectNameLabel.getScene().getWindow()).close();
                refreshProjectView();
            } else {

                refreshProjectView();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddActivity(ActionEvent event) {
        showActivityCreationPopup();
        loadActivityLogs(App.userSession.getCurrentProjectID());
    }

    private void showActivityCreationPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/project/activityCreate.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Activity");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(projectNameLabel.getScene().getWindow());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CreateActivity controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}