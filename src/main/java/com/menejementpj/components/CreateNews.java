package com.menejementpj.components;

import com.menejementpj.App;
import com.menejementpj.controller.GroupPageController;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.test.Debug;
import com.menejementpj.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateNews {

    private GroupPageController parentController;

    public void setParentController(GroupPageController parent) {
        this.parentController = parent;
    }

    @FXML
    private TextField field;

    @FXML
    void handleCencel(ActionEvent event) {
        Utils.closeWindow(event);
    }

    @FXML
    void handleCreate(ActionEvent event) {
        try {
            DatabaseManager.setNews(field.getText());
            DatabaseManager.getDataGroubA();
            Debug.info(App.mygroup.news);

            if (parentController != null) {
                parentController.refresWindow();
            }

            Utils.closeWindow(event);
        } catch (Exception e) {

            Debug.error("error update");
        }

    }

    @FXML
    private void initialize() {
        field.setText(App.mygroup.news);
    }
}
