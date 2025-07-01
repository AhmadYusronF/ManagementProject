package com.menejementpj.controller;

import com.menejementpj.components.ProjectCardController;
import java.io.IOException;
import java.util.List;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectTab {

    @FXML
    void handelCreateProject(ActionEvent event) {
        showPopup(event, "/com/menejementpj/components/project/projectCreatePopUp.fxml", "Create Project");
    }

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "\"Beranda - Management Project\"");
    }

    @FXML
    void toggleGotoChat(MouseEvent event) {

    }

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void handleGoToProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project");
    }

    @FXML
    void toggleGotoGrup(ActionEvent event) throws IOException {
        if (App.userSession.getCurrentLoggedInGroupID() != 0) {

            App.setRoot("components/group/groupPage", "GroubPage - myGroub");
        } else {
            App.setRoot("groupTab", "Group - join or create");
        }
    }

    @FXML
    private GridPane projectContainer;

    @FXML
    private void initialize() {
        Debug.log("initialize");

        try {

            List<Project> projects = DatabaseManager.getProjectGrup();

            if (projects == null || projects.isEmpty()) {
                Debug.warn("Tidak ada project yang ditemukan.");
                return;
            }

            for (Project project : projects) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/project/projectCard.fxml"));

                Parent cardRoot = loader.load();
                ProjectCardController controller = loader.getController();

                controller.setData(
                        project.title,
                        project.createdAt,
                        project.description,
                        project.id);

                projectContainer.getChildren().add(cardRoot);
            }

            Debug.success("Semua komponen project berhasil dimuat.");

        } catch (Exception e) {
            Debug.error("Gagal Menampilkan Projects: " + e.getMessage());
            e.printStackTrace();
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

}
