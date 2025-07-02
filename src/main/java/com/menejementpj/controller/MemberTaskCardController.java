package com.menejementpj.controller;

import com.menejementpj.auth.UserData;
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
    private VBox tasksContainer; // The VBox that will hold the individual task rows

    public void setData(UserData member, List<MemberTask> tasks) {
        memberNameLabel.setText(member.getUsername());
        tasksContainer.getChildren().clear(); // Clear any default tasks

        for (MemberTask task : tasks) {
            // Create a new row for each task
            HBox taskRow = new HBox(10);

            Circle statusCircle = new Circle(5);
            int statusValue = task.getStatus(); // Get the integer status

            // Set color based on the integer value
            if (statusValue >= 80) {
                statusCircle.setFill(Color.web("#4CAF50")); // Green
            } else if (statusValue >= 50) {
                statusCircle.setFill(Color.web("#FFC107")); // Yellow
            } else {
                statusCircle.setFill(Color.web("#F44336")); // Red
            }

            Label taskLabel = new Label(task.getTaskName());
            taskLabel.setStyle("-fx-font-size: 14px;");

            taskRow.getChildren().addAll(statusCircle, taskLabel);
            tasksContainer.getChildren().add(taskRow);
        }
    }
}