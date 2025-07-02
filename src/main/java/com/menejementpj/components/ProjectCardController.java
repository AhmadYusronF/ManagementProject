package com.menejementpj.components;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.menejementpj.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectCardController {
    public int projectId = 0;

    @FXML
    private Text describ;

    @FXML
    private Label subtitle;

    @FXML
    private Label title;

    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    public void handleDetail(ActionEvent event) {
        try {
            App.userSession.setCurrentProjectID(this.projectId);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/project/projectView.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(title.getScene().getWindow());
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();

            if (refreshCallback != null) {
                refreshCallback.run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(String title, LocalDate subTitle, String describ, int projectId) {
        this.title.setText(title);
        this.subtitle.setText("Created At: " + subTitle.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        this.describ.setText(describ);
        this.projectId = projectId;
    }

}
