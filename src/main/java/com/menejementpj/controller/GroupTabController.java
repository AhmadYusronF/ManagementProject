package com.menejementpj.controller;

import java.io.IOException;

import com.menejementpj.App;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class GroupTabController {

    @FXML
    private GridPane groubJoinContainer;

    @FXML
    void toggleCreateGroub(MouseEvent event) {

    }

    @FXML
    void toggleGoToGroub(ActionEvent event) {

    }

    @FXML
    void toggleGoToHome(ActionEvent event) throws IOException {
        App.setRoot("beranda", "Beranda");
    }

    @FXML
    void toggleGotoChat(ActionEvent event) {

    }

    @FXML
    void toggleGotoProject(ActionEvent event) {

    }

}
