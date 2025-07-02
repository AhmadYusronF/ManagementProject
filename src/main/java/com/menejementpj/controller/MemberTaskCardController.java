package com.menejementpj.controller;

import com.menejementpj.model.MemberTask;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class MemberTaskCardController {

    @FXML
    private Label memberNameLabel;
    @FXML
    private VBox tasksContainer;

    public void setData(String pjNma, List<MemberTask> tasks) {
        memberNameLabel.setText(pjNma);
        tasksContainer.getChildren().clear();

        for (MemberTask task : tasks) {

            HBox taskRow = new HBox(10);

            Circle statusCircle = new Circle(5);
            int statusValue = task.getStatus();

            if (statusValue >= 80) {
                statusCircle.setFill(Color.web("#4CAF50"));
            } else if (statusValue >= 50) {
                statusCircle.setFill(Color.web("#FFC107"));
            } else {
                statusCircle.setFill(Color.web("#F44336"));
            }

            Label taskLabel = new Label(task.getTaskName());
            taskLabel.setStyle("-fx-font-size: 14px;");

            taskRow.getChildren().addAll(statusCircle, taskLabel);
            tasksContainer.getChildren().add(taskRow);
        }
    }
}