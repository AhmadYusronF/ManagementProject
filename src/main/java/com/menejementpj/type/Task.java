package com.menejementpj.type;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty taskName = new SimpleStringProperty();
    private final ObjectProperty<TaskStatus> status = new SimpleObjectProperty<>();

    public Task(String taskName, TaskStatus status) {
        this.taskName.set(taskName);
        this.status.set(status);
    }

    // --- Getters and Setters ---

    public String getTaskName() {
        return taskName.get();
    }

    public void setTaskName(String taskName) {
        this.taskName.set(taskName);
    }

    public StringProperty taskNameProperty() {
        return taskName;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    public void setStatus(TaskStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<TaskStatus> statusProperty() {
        return status;
    }
}