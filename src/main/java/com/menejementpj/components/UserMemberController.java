package com.menejementpj.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserMemberController {

    public int id;

    @FXML
    private Label role;

    @FXML
    private Label username;

    public void setData(int id, String username, String role) {
        this.id = id;
        this.username.setText(username);
        this.role.setText(role);
    }
}
