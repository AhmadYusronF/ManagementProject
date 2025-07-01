package com.menejementpj.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ActivityLogController {

    @FXML
    private Label proggres;

    @FXML
    private Label subTitle;

    @FXML
    private Label title;

    void setData(String title, String subTitle, String progres) {
        this.title.setText(title);
        this.subTitle.setText(subTitle);
        this.proggres.setText(progres);

    }
}
