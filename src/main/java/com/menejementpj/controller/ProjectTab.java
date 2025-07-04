package com.menejementpj.controller;

import com.menejementpj.components.ProjectCardController;
import java.io.IOException;
import java.util.List;
import com.menejementpj.utils.Utils;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.Project;
import com.menejementpj.test.Debug;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectTab {

    @FXML
    private GridPane projectContainer;

    @FXML
    private void initialize() {

        refreshProjectGrid();
    }

    private void refreshProjectGrid() {
        Debug.log("Refreshing project grid...");
        projectContainer.getChildren().clear();

        try {
            List<Project> projects = DatabaseManager.getProjectGrup();

            if (projects == null || projects.isEmpty()) {
                Debug.warn("No projects found to display.");
                return;
            }

            int column = 0;
            int row = 0;

            for (Project project : projects) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/project/projectCard.fxml"));

                Parent cardRoot = loader.load();
                ProjectCardController controller = loader.getController();

                controller.setRefreshCallback(this::refreshProjectGrid);

                controller.setData(
                        project.title,
                        project.createdAt,
                        project.description,
                        project.id);

                projectContainer.add(cardRoot, column, row);

                column++;
                if (column >= 3) {
                    column = 0;
                    row++;
                }
            }
            Debug.success("Project grid refreshed successfully.");

        } catch (Exception e) {
            Debug.error("Failed to refresh project grid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handelCreateProject(ActionEvent event) {
        showPopup(event, "/com/menejementpj/components/project/projectCreatePopUp.fxml", "Create Project");

        refreshProjectGrid();
    }

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "Beranda - Management Project");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {
        showPopupChat(event, "/com/menejementpj/chatPopUp.fxml", "Group Chat");
    }

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        Utils.logout(event);
    }

    @FXML
    void toggleGotoGrup(ActionEvent event) throws IOException {
        if (App.userSession.getCurrentLoggedInGroupID() != 0) {
            App.setRoot("components/group/groupPage", "GroupPage - myGroup");
        } else {
            App.setRoot("groupTab", "Group - join or create");
        }
    }

    private void showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException e) {
            System.err.println("⚠️ Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void showPopupChat(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Scene popupScene = new Scene(root);
            String cssPath = "/com/menejementpj/css/chatMessage.css";
            popupScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

            Stage popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(popupScene);
            popupStage.initModality(Modality.NONE);
            popupStage.setResizable(false);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}