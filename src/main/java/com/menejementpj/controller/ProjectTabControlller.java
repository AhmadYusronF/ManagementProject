package com.menejementpj.controller;

import java.io.IOException;

import com.menejementpj.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectTabControlller {

    @FXML
    void handelCreateProject(ActionEvent event) {

    }

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "\"Beranda - Management Project\"");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {
        showPopupChat(event, "/com/menejementpj/chatPopUp.fxml", "Group Chat");
    }

    @FXML
    void toggleGotoGrup(ActionEvent event) throws IOException {
        if (App.userSession.getCurrentLoggedInGroupID() != 0) {

            App.setRoot("components/group/groupPage", "GroubPage - myGroub");
        } else {
            App.setRoot("groupTab", "Group - join or create");
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

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(ownerStage);

            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}