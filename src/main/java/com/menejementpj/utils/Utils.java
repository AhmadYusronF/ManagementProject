package com.menejementpj.utils;

import java.io.IOException;

import com.menejementpj.App;
import com.menejementpj.components.YesnoController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Utils {

    public static boolean showPopup(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlFile));
            Parent root = loader.load();

            YesnoController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            return controller.getActionResult();

        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
            return false;
        }
    }

    public static void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void logout(ActionEvent event) throws IOException {
        boolean rstl = showPopup(event, "/com/menejementpj/yesNo.fxml", "Confirm");

        if (rstl) {
            App.group.setNull();
            App.mygroup.setNul();
            App.user.setNull();
            App.userSession.setNull();
            App.setRoot("login", "Login - Management Project");
        } else {
            System.out.println("Cencel Reload");
        }

    }

}
