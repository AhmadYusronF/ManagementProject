package com.menejementpj.components;

import com.menejementpj.model.ActivityLog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.time.format.DateTimeFormatter;

public class ActivityLogCardController {
    
    @FXML private Label senderNameLabel;
    @FXML private Label title;
    @FXML private Label subTitle;
    @FXML private Label timestampLabel;
    @FXML private Label proggres;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setData(ActivityLog log) {
      
        senderNameLabel.setText(log.getSenderName());
        title.setText(log.getTitle());
        subTitle.setText(log.getDescription());
        timestampLabel.setText(log.getTimestamp().format(formatter));
        proggres.setText(log.getProgress() + "%");

        int progressValue = log.getProgress();
        Color statusColor;

        if (progressValue >= 80) {
            statusColor = Color.web("#4CAF50"); // Green
        } else if (progressValue >= 50) {
            statusColor = Color.web("#FFC107"); // Yellow
        } else {
            statusColor = Color.web("#F44336"); // Red
        }
        
        proggres.setTextFill(statusColor);
    }
}