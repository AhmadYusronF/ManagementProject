package com.menejementpj.components;

import java.time.LocalDate;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ProjectCardController {
    public int projectId = 0;

    @FXML
    private Text describ;

    @FXML
    private Label subtitle;

    @FXML
    private Label title;

    @FXML
    void handleDetail(ActionEvent event) {

    }

    public void setData(String title, LocalDate subTitle, String describ, int projectId) {
        this.title.setText(title);
        this.subtitle.setText("Create At: " + subTitle);
        this.describ.setText(describ);
        this.projectId = projectId;
    }

}
