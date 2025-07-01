package com.menejementpj.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GroupCreatePopupController {

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField groupNameField;

    @FXML
    private Pane iconPane;

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
    void handleAddMember(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

    @FXML
    void handleUploadFile(ActionEvent event) {

    }

}
