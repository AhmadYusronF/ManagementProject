package com.menejementpj.controller;

import java.io.IOException;
import java.util.List;

import com.menejementpj.App;
import com.menejementpj.components.ProjectCardController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.Group;
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

public class GroupTabController {

    @FXML
    private GridPane groubJoinContainer;

    @FXML
    void handleCreateGroub(ActionEvent event) {
        // showPopup(event, null, null);
        showPopup(event, "/com/menejementpj/components/group/groupCreatePopUp.fxml", "GrubCreate");
    }

    @FXML
    void toggleGoToGroub(ActionEvent event) {

    }

    @FXML
    void toggleLogout(ActionEvent event) throws IOException {
        App.setRoot("login", "Login - Management Project");
    }

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "Beranda");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {

    }

    @FXML
    void toggleGotoProject(ActionEvent event) throws IOException {
        App.setRoot("projectTab", "Project");
    }

    @FXML
    private void initialize() {
        try {

            List<Group> groups = DatabaseManager.getAllGroub();

            if (groups == null || groups.isEmpty()) {
                Debug.warn("Tidak ada project yang ditemukan.");
                return;
            }

            int col = 0;
            int row = 0;

            for (Group group : groups) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/menejementpj/components/project/projectCard.fxml"));
                Parent cardRoot = loader.load();
                ProjectCardController controller = loader.getController();

                controller.setData(
                        group.nama,
                        group.createAt,
                        group.describ,
                        group.id
                        );

                GridPane.setColumnIndex(cardRoot, col);
                GridPane.setRowIndex(cardRoot, row);
                groubJoinContainer.getChildren().add(cardRoot);

                col++;
                if (col == 2) {
                    col = 0;
                    row++;
                }
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
