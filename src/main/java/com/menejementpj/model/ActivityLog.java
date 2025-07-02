package com.menejementpj.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private final String senderName;
    private final String title; // Represents the task name
    private final String description; // Represents the activity_logs_message
    private final LocalDateTime timestamp;
    private final int progress;

    public ActivityLog(String senderName, String title, String description, LocalDateTime timestamp, int progress) {
        this.senderName = senderName;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.progress = progress;
    }

    // Getters for all fields
    public String getSenderName() {
        return senderName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getProgress() {
        return progress;
    }
}