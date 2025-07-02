package com.menejementpj.components;

import java.time.LocalDateTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class GroupCard {

    @FXML
    private Label createat;

    @FXML
    private Text describ;

    @FXML
    private Label title;

    @FXML
    void handleJoin(ActionEvent event) {

    }

    public void setData(String title, String describ, LocalDateTime createat) {
        this.title.setText(title);
        this.createat.setText("Create At: " + createat);
        this.describ.setText(describ);
    }

    
}
