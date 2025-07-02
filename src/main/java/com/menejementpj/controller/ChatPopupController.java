package com.menejementpj.controller;

import com.menejementpj.App;
import com.menejementpj.db.DatabaseManager;
import com.menejementpj.model.ChatMessage;
import com.menejementpj.test.Debug;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 

import java.io.IOException;
import java.time.LocalDateTime; 
import java.util.List;

public class ChatPopupController {

    @FXML
    private VBox chatHistoryBox;

    @FXML
    private Label labelGroupName;

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        Debug.log("Chat controller initialized.");
        labelGroupName.setText(App.group.getGroupName());
        loadChatHistory();
        scrollPane.vvalueProperty().bind(chatHistoryBox.heightProperty());
    }

    @FXML
    void handleSendAction(ActionEvent event) {
        String messageText = messageField.getText();
        if (messageText == null || messageText.trim().isEmpty()) {
            return;
        }

        DatabaseManager.sendMessage(messageText);

       
        ChatMessage newMessage = new ChatMessage(
                App.userSession.getCurrentLoggedInUserID(),
                App.user.getUsername(),
                messageText,
                LocalDateTime.now() 
        );
        addMessageToView(newMessage);

        messageField.clear();
    }

    private void loadChatHistory() {
        int currentGroupId = App.userSession.getCurrentLoggedInGroupID();
        List<ChatMessage> history = DatabaseManager.getChatMessages(currentGroupId);

        for (ChatMessage message : history) {
            addMessageToView(message);
        }
    }

    private void addMessageToView(ChatMessage message) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/menejementpj/components/ChatMessageItem.fxml"));

            HBox messageNode = loader.load();

            ChatMessageItemController controller = loader.getController();
            controller.setData(message);

            chatHistoryBox.getChildren().add(messageNode);

        } catch (IOException e) {
            System.err.println("Failed to load ChatMessage.fxml");
            e.printStackTrace();
        }
    }
}