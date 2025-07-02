package com.menejementpj.components;

import com.menejementpj.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class YesnoController {

    public boolean rslt;

    @FXML
    void handelCencel(ActionEvent event) {
        this.rslt = false;
        Utils.closeWindow(event);
    }

    @FXML
    void handelConfirm(ActionEvent event) {
        this.rslt = true;
        Utils.closeWindow(event);
    }

    public boolean getActionResult() {
        return this.rslt;
    }

    public void setActionResult(boolean result) {
        this.rslt = result;
    }

}
