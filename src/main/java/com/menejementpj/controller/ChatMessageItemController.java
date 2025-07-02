package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.model.ChatMessage;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

public class ChatMessageItemController {

    @FXML
    private HBox rootContainer;
    @FXML
    private VBox messageBubble;
    @FXML
    private Label nameLabel;
    @FXML
    private Label sentAtLabel;
    @FXML
    private Label messageLabel;

  
    @FXML
    private HBox headerBox;
    @FXML
    private Region headerSpacer;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public void setData(ChatMessage message) {
        nameLabel.setText(message.getSenderName());
        messageLabel.setText(message.getMessage());
        sentAtLabel.setText(message.getTimestamp().format(TIME_FORMATTER));

        int currentUserId = App.userSession.getCurrentLoggedInUserID();
        if (message.getSenderId() == currentUserId) {
       
            rootContainer.setAlignment(Pos.CENTER_RIGHT);
            headerBox.getChildren().setAll(sentAtLabel, headerSpacer, nameLabel); 
            messageBubble.getStyleClass().add("my-message-bubble");
        } else {
 
            rootContainer.setAlignment(Pos.CENTER_LEFT);
            headerBox.getChildren().setAll(nameLabel, headerSpacer, sentAtLabel); 
            messageBubble.getStyleClass().add("other-message-bubble");
        }
    }
}