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
    private int projectTaskId; // <--- NEW FIELD: Stores the ID of the task from the database
    private StringProperty taskName;
    private ObjectProperty<LocalDate> createdAt;
    private StringProperty assignedMemberName;
    private int assignedMemberId;

    // Existing constructor for creating NEW tasks (ID is not yet known)
    public ProjectTask(String taskName, String assignedMemberName, int assignedMemberId) {
        this.taskName = new SimpleStringProperty(taskName);
        this.createdAt = new SimpleObjectProperty<>(LocalDate.now()); // Set current date for new tasks
        this.assignedMemberName = new SimpleStringProperty(assignedMemberName);
        this.assignedMemberId = assignedMemberId;
        this.projectTaskId = 0; // Initialize for new tasks, will be set after DB insert if needed
    }

    // <--- NEW CONSTRUCTOR: For loading and updating existing tasks (with ID)
    public ProjectTask(int projectTaskId, String taskName, String assignedMemberName, int assignedMemberId) {
        this.projectTaskId = projectTaskId;
        this.taskName = new SimpleStringProperty(taskName);
        // When loading an existing task, you might want to fetch its original creation date from the DB.
        // For simplicity here, we'll use LocalDate.now() or you can omit if not strictly needed for this constructor.
        this.createdAt = new SimpleObjectProperty<>(LocalDate.now()); 
        this.assignedMemberName = new SimpleStringProperty(assignedMemberName);
        this.assignedMemberId = assignedMemberId;
    }

    // --- Getters ---
    public int getProjectTaskId() { // <--- NEW GETTER for the task's own ID
        return projectTaskId;
    }

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

    // --- Property Getters for TableView binding ---
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