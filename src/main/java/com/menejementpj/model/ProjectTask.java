package com.menejementpj.model;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a single task within a project. (Description removed)
 */
public class ProjectTask {
    private StringProperty taskName;
    private ObjectProperty<LocalDate> createdAt;
    private StringProperty assignedMemberName;
    private int assignedMemberId;

    public ProjectTask(String taskName, String assignedMemberName, int assignedMemberId) {
        this.taskName = new SimpleStringProperty(taskName);
        this.createdAt = new SimpleObjectProperty<>(LocalDate.now());
        this.assignedMemberName = new SimpleStringProperty(assignedMemberName);
        this.assignedMemberId = assignedMemberId;
    }

    // Getters
    public String getTaskName() {
        return taskName.get();
    }

    public LocalDate getCreatedAt() {
        return createdAt.get();
    }

    public String getAssignedMemberName() {
        return assignedMemberName.get();
    }

    public int getAssignedMemberId() {
        return assignedMemberId;
    }

    // Property Getters for TableView binding
    public StringProperty taskNameProperty() {
        return taskName;
    }

    public ObjectProperty<LocalDate> createdAtProperty() {
        return createdAt;
    }

    public StringProperty assignedMemberNameProperty() {
        return assignedMemberName;
    }
}