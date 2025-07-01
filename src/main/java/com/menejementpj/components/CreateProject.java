package com.menejementpj.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateProject {

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField groupNameField;

    @FXML
    private TextField groupNameField1;

    @FXML
    private TableColumn<?, ?> joinedAtColumn;

    @FXML
    private TableView<?> membersTable;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> rolesColumn;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveButton1;

    @FXML
    void handleAddMember(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

}
