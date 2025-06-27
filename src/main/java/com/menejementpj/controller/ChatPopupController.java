package com.menejementpj.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatPopupController {

    @FXML
    private VBox chatHistoryBox;

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void handleSendAction() {
        String message = messageField.getText();
        if (message != null && !message.trim().isEmpty()) {
            // For this example, we'll just display it locally.
            // In a real app, you would send this message to a server/other clients.
            addMessage("You", message);
            messageField.clear();

            // Auto-scroll to the bottom
            scrollPane.setVvalue(1.0);
        }
    }

    /**
     * A helper method to add a message to the chat history display.
     * 
     * @param user    The user who sent the message.
     * @param message The message content.
     */
    public void addMessage(String user, String message) {
        Text userText = new Text(user + ": ");
        userText.setStyle("-fx-font-weight: bold;");
        userText.setFill(Color.web("#212121")); // A nice blue

        Text messageText = new Text(message);
        messageText.setFill(Color.BLACK);

        TextFlow textFlow = new TextFlow(userText, messageText);
        textFlow.setStyle("-fx-padding: 5px; -fx-background-color: E0E0E0; -fx-background-radius: 5;");

        chatHistoryBox.getChildren().add(textFlow);
    }

    // You can add more methods here, e.g., to receive messages from others.
}